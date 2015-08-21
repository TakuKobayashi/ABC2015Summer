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

import java.net.URISyntaxException;


public class BluetoothActivity extends Activity {
    private static final String TAG = "abc2015summer";
    private AudioRecord mAudioRecord = null;
    private boolean bIsRecording;
    private byte[] mRecordingBuffer;
    private static final int SAMPLING_RATE = 44100;
    private AudioTrack mAudioTrack;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_view);
    }

    private void startRecording(){
        mRecordingBuffer = new byte[AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, mRecordingBuffer.length);

        mAudioRecord.startRecording();

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
                    // 録音データ読み込み
                    int bufferReadResult = mAudioRecord.read(mRecordingBuffer, 0, mRecordingBuffer.length);
                    byte[] tmpBuf = new byte[bufferReadResult];
                    System.arraycopy(mRecordingBuffer, 0, tmpBuf, 0, bufferReadResult);
                    mAudioTrack.write(mRecordingBuffer, 0, mRecordingBuffer.length);
                }
                mAudioTrack.stop();
                mAudioRecord.stop();
            }
        }).start();
    }

    private void ConnectionSocketIO(){
        try {
            mSocket = IO.socket("http://192.168.1.13:3001");
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "connect!!");
                    for(Object o : arg0){
                        Log.d(Config.TAG, "connect:" + o.toString());
                    }
                }
            });
            mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "error!!");
                    for(Object o : arg0){
                        Log.d(Config.TAG, "error:" + o.toString());
                    }
                }
            });
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "timeout!!");
                    for(Object o : arg0){
                        Log.d(Config.TAG, "timeout:" + o.toString());
                    }
                }
            });
            mSocket.on("message", new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "message!!:" + arg0.length);
                    for(Object o : arg0){
                        Log.d(Config.TAG, "className:" + o.getClass().getName());
                        Log.d(Config.TAG, "message:" + o.toString());
                    }
                }
            });
            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... arg0) {
                    Log.d(Config.TAG, "discomment!!");
                    for(Object o : arg0){
                        Log.d(Config.TAG, "discomment:" + o.toString());
                    }
                }
            });
            mSocket.connect();
        } catch (URISyntaxException e) {
            Log.d(Config.TAG, "error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //ConnectionSocketIO();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
        if(mSocket != null && mSocket.connected()){
            mSocket.disconnect();
        }
        */
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
        //startRecording();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bIsRecording = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
