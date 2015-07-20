package kobayashi.taku.com.abc2015summer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity {
    private SpeechRecognizerController mSpeechRecognizerController;
    private static final String TAG = "abc2015summer";
    private TextView mSpeechRecognizeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView progressRate = (TextView) findViewById(R.id.progressRateLabel);
        progressRate.setText(getString(R.string.progressRate, progressBar.getProgress(), progressBar.getMax()));

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSpeechRecognizerController.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizerController.finish();
    }
}
