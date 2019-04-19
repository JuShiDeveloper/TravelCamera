package com.jushi.library.takingPhoto.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 权限请求的类
 */
public class PermissionUtil {
    public static Boolean request(Activity activity, String[] permissions, int requestCode) {
        if (checkIsPermission(activity, permissions)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
        return false;
    }

    /**
     * 检查是否获取了权限
     *
     * @param activity
     * @param permissions
     * @return
     */
    private static Boolean checkIsPermission(Activity activity, String[] permissions) {
        boolean isGetPermission = false;
        for (String permission : permissions) {
            isGetPermission = isGetPermission || ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;//没有权限 true
        }
        return !isGetPermission;
    }

    /**
     * 是否要显示权限说明
     *
     * @param activity
     * @param permissions
     * @return
     */
    private static Boolean shouldShowExplanation(Activity activity, String[] permissions) {
        boolean shouldShow = false;
        for (String permission : permissions) {
            shouldShow = shouldShow || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }
        return shouldShow;
    }
}
