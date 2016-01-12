package wrh.pigeon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/9.
 */
public class DbManager {

    private SQLiteDatabase db_ = null;

    private static final String db_name_ = "/gezi/gezi.db";
    private static final String bdb_name_ = "/gezi/backup.db";
    private static final String LOG_NAME = "DbManager";

    private Context ctx_ = null;

    public SQLiteDatabase getDb(){
        return db_;
    }

    public static String getSdcardPath(String sub){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + sub;
        //return "/sdcard" + sub;
    }

    public DbManager(Context ctx){
        ctx_ = ctx;
        open();
        //backup();
    }

    public void open(){
        if(db_ != null){
            return;
        }
        db_ = SQLiteDatabase.openDatabase(getSdcardPath(db_name_), null, SQLiteDatabase.CREATE_IF_NECESSARY);
        db_.execSQL("create table if not exists cage_info(id INTEGER PRIMARY KEY,sn TEXT UNIQUE,status INTEGER,create_dt DATETIME)");
        db_.execSQL("create table if not exists egg_info(id INTEGER PRIMARY KEY,cage_id INTEGER, num INTEGER,lay_dt DATETIME,review_dt DATETIME,hatch_dt DATETIME,off_dt DATETIME,status INTEGER,fin_dt DATETIME)");
        db_.execSQL("create table if not exists today_works(id INTEGER PRIMARY KEY,cage_id INTEGER,egg_id INTEGER,work_type INTEGER,create_dt DATETIME,fin_dt DATETIME)");
        db_.execSQL("delete from today_works where date(create_dt) < date('now')");
        Log.d(LOG_NAME, "onCreate: db created");
    }

    public void close(){
        if (db_ != null) {
            db_.close();
        }
    }

    public Boolean copyFile(File s, File t){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try{
            if (!t.getParentFile().exists()){
                t.getParentFile().mkdirs();
            }

            fis = new FileInputStream(s);
            fos = new FileOutputStream(t);

            in = fis.getChannel();
            out = fos.getChannel();

            in.transferTo(0, in.size(), out);

            Log.i(LOG_NAME, "copyFile from " + s.getAbsolutePath() + " to " + t.getAbsolutePath() + " success");

            return true;
        }catch (IOException e){
            Log.e(LOG_NAME, "copyFile from " + s.getAbsolutePath() + " to " + t.getAbsolutePath() + " failed: " + e.getMessage());
            return false;
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null);
                    fos.close();
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }catch (IOException e){

            }
        }
    }

    public static String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public void backup(){
        File dbfile = new File(getSdcardPath(db_name_));
        File bdbfile = new File(getSdcardPath("/gezi/backup"+getTimeStamp()+".db"));

        if (bdbfile.exists()){
            bdbfile.delete();
        }

        if (copyFile(dbfile, bdbfile)){
            Toast.makeText(ctx_, R.string.msg_backup_succ, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ctx_, R.string.msg_backup_fail, Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean batchAddCages(String roomId, String groupId, String layerId, int firstSn, int lastSn, int status){
        String sql = "insert into cage_info(sn,status,create_dt) values(?,?,datetime('now'))";
        String sn = "";
        db_.beginTransaction();
        try {
            DecimalFormat df = new DecimalFormat("00");
            for (int i = firstSn; i <= lastSn; i++) {
                sn = roomId + "-" + groupId + "-" + layerId + df.format(i);
                db_.execSQL(sql, new Object[]{sn, status});
            }
            db_.setTransactionSuccessful();

            return true;
        } catch (SQLException e) {
            Toast.makeText(ctx_, sn + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        } finally {
            db_.endTransaction();
        }
    }

    public Map<String, List<Map<String, String>>> getGroupedCages(int which){
        String sql = "";
        switch (which){
            case CageExpandableListActivity.FILTER_IDLE:
                sql = "select id, sn, status, substr(sn,1,5) as catelog from cage_info where status = 0 order by catelog, sn";
                break;
            case CageExpandableListActivity.FILTER_HEALTHLY:
                sql = "select id, sn, status, substr(sn,1,5) as catelog from cage_info where status = 1 order by catelog, sn";
                break;
            case CageExpandableListActivity.FILTER_SICK:
                sql = "select id, sn, status, substr(sn,1,5) as catelog from cage_info where status = 2 order by catelog, sn";
                break;
            case CageExpandableListActivity.FILTER_ALL:
            default:
                sql = "select id, sn, status, substr(sn,1,5) as catelog from cage_info order by catelog, sn";
                break;
        }
        Map<String, List<Map<String, String>>> grouped = new LinkedHashMap<String, List<Map<String, String>>>();
        Cursor c = db_.rawQuery(sql, null);
        if (c != null){
            if (c.moveToFirst()){
                do {
                    String group_name = c.getString(3);
                    if (!grouped.containsKey(group_name)){
                        grouped.put(group_name, new ArrayList<Map<String, String>>());
                    }
                    Map<String, String> record = new HashMap<String, String>();
                    record.put("id", c.getString(0));
                    record.put("sn", c.getString(1));
                    record.put("status", c.getString(2));
                    grouped.get(group_name).add(record);
                }while(c.moveToNext());
            }
            c.close();
        }
        return grouped;
    }

    public Map<String, String> getBusyCages(){
        Map<String, String> cages = new HashMap<String, String>();
        Cursor c = db_.rawQuery("select id, sn from cage_info where status != 0 order by sn", null);
        if (c != null){
            if (c.moveToFirst()){
                do {
                    cages.put(c.getString(1), c.getString(0));
                }while(c.moveToNext());
            }
            c.close();
        }
        return cages;
    }

    public Map<String, List<Map<String, String>>> getGroupedFeedsByDate(){
        String sql = "select b.id, b.cage_id, a.sn as cage_sn, " +
                "( " +
                "case " +
                "when b.hatch_dt is not null then 1 " +
                "when b.lay_dt is not null then 0 " +
                "end " +
                ") as stage, " +
                "( " +
                "case " +
                "when b.hatch_dt is not null then date(b.hatch_dt) " +
                "when b.lay_dt is not null then date(b.lay_dt) " +
                "end " +
                ") as dt " +
                "from cage_info a, egg_info b " +
                "where a.id = b.cage_id  and a.status != 0 and b.status = 0 " +
                "order by dt desc, cage_sn, stage ";
        Map<String, List<Map<String, String>>> grouped = new LinkedHashMap<String, List<Map<String, String>>>();
        Cursor c = db_.rawQuery(sql, null);
        if (c != null){
            if (c.moveToFirst()){
                do {
                    String group_name = c.getString(4);
                    if (!grouped.containsKey(group_name)){
                        grouped.put(group_name, new ArrayList<Map<String, String>>());
                    }
                    Map<String, String> record = new HashMap<String, String>();
                    record.put("id", c.getString(0));
                    record.put("cage_id", c.getString(1));
                    record.put("cage_sn", c.getString(2));
                    record.put("stage", c.getString(3));
                    record.put("dt", c.getString(4));
                    grouped.get(group_name).add(record);
                }while(c.moveToNext());
            }
            c.close();
        }
        return grouped;
    }

    public Map<String, List<Map<String, String>>> getGroupedFeedsByStage(){
        String sql = "select b.id, b.cage_id, a.sn as cage_sn, " +
                "( " +
                "case " +
                "when b.hatch_dt is not null then 1 " +
                "when b.lay_dt is not null then 0 " +
                "end " +
                ") as stage, " +
                "( " +
                "case " +
                "when b.hatch_dt is not null then date(b.hatch_dt) " +
                "when b.lay_dt is not null then date(b.lay_dt) " +
                "end " +
                ") as dt " +
                "from cage_info a, egg_info b " +
                "where a.id = b.cage_id  and a.status != 0 and b.status = 0 " +
                "order by stage, dt desc, cage_sn ";
        Map<String, List<Map<String, String>>> grouped = new LinkedHashMap<String, List<Map<String, String>>>();
        Cursor c = db_.rawQuery(sql, null);
        if (c != null){
            if (c.moveToFirst()){
                do {
                    String group_name = c.getString(3);
                    switch (c.getInt(3)) {
                        case 0:
                            group_name = ctx_.getResources().getString(R.string.lay_egg);
                            break;
                        case 1:
                            group_name = ctx_.getResources().getString(R.string.hatch_egg);
                            break;
                    }
                    if (!grouped.containsKey(group_name)){
                        grouped.put(group_name, new ArrayList<Map<String, String>>());
                    }
                    Map<String, String> record = new HashMap<String, String>();
                    record.put("id", c.getString(0));
                    record.put("cage_id", c.getString(1));
                    record.put("cage_sn", c.getString(2));
                    record.put("stage", c.getString(3));
                    record.put("dt", c.getString(4));
                    grouped.get(group_name).add(record);
                }while(c.moveToNext());
            }
            c.close();
        }
        return grouped;
    }

    public Boolean rebuildTodayWorks(){
        db_.beginTransaction();
        try {
            db_.execSQL("delete from today_works");

            //没下蛋的要检查下蛋
            db_.execSQL(
                    "insert into today_works(cage_id, egg_id, work_type, create_dt, fin_dt) " +
                    "select distinct a.id as cage_id, null as egg_id, 0 as work_type, datetime('now') as create_dt, null as fin_dt " +
                    "from cage_info a " +
                    "where a.status != 0 and a.id not in(select distinct cage_id from egg_info x where x.status = 0) ");

            //孵化后第14天后要检查下蛋
            db_.execSQL(
                    "insert into today_works(cage_id, egg_id, work_type, create_dt, fin_dt) " +
                    "select distinct a.id as cage_id, b.id as egg_id, 0 as work_type, datetime('now') as create_dt, null as fin_dt " +
                    "from cage_info a , egg_info b " +
                    "where a.status != 0 and a.id = b.cage_id and b.status = 0 and b.hatch_dt is not null and date(b.hatch_dt,'14 day') < date('now') ");

            //下第一个蛋后2天内（明天，后天）检查下蛋
            db_.execSQL(
                    "insert into today_works(cage_id, egg_id, work_type, create_dt, fin_dt) " +
                    "select distinct a.id as cage_id, b.id as egg_id, 1 as work_type, datetime('now') as create_dt, null as fin_dt " +
                    "from cage_info a , egg_info b " +
                    "where a.status != 0 and a.id = b.cage_id and b.status = 0 and b.num = 1 and b.lay_dt is not null and date(b.lay_dt) < date('now') and date(b.lay_dt, '2 day') >= date('now') ");

            //下第二个蛋后第4天检查蛋的好坏
            db_.execSQL(
                    "insert into today_works(cage_id, egg_id, work_type, create_dt, fin_dt) " +
                    "select distinct a.id as cage_id, b.id as egg_id, 2 as work_type, datetime('now') as create_dt, null as fin_dt " +
                    "from cage_info a , egg_info b " +
                    "where a.status != 0 and a.id = b.cage_id and b.status = 0 and b.num = 2 and b.lay_dt is not null and date(b.lay_dt, '3 day') < date('now') ");

            //下第二个蛋后第17天检查孵化了没有
            db_.execSQL(
                    "insert into today_works(cage_id, egg_id, work_type, create_dt, fin_dt) " +
                    "select distinct a.id as cage_id, b.id as egg_id, 3 as work_type, datetime('now') as create_dt, null as fin_dt " +
                    "from cage_info a , egg_info b " +
                    "where a.status != 0 and a.id = b.cage_id and b.status = 0 and b.num = 2 and b.lay_dt is not null and date(b.lay_dt, '17 day') <= date('now') ");

            //孵化后第28天出栏
            db_.execSQL(
                    "insert into today_works(cage_id, egg_id, work_type, create_dt, fin_dt) " +
                    "select distinct a.id as cage_id, b.id as egg_id, 4 as work_type, datetime('now') as create_dt, null as fin_dt " +
                    "from cage_info a , egg_info b " +
                    "where a.status != 0 and a.id = b.cage_id and b.status = 0 and b.num = 2 and b.hatch_dt is not null and date(b.hatch_dt, '27 day') <= date('now') ");

            db_.setTransactionSuccessful();
            return true;
        } catch (SQLException e){
            Toast.makeText(ctx_, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }finally {
            db_.endTransaction();
        }
    }

    public Map<String, List<Map<String, String>>> getGroupedWorks(int which){
        String sql = "";
        switch (which) {
            case TodayWorkListActivity.FILTER_WAIT:
                sql = "select distinct a.id, a.cage_id, b.sn as cage_sn, a.egg_id, a.work_type, a.fin_dt, substr(b.sn,1,5) as catelog " +
                        "from today_works a, cage_info b " +
                        "where a.cage_id = b.id and date(a.create_dt) = date('now') and a.fin_dt is null " +
                        "order by catelog, cage_sn, work_type, fin_dt ";
                break;
            case TodayWorkListActivity.FILTER_FIN:
                sql = "select distinct a.id, a.cage_id, b.sn as cage_sn, a.egg_id, a.work_type, a.fin_dt, substr(b.sn,1,5) as catelog " +
                        "from today_works a, cage_info b " +
                        "where a.cage_id = b.id and date(a.create_dt) = date('now') and a.fin_dt is not null " +
                        "order by catelog, cage_sn, work_type, fin_dt ";
                break;
            case TodayWorkListActivity.FILTER_ALL:
            default:
                sql = "select distinct a.id, a.cage_id, b.sn as cage_sn, a.egg_id, a.work_type, a.fin_dt, substr(b.sn,1,5) as catelog " +
                        "from today_works a, cage_info b " +
                        "where a.cage_id = b.id and date(a.create_dt) = date('now') " +
                        "order by catelog, cage_sn, work_type, fin_dt ";
                break;
        }
        Map<String, List<Map<String, String>>> grouped = new LinkedHashMap<String, List<Map<String, String>>>();
        Cursor c = db_.rawQuery(sql, null);
        if (c != null){
            if (c.moveToFirst()){
                do {
                    String group_name = c.getString(6);
                    if (!grouped.containsKey(group_name)){
                        grouped.put(group_name, new ArrayList<Map<String, String>>());
                    }
                    Map<String, String> record = new HashMap<String, String>();
                    record.put("id", c.getString(0));
                    record.put("cage_id", c.getString(1));
                    record.put("cage_sn", c.getString(2));
                    record.put("egg_id", c.getString(3));
                    record.put("work_type", c.getString(4));
                    record.put("fin_dt", c.getString(5));
                    grouped.get(group_name).add(record);
                }while(c.moveToNext());
            }
            c.close();
        }
        return grouped;
    }

    public Boolean addFirstEgg(String work_id, String cage_id){
        db_.beginTransaction();
        try {
            db_.execSQL("insert into egg_info(cage_id,num,lay_dt,status) values(?,1,datetime('now'),0)", new String[]{cage_id});
            db_.execSQL("update today_works set fin_dt=datetime('now') where id=?", new String[]{work_id});
            db_.setTransactionSuccessful();
            return true;
        }catch (SQLException e){
            Toast.makeText(ctx_, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }finally {
            db_.endTransaction();
        }
    }

    public Boolean addEgg(String work_id, String egg_id){
        db_.beginTransaction();
        try {
            db_.execSQL("update egg_info set num=2,lay_dt=datetime('now') where id=?", new String[]{egg_id});
            db_.execSQL("update today_works set fin_dt=datetime('now') where id=?", new String[] {work_id});
            db_.setTransactionSuccessful();
            return true;
        }catch (SQLException e){
            Toast.makeText(ctx_, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }finally {
            db_.endTransaction();
        }
    }

    public Boolean reviewEgg(String work_id, String egg_id){
        db_.beginTransaction();
        try {
            db_.execSQL("update egg_info set review_dt=datetime('now') where id=?", new String[]{egg_id});
            db_.execSQL("update today_works set fin_dt=datetime('now') where id=?", new String[] {work_id});
            db_.setTransactionSuccessful();
            return true;
        }catch (SQLException e){
            Toast.makeText(ctx_, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }finally {
            db_.endTransaction();
        }
    }

    public Boolean hatchEgg(String work_id, String egg_id){
        db_.beginTransaction();
        try {
            db_.execSQL("update egg_info set hatch_dt=datetime('now') where id=?", new String[]{egg_id});
            db_.execSQL("update today_works set fin_dt=datetime('now') where id=?", new String[] {work_id});
            db_.setTransactionSuccessful();
            return true;
        }catch (SQLException e){
            Toast.makeText(ctx_, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }finally {
            db_.endTransaction();
        }
    }

    public Boolean offEgg(String work_id, String egg_id){
        db_.beginTransaction();
        try {
            db_.execSQL("update egg_info set status=1,off_dt=datetime('now') where id=?", new String[]{egg_id});
            db_.execSQL("update today_works set fin_dt=datetime('now') where id=?", new String[] {work_id});
            db_.setTransactionSuccessful();
            return true;
        }catch (SQLException e){
            Toast.makeText(ctx_, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }finally {
            db_.endTransaction();
        }
    }

    public void finWork(String work_id){
        db_.execSQL("update today_works set fin_dt=datetime('now') where id=?", new String[] {work_id});
    }
}
