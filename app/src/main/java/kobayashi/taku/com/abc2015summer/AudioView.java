package kobayashi.taku.com.abc2015summer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class AudioView extends View {
    Bitmap mTmpImage;
    private ArrayList<Byte> mByteList;
    private Rect mRect = new Rect();
    private Rect mVisibleRect;
    private float mPointX = 0;
    private int mLastIndex = 0;

    private Paint mForePaint = new Paint();

    public AudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mVisibleRect = new Rect();
        mByteList = new ArrayList<Byte>();
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
    }

    public void updateVisualizer(byte[] bytes) {
        mByteList.clear();
        for(byte b : bytes){
            mByteList.add(b);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerY = mVisibleRect.centerY();
        if(mTmpImage != null){
            mTmpImage.recycle();
            mTmpImage = null;
        }
        mTmpImage = Bitmap.createBitmap(mVisibleRect.bottom, mVisibleRect.height(), Bitmap.Config.ARGB_8888);
        Canvas bmp = new Canvas(mTmpImage);

        mPointX = 0;
        float byteLength = (float) mVisibleRect.width() / mByteList.size();
        for(int i = 0;i < mByteList.size();++i){
            if(i + 1 >= mByteList.size()) continue;
            float next = mPointX + byteLength;
            canvas.drawLine(mPointX, centerY + mByteList.get(i), next, centerY + mByteList.get(i + 1), mForePaint);
            mPointX = next;
        }
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        mVisibleRect = new Rect(0,0,xNew, yNew);
    }

    public void release() {
        if(mTmpImage != null){
            mTmpImage.recycle();
            mTmpImage = null;
        }
    }
}