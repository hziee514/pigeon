package cn.wrh.smart.dove.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import static android.view.View.NO_ID;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public abstract class CompatActivityPresenter<T extends ViewDelegate> extends AppCompatActivity {

    private T delegate;

    @NonNull
    public T getViewDelegate() {
        return delegate;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        delegate = Helper.createViewDelegate(getClass());

        delegate.create(getLayoutInflater(), null, savedInstanceState);
        setContentView(delegate.getRootView());

        initToolbar();

        delegate.onInit();

        onLoaded(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onUnload();
        delegate.onRelease();
        delegate = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (delegate.getOptionsMenuId() != NO_ID) {
            getMenuInflater().inflate(delegate.getOptionsMenuId(), menu);
            if (delegate.isShowMenuIcon()) {
                Helper.setOptionalIconsVisible(menu);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * called after onCreate completed
     *
     * @param state restored state
     */
    protected void onLoaded(@Nullable Bundle state) {

    }

    /**
     * called before onDestroy
     */
    protected void onUnload() {

    }

    protected void initToolbar() {
        Toolbar toolbar = getViewDelegate().getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

}
