package com.example.emotionrecognizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

public class MainActivity extends AppCompatActivity {

    protected static final long TIME_DELAY = 300;

    CameraView cameraView;
    FloatingActionButton fab;
    TextView textView;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    ImageView emotionIcon;
    ImageView emotionIcon2;

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
        fab = findViewById(R.id.fab);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        emotionIcon = findViewById(R.id.emotionIcon);
        emotionIcon2 = findViewById(R.id.emotionIcon2);
        RemoteModel.configureHostedModelSource();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                unSetVisibility();
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

                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                RemoteModel.runEmotionRecognizer(bitmap);

                new CountDownTimer(TIME_DELAY, 100) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        setTextViewTexts();
                        setEmotionIcons();
                        setVisibility();
                    }
                }.start();
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }

    private void setEmotionIcon(String emotionName, ImageView emotionIconSet) {
        switch (emotionName) {
            case "Angry":
                emotionIconSet.setImageResource(R.drawable.ic_angry);
                break;
            case "Surprise":
                emotionIconSet.setImageResource(R.drawable.ic_suprise);
                break;
            case "Sad":
                emotionIconSet.setImageResource(R.drawable.ic_sad);
                break;
            case "Disgust":
                emotionIconSet.setImageResource(R.drawable.ic_disgust);
                break;
            case "Afraid":
                emotionIconSet.setImageResource(R.drawable.ic_afraid);
                break;
            case "Neutral":
                emotionIconSet.setImageResource(R.drawable.ic_neutral);
                break;
            case "Happy":
                emotionIconSet.setImageResource(R.drawable.ic_happy);
                break;
            default:
                emotionIconSet.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    private void setTextViewTexts() {

        textView.setText(RemoteModel.getFirstEmotionText());
        textView2.setText(String.format("%.2f", RemoteModel.getFirstConfidence()*100)+"%");
        textView3.setText(RemoteModel.getSecondEmotionText());
        textView4.setText(String.format("%.2f", RemoteModel.getSecondConfidence()*100)+"%");
    }

    private void setEmotionIcons() {

        setEmotionIcon(RemoteModel.getFirstEmotionText(), emotionIcon);
        setEmotionIcon(RemoteModel.getSecondEmotionText(), emotionIcon2);


    }

    private void setVisibility() {

        textView.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        textView4.setVisibility(View.VISIBLE);
        emotionIcon.setVisibility(View.VISIBLE);
        emotionIcon2.setVisibility(View.VISIBLE);
    }

    private void unSetVisibility() {

        textView.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView4.setVisibility(View.GONE);
        emotionIcon.setVisibility(View.GONE);
        emotionIcon2.setVisibility(View.GONE);
    }
}