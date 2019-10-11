package com.scheduletimeline;

import java.time.Instant;

public class Event {
    public final String eventName;
    public final String eventLocation;
    public final String eventDay;
    public final Instant eventStartTime;
    public final Instant eventEndTime;
//    public final String type;



    String descriptionURL;

    public Event(String eventName,
            String eventLocation,
            String eventDay,
            Instant eventStartTime,
            Instant eventEndTime){
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventDay = eventDay;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", eventLocation='" + eventLocation + '\'' +
                ", eventDay='" + eventDay + '\'' +
                ", eventStartTime=" + eventStartTime +
                ", eventEndTime=" + eventEndTime +
                ", descriptionURL='" + descriptionURL + '\'' +
                '}';
    }
}
