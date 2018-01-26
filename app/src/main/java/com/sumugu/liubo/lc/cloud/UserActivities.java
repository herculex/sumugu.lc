package com.sumugu.liubo.lc.cloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import java.util.Date;
import java.util.UUID;

/**
 * Created by liubo on 2018/1/25.
 */

public class UserActivities {

    static String USER_INFO = "USER_INFOS";

    static public void trackActivity(Context context, String action) {
        AVObject testObject = new AVObject("A5_Activities");
        testObject.put("client", getClientID(context));
        testObject.put("activedAt", new Date());
        testObject.put("action", action);
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d(this.getClass().getName(), "track successed!");
                } else {
                    Log.d(this.getClass().getName(), "failed:" + e.toString());
                }
            }
        });
    }

    static String getClientID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String clientId = preferences.getString("clientId", "");

        if (clientId.isEmpty()) {
            clientId = UUID.randomUUID().toString();
            editor.putString("clientId", clientId);
            editor.apply();
        }
        return clientId;

    }
}
