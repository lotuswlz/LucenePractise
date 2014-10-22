package com.thoughtworks.lotuswlz.common;


import com.google.common.base.Function;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class IndexConfig<T extends IndexTarget> {

    private Function<T, Document> documentTranslateFunction;

    private Function<Document, T> targetTranslateFunction;

    private Directory directory;

    private IndexConfig(Class<T> cls) throws IOException {
        initDirectory(Constants.INDEX_DIRECTORY + File.separator + cls.getName());
    }

    private void initDirectory(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        directory = FSDirectory.open(dir);
    }

    public static <T extends IndexTarget> IndexConfig<T> newConfig(Class<T> cls) throws IOException {
        return new IndexConfig<T>(cls);
    }

    public void setDocTranslator(Function<T, Document> function) {
        documentTranslateFunction = function;
    }

    public void setTargetTranslator(Function<Document, T> function) {
        targetTranslateFunction = function;
    }

    public Directory getDirectory() {
        return directory;
    }

    public Function<T, Document> getDocumentTranslateFunction() {
        return documentTranslateFunction;
    }

    public Function<Document, T> getTargetTranslateFunction() {
        return targetTranslateFunction;
    }
}
