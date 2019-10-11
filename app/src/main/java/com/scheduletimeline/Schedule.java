package com.scheduletimeline;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

public class Schedule {
    public final LinkedHashMap<String, List<Event>> locationToEvents;
    final Instant minStartTime;
    final Instant maxEndTime;

    public Schedule(LinkedHashMap<String, List<Event>> locationToEvents, Instant minStartTime, Instant maxEndTime) {
        this.locationToEvents = locationToEvents;
        this.minStartTime = minStartTime;
        this.maxEndTime = maxEndTime;
    }


}
