package cn.wrh.smart.dove.view;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskListDelegate extends AbstractViewDelegate {

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_main_list;
    }

    @Override
    public int getOptionsMenuId() {
        return R.menu.main_tab_task;
    }

}
