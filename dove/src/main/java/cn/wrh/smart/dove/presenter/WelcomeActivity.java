package cn.wrh.smart.dove.presenter;

import cn.wrh.smart.dove.Router;
import cn.wrh.smart.dove.view.WelcomeDelegate;

public class WelcomeActivity extends BaseActivity<WelcomeDelegate> {

    @Override
    protected void onPermissionsGranted() {
        next();
    }

    private void next() {
        finish();
        Router.route(this, MainActivity.class);
    }

}
