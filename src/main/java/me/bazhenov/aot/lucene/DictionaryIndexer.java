package me.bazhenov.aot.lucene;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Integer.parseInt;
import static me.bazhenov.aot.lucene.LuceneDictionary.ANCODES_FILE;

public class DictionaryIndexer {

	private final InputStream dictionary;
	private final File indexDirectory;
	private ObjectMapper mapper = new ObjectMapper();

	public DictionaryIndexer(InputStream dictionary, File indexDirectory) {
		this.dictionary = checkNotNull(dictionary);
		this.indexDirectory = checkNotNull(indexDirectory);
	}

	public static void main(String[] args) throws IOException {
		new DictionaryIndexer(new FileInputStream("dict.txt"), new File("index")).index();
	}

	public void index() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(dictionary));

		BiMap<Integer, String> ancodes = HashBiMap.create();

		Directory directory = FSDirectory.open(indexDirectory);
		IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_36, new KeywordAnalyzer()));
		writer.deleteAll();

		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\t");
			int id = parseInt(parts[0]);
			int lemmaId = parseInt(parts[2]);
			String word = parts[1];
			String morphInfo = parts[4];
			short ancode = Shorts.checkedCast(index(ancodes, morphInfo));

			Document d = new Document();

			if (lemmaId == id) {
				d.add(new Field("id", Integer.toString(id, 30), Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
				d.add(new Field("word", word, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
			} else {
				d.add(new Field("word", word, Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
				d.add(new Field("lemmaId", Ints.toByteArray(lemmaId)));
			}
			d.add(new Field("ancode", Shorts.toByteArray(ancode)));

			writer.addDocument(d);
		}
		writer.optimize();
		writer.close();

		mapper.writeValue(new File(indexDirectory, ANCODES_FILE), ancodes);
	}

	public static <T> int index(BiMap<Integer, T> map, T obj) {
		if (!map.containsValue(obj)) {
			map.put(map.size() + 1, obj);
		}
		return map.inverse().get(obj);
	}

}
