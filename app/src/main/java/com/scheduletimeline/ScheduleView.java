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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class ScheduleView extends View {

    private Paint paint;
    Paint paintText;
    Point size;

    //TODO xOffset is fixing the info bar up top with time, need programmable
    int yHeightOffset = 50;
    int pmOffset = 60;
    public int timeyHeight = 400;
    int xBlockWidth;
    final long numberOfHours;
    int xSizeForTimeWrittenOnLeft = 120;
//    int startHourPad = 1;
    int secondsInAnHour = 60 * 60;
    public int yHeightPixelForCurrentTime;

    Schedule schedule;

    public ScheduleView(Context context, Schedule schedule, Point size) {
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
        Instant currentTime = Instant.now();
        //Instant currentTime = Instant.ofEpochSecond(schedule.minStartTime.getEpochSecond()).plusSeconds(secondsInAnHour +(60*23));
        double hoursPastCurrentTimeStartTime = getHoursPastStartTime(currentTime);
        yHeightPixelForCurrentTime = getPixelForHour(hoursPastCurrentTimeStartTime);


    }

    // onMeasure must be included otherwise one or both scroll views will be compressed to zero pixels
    // and the scrollview will then be invisible
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = size.x;

        Duration duration = Duration.between(schedule.minStartTime, schedule.maxEndTime);

        int height = yHeightOffset + (int) (timeyHeight * numberOfHours);

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        drawTime(canvas);
        drawCurrentTime(canvas);

        drawSchedule(canvas);

    }

    private void drawCurrentTime(Canvas canvas) {

        Paint paintLine = new Paint();
        paintLine.setColor(Color.GREEN);
        canvas.drawRect(0, yHeightPixelForCurrentTime, size.x, yHeightPixelForCurrentTime + 10, paintLine);

    }

    private void drawSchedule(Canvas canvas) {
        //locationIndex to calculate xWidth begin/end pixel location
        int locationIndex = 0;
        for (Map.Entry<String, List<Event>> locationAndEvents : schedule.locationToEvents.entrySet()) {
            for (Event event : locationAndEvents.getValue()) {
                Log.i("drawing event", event.toString());
                Instant eventStartTime = event.eventStartTime;
                double hoursPastStartEventStartTime = getHoursPastStartTime(eventStartTime);
                double hoursPastStartEventEndTime = getHoursPastStartTime(event.eventEndTime);


                int left = (locationIndex * xBlockWidth) + xSizeForTimeWrittenOnLeft;
                int top = getPixelForHour(hoursPastStartEventStartTime);
                int bottom = getPixelForHour(hoursPastStartEventEndTime);

                Rect rectangle = new Rect(left, top, left + xBlockWidth, bottom);
                Log.i("rectange", rectangle.toString());
                canvas.drawRect(rectangle, paint);

                TextPaint textPaint = new TextPaint();
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(50);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm").withZone(ZoneId.systemDefault());
                String timeRange = " " + dateTimeFormatter.format(eventStartTime) + "-" + dateTimeFormatter.format(event.eventEndTime);
                StaticLayout eventText = StaticLayout.Builder
                        .obtain(event.eventName + timeRange, 0, event.eventName.length() + timeRange.length(), textPaint, xBlockWidth)
                        .build();

                //TODO Don't know if translate, then translate back -left, -top is desired approach
                canvas.translate(left, top);
                eventText.draw(canvas);
                canvas.translate(-left, -top);
            }
            locationIndex++;
        }

    }

    private void drawTime(Canvas canvas) {
        Duration duration = Duration.between(schedule.minStartTime, schedule.maxEndTime);

        long numberOfHours = duration.getSeconds() / secondsInAnHour;

        int startHour = schedule.minStartTime.atZone(ZoneId.systemDefault()).getHour();

        Paint paintLine = new Paint();
        paintLine.setColor(Color.BLUE);
        int currentHour = startHour;
        int dayNumber = 1;
        int dateNumber = 7;
        for (int x = 0; x <= numberOfHours + 2; x++) {
            if (currentHour > 24) {
                currentHour = 1;
                dayNumber++;
                dateNumber++;
            }

            canvas.drawRect(0, (x * timeyHeight) + yHeightOffset, size.x, (x * timeyHeight) + yHeightOffset + 5, paintLine);

            if (currentHour == 24)
            {
                canvas.drawText(Integer.toString(currentHour - 12) + "am", 0, (x * timeyHeight) + yHeightOffset, paintText);
            }
            else {
                if (currentHour <= 12) {
                    canvas.drawText(Integer.toString(currentHour) + "am", 0, (x * timeyHeight) + yHeightOffset, paintText);
                }
                if (currentHour > 12) {
                    canvas.drawText(Integer.toString(currentHour - 12) + "pm", 0, (x * timeyHeight) + yHeightOffset, paintText);
                }
            }

            //TODO remove these 70k hacks
            canvas.drawText(Integer.toString(currentHour) + ":00", 0, (x * timeyHeight) + yHeightOffset + pmOffset, paintText);
            canvas.drawText("Day" + dayNumber, 0, (x * timeyHeight)+ yHeightOffset + pmOffset + pmOffset, paintText);
            canvas.drawText("Jan" + dateNumber, 0, (x * timeyHeight)+ yHeightOffset + pmOffset + pmOffset+ pmOffset, paintText);

            currentHour++;
        }
    }

    /**
     * @param hoursPastStartTime number of hours past the top/beginning of the schedule
     * @return the yHeight axis pixel for the given number of hours past the top/beginning of the schedule
     */
    private int getPixelForHour(double hoursPastStartTime) {
        int pixel = (int) (timeyHeight * hoursPastStartTime)- timeyHeight + yHeightOffset;
        Log.i("pixel getPixelForHour: ", Integer.toString(pixel));
        return pixel;
    }

    /**
     * @param time the time to calculate hours past beginning of the schedule note the schedule pads 1 hour
     * @return hoursPastStartTime number of hours past the top/beginning of the schedule
     */
    private double getHoursPastStartTime(Instant time) {
        double secondsPastStart = time.getEpochSecond() - schedule.minStartTime.getEpochSecond() + (secondsInAnHour);
        double hoursPastStart = secondsPastStart / secondsInAnHour;
        Log.i("time getyHeightPixelOffset: ", time.toString());
        Log.i("hoursPastStart getyHeightPixelOffset: ", Double.toString(hoursPastStart));

        return hoursPastStart;
    }


}