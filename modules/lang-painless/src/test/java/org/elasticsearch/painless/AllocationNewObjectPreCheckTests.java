/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.painless;

import org.elasticsearch.painless.spi.PainlessTestScript;

/**
 * End-to-end tests for PR 6 {@code new T()} object-allocation pre-checks driven by the {@code @allocates_constant[bytes="N"]} whitelist
 * annotation on a constructor. The declared byte cost is charged against the running counter before the object is allocated and
 * trips the per-context limit when it exceeds it. Constructors without the annotation charge nothing (annotation-only sizing).
 */
public class AllocationNewObjectPreCheckTests extends AllocationTestCase {

    public void testAnnotatedConstructorCharged() {
        // new ArrayList() is annotated @allocates_constant[bytes="40"] in java.util.txt.
        assertEquals(40L, allocatedBytes("new ArrayList(); return \"x\";"));
    }

    public void testAnnotatedConstructorTripsLimit() {
        assertTripsLimit("new ArrayList(); return \"x\";");
    }

    public void testUnannotatedConstructorNotCharged() {
        // A constructor without @allocates_constant charges nothing: object sizing is annotation-only, a documented v1 gap.
        assertEquals(0L, allocatedBytes("new StringBuilder(); return \"x\";"));
    }

    public void testUnannotatedConstructorDoesNotTripLimit() {
        // With nothing charged, even a 1b limit is untouched by the construction itself.
        PainlessTestScript script = compile("new StringBuilder(); return \"x\";", "1b");
        script.execute();
        assertEquals(0L, ((PainlessScript) script).getAllocBytes());
    }
}
