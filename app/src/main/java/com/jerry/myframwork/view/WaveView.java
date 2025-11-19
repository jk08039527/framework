package com.jerry.myframwork.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View {

    private final Paint paint = new Paint();
    private float[] points = new float[0];


    public WaveView(Context c, AttributeSet a) {
        super(c, a);
        paint.setStrokeWidth(2f);
        paint.setColor(0xff00aaFF);
    }


    public void updateWaveform(byte[] data, int len) {
        int size = len / 2;
        if (points.length != size) {
            points = new float[size];
        }

        for (int i = 0; i < size; i++) {
            int v = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] << 8));
            points[i] = v / 32768f;
        }
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float mid = getHeight() / 2f;
        float w = (float) getWidth();
        float step = w / points.length;

        float x = 0;
        for (float point : points) {
            float y = mid + point * mid;
            canvas.drawLine(x, mid, x, y, paint);
            x += step;
        }
    }
}