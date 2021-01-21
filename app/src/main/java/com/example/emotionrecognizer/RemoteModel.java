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
    private static String firstEmotionName;
    private static float firstEmotionConfidence;
    private static String secondEmotionName;
    private static float secondEmotionConfidence;

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
                    public void onSuccess(Boolean isDownloaded) {
                        AutoMLImageLabelerOptions.Builder optionsBuilder;
                        if (isDownloaded) {
                            optionsBuilder = new AutoMLImageLabelerOptions.Builder(remoteModel);
                        } else {
                            optionsBuilder = null;
                            startModelDownloadTask(remoteModel);
                            imageLabeler(remoteModel, image);
                        }

                        AutoMLImageLabelerOptions options = optionsBuilder
                                .setConfidenceThreshold(0.001f)
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
                                        firstEmotionName = imageLabels.get(0).getText();
                                        secondEmotionName = imageLabels.get(1).getText();
                                        firstEmotionConfidence = imageLabels.get(0).getConfidence();
                                        secondEmotionConfidence = imageLabels.get(1).getConfidence();
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

    public static String getFirstEmotionText() {
        return firstEmotionName;
    }
    public static String getSecondEmotionText() {
        return secondEmotionName;
    }
    public static float getFirstConfidence() {
        return firstEmotionConfidence;
    }
    public static float getSecondConfidence() {
        return secondEmotionConfidence;
    }
}
