package cn.wrh.smart.dove.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import cn.wrh.smart.dove.dal.AppDatabase;
import cn.wrh.smart.dove.dal.converter.CageStatusConverter;
import cn.wrh.smart.dove.dal.converter.DateConverter;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.dal.exception.RestoreFailedException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.schedulers.Schedulers;

/**
 * A:SN|STATUS|CREATED_AT
 * B:CAGE_SN|COUNT|LAYING_AT|REVIEW_AT|HATCH_AT|SOLD_AT
 *
 * @author bruce.wu
 * @date 2018/7/19
 */
public class BackupManager {

    private static final String TAG = "BackupManager";

    private static final String TARGET_DIR;

    private static final int MAX_HISTORY = 5;
    private static final String NAME_FORMAT = "dove.bak.%d";
    private static final String PREFIX_CAGE = "A:";
    private static final String PREFIX_EGG = "B:";
    private static final String FIELD_SPLITTER = "|";

    static {
        TARGET_DIR = Environment.getExternalStorageDirectory().getPath() + "/dove";
        initBackupDirectory(TARGET_DIR);
    }

    private static void initBackupDirectory(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            Log.d(TAG, "exists dirs: " + path);
            return;
        }
        if (dir.mkdirs()) {
            Log.d(TAG, "make dirs: " + path);
        } else {
            Log.e(TAG, "make dirs failed: " + path);
        }
    }

    private final AppDatabase database;

    public BackupManager(AppDatabase database) {
        this.database = database;
    }

    public void backup(Consumer<Tuple<Integer, Integer>> complete,
                       Consumer<Throwable> error) {
        final Writer writer = new Writer();
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .map(i -> database.cageDao().queryAll())//load all cage
                .flatMap(Flowable::fromIterable)//convert list to one by one
                .map(cage -> {
                    List<EggEntity> eggs = database.eggDao().queryByCageId(cage.getId());
                    return new Tuple<>(cage, eggs);
                })
                .doOnSubscribe(subscription -> {
                    rotate();
                    writer.open(getFile(0));
                })
                .doOnNext(tuple -> {
                    CageEntity cage = tuple.getFirst();
                    writer.write(cage);
                    tuple.getSecond().forEach(egg -> writer.write(cage.getSerialNumber(), egg));
                })
                .doOnError(error::accept)
                .doOnComplete(() -> {
                    writer.close();
                    complete.accept(writer.getTuple());
                })
                .subscribe();
    }

    public void restore(Consumer<Tuple<Integer, Integer>> complete,
                        Consumer<Throwable> error) {
        final Counter counter = new Counter();
        final File file0 = getFile(0);
        if (!file0.exists()) {
            error.accept(new FileNotFoundException());
            return;
        }
        Flowable.just(1)
                .subscribeOn(Schedulers.io())
                .flatMap(i -> Flowable.create((FlowableEmitter<String> emitter) -> {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file0))) {
                        clear();
                        reader.lines().forEach(emitter::onNext);
                        emitter.onComplete();
                    } catch (IOException e) {
                        emitter.onError(e);
                    }
                }, BackpressureStrategy.BUFFER))
                .map(this::parse)
                .doOnError(error::accept)
                .doOnComplete(() -> complete.accept(counter.toTuple()))
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

    private void clear() {
        database.cageDao().clear();
        database.eggDao().clear();
        database.taskDao().clear();
    }

    private Object parse(String line) {
        if (isCage(line)) {
            return Converter.toCage(line);
        }
        if (isEgg(line)) {
            return Converter.toEgg(line);
        }
        return null;
    }

    private boolean isCage(String text) {
        return text.startsWith(PREFIX_CAGE);
    }

    private boolean isEgg(String text) {
        return text.startsWith(PREFIX_EGG);
    }

    /**
     * @return 恢复数据库时的首选文件
     */
    public static String getCandidate() {
        return getFile(0).getAbsolutePath();
    }

    private static void rotate() {
        //don't rotate if index 0 not xist
        File file0 = getFile(0);
        if (!file0.exists()) {
            Log.d(TAG, "file 0 not exist");
            return;
        }

        deleteOldest();

        for (int i = MAX_HISTORY - 2; i >= 0; i--) {
            rotate(i);
        }
    }

    private static void rotate(int index) {
        if (index < 0) {
            return;
        }
        File file1 = getFile(index + 1);
        if (file1.exists()) {
            file1.delete();
        }

        File file0 = getFile(index);
        if (file0.exists()) {
            file0.renameTo(file1);
        }
    }

    private static void deleteOldest() {
        File last = getFile(MAX_HISTORY - 1);
        if (last.exists()) {
            last.delete();
        }
    }

    private static File getFile(int index) {
        String name = String.format(Locale.getDefault(), NAME_FORMAT, index);
        return new File(TARGET_DIR, name);
    }

    private static class Counter {

        private int totalCage = 0;

        private int totalEgg = 0;

        void incCage() {
            totalCage++;
        }

        void incEgg() {
            totalEgg++;
        }

        public Tuple<Integer, Integer> toTuple() {
            return new Tuple<>(totalCage, totalEgg);
        }

    }

    private static class Writer {

        private PrintWriter writer;

        private final Counter counter = new Counter();

        void open(File file) {
            try {
                this.writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            } catch (IOException e) {
                Log.e(TAG, "Can not open file for write: " + file.getAbsolutePath(), e);
            }
        }

        void write(CageEntity entity) {
            counter.incCage();
            write(Converter.fromCage(entity));
        }

        void write(String cageSn, EggEntity entity) {
            counter.incEgg();
            write(Converter.fromEgg(cageSn, entity));
        }

        private void write(String text) {
            Log.v(TAG, "write: " + text);
            writer.println(text);
        }

        void close() {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }

        Tuple<Integer, Integer> getTuple() {
            return counter.toTuple();
        }

    }

    private static class Converter {

        /**
         * A:SN|STATUS|CREATED_AT
         *
         * @param entity cage entity
         * @return formatted string
         */
        static String fromCage(CageEntity entity) {
            return PREFIX_CAGE + entity.getSerialNumber()
                    + FIELD_SPLITTER + CageStatusConverter.fromStatus(entity.getStatus())
                    + FIELD_SPLITTER + toTimestamp(entity.getCreatedAt());
        }

        /**
         * B:CAGE_SN|COUNT|LAYING_AT|REVIEW_AT|HATCH_AT|SOLD_AT
         *
         * @param sn cage sn
         * @param entity egg entity
         * @return formatted string
         */
        static String fromEgg(String sn, EggEntity entity) {
            return PREFIX_EGG + sn + FIELD_SPLITTER + entity.getCount()
                    + FIELD_SPLITTER + toTimestamp(entity.getLayingAt())
                    + FIELD_SPLITTER + toTimestamp(entity.getReviewAt())
                    + FIELD_SPLITTER + toTimestamp(entity.getHatchAt())
                    + FIELD_SPLITTER + toTimestamp(entity.getSoldAt());
        }

        static CageEntity toCage(String text) {
            String[] fields = text.substring(2).split("[" + FIELD_SPLITTER + "]");
            CageEntity entity = new CageEntity();
            entity.setSerialNumber(fields[0]);
            entity.setStatus(CageStatusConverter.toStatus(fields[1]));
            entity.setCreatedAt(DateConverter.toDate(fields[2]));
            return entity;
        }

        static Tuple<String, EggEntity> toEgg(String text) {
            String[] fields = text.substring(2).split("[" + FIELD_SPLITTER + "]");
            EggEntity entity = new EggEntity();
            String cageSn = fields[0];
            entity.setCount(toCount(fields[1]));
            entity.setLayingAt(DateConverter.toDate(fields[2]));
            if (fields.length > 3) {
                entity.setReviewAt(DateConverter.toDate(fields[3]));
            }
            if (fields.length > 4) {
                entity.setHatchAt(DateConverter.toDate(fields[4]));
            }
            if (fields.length > 5) {
                entity.setSoldAt(DateConverter.toDate(fields[5]));
            }
            entity.determineStage();
            return new Tuple<>(cageSn, entity);
        }

        private static String toTimestamp(Date date) {
            String ts = DateConverter.toTimestamp(date);
            return ts == null ? "" : ts;
        }

        private static Integer toCount(String text) {
            return Integer.valueOf(text);
        }

    }

}
