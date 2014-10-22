package com.thoughtworks.lotuswlz.common;

import com.google.common.base.Function;
import org.apache.lucene.document.Document;

public class IndexTarget {

    protected float score;

    public float getScore() {
        return score;
    }

    public void setScore(float score) {

        this.score = score;
    }
}
