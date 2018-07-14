package cn.wrh.smart.dove.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public interface ViewDelegate {

    void create(LayoutInflater inflater, ViewGroup container, Bundle state);

    int getOptionsMenuId();

    boolean isShowMenuIcon();

    <T extends ViewGroup> T getToolbar();

    View getRootView();

    /**
     * called after view created and toolbar initialized
     *  Activity.onCreate
     *  Fragment.onViewCreated
     */
    void onInit();

    /**
     * called before destroy
     *  Activity.onDestroy
     *  Fragment.onDestroy
     */
    void onRelease();

}
