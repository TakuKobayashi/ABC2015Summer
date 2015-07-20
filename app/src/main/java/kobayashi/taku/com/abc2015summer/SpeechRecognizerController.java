package kobayashi.taku.com.abc2015summer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;


public class SpeechRecognizerController {

    private Context mContext;
    private SpeechRecognizer mSpeechRecognizer;
    private SpeechRecognitionResultCallback mSpeechRecognitionResultCallback;
    private static final String TAG = "abc2015summer";

    public SpeechRecognizerController(Context context) {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onRmsChanged(float rmsdB) {
                Log.d(TAG, "onRmsChanged:" + rmsdB);
            }

            @Override
            public void onResults(Bundle results) {
                Log.d(TAG, "onResults:" + results);
                resultAction(results);
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "onReadyForSpeech:" + params);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d(TAG, "onPartialResults:" + partialResults);
                resultAction(partialResults);
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d(TAG, "onEvent:" + eventType);
                Log.d(TAG, "onEvent:" + params);
            }

            @Override
            public void onError(int error) {
                Log.d(TAG, "onError:" + error);
                if (mSpeechRecognitionResultCallback != null) mSpeechRecognitionResultCallback.onError(error);
            }

            @Override
            public void onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                for (int i = 0; i < buffer.length; i++) {
                    Log.d(TAG, "onEndonBufferReceived:" + buffer[i]);
                }
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech");
            }
        });
        mContext = context;
    }

    public void setSpeechRecognitionResultCallback(SpeechRecognitionResultCallback callback) {
        mSpeechRecognitionResultCallback = callback;
    }

    private void resultAction(Bundle results) {
        float[] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        ArrayList<String> recData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(TAG, "" + recData);
        float prev = -1;
        int index = 0;
        for (int i = 0; i < confidence.length; ++i) {
            if (confidence[i] > prev) {
                index = i;
            }
        }
        if (mSpeechRecognitionResultCallback != null) mSpeechRecognitionResultCallback.onResult(recData.get(index), confidence[index]);
    }

    public interface SpeechRecognitionResultCallback {
        public void onResult(String word, float confidence);

        public void onError(int error);
    }

    public void start() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
        mSpeechRecognizer.startListening(intent);
    }

    public void stop() {
        mSpeechRecognizer.stopListening();
    }

    public void finish() {
        stop();
        mSpeechRecognizer.destroy();
    }
}
