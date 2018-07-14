package cn.wrh.smart.dove.presenter;

import cn.wrh.smart.dove.dal.AppDatabase;
import cn.wrh.smart.dove.mvp.CompatFragmentPresenter;
import cn.wrh.smart.dove.mvp.ViewDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/13
 */
public class BaseFragment<T extends ViewDelegate> extends CompatFragmentPresenter<T> {

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(getContext());
    }

}
