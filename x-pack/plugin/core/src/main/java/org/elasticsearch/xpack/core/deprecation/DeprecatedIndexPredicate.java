/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.core.deprecation;

import org.elasticsearch.TransportVersion;
import org.elasticsearch.cluster.metadata.IndexMetadata;
import org.elasticsearch.cluster.metadata.Metadata;
import org.elasticsearch.cluster.metadata.MetadataIndexStateService;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexVersion;
import org.elasticsearch.index.IndexVersions;

import java.util.function.Predicate;

import static org.elasticsearch.cluster.metadata.IndexMetadata.SETTING_TRANSPORT_VERSION_CREATED;

public class DeprecatedIndexPredicate {

    public static final IndexVersion MINIMUM_WRITEABLE_VERSION_AFTER_UPGRADE = IndexVersions.V_8_0_0;

    /**
     * This predicate allows through only indices that were created with a previous lucene version, meaning that they need to be reindexed
     * in order to be writable in the _next_ lucene version. It excludes searchable snapshots as they are not writable.
     *
     * It ignores searchable snapshots as they are not writable.
     *
     * @param metadata the cluster metadata
     * @param filterToBlockedStatus if true, only indices that are write blocked will be returned,
     *                              if false, only those without a block are returned
     * @param includeSystem if true, all indices including system will be returned,
     *                              if false, only non-system indices are returned
     * @return a predicate that returns true for indices that need to be reindexed
     */
    public static Predicate<Index> getReindexRequiredPredicate(Metadata metadata, boolean filterToBlockedStatus, boolean includeSystem) {
        return index -> {
            IndexMetadata indexMetadata = metadata.index(index);
            return reindexRequired(indexMetadata, filterToBlockedStatus, includeSystem);
        };
    }

    /**
     * This method check if the indices that were created with a previous lucene version, meaning that they need to be reindexed
     * in order to be writable in the _next_ lucene version. It excludes searchable snapshots as they are not writable.
     *
     * @param indexMetadata the index metadata
     * @param filterToBlockedStatus if true, only indices that are write blocked will be returned,
     *                              if false, only those without a block are returned
     * @param includeSystem if true, all indices including system will be returned,
     *                              if false, only non-system indices are returned
     * @return returns true for indices that need to be reindexed
     */
    public static boolean reindexRequired(IndexMetadata indexMetadata, boolean filterToBlockedStatus, boolean includeSystem) {
        return creationVersionBeforeMinimumWritableVersion(indexMetadata)
            && (includeSystem || isNotSystem(indexMetadata))
            && isNotSearchableSnapshot(indexMetadata)
            && matchBlockedStatus(indexMetadata, filterToBlockedStatus);
    }

    /**
     * This method checks if this index is on the current transport version for the current major release version.
     *
     * @param indexMetadata the index metadata
     * @param filterToBlockedStatus if true, only indices that are write blocked will be returned,
     *                              if false, only those without a block are returned
     * @param includeSystem if true, all indices including system will be returned,
     *                              if false, only non-system indices are returned
     * @return returns true for indices that need to be reindexed
     */
    public static boolean reindexRequiredForTransportVersion(IndexMetadata indexMetadata, boolean filterToBlockedStatus, boolean includeSystem) {
        return transportVersionBeforeCurrentMajorRelease(indexMetadata)
            && (includeSystem || isNotSystem(indexMetadata))
            && isNotSearchableSnapshot(indexMetadata)
            && matchBlockedStatus(indexMetadata, filterToBlockedStatus);
    }

    private static boolean isNotSystem(IndexMetadata indexMetadata) {
        return indexMetadata.isSystem() == false;
    }

    private static boolean isNotSearchableSnapshot(IndexMetadata indexMetadata) {
        return indexMetadata.isSearchableSnapshot() == false;
    }

    private static boolean creationVersionBeforeMinimumWritableVersion(IndexMetadata metadata) {
        return metadata.getCreationVersion().before(MINIMUM_WRITEABLE_VERSION_AFTER_UPGRADE);
    }

    private static boolean matchBlockedStatus(IndexMetadata indexMetadata, boolean filterToBlockedStatus) {
        return MetadataIndexStateService.VERIFIED_READ_ONLY_SETTING.get(indexMetadata.getSettings()) == filterToBlockedStatus;
    }

    private static final TransportVersion REINDEX_MINIMUM = TransportVersion.fromId(8841000);
    private static boolean transportVersionBeforeCurrentMajorRelease(IndexMetadata indexMetadata) {
        return IndexMetadata.SETTING_INDEX_TRANSPORT_VERSION_CREATED.get(indexMetadata.getSettings()).id() < REINDEX_MINIMUM.id();
    }
}
