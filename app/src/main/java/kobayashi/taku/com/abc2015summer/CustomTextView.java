package kobayashi.taku.com.abc2015summer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;


public class CustomTextView extends TextView {
    private int frame = 0;

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(frame > 10) {
            Random r = new Random();
            int red = r.nextInt(255);
            int blue = r.nextInt(255);
            int green = r.nextInt(255);
            setTextColor(Color.rgb(red, green, blue));
            frame = 0;
        }
        ++frame;
        this.invalidate();
    }
}
