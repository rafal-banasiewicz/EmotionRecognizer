package com.example.emotionrecognizer;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    protected static final long TIME_DELAY = 1000;

    Handler handler = new Handler();
    CameraView cameraView;
    Button button;
    TextView textView;
    TextView textView2;

    AlertDialog alertDialog;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = findViewById(R.id.camera);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        RemoteModel.configureHostedModelSource();
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Processing")
                .setCancelable(false)
                .build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                alertDialog.show();

                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                RemoteModel.runEmotionRecognizer(bitmap);
                alertDialog.dismiss();
                handler.post(updateTextRunnable);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    Runnable updateTextRunnable = new Runnable() {
        @Override
        public void run() {
            textView.setText(RemoteModel.getText());
            float floatConfidence = RemoteModel.getConfidence();
            String stringConfidence = Float.toString(floatConfidence);
            textView2.setText(stringConfidence);
            handler.postDelayed(this, TIME_DELAY);
        }
    };
}