package wrh.pigeon;

import android.app.Application;
import android.util.Log;

/**
 * Created by Administrator on 2016/1/9.
 */
public class MyApplication extends Application {

    private DbManager dbm_ = null;

    private static final String LOG_NAME = "MyApplication";

    public DbManager getDbManager(){
        return dbm_;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbm_ = new DbManager(this);
        Log.d(LOG_NAME, "onCreate");
    }

    @Override
    public void onTerminate() {
        if (dbm_ != null) {
            dbm_.close();
        }
        super.onTerminate();
        Log.d(LOG_NAME, "onTerminate");
    }
}
