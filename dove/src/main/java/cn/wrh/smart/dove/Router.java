package cn.wrh.smart.dove;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public class Router {

    public static void route(Context context, String className) {
        route(context, className, new Bundle());
    }

    public static void route(Context context, String className, Bundle extras) {
        try {
            route(context, Class.forName(className), extras);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No type: " + className, e);
        }
    }

    public static void route(Context context, Class<?> type) {
        route(context, type, new Bundle());
    }

    public static void route(Context context, Class<?> type, Bundle extras) {
        try {
            Intent intent = new Intent(context, type);
            intent.putExtras(extras);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            throw new RuntimeException("No activity: " + type.getSimpleName(), e);
        }
    }

}
