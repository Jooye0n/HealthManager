package com.example.bbbb.healthmanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CameraDialog extends Dialog {
    private Button btnCapture, btnAlbum;

    private CustomDialogListener customDialogListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_camera);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        btnCapture = findViewById(R.id.button_capture);
        btnAlbum = findViewById(R.id.button_album);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogListener.onCaptureClicked();
                dismiss();
            }
        });

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogListener.onAlbumClicked();
                dismiss();
            }
        });
    }

    public CameraDialog(Context context) {
        super(context, R.style.theme_dialog);
    }

    interface CustomDialogListener {
        void onCaptureClicked();

        void onAlbumClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(CustomDialogListener customDialogListener) {
        this.customDialogListener = customDialogListener;
    }
}
