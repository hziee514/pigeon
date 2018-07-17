package cn.wrh.smart.dove.view;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.ViewGroup;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.mvp.AbstractViewDelegate;
import cn.wrh.smart.dove.presenter.CageListFragment;
import cn.wrh.smart.dove.presenter.EggListFragment;
import cn.wrh.smart.dove.presenter.MainActivity;
import cn.wrh.smart.dove.presenter.TaskListFragment;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public class MainDelegate extends AbstractViewDelegate
        implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private DrawerLayout drawer;
    private ViewPager pager;
    private BottomNavigationView navigationView;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public <T extends ViewGroup> T getToolbar() {
        return findViewById(R.id.toolbar);
    }

    @Override
    public void onInit() {
        drawer = findViewById(R.id.drawer_layout);
        pager = findViewById(R.id.pager);
        navigationView = findViewById(R.id.tabs);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initPager(((MainActivity)getActivity()).getSupportFragmentManager());
    }

    public void setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener listener) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(listener);
    }

    private void initPager(FragmentManager fm) {
        navigationView.setOnNavigationItemSelectedListener(this);
        pager.addOnPageChangeListener(this);
        pager.setAdapter(new TabPagerAdapter(fm));

        changeTitle(navigationView.getMenu().getItem(0).getTitle());
    }

    public void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    public boolean checkOrCloseDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
            return false;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_cage:
                switchTo(0);
                break;
            case R.id.tab_feed:
                switchTo(1);
                break;
            case R.id.tab_task:
                switchTo(2);
                break;
        }
        changeTitle(item.getTitle());
        return false;
    }

    private void switchTo(int position) {
        if (pager.getCurrentItem() != position) {
            pager.setCurrentItem(position);
        }
    }

    private void changeTitle(CharSequence title) {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem item = navigationView.getMenu().getItem(position);
        if (!item.isChecked()) {
            item.setChecked(true);
            changeTitle(item.getTitle());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static class TabPagerAdapter extends FragmentPagerAdapter {

        TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CageListFragment.newInstance();
                case 1:
                    return EggListFragment.newInstance();
                case 2:
                    return TaskListFragment.newInstance();
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
