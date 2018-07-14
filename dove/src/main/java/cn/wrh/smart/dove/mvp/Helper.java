package cn.wrh.smart.dove.mvp;

import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.view.Menu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public final class Helper {

    private static final String TAG = "Helper";

    @SuppressWarnings("unchecked")
    public static <T extends ViewDelegate> T createViewDelegate(Class<?> presenterType) {
        try {
            Class<T> type = (Class<T>) ((ParameterizedType) presenterType.getGenericSuperclass()).getActualTypeArguments()[0];
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Create ViewDelegate error", e);
        }
    }

    public static void setOptionalIconsVisible(Menu menu) {
        if (menu instanceof MenuBuilder) {
            try {
                Class<?> clazz = Class.forName("android.support.v7.view.menu.MenuBuilder");
                Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (Exception e) {
                Log.d(TAG, "enableMenuIcon failed", e);
            }
        } else {
            try {
                Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
                Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (Exception e1) {
                Log.d(TAG, "enableMenuIcon failed", e1);
            }
        }
    }

}
