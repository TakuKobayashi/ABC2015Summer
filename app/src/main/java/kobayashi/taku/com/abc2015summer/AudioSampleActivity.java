package kobayashi.taku.com.abc2015summer;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;


public class AudioSampleActivity extends Activity {
    private static final String TAG = "abc2015summer";
    private AudioRecord mAudioRecord = null;
    private Handler mHandler;
    private boolean bIsRecording;
    private byte[] mRecordingBuffer;
    private static final int SAMPLING_RATE = 44100;
    private AudioTrack mAudioTrack;
    private AudioView mAudioView;
    private long mTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_sample_view);

        mHandler = new Handler();
        mAudioView = (AudioView) findViewById(R.id.audioWavView);
    }

    private void startRecording(){
        mRecordingBuffer = new byte[AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)];
        //mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, mRecordingBuffer.length);

        //mAudioRecord.startRecording();

        mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLING_RATE,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, mRecordingBuffer.length, AudioTrack.MODE_STREAM);
        mAudioTrack.play();
        bIsRecording = true;
        // 録音スレッド
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (bIsRecording) {
                    ApplicationHelper.StreamFromUrl("http://192.168.1.9:3000/api/sound/stream?id=3", new ApplicationHelper.DownloadStreamCallback() {
                        @Override
                        public void onStreaming(byte[] bytes) {
                            //Gson gson = new Gson();
                            //int[] raws = gson.fromJson("[" + new String(bytes) + "]", int[].class);
                            Log.d(TAG, "bytes:" + bytes.length);
                            Log.d(TAG, "string:" + new String(bytes));
                            //Log.d(TAG, "raws:" + raws.length);
                            /*
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            DataOutputStream dos = new DataOutputStream(baos);
                            try {
                                for(int i=0; i < raws.length; ++i) {
                                    dos.writeInt(raws[i]);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mRecordingBuffer = baos.toByteArray();
                            mAudioTrack.write(mRecordingBuffer, 0, mRecordingBuffer.length);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mAudioView.updateVisualizer(mRecordingBuffer);
                                }
                            });
                            */
                        }

                        @Override
                        public void onFinish(long totalBytes) {
                            Log.d(TAG, "total:" + totalBytes);
                            bIsRecording = false;
                        }
                    });
                    // 録音データ読み込み
                    //int bufferReadResult = mAudioRecord.read(mRecordingBuffer, 0, mRecordingBuffer.length);
                    //byte[] tmpBuf = new byte[bufferReadResult];
                    //System.arraycopy(mRecordingBuffer, 0, tmpBuf, 0, bufferReadResult);
                    /*
                    mAudioTrack.write(mRecordingBuffer, 0, mRecordingBuffer.length);
                    long current = System.currentTimeMillis();
                    Log.d(TAG, "time:" + (mTime - current)+ " Buffer:" + mRecordingBuffer.length + " sessionId:" + mAudioTrack.getAudioSessionId());
                    mTime = current;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAudioView.updateVisualizer(mRecordingBuffer);
                        }
                    });
                    */
                }
                mAudioTrack.stop();
                //mAudioRecord.stop();
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        startRecording();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bIsRecording = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioView.release();
    }
}
