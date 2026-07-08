/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

/**
 * End-to-end tests for {@code @allocates_dynamic} estimators on collection materialization ({@code toArray}), whose result
 * scales with the collection size.
 */
public class AllocationCollectionCopyTests extends AllocationTestCase {

    public void testToArrayCharged() {
        // new ArrayList() charges 40; toArray() on the empty list charges a new Object[0].
        long expected = 40L + AllocSizes.arrayBytes(0, AllocSizes.REFERENCE_SIZE);
        assertEquals(expected, allocatedBytes("new ArrayList().toArray(); return \"x\";"));
    }

    public void testToArrayTripsLimit() {
        assertTripsLimit("new ArrayList().toArray(); return \"x\";");
    }
}
