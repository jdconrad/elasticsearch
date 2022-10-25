/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.index.mapper.vectors;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.Version;
import org.elasticsearch.index.mapper.vectors.DenseVectorFieldMapper.ElementType;

import java.nio.ByteBuffer;

import static org.elasticsearch.index.mapper.vectors.DenseVectorFieldMapper.MAGNITUDE_BYTES;

public final class VectorEncoderDecoder {

    private VectorEncoderDecoder() {}

    public static int denseVectorLength(Version indexVersion, ElementType elementType, BytesRef vectorBR) {
        return indexVersion.onOrAfter(Version.V_7_5_0)
            ? (vectorBR.length - MAGNITUDE_BYTES) / elementType.elementBytes
            : vectorBR.length / elementType.elementBytes;
    }

    /**
     * Decodes the last 4 bytes of the encoded vector, which contains the vector magnitude.
     * NOTE: this function can only be called on vectors from an index version greater than or
     * equal to 7.5.0, since vectors created prior to that do not store the magnitude.
     */
    public static float decodeMagnitude(Version indexVersion, BytesRef vectorBR) {
        assert indexVersion.onOrAfter(Version.V_7_5_0);
        ByteBuffer byteBuffer = ByteBuffer.wrap(vectorBR.bytes, vectorBR.offset, vectorBR.length);
        return byteBuffer.getFloat(vectorBR.offset + vectorBR.length - MAGNITUDE_BYTES);
    }

    /**
     * Calculates vector magnitude
     */
    private static float calculateMagnitude(Version indexVersion, ElementType elementType, BytesRef vectorBR) {
        final int length = denseVectorLength(indexVersion, elementType, vectorBR);
        ByteBuffer byteBuffer = ByteBuffer.wrap(vectorBR.bytes, vectorBR.offset, vectorBR.length);
        double magnitude = 0.0f;
        for (int i = 0; i < length; i++) {
            float value = elementType.getValue(byteBuffer);
            magnitude += value * value;
        }
        magnitude = Math.sqrt(magnitude);
        return (float) magnitude;
    }

    public static float getMagnitude(Version indexVersion, ElementType elementType, BytesRef vectorBR) {
        if (vectorBR == null) {
            throw new IllegalArgumentException(DenseVectorScriptDocValues.MISSING_VECTOR_FIELD_MESSAGE);
        }
        if (indexVersion.onOrAfter(Version.V_7_5_0)) {
            return decodeMagnitude(indexVersion, vectorBR);
        } else {
            return calculateMagnitude(indexVersion, elementType, vectorBR);
        }
    }

    /**
     * Decodes a BytesRef into the provided array of floats
     * @param vectorBR - dense vector encoded in BytesRef
     * @param vector - array of floats where the decoded vector should be stored
     */
    public static void decodeDenseVector(BytesRef vectorBR, ElementType elementType, float[] vector) {
        if (vectorBR == null) {
            throw new IllegalArgumentException(DenseVectorScriptDocValues.MISSING_VECTOR_FIELD_MESSAGE);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(vectorBR.bytes, vectorBR.offset, vectorBR.length);
        for (int dim = 0; dim < vector.length; dim++) {
            vector[dim] = elementType.getValue(byteBuffer);
        }
    }

}
