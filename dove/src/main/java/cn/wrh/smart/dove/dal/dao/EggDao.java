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

    @Query("DELETE FROM T_EGG WHERE ID = :id")
    void delete(int id);

    @Query("SELECT * FROM T_EGG WHERE id = :id")
    EggEntity fetch(int id);

    @Query("SELECT a.*, b.SERIAL_NUMBER as CAGE_SN " +
            "FROM T_EGG a, T_CAGE b " +
            "WHERE a.CAGE_ID = b.ID AND a.ID = :id " +
            "LIMIT 1 ")
    EggBO findById(int id);

    @Query("SELECT a.*, b.SERIAL_NUMBER as CAGE_SN " +
            "FROM T_EGG a, T_CAGE b " +
            "WHERE a.CAGE_ID = b.ID AND a.STAGE != :stage " +
            "ORDER BY b.SERIAL_NUMBER")
    List<EggBO> withoutStage(EggModel.Stage stage);

}
