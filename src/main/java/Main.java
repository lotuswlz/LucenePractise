import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thoughtworks.lotuswlz.IndexUtil;
import com.thoughtworks.lotuswlz.common.IndexConfig;
import com.thoughtworks.lotuswlz.model.Book;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;

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

    public static void main(String[] args) {


    }


}
