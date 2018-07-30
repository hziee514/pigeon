package cn.wrh.smart.dove.storage;

import java.util.List;
import java.util.function.Consumer;

import cn.wrh.smart.dove.dal.AppDatabase;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.util.Tuple;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.schedulers.Schedulers;

/**
 * @author bruce.wu
 * @date 2018/7/30
 */
public class BackupRunner {

    public void backup(final AppDatabase database,
                       final BackupFile.Writer writer,
                       final Consumer<Counter> complete,
                       final Consumer<Throwable> error) {
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> database.cageDao().queryAll())//load all cage
                .flatMap(Flowable::fromIterable)//convert list to one by one
                .map(cage -> {
                    List<EggEntity> eggs = database.eggDao().queryByCageId(cage.getId());
                    return new Tuple<>(cage, eggs);
                })
                .doOnNext(tuple -> {
                    CageEntity cage = tuple.getFirst();
                    writer.write(cage);
                    tuple.getSecond().forEach(egg -> writer.write(cage.getSerialNumber(), egg));
                })
                .doOnError(tr -> {
                    writer.close();
                    error.accept(tr);
                })
                .doOnComplete(() -> {
                    writer.close();
                    complete.accept(writer.getCounter());
                })
                .subscribe();
    }

    public void restore(final AppDatabase database,
                        final BackupFile.Reader reader,
                        final Consumer<Counter> complete,
                        final Consumer<Throwable> error) {
        final Counter counter = new Counter();
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .flatMap(i -> Flowable.create((FlowableEmitter<Object> emitter) -> {
                    database.cageDao().clear();
                    database.eggDao().clear();
                    database.taskDao().clear();
                    try (BackupFile.Reader r = reader) {
                        String line;
                        while ((line = r.readLine()) != null) {
                            emitter.onNext(Converter.parseLine(line));
                        }
                        emitter.onComplete();
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }, BackpressureStrategy.BUFFER))
                .doOnError(error::accept)
                .doOnComplete(() -> complete.accept(counter))
                .doOnNext(o -> {
                    if (o instanceof CageEntity) {
                        database.cageDao().insert((CageEntity)o);
                        counter.incCage();
                    } else if (o instanceof Tuple) {
                        //noinspection unchecked
                        Tuple<String, EggEntity> tuple = (Tuple<String, EggEntity>)o;
                        String sn = tuple.getFirst();
                        int cageId = database.cageDao().getIdBySn(sn);
                        if (cageId <= 0) {
                            throw new RestoreFailedException("Invalid id by cage sn: " + sn);
                        }
                        EggEntity entity = tuple.getSecond();
                        entity.setCageId(cageId);
                        database.eggDao().insert(entity);
                        counter.incEgg();
                    }
                })
                .subscribe();
    }

}
