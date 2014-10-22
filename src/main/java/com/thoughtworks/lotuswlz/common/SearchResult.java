package com.thoughtworks.lotuswlz.common;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.util.List;

public class SearchResult<T> {

    private TopDocs result;

    public int getTotalHits() {
        return result.totalHits;
    }

//    public <T> List<T> getResult(Function<T, Document> translator) {
//        List<Document> docs = Lists.newArrayList();
//        for (ScoreDoc doc : result.scoreDocs) {
//            docs.add(new Document(doc.doc));
//        }
//        List<T> objects = Lists.transform()
//    }
}
