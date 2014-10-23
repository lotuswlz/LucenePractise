package com.thoughtworks.lotuswlz.common;

import com.google.common.primitives.Floats;

public class IndexTarget implements Comparable<IndexTarget> {

    protected float score;

    public float getScore() {
        return score;
    }

    public void setScore(float score) {

        this.score = score;
    }
    @Override
    public int compareTo(IndexTarget o) {
        return Floats.compare(o.score, this.score);
    }
}
