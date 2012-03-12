package me.bazhenov.aot;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.tartarus.snowball.SnowballProgram;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;

public class DictionaryFilter extends TokenFilter {

	private final Dictionary dictionary;
	private final SnowballProgram stemmer;

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);
	private Queue<String> queue = new LinkedBlockingQueue<String>();

	public DictionaryFilter(TokenStream input, Dictionary dictionary, SnowballProgram stemmer) {
		super(input);
		this.dictionary = dictionary;
		this.stemmer = stemmer;
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (queue.isEmpty()) {
			if (input.incrementToken()) {
				if (!keywordAttr.isKeyword()) {
					String word = new String(termAtt.buffer(), 0, termAtt.length());
					List<Variation> norms = dictionary.getWordNorm(word);
					if (norms.isEmpty()) {
						doStemming();
					} else {
						for (String w : newHashSet(transform(norms, Variation.retrieveWord))) {
							queue.add(w);
						}
						String first = queue.poll();
						termAtt.copyBuffer(first.toCharArray(), 0, first.length());
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			String first = queue.poll();
			termAtt.copyBuffer(first.toCharArray(), 0, first.length());
			return true;
		}
	}

	private void doStemming() {
		char termBuffer[] = termAtt.buffer();
		final int length = termAtt.length();
		stemmer.setCurrent(termBuffer, length);
		stemmer.stem();
		final char finalTerm[] = stemmer.getCurrentBuffer();
		final int newLength = stemmer.getCurrentBufferLength();
		if (finalTerm != termBuffer)
			termAtt.copyBuffer(finalTerm, 0, newLength);
		else
			termAtt.setLength(newLength);
	}
}
