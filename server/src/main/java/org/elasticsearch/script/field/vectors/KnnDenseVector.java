/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.script.field.vectors;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

import org.apache.lucene.util.VectorUtil;

import java.util.Arrays;
import java.util.List;

public class KnnDenseVector implements DenseVector {

    protected final float[] docVector;

    public KnnDenseVector(float[] docVector) {
        this.docVector = docVector;
    }

    @Override
    public float[] getVector() {
        // we need to copy the value, since {@link VectorValues} can reuse
        // the underlying array across documents
        return Arrays.copyOf(docVector, docVector.length);
    }

    @Override
    public float getMagnitude() {
        return DenseVector.getMagnitude(docVector);
    }

    @Override
    public int dotProduct(byte[] queryVector) {
        throw new UnsupportedOperationException("use [double dotProduct(float[] queryVector)] instead");
    }

    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    @Override
    public double dotProduct(float[] queryVector) {
        FloatVector sum = FloatVector.zero(SPECIES);
        int bound = SPECIES.loopBound(queryVector.length);
        int index = 0;

        for (; index < bound; index += SPECIES.length()) {
            FloatVector qv = FloatVector.fromArray(SPECIES, queryVector, index);
            FloatVector dv = FloatVector.fromArray(SPECIES, docVector, index);
            sum = dv.fma(qv, sum);
        }

        double result = 0.0;
        for (; index < queryVector.length; ++index) {
            result += docVector[index] * queryVector[index];
        }

        for (int lane = 0; lane < SPECIES.length(); ++lane) {
            result += sum.lane(lane);
        }

        return result;
        //return VectorUtil.dotProduct(docVector, queryVector);
    }

    @Override
    public double dotProduct(float[] queryVector, float[] resultVector) {
        int bound = SPECIES.loopBound(queryVector.length);
        int index = 0;

        for (; index < bound; index += SPECIES.length()) {
            FloatVector qv = FloatVector.fromArray(SPECIES, queryVector, index);
            FloatVector dv = FloatVector.fromArray(SPECIES, docVector, index);
            dv.mul(qv).intoArray(resultVector, index);
        }
        for (; index < queryVector.length; ++index) {
            resultVector[index] = docVector[index] * queryVector[index];
        }

        double sum = 0.0;
        for (index = 0; index < resultVector.length; ++index) {
            sum += resultVector[index];
        }

        return sum;
    }

    @Override
    public double dotProduct(List<Number> queryVector) {
        double result = 0;
        for (int i = 0; i < docVector.length; i++) {
            result += docVector[i] * queryVector.get(i).floatValue();
        }
        return result;
    }

    @Override
    public int l1Norm(byte[] queryVector) {
        throw new UnsupportedOperationException("use [double l1Norm(float[] queryVector)] instead");
    }

    @Override
    public double l1Norm(float[] queryVector) {
        double result = 0.0;
        for (int i = 0; i < docVector.length; i++) {
            result += Math.abs(docVector[i] - queryVector[i]);
        }
        return result;
    }

    @Override
    public double l1Norm(List<Number> queryVector) {
        double result = 0.0;
        for (int i = 0; i < docVector.length; i++) {
            result += Math.abs(docVector[i] - queryVector.get(i).floatValue());
        }
        return result;
    }

    @Override
    public double l2Norm(byte[] queryVector) {
        throw new UnsupportedOperationException("use [double l2Norm(float[] queryVector)] instead");
    }

    @Override
    public double l2Norm(float[] queryVector) {
        return Math.sqrt(VectorUtil.squareDistance(docVector, queryVector));
    }

    @Override
    public double l2Norm(List<Number> queryVector) {
        double l2norm = 0;
        for (int i = 0; i < docVector.length; i++) {
            double diff = docVector[i] - queryVector.get(i).floatValue();
            l2norm += diff * diff;
        }
        return Math.sqrt(l2norm);
    }

    @Override
    public double cosineSimilarity(byte[] queryVector, float qvMagnitude) {
        throw new UnsupportedOperationException("use [double cosineSimilarity(float[] queryVector, boolean normalizeQueryVector)] instead");
    }

    @Override
    public double cosineSimilarity(float[] queryVector, boolean normalizeQueryVector) {
        if (normalizeQueryVector) {
            return dotProduct(queryVector) / (DenseVector.getMagnitude(queryVector) * getMagnitude());
        }

        return dotProduct(queryVector) / getMagnitude();
    }

    @Override
    public double cosineSimilarity(List<Number> queryVector) {
        return dotProduct(queryVector) / (DenseVector.getMagnitude(queryVector) * getMagnitude());
    }

    @Override
    public double cosineSimilarity(float[] queryVector, boolean normalizeQueryVector, float[] resultVector) {
        if (normalizeQueryVector) {
            return dotProduct(queryVector, resultVector) / (DenseVector.getMagnitude(queryVector) * getMagnitude());
        }

        return dotProduct(queryVector, resultVector) / getMagnitude();
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
