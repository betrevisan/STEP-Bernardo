// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FindMeetingQuery {
    
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<String> attendeesRequest = request.getAttendees();
        Collection<String> attendeesOptional = request.getOptionalAttendees();
        // If there are no requested attendees and no optional attendees, return available time range as the whole day.
        if (attendeesRequest.isEmpty() && attendeesOptional.isEmpty()) {
            return Arrays.asList(TimeRange.WHOLE_DAY);
        }

        long durationRequest = request.getDuration();
        // If the requested duration is longer than a day, there should be no available time range.
        if (durationRequest > TimeRange.WHOLE_DAY.duration()) {
            return Arrays.asList();
        }

        // If there are at least one optional attendee, consider them.
        if (!attendeesOptional.isEmpty()) {
            List<TimeRange> available = considerAttendees(events, attendeesRequest, attendeesOptional, durationRequest);

            // If there was no available time range considering the optional attendees, try just considering the required attendees (if not empty).
            if (available.isEmpty() && !attendeesRequest.isEmpty()) {
                available = considerAttendees(events, attendeesRequest, durationRequest);
            }

            return available;
        } else {
            // If there are no optional attendees, just consider the required ones.
            List<TimeRange> available = considerAttendees(events, attendeesRequest, durationRequest);
            return available;
        }
    }

    public List<TimeRange> considerAttendees(Collection<Event> events, Collection<String> attendeesRequest, long durationRequest) {
        List<TimeRange> conflicts = new ArrayList<TimeRange>();
        Iterator<Event> eventsIterator = events.iterator();
        while (eventsIterator.hasNext()) {
            Event event = eventsIterator.next();

            // If there is at least one requested attendee in the given event, this event should affect the returned time range.
            if (!Collections.disjoint(attendeesRequest, event.getAttendees())) {
                conflicts.add(event.getWhen());
            }
        }

        return findAvailableTimeSlot(conflicts, durationRequest);
    }

    public List<TimeRange> considerAttendees(Collection<Event> events, Collection<String> attendeesRequest, Collection<String> attendeesOptional, long durationRequest) {
        // Add optional attendees to the requested attendees. 
        ArrayList<String> attendeesRequestList = new ArrayList<String>(attendeesRequest);
        ArrayList<String> attendeesOptionalList = new ArrayList<String>(attendeesOptional);
        attendeesRequestList.addAll(attendeesOptionalList);

        return considerAttendees(events, attendeesRequestList, durationRequest);
    }

    public List<TimeRange> findAvailableTimeSlot(List<TimeRange> conflicts, long durationRequest) {
        Collections.sort(conflicts, TimeRange.ORDER_BY_START);

        List<TimeRange> available = new ArrayList<TimeRange>();
        int start = TimeRange.START_OF_DAY;
        int end = TimeRange.END_OF_DAY;
        int first = 1;
        for (TimeRange when : conflicts) {
            if (first == 1) {
                first = 0;
                start = when.start();
                end = when.end();
                if (start - TimeRange.START_OF_DAY > durationRequest) {
                    available.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, start, false));
                }
            } else if (when.end() > end && when.start() < end) {
                start = when.start();
                end = when.end();
            } else if (when.end() > end && (when.start() - end) < durationRequest) {
                start = when.start();
                end = when.end();
            } else if (when.end() <= end) {
                continue;
            } else {
                available.add(TimeRange.fromStartEnd(end, when.start(), false));
                start = when.start();
                end = when.end();
            }
        }
        if (end == TimeRange.END_OF_DAY) {
            available.add(TimeRange.fromStartEnd(start, end, true));
        } else if (TimeRange.END_OF_DAY - end >= durationRequest) {
            available.add(TimeRange.fromStartEnd(end, TimeRange.END_OF_DAY, true));
        }

        return available;
    }
}
