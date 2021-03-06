package com.scheduletimeline;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CSVReader {

    public Schedule getSchedule(Resources resources) {
        InputStream scheduleInputStream = resources.openRawResource(R.raw.schedule);
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(scheduleInputStream));
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
                    String eventStartDateTimeString = eventDate + " " + eventStartTime + " 2020";


                    String eventEndDateTimeString = eventDate + " " + eventEndTime + " 2020";
                    Instant eventStart = dateTimeFormatter.parse(eventStartDateTimeString, Instant::from);
                    Instant eventEnd = dateTimeFormatter.parse(eventEndDateTimeString, Instant::from);
                    if (spansDays(eventStartTime, eventEndTime))
                    {
                        //better dates in csv preferred but here it is
                        int secondsInADay = 86400;
                        eventEnd = eventEnd.plusSeconds(secondsInADay);
                    }
                    Event event = new Event(eventName, eventLocation,eventDay, eventStart, eventEnd);

                    if (minStartTime == null || event.eventStartTime.isBefore(minStartTime)) {
                        minStartTime = event.eventStartTime.truncatedTo(ChronoUnit.HOURS);
                    }

                    if (maxEndTime == null || event.eventEndTime.isAfter(maxEndTime)) {
                        maxEndTime = event.eventEndTime.plus(ChronoUnit.HOURS.getDuration()).truncatedTo(ChronoUnit.HOURS);
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
                scheduleInputStream.close();
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
        if (startHour!= 24 && endHour == 24)
        {
            return true;
        }
        if (startHour != 24 && endHour < startHour)
        {
            return true;
        }
        return false;
    }
}
