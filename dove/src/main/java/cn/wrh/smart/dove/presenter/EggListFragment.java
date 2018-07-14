package cn.wrh.smart.dove.presenter;

import android.view.MenuItem;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.view.EggListDelegate;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class EggListFragment extends BaseFragment<EggListDelegate> {

    public static EggListFragment newInstance() {
        return new EggListFragment();
    }

    public EggListFragment() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                onAdd();
                return true;
            case R.id.action_grouping:
                onGrouping();
                return true;
        }
        return false;
    }

    private void onAdd() {

    }

    private void onGrouping() {

    }

}
