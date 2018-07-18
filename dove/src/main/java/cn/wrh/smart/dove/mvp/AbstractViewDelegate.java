package cn.wrh.smart.dove.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import static android.view.View.NO_ID;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public abstract class AbstractViewDelegate implements ViewDelegate {

    private View rootView;

    public abstract @LayoutRes int getRootLayoutId();

    @Override
    public void create(LayoutInflater inflater, ViewGroup container, Bundle state) {
        rootView = inflater.inflate(getRootLayoutId(), container, false);
    }

    @Override
    public int getOptionsMenuId() {
        return NO_ID;
    }

    @Override
    public boolean isShowMenuIcon() {
        return true;
    }

    @Override
    public <T extends ViewGroup> T getToolbar() {
        return null;
    }

    @Override
    public View getRootView() {
        return this.rootView;
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onRelease() {

    }

    public <T extends View> T findViewById(@IdRes int id) {
        return rootView.findViewById(id);
    }

    public TextView getTextView(@IdRes int resId) {
        return findViewById(resId);
    }

    public Spinner getSpinner(@IdRes int id) {
        return findViewById(id);
    }

    public Context getApplicationContext() {
        return rootView.getContext().getApplicationContext();
    }

    @SuppressWarnings("unchecked")
    public <T extends Activity> T getActivity() {
        return (T)rootView.getContext();
    }

    public Resources getResources() {
        return rootView.getResources();
    }

    public String getString(@StringRes int resId) {
        return getResources().getString(resId);
    }

    public void setOnClickListener(View.OnClickListener listener, @IdRes int...ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            findViewById(id).setOnClickListener(listener);
        }
    }

}
