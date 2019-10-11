package com.scheduletimeline;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CSVReader {

    public Schedule getSchedule(Resources resources, File filesDir) {
        String path = filesDir.getAbsolutePath();
        File file = new File(path + "/schedule.csv");

        InputStream scheduleInputStream = null;
        FileReader fileReader = null;

        BufferedReader csvReader = null;

        //Upload file using device file explorer to /data/data/com.example.myapplication/files/schedule.csv
        //https://stackoverflow.com/questions/13006315/how-to-access-data-data-folder-in-android-device
        //If there is a file to read, use that, otherwise use the schedule from /res/raw
        if (file.exists()) {
            try {
                fileReader = new FileReader(file);
                csvReader = new BufferedReader(fileReader);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("file not found");
            }

        } else {
            scheduleInputStream = resources.openRawResource(R.raw.schedule);
            csvReader = new BufferedReader(new InputStreamReader(scheduleInputStream));
        }


        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd kk:mm yyyy").withZone(ZoneId.systemDefault());
        ;
        Instant minStartTime = null;
        Instant maxEndTime = null;
        LinkedHashMap<String, List<Event>> locationToEvents = new LinkedHashMap<>();
        try {

            String row = csvReader.readLine();
            String[] venueOrderArray = row.split(",");
            for (String venue : venueOrderArray) {
                locationToEvents.put(venue, new ArrayList<Event>());
            }

            while ((row = csvReader.readLine()) != null) {
                //Band0,Location1,Date2,Day3,Start Time4,End Time5,Type6,Description URL7,Notes8,ImageURL9
                String[] data = row.split(",");
                String eventName = data[0];
                String eventLocation = data[1];
                String eventDate = data[2];
                String eventDay = data[3];
                String eventStartTime = data[4];
                String eventEndTime = data[5];
                List<Event> existingEvents = locationToEvents.get(eventLocation);
                if (existingEvents != null) {
                    String eventStartDateTimeString = eventDate + " " + eventStartTime + " 2019";

                    String eventEndDateTimeString = eventDate + " " + eventEndTime + " 2019";
                    Instant eventStart = dateTimeFormatter.parse(eventStartDateTimeString, Instant::from);
                    Instant eventEnd = dateTimeFormatter.parse(eventEndDateTimeString, Instant::from);
                    if (spansDays(eventStartTime, eventEndTime)) {
                        //better dates in csv preferred but here it is
                        int secondsInADay = 86400;
                        eventEnd = eventEnd.plusSeconds(secondsInADay);
                    }
                    Event event = new Event(eventName, eventLocation, eventDay, eventStart, eventEnd);

                    if (minStartTime == null || event.eventStartTime.isBefore(minStartTime)) {
                        minStartTime = event.eventStartTime;
                    }

                    if (maxEndTime == null || event.eventEndTime.isAfter(maxEndTime)) {
                        maxEndTime = event.eventEndTime;
                    }
                    Log.i("event", event.toString());
                    existingEvents.add(event);
                } else {
                    Log.e("Event location not listed in schedule.csv VenueOrder line", eventName);
                }

            }


        } catch (IOException e) {
            throw new RuntimeException("Error in reading CSV file: ", e);
        } finally {
            try {
                if (scheduleInputStream != null) {
                    scheduleInputStream.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
                csvReader.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        Schedule schedule = new Schedule(locationToEvents, minStartTime, maxEndTime);

        return schedule;
    }

    private boolean spansDays(String eventStartTime, String eventEndTime) {
        //hacky way to deal with how the csv provides dates
        Integer startHour = Integer.parseInt(eventStartTime.split(":")[0]);
        Integer endHour = Integer.parseInt(eventEndTime.split(":")[0]);
        if (startHour != 24 && endHour == 24) {
            return true;
        }
        if (startHour != 24 && endHour < startHour) {
            return true;
        }
        return false;
    }
}
