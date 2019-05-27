package com.jushi.library.utils;

import android.content.Context;

/**
 * 全局异常捕获类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler handler;
    private Context context;

    private CrashHandler() {

    }

    public static synchronized CrashHandler getInstance() {
        if (handler == null) {
            handler = new CrashHandler();
        }
        return handler;
    }

    public void init(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        long threadId = thread.getId();
        String message = throwable.getMessage();
        String locallizedMessage = throwable.getLocalizedMessage();
        throwable.printStackTrace();

        /**------------捕获到异常后要做的事情-----(重启应用、获取手机信息、上传服务器等)--------------------*/
        //重启应用
        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()));
        //kill掉当前进程
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
