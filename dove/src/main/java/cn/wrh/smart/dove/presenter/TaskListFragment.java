package cn.wrh.smart.dove.presenter;

import android.view.MenuItem;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.view.TaskListDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskListFragment extends BaseFragment<TaskListDelegate> {

    public static TaskListFragment newInstance() {
        return new TaskListFragment();
    }

    public TaskListFragment() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                onReset();
                return true;
            case R.id.action_filter:
                onFilter();
                return true;
        }
        return false;
    }

    private void onReset() {

    }

    private void onFilter() {

    }

}
