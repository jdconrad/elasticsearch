/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the "Elastic License
 * 2.0", the "GNU Affero General Public License v3.0 only", and the "Server Side
 * Public License v 1"; you may not use this file except in compliance with, at
 * your election, the "Elastic License 2.0", the "GNU Affero General Public
 * License v3.0 only", or the "Server Side Public License, v 1".
 */

package org.elasticsearch.entitlement.runtime.policy;

import org.elasticsearch.entitlement.runtime.policy.entitlements.FilesEntitlement;
import org.elasticsearch.entitlement.runtime.policy.entitlements.FilesEntitlement.Mode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.elasticsearch.core.PathUtils.getDefaultFileSystem;

public final class FileAccessTree {

    private record AccessPath(String path, Mode mode) {

        private AccessPath(String path, Mode mode) {
            this.path = Objects.requireNonNull(path);
            this.mode = Objects.requireNonNull(mode);
        }

        public static int compareTo(AccessPath one, AccessPath two) {
            return one.path.compareTo(two.path);
        }
    }

    private static final String FILE_SEPARATOR = getDefaultFileSystem().getSeparator();

    private final AccessPath[] paths;

    private FileAccessTree(FilesEntitlement filesEntitlement, PathLookup pathLookup, Set<String> exclusivePaths) {
        List<AccessPath> paths = new ArrayList<>();
        Set<String> excludes = new HashSet<>();
        for (FilesEntitlement.FileData fileData : filesEntitlement.filesData()) {
            var mode = fileData.mode();
            var resolvedPaths = fileData.resolvePaths(pathLookup);
            resolvedPaths.forEach(path -> {
                var normalized = normalizePath(path);
                paths.add(new AccessPath(normalized, mode));
                if (exclusivePaths.contains(normalized)) {
                    excludes.add(normalized);
                }
            });
        }

        // everything has access to the temp dir
        paths.add(new AccessPath(pathLookup.tempDir().toString(), Mode.READ_WRITE));
        for (String exclusivePath : exclusivePaths) {
            if (excludes.contains(exclusivePath) == false) {
                paths.add(new AccessPath(exclusivePath, Mode.NONE));
            }
        }
        paths.sort(AccessPath::compareTo);
        this.paths = paths.toArray(new AccessPath[0]);
    }

    public static FileAccessTree of(FilesEntitlement filesEntitlement, PathLookup pathLookup, Set<String> exclusivePaths) {
        return new FileAccessTree(filesEntitlement, pathLookup, exclusivePaths);
    }

    boolean canRead(Path path) {
        return checkPath(normalizePath(path), Mode.READ);
    }

    boolean canWrite(Path path) {
        return checkPath(normalizePath(path), Mode.READ_WRITE);
    }

    /**
     * @return the "canonical" form of the given {@code path}, to be used for entitlement checks.
     */
    static String normalizePath(Path path) {
        // Note that toAbsolutePath produces paths separated by the default file separator,
        // so on Windows, if the given path uses forward slashes, this consistently
        // converts it to backslashes.
        return path.toAbsolutePath().normalize().toString();
    }

    private boolean checkPath(String path, Mode mode) {
        if (paths.length == 0) {
            return false;
        }
        AccessPath match;
        int ndx = Arrays.binarySearch(paths, new AccessPath(path, mode), AccessPath::compareTo);
        if (ndx < -1) {
            AccessPath maybeParent = paths[-ndx - 2];
            if (path.startsWith(maybeParent.path()) && path.startsWith(FILE_SEPARATOR, maybeParent.path().length())) {
                match = maybeParent;
            } else {
                return false;
            }
        } else if (ndx >= 0) {
            match = paths[ndx];
        } else {
            return false;
        }
        return match.mode == mode || match.mode == Mode.READ_WRITE && mode == Mode.READ;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FileAccessTree that = (FileAccessTree) o;
        return Objects.deepEquals(paths, that.paths);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(paths));
    }
}
