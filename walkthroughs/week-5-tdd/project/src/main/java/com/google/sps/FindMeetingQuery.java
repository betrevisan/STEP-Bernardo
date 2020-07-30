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

        // If there is at least one optional attendee, consider them.
        if (!attendeesOptional.isEmpty()) {
            List<TimeRange> available = considerAttendees(events, attendeesRequest, attendeesOptional, durationRequest);

            // If there was an available time range considering the optional attendees or if there are no required attendees, return available.
            if (!available.isEmpty() || attendeesRequest.isEmpty()) {
                return available;
            }
        }

        // If there are no optional attendees or no available time range considering optional attendees, just consider the required ones.
        return considerAttendees(events, attendeesRequest, durationRequest);
    }

    public List<TimeRange> considerAttendees(Collection<Event> events, Collection<String> attendeesRequest, long durationRequest) {
        List<TimeRange> conflicts = new ArrayList<TimeRange>();
        Iterator<Event> eventsIterator = events.iterator();
        // Get all the conflicting events.
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

    // Returns a list of available time ranges for the requested event, given its duration and a list of time ranges for any conflicting events.
    public List<TimeRange> findAvailableTimeSlot(List<TimeRange> conflicts, long durationRequest) {
        Collections.sort(conflicts, TimeRange.ORDER_BY_START);

        List<TimeRange> available = new ArrayList<TimeRange>();
        int start = TimeRange.START_OF_DAY;
        int end = TimeRange.END_OF_DAY;
        boolean first = true;
        for (TimeRange conflict : conflicts) {
            // If this is the first conflict seen, then set the start and end variables as the start and end of that conflict.
            if (first) {
                first = false;
                start = conflict.start();
                end = conflict.end();

                // If there is enough time for the requested event to happen before the first conflic, then add the time range from the start
                // of the day to the start of the first conflict as an available time range.
                if (start - TimeRange.START_OF_DAY > durationRequest) {
                    available.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, start, false));
                }
            
            // This case accounts for conflicts that have some overlap with the previous conflict but end after it.
            // If the end of the current conflict is greater than the end of the previous conflict and the start of the current conflict is less
            // than the end of the previous conflict, then update the start and end variables to the start and end of the new conflict.
            } else if (conflict.end() > end && conflict.start() < end) {
                start = conflict.start();
                end = conflict.end();

            // This case accounts for conflicts that do not overlap but that do not have enough space between them to accommodate the requested event.
            // If the end of the current conflict is greater than the end of the previous conflict and there is not enough space for the requested
            // event to be scheduled between the end of the previous conflict and then start of the current conflict, then update the start and end
            // variables to the start and end of the new conflict.
            } else if (conflict.end() > end && (conflict.start() - end) < durationRequest) {
                start = conflict.start();
                end = conflict.end();
            
            // This case accounts for conflicts that completely overlap with an earlier conflict. 
            // If that is the case they should be ignored because the earlier conflict accounts for that.
            } else if (conflict.end() <= end) {
                continue;
            
            // If there is enough time between the end of the last conflict and the start of the current one, add the time range between them as an
            // available time range and update the start and end variables to the start and end of the current conflict.
            } else {
                available.add(TimeRange.fromStartEnd(end, conflict.start(), false));
                start = conflict.start();
                end = conflict.end();
            }
        }
        // If there was no conflict, set the available time range as the whole day (from start to end of the day).
        if (first == true) {
            available.add(TimeRange.fromStartEnd(start, end, true));
        // If there was at least one conflict and there is enough time between the end of the last conflict and the end of the day,
        // then add that time range as an available time range.
        } else if (TimeRange.END_OF_DAY - end >= durationRequest) {
            available.add(TimeRange.fromStartEnd(end, TimeRange.END_OF_DAY, true));
        }

        return available;
    }
}
