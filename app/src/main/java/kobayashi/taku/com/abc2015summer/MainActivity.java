package kobayashi.taku.com.abc2015summer;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity {
    private SpeechRecognizerController mSpeechRecognizerController;
    private static final String TAG = "abc2015summer";
    private TextView mSpeechRecognizeResult;
    private AudioRecord mAudioRecord = null;
    private Handler mHandler;
    private boolean bIsRecording;
    private byte[] mRecordingBuffer;
    private static final int SAMPLING_RATE = 44100;
    private AudioView mAudioView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView progressRate = (TextView) findViewById(R.id.progressRateLabel);
        progressRate.setText(getString(R.string.progressRate, progressBar.getProgress(), progressBar.getMax()));
        mAudioView = (AudioView) findViewById(R.id.audioWavView);


        mSpeechRecognizeResult = (TextView) findViewById(R.id.speechRecognizeResult);

        mSpeechRecognizerController = new SpeechRecognizerController(this);
        mSpeechRecognizerController.setSpeechRecognitionResultCallback(new SpeechRecognizerController.SpeechRecognitionResultCallback() {
            @Override
            public void onResult(String word, float confidence) {
                mSpeechRecognizeResult.setText(word);
            }

            @Override
            public void onError(int error) {

            }
        });
    }

    private void startRecording(){
        mRecordingBuffer = new byte[AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 2];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mRecordingBuffer.length);

        mAudioRecord.startRecording();
        bIsRecording = true;
        // 録音スレッド
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (bIsRecording) {
                    // 録音データ読み込み
                    mAudioRecord.read(mRecordingBuffer, 0, mRecordingBuffer.length);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAudioView.updateVisualizer(mRecordingBuffer);
                        }
                    });
                }
                mAudioRecord.stop();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSpeechRecognizerController.start();
        startRecording();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSpeechRecognizerController.stop();
        bIsRecording = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioView.release();
        mSpeechRecognizerController.finish();
    }
}
