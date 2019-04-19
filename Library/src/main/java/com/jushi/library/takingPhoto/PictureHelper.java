package com.jushi.library.takingPhoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;


import com.jushi.library.BuildConfig;
import com.jushi.library.takingPhoto.view.ClipImageActivity;

import java.io.File;


public class PictureHelper {

    /**
     * 打开相机拍照
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static File gotoCamera(Activity activity, int requestCode) {
        File rootFile = new File(activity.getExternalFilesDir(null).getPath() + "/QjhUserImages");
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        File imageFile = new File(rootFile, System.currentTimeMillis() + "uImage.jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri contentUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            contentUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", imageFile);
        } else {
            contentUri = Uri.fromFile(imageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        activity.startActivityForResult(intent, requestCode);
        return imageFile;
    }

    /**
     * 打开相册选择图片
     */
    public static void gotoPhotoAlbum(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(Intent.createChooser(intent, "请选择图片"), requestCode);
    }

    /**
     * 打开图片裁剪界面
     *
     * @param activity
     * @param uri
     * @param requestCode
     */
    public static void gotoClipActivity(Activity activity, Uri uri, int requestCode) {
        if (uri == null) return;
        Intent intent = new Intent(activity, ClipImageActivity.class);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }
}
