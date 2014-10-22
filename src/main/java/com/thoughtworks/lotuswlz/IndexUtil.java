package com.thoughtworks.lotuswlz;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thoughtworks.lotuswlz.common.IndexConfig;
import com.thoughtworks.lotuswlz.common.Constants;
import com.thoughtworks.lotuswlz.common.IndexTarget;
import com.thoughtworks.lotuswlz.model.Book;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class IndexUtil {

    private IndexWriter indexWriter;

    private IndexUtil() throws IOException {


    }

    public List<String> searchBook(String field, String text) throws Exception{
        QueryParser parser;
        if (field.contains(",")) {
            parser = new MultiFieldQueryParser(field.split(" *, *"), new StandardAnalyzer());
        } else {
            parser = new QueryParser(field, new StandardAnalyzer());
        }
        Query query = parser.parse(text);

        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(Constants.INDEX_PATH)));
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs results = searcher.search(query, 100);
        ScoreDoc[] hits = results.scoreDocs;
        int numTotalHits = results.totalHits;

        System.out.println(numTotalHits + " total matched.");

        List<String> matchBookIds = Lists.newArrayList();

        for (ScoreDoc doc : hits) {
            System.out.print(doc.score);
            Document matchedDoc = searcher.doc(doc.doc);

            System.out.print(matchedDoc.get("bookId"));
            System.out.print("\t");
            System.out.println(matchedDoc.get("bookName"));

            matchBookIds.add(matchedDoc.get("bookName"));
        }

        return matchBookIds;
    }

    public static IndexUtil getInstance() {
        try {
            return new IndexUtil();
        } catch (IOException e) {
            return null;
        }
    }

    public <T extends IndexTarget> void createIndex(IndexConfig indexConfig, List<T> targets) throws IOException {

        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        indexWriter = new IndexWriter(indexConfig.getDirectory(), config);
        List<Document> documents = Lists.transform(targets, indexConfig.getDocumentTranslateFunction());
        indexWriter.addDocuments(documents);
        indexWriter.close();
    }

    public <T extends IndexTarget> List<T> search(String[] searchFields, String keyword, IndexConfig<T> bookIndexConfig) throws IOException, ParseException {
        QueryParser parser = new MultiFieldQueryParser(searchFields, new StandardAnalyzer());
        Query query = parser.parse(keyword);

        IndexReader reader = DirectoryReader.open(bookIndexConfig.getDirectory());
        final IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs result = searcher.search(query, Constants.MAX_SEARCH_COUNT);

        List<ScoreDoc> scoreDocs = Lists.newArrayList(result.scoreDocs);
        List<Document> docs = Lists.transform(scoreDocs, new Function<ScoreDoc, Document>() {
            @Override
            public Document apply(ScoreDoc scoreDoc) {
                try {
                    return searcher.doc(scoreDoc.doc);
                } catch (IOException e) {
                    return null;
                }
            }
        });
        reader.close();
        return Lists.transform(docs, bookIndexConfig.getTargetTranslateFunction());
    }

}
