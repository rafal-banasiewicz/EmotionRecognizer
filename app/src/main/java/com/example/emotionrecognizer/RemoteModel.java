package com.example.emotionrecognizer;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions;
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerRemoteModel;

import java.util.List;

public class RemoteModel {

    private static final AutoMLImageLabelerRemoteModel remoteModel = new AutoMLImageLabelerRemoteModel.Builder("EmotionRecognizerModel").build();

    public static void configureHostedModelSource() {
        startModelDownloadTask(remoteModel);
    }

    private static void startModelDownloadTask(AutoMLImageLabelerRemoteModel remoteModel) {

        DownloadConditions downloadConditions = new DownloadConditions.Builder().requireWifi().build();
        RemoteModelManager.getInstance().download(remoteModel, downloadConditions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        isDownloadCompleted(remoteModel, downloadConditions);
    }

    private static void isDownloadCompleted(AutoMLImageLabelerRemoteModel remoteModel, DownloadConditions downloadConditions) {

        RemoteModelManager.getInstance().download(remoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private static void imageLabeler(AutoMLImageLabelerRemoteModel remoteModel, InputImage image) {

        RemoteModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        AutoMLImageLabelerOptions.Builder optionsBuilder;
                        if (aBoolean) {
                            optionsBuilder = new AutoMLImageLabelerOptions.Builder(remoteModel);
                        } else {
                            optionsBuilder = new AutoMLImageLabelerOptions.Builder(remoteModel); //what if not downloaded
                        }

                        AutoMLImageLabelerOptions options = optionsBuilder
                                .setConfidenceThreshold(0.25f)
                                .build();

                        ImageLabeler labeler = ImageLabeling.getClient(options);

                        labeler.process(image)
                                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                                    @Override
                                    public void onSuccess(List<ImageLabel> imageLabels) {
                                        for (ImageLabel label : imageLabels) {
                                            String text = label.getText();
                                            float confidence = label.getConfidence();
                                            int index = label.getIndex();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                });

    }

    public static void runEmotionRecognizer(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        imageLabeler(remoteModel, image);
    }
}
