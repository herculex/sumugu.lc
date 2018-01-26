package com.sumugu.liubo.lc;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by liubo on 2018/1/25.
 */

public class Alpha5_Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "x7qztlrxGvKaLiRUb7WEWaYQ-gzGzoHsz", "5EY98cKu3suWgAt39UjhFUXu");
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(true);
    }
}
