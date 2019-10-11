package com.scheduletimeline;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.AbsListView.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        CSVReader csvReader = new CSVReader();
        Schedule schedule = csvReader.getSchedule(getResources(), getFilesDir());
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        ScheduleView scheduleViewIsAChildOfScrollView = new ScheduleView(this, schedule, size);

        scheduleViewIsAChildOfScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        scrollView.addView(scheduleViewIsAChildOfScrollView);

        scrollView.post(new Runnable() {
            public void run() {
                scrollView.smoothScrollTo(0, scheduleViewIsAChildOfScrollView.yHeightPixelForCurrentTime - scheduleViewIsAChildOfScrollView.timeyHeight);
            }
        });


        LocationTopBannerView tv = new LocationTopBannerView(this, schedule, size);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tv);
        linearLayout.addView(scrollView);

        setContentView(linearLayout);


    }


}
