/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.aggregations;

import org.apache.lucene.index.LeafReaderContext;
import org.elasticsearch.common.CheckedFunction;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.mapper.MapperServiceTestCase;
import org.elasticsearch.search.aggregations.bucket.histogram.SizedBucketAggregator;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.support.AggregationContext;
import org.elasticsearch.search.aggregations.support.ValuesSourceRegistry;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdaptingAggregatorTests extends MapperServiceTestCase {
    /**
     * Its important that sub-aggregations of the {@linkplain AdaptingAggregator}
     * receive a reference to the {@linkplain AdaptingAggregator} as the parent.
     * Without it we can't do things like implement {@link SizedBucketAggregator}.
     */
    public void testParent() throws IOException {
        MapperService mapperService = createMapperService(mapping(b -> {}));
        ValuesSourceRegistry.Builder registry = new ValuesSourceRegistry.Builder();
        MaxAggregationBuilder.registerAggregators(registry);
        withAggregationContext(registry.build(), mapperService, List.of(), null, context -> {
            AggregatorFactories.Builder sub = AggregatorFactories.builder();
            sub.addAggregator(new MaxAggregationBuilder("test").field("foo"));
            AggregatorFactory factory = new DummyAdaptingAggregatorFactory("test", context, null, sub, null);
            Aggregator adapting = factory.create(null, CardinalityUpperBound.ONE);
            assertThat(adapting.subAggregators()[0].parent(), sameInstance(adapting));
        });
    }

    public void testBuildCallsAdapt() throws IOException {
        MapperService mapperService = createMapperService(mapping(b -> {}));
        withAggregationContext(mapperService, List.of(), context -> {
            AggregatorFactory factory = new DummyAdaptingAggregatorFactory("test", context, null, AggregatorFactories.builder(), null);
            Aggregator adapting = factory.create(null, CardinalityUpperBound.ONE);
            assertThat(adapting.buildEmptyAggregation().getMetadata(), equalTo(Map.of("dog", "woof")));
            assertThat(adapting.buildTopLevel().getMetadata(), equalTo(Map.of("dog", "woof")));
        });
    }

    private static class DummyAdaptingAggregatorFactory extends AggregatorFactory {
        DummyAdaptingAggregatorFactory(
            String name,
            AggregationContext context,
            AggregatorFactory parent,
            AggregatorFactories.Builder subFactoriesBuilder,
            Map<String, Object> metadata
        ) throws IOException {
            super(name, context, parent, subFactoriesBuilder, metadata);
        }

        @Override
        protected Aggregator createInternal(
            Aggregator parent,
            CardinalityUpperBound cardinality,
            Map<String, Object> metadata
        ) throws IOException {
            return new DummyAdaptingAggregator(
                parent,
                factories,
                subAggs -> new DummyAggregator(name, subAggs, context, parent, CardinalityUpperBound.ONE, metadata)
            );
        }
    }

    private static class DummyAdaptingAggregator extends AdaptingAggregator {
        DummyAdaptingAggregator(
            Aggregator parent,
            AggregatorFactories subAggregators,
            CheckedFunction<AggregatorFactories, Aggregator, IOException> delegate
        ) throws IOException {
            super(parent, subAggregators, delegate);
        }

        @Override
        protected InternalAggregation adapt(InternalAggregation delegateResult) {
            InternalAggregation result = mock(InternalAggregation.class);
            when(result.getMetadata()).thenReturn(Map.of("dog", "woof"));
            return result;
        }
    }

    private static class DummyAggregator extends AggregatorBase {
        protected DummyAggregator(
            String name,
            AggregatorFactories factories,
            AggregationContext context,
            Aggregator parent,
            CardinalityUpperBound subAggregatorCardinality,
            Map<String, Object> metadata
        ) throws IOException {
            super(name, factories, context, parent, subAggregatorCardinality, metadata);
        }

        @Override
        protected LeafBucketCollector getLeafCollector(LeafReaderContext ctx, LeafBucketCollector sub) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public InternalAggregation[] buildAggregations(long[] owningBucketOrds) throws IOException {
            return new InternalAggregation[] {null};
        }

        @Override
        public InternalAggregation buildEmptyAggregation() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
