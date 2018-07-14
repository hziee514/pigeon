package cn.wrh.smart.dove.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.NO_ID;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public abstract class CompatFragmentPresenter<T extends ViewDelegate> extends Fragment {

    private T delegate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        delegate = Helper.createViewDelegate(getClass());
        delegate.create(inflater, container, savedInstanceState);
        setHasOptionsMenu(delegate.getOptionsMenuId() != NO_ID);
        return delegate.getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        delegate.onInit();

        onLoaded(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onUnload();
        delegate.onRelease();
        delegate = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (delegate.getOptionsMenuId() != NO_ID) {
            inflater.inflate(delegate.getOptionsMenuId(), menu);
            if (delegate.isShowMenuIcon()) {
                Helper.setOptionalIconsVisible(menu);
            }
        }
    }

    /**
     * called after onViewCreated
     *
     * @param state restored state
     */
    protected void onLoaded(@Nullable Bundle state) {

    }

    /**
     * called after onDestroy
     */
    protected void onUnload() {

    }

    public T getViewDelegate() {
        return delegate;
    }

}
