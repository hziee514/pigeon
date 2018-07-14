package cn.wrh.smart.dove;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public class AppExecutors {

    private static AppExecutors instance;

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (AppExecutors.class) {
                if (instance == null) {
                    instance = new AppExecutors();
                }
            }
        }
        return instance;
    }

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(),
                new ThreadPoolExecutor(0,
                2,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>()),
                new MainThreadExecutor());
    }

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public void diskIO(@NonNull Runnable r) {
        diskIO.execute(r);
    }

    public void networkIO(@NonNull Runnable r) {
        networkIO.execute(r);
    }

    public void mainThread(@NonNull Runnable r) {
        mainThread.execute(r);
    }

    private static class MainThreadExecutor implements Executor {

        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
