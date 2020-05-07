/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.common;

import org.elasticsearch.common.LocalTimeOffset.Gap;
import org.elasticsearch.common.LocalTimeOffset.Overlap;
import org.elasticsearch.common.time.DateFormatter;
import org.elasticsearch.test.ESTestCase;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneOffsetTransition;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

public class LocalTimeOffsetTests extends ESTestCase {
    public void testRangeTooLarge() {
        ZoneId zone = ZoneId.of("America/New_York");
        assertThat(LocalTimeOffset.lookup(zone, Long.MIN_VALUE, Long.MAX_VALUE), nullValue());
    }

    public void testNotFixed() {
        ZoneId zone = ZoneId.of("America/New_York");
        assertThat(LocalTimeOffset.lookupFixedOffset(zone), nullValue());
    }

    public void testUtc() {
        assertFixOffset(ZoneId.of("UTC"), 0);
    }

    public void testFixedOffset() {
        ZoneOffset zone = ZoneOffset.ofTotalSeconds(between((int) -TimeUnit.HOURS.toSeconds(18), (int) TimeUnit.HOURS.toSeconds(18)));
        assertFixOffset(zone, zone.getTotalSeconds() * 1000);
    }

    private void assertFixOffset(ZoneId zone, long offsetMillis) {
        LocalTimeOffset fixed = LocalTimeOffset.lookupFixedOffset(zone);
        assertThat(fixed, notNullValue());

        LocalTimeOffset.Lookup lookup = LocalTimeOffset.lookup(zone, Long.MIN_VALUE, Long.MAX_VALUE);
        assertThat(lookup.size(), equalTo(1));
        long min = randomLong();
        long max = randomValueOtherThan(min, ESTestCase::randomLong);
        if (min > max) {
            long s = min;
            min = max;
            max = s;
        }
        LocalTimeOffset fixedInRange = lookup.fixedInRange(min, max);
        assertThat(fixedInRange, notNullValue());

        assertRoundingAtOffset(randomBoolean() ? fixed : fixedInRange, randomLong(), offsetMillis);
    }
    
    private void assertRoundingAtOffset(LocalTimeOffset offset, long time, long offsetMillis) {
        assertThat(offset.utcToLocalTime(time), equalTo(time + offsetMillis));
        assertThat(offset.localToUtcInThisOffset(time + offsetMillis), equalTo(time));
        assertThat(offset.localToUtc(time + offsetMillis, unusedStrategy()), equalTo(time));
    }

    public void testJustTransitions() {
        ZoneId zone = ZoneId.of("America/New_York");
        long min = time("1980-01-01", zone);
        long max = time("1981-01-01", zone) - 1;
        assertThat(Instant.ofEpochMilli(max), lessThan(lastTransitionIn(zone).getInstant()));
        assertTransitions(zone, min, max, time("1980-06-01", zone), min + hours(1), 3, hours(-5), hours(-4));
    }

    public void testTransitionsWithTransitionsAndRules() {
        ZoneId zone = ZoneId.of("America/New_York");
        long min = time("1980-01-01", zone);
        long max = time("2021-01-01", zone) - 1;
        assertThat(Instant.ofEpochMilli(min), lessThan(lastTransitionIn(zone).getInstant()));
        assertThat(Instant.ofEpochMilli(max), greaterThan(lastTransitionIn(zone).getInstant()));
        assertTransitions(zone, min, max, time("2000-06-01", zone), min + hours(1), 83, hours(-5), hours(-4));
        assertThat(LocalTimeOffset.lookup(zone, min, max).fixedInRange(utcTime("2000-06-01"), utcTime("2000-06-02")), notNullValue());
    }

    public void testAfterRules() {
        ZoneId zone = ZoneId.of("America/New_York");
        long min = time("2020-01-01", zone);
        long max = time("2021-01-01", zone) - 1;
        assertThat(Instant.ofEpochMilli(min), greaterThan(lastTransitionIn(zone).getInstant()));
        assertTransitions(zone, min, max, time("2020-06-01", zone), min + hours(1), 3, hours(-5), hours(-4));
    }

    private void assertTransitions(ZoneId zone, long min, long max, long between, long sameOffsetAsMin,
            int size, long minMaxOffset, long betweenOffset) {
        LocalTimeOffset.Lookup lookup = LocalTimeOffset.lookup(zone, min, max);
        assertThat(lookup.size(), equalTo(size));
        assertRoundingAtOffset(lookup.lookup(min), min, minMaxOffset);
        assertRoundingAtOffset(lookup.lookup(between), between, betweenOffset);
        assertRoundingAtOffset(lookup.lookup(max), max, minMaxOffset);
        assertThat(lookup.fixedInRange(min, max), nullValue());
        assertThat(lookup.fixedInRange(min, sameOffsetAsMin), sameInstance(lookup.lookup(min)));
    }

    // Some sanity checks for when you pas a single time. We don't expect to do this much but it shouldn't be totally borked.
    public void testSingleTimeBeforeRules() {
        ZoneId zone = ZoneId.of("America/New_York");
        long time = time("1980-01-01", zone);
        assertThat(Instant.ofEpochMilli(time), lessThan(lastTransitionIn(zone).getInstant()));
        assertRoundingAtOffset(LocalTimeOffset.lookup(zone, time, time).lookup(time), time, hours(-5));
    }

    public void testSingleTimeAfterRules() {
        ZoneId zone = ZoneId.of("America/New_York");
        long time = time("2020-01-01", zone);
        assertThat(Instant.ofEpochMilli(time), greaterThan(lastTransitionIn(zone).getInstant()));
        assertRoundingAtOffset(LocalTimeOffset.lookup(zone, time, time).lookup(time), time, hours(-5));
    }

    public void testJustOneRuleApplies() {
        ZoneId zone = ZoneId.of("Atlantic/Azores");
        long time = time("2000-10-30T00:00:00", zone);
        assertRoundingAtOffset(LocalTimeOffset.lookup(zone, time, time).lookup(time), time, hours(-1));
    }

    public void testLastTransitionWithoutRules() {
        /*
         * Asia/Kathmandu turned their clocks 15 minutes forward at
         * 1986-01-01T00:00:00 local time and hasn't changed time since.
         * This has broken the transition collection code in the past.
         */
        ZoneId zone = ZoneId.of("Asia/Kathmandu");
        long time = time("1986-01-01T00:00:00", zone);
        LocalTimeOffset.Lookup lookup = LocalTimeOffset.lookup(zone, time - 1, time);
        assertThat(lookup.size(), equalTo(2));
        assertRoundingAtOffset(lookup.lookup(time - 1), time - 1, TimeUnit.MINUTES.toMillis(330));
        assertRoundingAtOffset(lookup.lookup(time), time, TimeUnit.MINUTES.toMillis(345));
    }

    public void testOverlap() {
        /*
         * Europe/Rome turn their clocks back an hour 1978 which is totally
         * normal, but they rolled back past midnight which is pretty rare and neat.
         */
        ZoneId tz = ZoneId.of("Europe/Rome");
        long overlapMillis = TimeUnit.HOURS.toMillis(1);
        long firstMidnight = utcTime("1978-09-30T22:00:00");
        long secondMidnight = utcTime("1978-09-30T23:00:00");
        long overlapEnds = utcTime("1978-10-01T0:00:00");
        LocalTimeOffset.Lookup lookup = LocalTimeOffset.lookup(tz, firstMidnight, overlapEnds);
        LocalTimeOffset secondMidnightOffset = lookup.lookup(secondMidnight);
        long localSecondMidnight = secondMidnightOffset.utcToLocalTime(secondMidnight);
        LocalTimeOffset firstMidnightOffset = lookup.lookup(firstMidnight);
        long localFirstMidnight = firstMidnightOffset.utcToLocalTime(firstMidnight);
        assertThat(localSecondMidnight - localFirstMidnight, equalTo(0L));
        assertThat(lookup.lookup(overlapEnds), sameInstance(secondMidnightOffset));
        long localOverlapEnds = secondMidnightOffset.utcToLocalTime(overlapEnds);
        assertThat(localOverlapEnds - localSecondMidnight, equalTo(overlapMillis));

        long localOverlappingTime = randomLongBetween(localFirstMidnight, localOverlapEnds);

        assertThat(firstMidnightOffset.localToUtcInThisOffset(localFirstMidnight - 1), equalTo(firstMidnight - 1));
        assertThat(secondMidnightOffset.localToUtcInThisOffset(localFirstMidnight - 1), equalTo(secondMidnight - 1));
        assertThat(firstMidnightOffset.localToUtcInThisOffset(localFirstMidnight), equalTo(firstMidnight));
        assertThat(secondMidnightOffset.localToUtcInThisOffset(localFirstMidnight), equalTo(secondMidnight));
        assertThat(secondMidnightOffset.localToUtcInThisOffset(localOverlapEnds), equalTo(overlapEnds));
        assertThat(secondMidnightOffset.localToUtcInThisOffset(localOverlappingTime),
                equalTo(firstMidnightOffset.localToUtcInThisOffset(localOverlappingTime) + overlapMillis));

        long beforeOverlapValue = randomLong();
        assertThat(secondMidnightOffset.localToUtc(localFirstMidnight - 1, useValueForBeforeOverlap(beforeOverlapValue)),
                equalTo(beforeOverlapValue));
        long overlapValue = randomLong();
        assertThat(secondMidnightOffset.localToUtc(localFirstMidnight, useValueForOverlap(overlapValue)), equalTo(overlapValue));
        assertThat(secondMidnightOffset.localToUtc(localOverlapEnds, unusedStrategy()), equalTo(overlapEnds));
        assertThat(secondMidnightOffset.localToUtc(localOverlappingTime, useValueForOverlap(overlapValue)), equalTo(overlapValue));
    }

    public void testGap() {
        /*
         * Asia/Kathmandu turned their clocks 15 minutes forward at
         * 1986-01-01T00:00:00, creating a really "fun" gap.
         */
        ZoneId tz = ZoneId.of("Asia/Kathmandu");
        long gapLength = TimeUnit.MINUTES.toMillis(15);
        long transition = time("1986-01-01T00:00:00", tz);
        LocalTimeOffset.Lookup lookup = LocalTimeOffset.lookup(tz, transition - 1, transition);
        LocalTimeOffset gapOffset = lookup.lookup(transition);
        long localAtTransition = gapOffset.utcToLocalTime(transition);
        LocalTimeOffset beforeGapOffset = lookup.lookup(transition - 1);
        long localBeforeTransition = beforeGapOffset.utcToLocalTime(transition - 1);
        assertThat(localAtTransition - localBeforeTransition, equalTo(gapLength + 1));

        assertThat(beforeGapOffset.localToUtcInThisOffset(localBeforeTransition), equalTo(transition - 1));
        assertThat(gapOffset.localToUtcInThisOffset(localBeforeTransition), equalTo(transition - 1 - gapLength));
        assertThat(gapOffset.localToUtcInThisOffset(localAtTransition), equalTo(transition));

        long beforeGapValue = randomLong();
        assertThat(gapOffset.localToUtc(localBeforeTransition, useValueForBeforeGap(beforeGapValue)), equalTo(beforeGapValue));
        assertThat(gapOffset.localToUtc(localAtTransition, unusedStrategy()), equalTo(transition));
        long gapValue = randomLong();
        long localSkippedTime = randomLongBetween(localBeforeTransition, localAtTransition);
        assertThat(gapOffset.localToUtc(localSkippedTime, useValueForGap(gapValue)), equalTo(gapValue));
    }

    private static long utcTime(String time) {
        return DateFormatter.forPattern("date_optional_time").parseMillis(time);
    }

    private static long time(String time, ZoneId zone) {
        return DateFormatter.forPattern("date_optional_time").withZone(zone).parseMillis(time);
    }

    /**
     * The the last "fully defined" transitions in the provided {@linkplain ZoneId}.
     */
    private static ZoneOffsetTransition lastTransitionIn(ZoneId zone) {
        List<ZoneOffsetTransition> transitions = zone.getRules().getTransitions();
        return transitions.get(transitions.size() -1);
    }

    private static LocalTimeOffset.Strategy unusedStrategy() {
        return new LocalTimeOffset.Strategy() {
            @Override
            public long inGap(long localMillis, Gap gap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long beforeGap(long localMillis, Gap gap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long inOverlap(long localMillis, Overlap overlap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long beforeOverlap(long localMillis, Overlap overlap) {
                fail("Shouldn't be called");
                return 0;
            }
        };
    }

    private static LocalTimeOffset.Strategy useValueForGap(long gapValue) {
        return new LocalTimeOffset.Strategy() {
            @Override
            public long inGap(long localMillis, Gap gap) {
                return gapValue;
            }

            @Override
            public long beforeGap(long localMillis, Gap gap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long inOverlap(long localMillis, Overlap overlap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long beforeOverlap(long localMillis, Overlap overlap) {
                fail("Shouldn't be called");
                return 0;
            }
        };
    }

    private static LocalTimeOffset.Strategy useValueForBeforeGap(long beforeGapValue) {
        return new LocalTimeOffset.Strategy() {
            @Override
            public long inGap(long localMillis, Gap gap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long beforeGap(long localMillis, Gap gap) {
                return beforeGapValue;
            }

            @Override
            public long inOverlap(long localMillis, Overlap overlap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long beforeOverlap(long localMillis, Overlap overlap) {
                fail("Shouldn't be called");
                return 0;
            }
        };
    }

    private static LocalTimeOffset.Strategy useValueForOverlap(long overlapValue) {
        return new LocalTimeOffset.Strategy() {
            @Override
            public long inGap(long localMillis, Gap gap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long beforeGap(long localMillis, Gap gap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long inOverlap(long localMillis, Overlap overlap) {
                return overlapValue;
            }

            @Override
            public long beforeOverlap(long localMillis, Overlap overlap) {
                fail("Shouldn't be called");
                return 0;
            }
        };
    }


    private static LocalTimeOffset.Strategy useValueForBeforeOverlap(long beforeOverlapValue) {
        return new LocalTimeOffset.Strategy() {
            @Override
            public long inGap(long localMillis, Gap gap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long beforeGap(long localMillis, Gap gap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long inOverlap(long localMillis, Overlap overlap) {
                fail("Shouldn't be called");
                return 0;
            }

            @Override
            public long beforeOverlap(long localMillis, Overlap overlap) {
                return beforeOverlapValue;
            }
        };
    }

    private static long hours(long hours) {
        return TimeUnit.HOURS.toMillis(hours);
    }
}
