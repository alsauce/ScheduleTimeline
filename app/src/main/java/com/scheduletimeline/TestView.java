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

public class TestView extends View {

    private Paint paint;
    Paint paintText;
    Point size;

    //TODO xOffset is fixing the info bar up top with time, need programmable
    int yHeightOffset = 50;
    int pmOffset = 60;
    public int yHeight = 60;
    int xBlockWidth;
    final long numberOfHours;
    int xSizeForTimeWrittenOnLeft = 120;
    int startHourPad = 1;
    int secondsInAnHour = 60 * 60;
    public int yHeightPixelForCurrentTime;
    Schedule schedule;

    public TestView(Context context, Schedule schedule, Point size) {
        super(context);
        this.size = size;

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.WHITE);

        xBlockWidth = (size.x - xSizeForTimeWrittenOnLeft) / schedule.locationToEvents.size();

        this.schedule = schedule;
        Duration duration = Duration.between(schedule.minStartTime, schedule.maxEndTime);

        numberOfHours = (duration.getSeconds() / secondsInAnHour) + 1;

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(40);

        //TODO use now when live, using a test time for now
        //Instant currentTime = Instant.now();
        Instant currentTime = Instant.ofEpochSecond(schedule.minStartTime.getEpochSecond()).plusSeconds(secondsInAnHour +(60*30));
//        double hoursPastCurrentTimeStartTime = getHoursPastStartTime(currentTime);
//        yHeightPixelForCurrentTime = getPixelForHour(hoursPastCurrentTimeStartTime);

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
        for (Map.Entry<String, List<Event>> locationAndEvents : schedule.locationToEvents.entrySet()) {
//            for (Event event : locationAndEvents.getValue()) {
//                Log.i("drawing event", event.toString());
//                Instant eventStartTime = event.eventStartTime;
//                double hoursPastStartEventStartTime = getHoursPastStartTime(eventStartTime);
//                double hoursPastStartEventEndTime = getHoursPastStartTime(event.eventEndTime);


                int left = (locationIndex * xBlockWidth) + xSizeForTimeWrittenOnLeft;
//                int top = getPixelForHour(hoursPastStartEventStartTime);
//                int bottom = getPixelForHour(hoursPastStartEventEndTime);

                Rect rectangle = new Rect(left, 0, left + xBlockWidth, yHeight);
                Log.i("rectange", rectangle.toString());
                canvas.drawRect(rectangle, paint);

                TextPaint textPaint = new TextPaint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(50);
                StaticLayout eventText = StaticLayout.Builder
                        .obtain(locationAndEvents.getKey(), 0, locationAndEvents.getKey().length(), textPaint, xBlockWidth)
                        .build();

                //TODO Don't know if translate, then translate back -left, -top is desired approach
                canvas.translate(left, 0);
                eventText.draw(canvas);
                canvas.translate(-left, 0);

            locationIndex++;
            }
        }

}
