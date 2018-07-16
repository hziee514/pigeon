package cn.wrh.smart.dove.dal.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.domain.bo.EggBO;
import cn.wrh.smart.dove.domain.model.EggModel;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
@Dao
public interface EggDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EggEntity...entities);

    @Update
    void update(EggEntity...entities);

    @Delete
    void delete(EggEntity...entities);

    @Query("SELECT * FROM EGG WHERE id = :id")
    EggEntity fetch(int id);

    @Query("SELECT a.*, b.SERIAL_NUMBER as CAGE_SN " +
            "FROM EGG a, CAGE b " +
            "WHERE a.CAGE_ID = b.ID AND a.STAGE != :stage " +
            "ORDER BY CAGE_SN")
    List<EggBO> withoutStage(EggModel.Stage stage);

}
