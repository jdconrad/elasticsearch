/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.rest.action.search;

import org.elasticsearch.telemetry.metric.LongCounter;
import org.elasticsearch.telemetry.metric.LongHistogram;
import org.elasticsearch.telemetry.metric.MeterRegistry;

import java.util.Map;

public class SearchResponseMetrics {

    public enum ResponseCountTotalStatus {
        SUCCESS,
        PARTIAL_FAILURE,
        FAILURE
    }

    public static final String RESPONSE_COUNT_TOTAL_STATUS_ATTRIBUTE_NAME = "status";

    public static final String TOOK_DURATION_TOTAL_HISTOGRAM_NAME = "es.search_response.took_durations.histogram";
    public static final String RESPONSE_COUNT_TOTAL_COUNTER_NAME = "es.search_response.response_count.counter";

    private final LongHistogram tookDurationTotalMillisHistogram;
    private final LongCounter responseCountTotalCounter;

    public SearchResponseMetrics(MeterRegistry meterRegistry) {
        this(
            meterRegistry.registerLongHistogram(
                TOOK_DURATION_TOTAL_HISTOGRAM_NAME,
                "The SearchResponse.took durations in milliseconds, expressed as a histogram",
                "millis"
            ),
            meterRegistry.registerLongCounter(
                RESPONSE_COUNT_TOTAL_COUNTER_NAME,
                "The cumulative total of search responses with an attribute to describe"
                    + "success, partial failure, or failure, expressed as a counter and an attribute",
                "count"
            )
        );
    }

    private SearchResponseMetrics(LongHistogram tookDurationTotalMillisHistogram, LongCounter responseCountTotalCounter) {
        this.tookDurationTotalMillisHistogram = tookDurationTotalMillisHistogram;
        this.responseCountTotalCounter = responseCountTotalCounter;
    }

    public long recordTookTime(long tookTime) {
        tookDurationTotalMillisHistogram.record(tookTime);
        return tookTime;
    }

    public void incrementResponseCount(ResponseCountTotalStatus responseCountTotalStatus) {
        responseCountTotalCounter.incrementBy(1L, Map.of(RESPONSE_COUNT_TOTAL_STATUS_ATTRIBUTE_NAME, responseCountTotalStatus));
    }
}
