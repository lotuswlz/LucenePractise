package com.thoughtworks.lotuswlz;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thoughtworks.lotuswlz.common.IndexConfig;
import com.thoughtworks.lotuswlz.model.Book;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;

public class IndexUtilTest {

    private IndexConfig<Book> bookIndexConfig;

    @Before
    public void setUp() throws Exception {
        bookIndexConfig = IndexConfig.newConfig(Book.class);
        bookIndexConfig.setDocTranslator(new Function<Book, Document>() {
            @Override
            public Document apply(Book book) {
                Document doc = new Document();
                doc.add(new IntField("id", book.getId(), Field.Store.YES));
                doc.add(new TextField("name", book.getName(), Field.Store.YES));
                doc.add(new TextField("author", book.getAuthor(), Field.Store.YES));
                doc.add(new TextField("publisher", book.getPublisher(), Field.Store.YES));
                doc.add(new TextField("isbn", book.getIsbn(), Field.Store.YES));
                return doc;
            }
        });

        bookIndexConfig.setTargetTranslator(new Function<Document, Book>() {
            @Override
            public Book apply(Document doc) {
                return new Book(Integer.parseInt(doc.get("id")), doc.get("name"), doc.get("author"), doc.get("isbn"), doc.get("publisher"), null);
            }
        });
    }

    @Test
    public void testCreateIndex() {
        try {
            IndexUtil indexUtil = IndexUtil.getInstance();
            indexUtil.createIndex(bookIndexConfig, prepareBooks());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testSearch() throws Exception {
        IndexUtil indexUtil = IndexUtil.getInstance();
        List<Book> books = indexUtil.search(new String[] {"name", "author", "publisher", "isbn"}, "think*", bookIndexConfig);
        assertEquals(2, books.size());
    }

    private static List<Book> prepareBooks() {
        List<Book> books = Lists.newArrayList();
        books.add(new Book(1, "Thinking in Java", "Cathy", "22-33", "pub1", "abcd"));
        books.add(new Book(2, "Thinking in Post", "Jason", "22-11", "pub 2", "abcd"));
        books.add(new Book(3, "My Java Experience", "Catherine", "22-33", "pub put abc", "abcd"));
        books.add(new Book(4, "What's your name?", "John", "22-55", "pub1", "33"));
        books.add(new Book(5, "Who is John", "Sunny", "22-44", "pub 5", "abcd"));
        return books;
    }
}
