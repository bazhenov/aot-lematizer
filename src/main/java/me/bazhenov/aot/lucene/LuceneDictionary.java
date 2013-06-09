package me.bazhenov.aot.lucene;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import me.bazhenov.aot.PartOfSpeech;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static me.bazhenov.aot.PartOfSpeech.Unknown;

public class LuceneDictionary {

	public static final String ANCODES_FILE = "ancodes.json";
	private final IndexReader reader;
	private final Map<String, String> ancodes;

	private static final Map<String, PartOfSpeech> posTags = new HashMap<String, PartOfSpeech>() {{
		put("Г", PartOfSpeech.Verb);
		put("Н", PartOfSpeech.Adverb);
		put("П", PartOfSpeech.Adjective);
		put("С", PartOfSpeech.Noun);
		put("МС", PartOfSpeech.Pronoun);
		put("МС-П", PartOfSpeech.Pronoun);
		put("МС-ПРЕДК", PartOfSpeech.Pronoun);
		put("КР_ПРИЛ", PartOfSpeech.Adjective);
		put("КР_ПРИЧАСТИЕ", PartOfSpeech.Participle);
		put("МЕЖД", PartOfSpeech.Interjection);
		//put("ПОСЛ", PartOfSpeech.Verb);
		//put("СОЮЗ", PartOfSpeech.Verb);
		//put("ФРАЗ", PartOfSpeech.Verb);
		//put("ЧАСТ", PartOfSpeech.Verb);
		put("ЧИСЛ", PartOfSpeech.Numeral);
		put("ЧИСЛ-П", PartOfSpeech.Numeral);
		//put("ВВОДН", PartOfSpeech.Verb);
		//put("ПРЕДК", PartOfSpeech.Verb);
		//put("ПРЕДЛ", PartOfSpeech.Verb);
		put("ИНФИНИТИВ", PartOfSpeech.Verb);
		put("ПРИЧАСТИЕ", PartOfSpeech.Participle);
		put("ДЕЕПРИЧАСТИЕ", PartOfSpeech.Participle);
	}};

	public LuceneDictionary(File file) throws IOException {
		Directory directory = FSDirectory.open(file);
		reader = IndexReader.open(directory);
		//noinspection unchecked
		ancodes = new ObjectMapper().readValue(new File(file, ANCODES_FILE), Map.class);
	}

	public Set<Morph> lookupLemmas(String word) throws IOException {
		Term term = new Term("word", word);
		TermDocs termDocs = reader.termDocs(term);
		Set<Morph> result = newHashSet();
		while (termDocs.next()) {
			Document document = reader.document(termDocs.doc());
			if (document.getBinaryValue("id") != null) {
				result.add(createMorph(document, word));
			} else {
				int lemmaId = Ints.fromByteArray(document.getBinaryValue("lemmaId"));
				result.add(createMorph(lookupDocumentByLemmaId(lemmaId), null));
			}
		}
		return result;
	}

	private Morph createMorph(Document document, String word) {
		int ancodeId = Shorts.fromByteArray(document.getBinaryValue("ancode"));
		String ancode = ancodes.get(Integer.toString(ancodeId));
		int firstSpace = ancode.indexOf(' ') + 1;
		String posDesc = ancode.substring(firstSpace, ancode.indexOf(' ', firstSpace));
		PartOfSpeech pos = posTags.containsKey(posDesc)
			? posTags.get(posDesc)
			: Unknown;

		String w = (word == null) ? document.get("word") : word;
		return new Morph(w, pos);
	}

	private Document lookupDocumentByLemmaId(int lemmaId) throws IOException {
		Term term = new Term("id", Integer.toString(lemmaId, 30));
		TermDocs termDocs = reader.termDocs(term);
		checkState(termDocs.next());
		return reader.document(termDocs.doc());
	}

	public Collection<Morph> lookup(String word) throws IOException {
		Term term = new Term("word", word);
		TermDocs termDocs = reader.termDocs(term);
		Set<Morph> result = newHashSet();
		while (termDocs.next()) {
			Document document = reader.document(termDocs.doc());
			result.add(createMorph(document, word));
		}
		return result;
	}
}
