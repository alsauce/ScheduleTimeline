package com.scheduletimeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class LocationTopBannerView extends View {

    private Paint paint;
    Point size;
    TextPaint textPaint;

    //TODO xOffset is fixing the info bar up top with time, need programmable
    public int yHeight = 60;
    int xBlockWidth;
    int xSizeForTimeWrittenOnLeft = 120;
    Schedule schedule;

    public LocationTopBannerView(Context context, Schedule schedule, Point size) {
        super(context);
        this.size = size;

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.WHITE);

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);

        xBlockWidth = (size.x - xSizeForTimeWrittenOnLeft) / schedule.locationToEvents.size();

        this.schedule = schedule;
        Duration duration = Duration.between(schedule.minStartTime, schedule.maxEndTime);

    }

    // onMeasure must be included otherwise one or both scroll views will be compressed to zero pixels
    // and the scrollview will then be invisible
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, yHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //locationIndex to calculate xWidth begin/end pixel location
        int locationIndex = 0;
        for (String location : schedule.locationToEvents.keySet()) {

            int left = (locationIndex * xBlockWidth) + xSizeForTimeWrittenOnLeft;

            Rect rectangle = new Rect(left, 0, left + xBlockWidth, yHeight);
            canvas.drawRect(rectangle, paint);

            StaticLayout eventText = StaticLayout.Builder
                    .obtain(location, 0, location.length(), textPaint, xBlockWidth)
                    .build();

            //TODO Don't know if translate, then translate back -left, -top is desired approach
            canvas.translate(left, 0);
            eventText.draw(canvas);
            canvas.translate(-left, 0);
            locationIndex++;
        }
    }

}
