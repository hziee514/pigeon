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

    @Query("SELECT * FROM CAGE ORDER BY SERIAL_NUMBER")
    List<CageEntity> all();

    @Query("SELECT * FROM CAGE WHERE STATUS = :status ORDER BY SERIAL_NUMBER")
    List<CageEntity> loadByStatus(CageModel.Status status);

}
