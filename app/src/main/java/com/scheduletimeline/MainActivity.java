package com.scheduletimeline;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);

        CSVReader csvReader = new CSVReader();
        Schedule schedule = csvReader.getSchedule(getResources());

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        ScheduleView scheduleViewIsAChildOfScrollView = new ScheduleView(this, schedule, displaySize);

        scheduleViewIsAChildOfScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        scrollView.addView(scheduleViewIsAChildOfScrollView);

        scrollView.post(new Runnable() {
            public void run() {
                scrollView.smoothScrollTo(0, scheduleViewIsAChildOfScrollView.yHeightPixelForCurrentTime - scheduleViewIsAChildOfScrollView.timeyHeight);
            }
        });

        Button bottomButton = getButton(displaySize);

        LocationTopBannerView tv = new LocationTopBannerView(this, schedule, displaySize);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tv);
        linearLayout.addView(scrollView);
        //linearLayout.addView(bottomButton);

        setContentView(linearLayout);


    }

    private Button getButton(Point displaySize) {
        Button button = new Button(this);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1000, 1000);
        button.setLayoutParams(params);

//        button.setY(displaySize.y-500);
//        button.setX(0);
  //      button.setWidth(displaySize.x);
     //   button.setHeight(100);
       // button.setBackgroundColor(Color.WHITE);


        button.setText("Refresh Time");

        button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {


            }
        });

        return button;
    }


}
