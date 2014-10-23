import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thoughtworks.lotuswlz.IndexUtil;
import com.thoughtworks.lotuswlz.common.IndexConfig;
import com.thoughtworks.lotuswlz.model.Book;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;


public class Main {

    private IndexConfig<Book> bookIndexConfig;

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

    public void test() {
        IndexUtil indexUtil = IndexUtil.getInstance();
        try {
            indexUtil.createIndex(bookIndexConfig, prepareBooks());
            List<Book> books = indexUtil.search(new String[] {"name", "author", "publisher", "isbn"}, "John", bookIndexConfig);

            for (Book book : books) {
                System.out.println(book.getId() + "," + book.getName() + "," + book.getScore());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) {
        try {
            Main main = new Main();
            main.setUp();
            main.test();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
