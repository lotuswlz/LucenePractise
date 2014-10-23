package com.thoughtworks.lotuswlz;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thoughtworks.lotuswlz.common.IndexConfig;
import com.thoughtworks.lotuswlz.common.Constants;
import com.thoughtworks.lotuswlz.common.IndexTarget;
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
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.List;


public class IndexUtil {

    private IndexWriter indexWriter;

    private IndexUtil() throws IOException {

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
        indexWriter.addDocuments(Lists.transform(targets, indexConfig.getDocumentTranslateFunction()));
        indexWriter.close();
    }

    public <T extends IndexTarget> List<T> search(String[] searchFields, String keyword, IndexConfig<T> indexConfig) throws IOException, ParseException {
        QueryParser parser = new MultiFieldQueryParser(searchFields, new StandardAnalyzer());
        Query query = parser.parse(keyword);

        IndexReader reader = DirectoryReader.open(indexConfig.getDirectory());
        final IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs result = searcher.search(query, Constants.MAX_SEARCH_COUNT);

        List<ScoreDoc> scoreDocs = Lists.newArrayList(result.scoreDocs);
        Function<Document, T> targetTranslator = indexConfig.getTargetTranslateFunction();

        List<T> targets = Lists.newArrayList();
        for (ScoreDoc scoreDoc : scoreDocs) {
            Document document = searcher.doc(scoreDoc.doc);
            T target = targetTranslator.apply(document);
            if (target != null) {
                target.setScore(scoreDoc.score);
                targets.add(target);
            }
        }
        reader.close();
        return targets;
    }

}
