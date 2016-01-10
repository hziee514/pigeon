package wrh.pigeon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
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

        backup();
    }

    public void open(){
        if(db_ != null){
            return;
        }
        db_ = SQLiteDatabase.openDatabase(getSdcardPath(db_name_), null, SQLiteDatabase.CREATE_IF_NECESSARY);
        db_.execSQL("create table if not exists cage_info(id INTEGER PRIMARY KEY,sn TEXT UNIQUE,status INTEGER,create_dt DATETIME)");
        db_.execSQL("create table if not exists egg_info(id INTEGER PRIMARY KEY,cage_id INTEGER,lay_dt DATETIME,review_dt DATETIME,hatch_dt DATETIME,off_dt DATETIME,status INTEGER,fin_dt DATETIME)");
        db_.execSQL("create table if not exists today_works(id INTEGER PRIMARY KEY,cage_id INTEGER,egg_id INTEGER,work_type INTEGER,create_dt DATETIME,fin_dt DATETIME)");
        db_.execSQL("delete from today_works where date(create_dt) < date('now')");
        Log.d(LOG_NAME, "onCreate: db created");
    }

    public void copyFile(File s, File t){
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
        }catch (IOException e){
            Log.e(LOG_NAME, "copyFile from " + s.getAbsolutePath() + " to " + t.getAbsolutePath() + " failed: " + e.getMessage());
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

    private String getTimeStamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public void backup(){
        File dbfile = new File(getSdcardPath(db_name_));
        File bdbfile = new File(getSdcardPath("/gezi/backup"+getTimeStamp()+".db"));

        if (bdbfile.exists()){
            bdbfile.delete();
        }
        copyFile(dbfile, bdbfile);
    }

    public void add(List<CageInfo> cages){
        db_.beginTransaction();
        try {
            for(CageInfo cage : cages){
                db_.execSQL("insert into cage_info(sn,status,create_dt) values(?,?,datetime('now'))", new Object[]{cage.sn,cage.status});
            }
            db_.setTransactionSuccessful();
        } finally {
            db_.endTransaction();
        }
    }

    public String getCageStatus(int status){
        switch (status){
            case 0:
                return ctx_.getResources().getString(R.string.cage_idle);
            case 1:
                return ctx_.getResources().getString(R.string.cage_healthly);
            case 2:
                return ctx_.getResources().getString(R.string.cage_sick);
            default:
                return "";
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
        Cursor c = db_.rawQuery(sql, new String[]{});
        if (c != null){
            if (c.moveToFirst()){
                do {
                    String group_name = c.getString(3);
                    if (!grouped.containsKey(group_name)){
                        grouped.put(group_name, new ArrayList<Map<String, String>>());
                    }
                    Map<String, String> record = new LinkedHashMap<String, String>();
                    record.put("id", c.getString(0));
                    record.put("sn", c.getString(1));
                    record.put("status", c.getString(2));
                    record.put("str_status", getCageStatus(c.getInt(2)));
                    grouped.get(group_name).add(record);
                }while(c.moveToNext());
            }
            c.close();
        }
        return grouped;
    }

    public void close(){
        if (db_ != null) {
            db_.close();
        }
    }
}
