package cn.wrh.smart.dove.dal.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.domain.model.CageModel;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
@Dao
public interface CageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CageEntity...entities);

    @Update
    void update(CageEntity...entities);

    @Delete
    void delete(CageEntity...entities);

    @Query("DELETE FROM T_CAGE")
    void clear();

    @Query("SELECT * FROM T_CAGE WHERE ID = :id")
    CageEntity fetch(int id);

    @Query("SELECT * FROM T_CAGE ORDER BY SERIAL_NUMBER")
    List<CageEntity> query();

    @Query("SELECT * FROM T_CAGE")
    List<CageEntity> queryAll();

    @Query("SELECT * FROM T_CAGE WHERE STATUS = :status ORDER BY SERIAL_NUMBER")
    List<CageEntity> query(CageModel.Status status);

    @Query("SELECT SERIAL_NUMBER FROM T_CAGE WHERE STATUS IN (:statuses) ORDER BY SERIAL_NUMBER")
    List<String> querySns(CageModel.Status...statuses);

    default List<String> querySnsInUsing() {
        return querySns(CageModel.Status.Healthy, CageModel.Status.Sickly);
    }

    @Query("SELECT ID FROM T_CAGE WHERE SERIAL_NUMBER = :sn LIMIT 1")
    int getIdBySn(String sn);

}
