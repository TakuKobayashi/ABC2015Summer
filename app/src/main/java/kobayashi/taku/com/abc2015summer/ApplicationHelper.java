package kobayashi.taku.com.abc2015summer;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class ApplicationHelper{
    private static final String TAG = "abc2015summer";
    public static void StreamFromUrl(String string_url, DownloadStreamCallback callback) {
        try {
            URL url = new URL(string_url);

            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            byte[] chunk = new byte[12288];
            int bytesRead = 0;
            long total = 0;
            while ((bytesRead = bis.read(chunk)) != -1) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(chunk, 0, chunk.length);
                byte[] bytes = outputStream.toByteArray();
                callback.onStreaming(bytes);
                total += bytes.length;
            }
            callback.onFinish(total);
        } catch (IOException e) {
        }
    }

    public interface DownloadStreamCallback{
        public void onStreaming(byte[] bytes);
        public void onFinish(long totalBytes);
    }
}
