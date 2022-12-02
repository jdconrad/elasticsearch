/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.script.field.vectors;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorSpecies;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.core.SuppressForbidden;

import java.util.List;

import static jdk.incubator.vector.VectorOperators.ADD;

public class ByteKnnDenseVector implements DenseVector {

    protected final BytesRef docVector;

    protected float[] floatDocVector;
    protected boolean magnitudeCalculated = false;
    protected float magnitude;

    public ByteKnnDenseVector(BytesRef vector) {
        this.docVector = vector;
    }

    @Override
    public float[] getVector() {
        if (floatDocVector == null) {
            floatDocVector = new float[docVector.length];

            int i = 0;
            int j = docVector.offset;

            while (i < docVector.length) {
                floatDocVector[i++] = docVector.bytes[j++];
            }
        }

        return floatDocVector;
    }

    @Override
    public float getMagnitude() {
        if (magnitudeCalculated == false) {
            magnitude = DenseVector.getMagnitude(docVector, docVector.length);
            magnitudeCalculated = true;
        }
        return magnitude;
    }

    private static final VectorSpecies<Byte> BYTE_SPECIES = ByteVector.SPECIES_PREFERRED;
    private static final VectorSpecies<Short> SHORT_SPECIES = ShortVector.SPECIES_PREFERRED;

    @Override
    public int dotProduct(byte[] queryVector) {
        /*int result = 0;
        int i = 0;
        int j = docVector.offset;
        while (i < docVector.length) {
            result += docVector.bytes[j++] * queryVector[i++];
        }
        return result;*/

        int bound = BYTE_SPECIES.loopBound(queryVector.length);
        int index = 0;
        int offset = docVector.offset;
        int result = 0;

        if (queryVector.length > BYTE_SPECIES.length()) {
            for (; index < bound; index += BYTE_SPECIES.length(), offset += BYTE_SPECIES.length()) {
                ByteVector qvb = ByteVector.fromArray(BYTE_SPECIES, queryVector, index);
                ByteVector dvb = ByteVector.fromArray(BYTE_SPECIES, docVector.bytes, offset);
                ShortVector qvi0 = (ShortVector) qvb.castShape(SHORT_SPECIES, 0);
                ShortVector dvi0 = (ShortVector) dvb.castShape(SHORT_SPECIES, 0);
                ShortVector qvi1 = (ShortVector) qvb.castShape(SHORT_SPECIES, 1);
                ShortVector dvi1 = (ShortVector) dvb.castShape(SHORT_SPECIES, 1);
                result += (qvi0.mul(dvi0).add(qvi1.mul(dvi1))).reduceLanes(ADD);
            }
        }

        for (; index < queryVector.length; ++index, ++offset) {
            result += docVector.bytes[offset] * queryVector[index];
        }

        return result;
    }

    @Override
    public double dotProduct(float[] queryVector) {
        throw new UnsupportedOperationException("use [int dotProduct(byte[] queryVector)] instead");
    }

    @Override
    public double dotProduct(List<Number> queryVector) {
        int result = 0;
        int i = 0;
        int j = docVector.offset;
        while (i < docVector.length) {
            result += docVector.bytes[j++] * queryVector.get(i++).intValue();
        }
        return result;
    }

    @SuppressForbidden(reason = "used only for bytes so it cannot overflow")
    private int abs(int value) {
        return Math.abs(value);
    }

    @Override
    public int l1Norm(byte[] queryVector) {
        int result = 0;
        int i = 0;
        int j = docVector.offset;
        while (i < docVector.length) {
            result += abs(docVector.bytes[j++] - queryVector[i++]);
        }
        return result;
    }

    @Override
    public double l1Norm(float[] queryVector) {
        throw new UnsupportedOperationException("use [int l1Norm(byte[] queryVector)] instead");
    }

    @Override
    public double l1Norm(List<Number> queryVector) {
        int result = 0;
        int i = 0;
        int j = docVector.offset;
        while (i < docVector.length) {
            result += abs(docVector.bytes[j++] - queryVector.get(i++).intValue());
        }
        return result;
    }

    @Override
    public double l2Norm(byte[] queryVector) {
        int result = 0;
        int i = 0;
        int j = docVector.offset;
        while (i < docVector.length) {
            int diff = docVector.bytes[j++] - queryVector[i++];
            result += diff * diff;
        }
        return Math.sqrt(result);
    }

    @Override
    public double l2Norm(float[] queryVector) {
        throw new UnsupportedOperationException("use [double l2Norm(byte[] queryVector)] instead");
    }

    @Override
    public double l2Norm(List<Number> queryVector) {
        int result = 0;
        int i = 0;
        int j = docVector.offset;
        while (i < docVector.length) {
            int diff = docVector.bytes[j++] - queryVector.get(i++).intValue();
            result += diff * diff;
        }
        return Math.sqrt(result);
    }

    @Override
    public double cosineSimilarity(byte[] queryVector, float qvMagnitude) {
        return dotProduct(queryVector) / (qvMagnitude * getMagnitude());
    }

    @Override
    public double cosineSimilarity(float[] queryVector, boolean normalizeQueryVector) {
        throw new UnsupportedOperationException("use [double cosineSimilarity(byte[] queryVector, float qvMagnitude)] instead");
    }

    @Override
    public double cosineSimilarity(List<Number> queryVector) {
        return dotProduct(queryVector) / (DenseVector.getMagnitude(queryVector) * getMagnitude());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getDims() {
        return docVector.length;
    }

    @Override
    public int size() {
        return 1;
    }
}
