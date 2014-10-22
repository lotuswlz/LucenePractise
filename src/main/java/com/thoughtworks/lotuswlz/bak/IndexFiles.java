package com.thoughtworks.lotuswlz.bak;

import com.thoughtworks.lotuswlz.common.Constants;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class IndexFiles {

    public void createIndex() throws IOException {
        File indexDir = getDirectory(Constants.INDEX_DIRECTORY);
        Directory ramDir = FSDirectory.open(indexDir);

        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        File dataDir = new File("indexdocs");

        IndexWriter ramIndexWriter = new IndexWriter(ramDir, config);
        indexDocs(ramIndexWriter, dataDir);

        ramIndexWriter.close();
    }

    private File getDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    static void indexDocs(IndexWriter writer, File file)
            throws IOException {
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        indexDocs(writer, new File(file, files[i]));
                    }
                }
            } else {

                FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException fnfe) {
                    return;
                }

                try {
                    Document doc = new Document();

                    Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
                    doc.add(pathField);

                    doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));

                    doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))));

                    if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                        System.out.println("adding " + file);
                        writer.addDocument(doc);
                    } else {
                        System.out.println("updating " + file);
                        writer.updateDocument(new Term("path", file.getPath()), doc);
                    }

                } finally {
                    fis.close();
                }
            }
        }
    }
}
