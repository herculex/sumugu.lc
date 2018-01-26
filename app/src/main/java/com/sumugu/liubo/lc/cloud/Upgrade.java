package com.sumugu.liubo.lc.cloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.Date;
import java.util.List;

/**
 * Created by liubo on 2018/1/25.
 */

public class Upgrade {

    static String UPGRADE_INFO = "UPGRADE_INFO";

    static long getLastCheckAt(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(UPGRADE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        long defaultAt = new Date().getTime() - (1000 * 3600 * 24 * 10);
        long checkAt = preferences.getLong("checkAt", 0);

        if (checkAt == 0) {
            editor.putLong("checkAt", defaultAt);
            editor.apply();
            return defaultAt;
        }
        return checkAt;
    }

    static public void checkUpgradeInfoFromCloud(final Context context, final OnCheckedListener listener) {

        final String downUrl = "";
        long lastCheckAt = getLastCheckAt(context);

        int verCode = 0;
        //get app ver code
        try {
            PackageManager pm = context.getPackageManager();
            verCode = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        int pastDays = (int) (new Date().getTime() - lastCheckAt) / 1000 / 3600 / 24;

        if (pastDays > 0) {
            try {

                final int code = verCode;
                AVQuery<AVObject> query = new AVQuery<>("A5_Upgrades");
                query.whereGreaterThan("verCode", code);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {

                        if (list == null || list.isEmpty()) {
                            Log.d(this.getClass().getName(), String.valueOf(code) + "没有新版本");
                        } else {
                            AVObject object = list.get(0);
                            Log.d(this.getClass().getName(), "发现新版本，"
                                    + object.get("verCode").toString()
                                    + "," + object.get("verName").toString()
                                    + "," + object.get("downUrl").toString());

                            updateLastCheckAt(context);

                            if (null != listener)
                                listener.finished((int) object.get("verCode"), object.get("downUrl").toString());
                        }
                    }
                });
            } catch (Exception ex) {
                Log.d(Upgrade.class.getName(), ex.toString());
            }
        } else {
            Log.d(Upgrade.class.getName(), "上次检查更新在 "
                    + DateFormat.format("yyyy-MM-dd HH:mm:ss", lastCheckAt)
                    + "，当前版本：" + verCode
                    + "，没到时间检查。");
        }
    }

    static void updateLastCheckAt(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(UPGRADE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("checkAt", new Date().getTime());
        editor.apply();
    }

    public interface OnCheckedListener {
        void finished(int vercode, String url);
    }
}
