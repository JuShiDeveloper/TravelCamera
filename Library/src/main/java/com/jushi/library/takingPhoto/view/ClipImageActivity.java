package com.jushi.library.takingPhoto.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.jushi.library.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


/**
 * 图片裁剪界面
 */
public class ClipImageActivity extends AppCompatActivity {

    private ClipViewLayout clipView;
    private TextView clipCancel, clipReset, clipOk;
    private Uri imageSrc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_image);
        imageSrc = getIntent().getData();
        initialize();
    }

    private void initialize() {
        findWidget();
        setViewClickListener();
    }

    private void findWidget() {
        clipView = findViewById(R.id.clip_view);
        clipView.setImageSrc(imageSrc);
        clipCancel = findViewById(R.id.clip_cancel);
        clipReset = findViewById(R.id.clip_reset);
        clipOk = findViewById(R.id.clip_ok);
    }

    private void setViewClickListener() {
        clipCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        clipReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipView.reset();
            }
        });
        clipOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCropPhoto();
            }
        });
    }

    /**
     * 裁剪图片
     */
    private void toCropPhoto() {
        Bitmap bitmap = clipView.clip();
        if (bitmap == null) return;
        File rootFile = new File(getExternalFilesDir(null).getPath() + "/cropPhoto");
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        Uri imageUri = Uri.fromFile(new File(rootFile, "crop" + System.currentTimeMillis() + ".jpg"));
        if (imageUri == null) return;
        OutputStream outputStream = null;
        try {
            outputStream = getContentResolver().openOutputStream(imageUri);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Intent intent = new Intent();
        intent.setData(imageUri);
        setResult(RESULT_OK, intent);
        finish();
    }

}
