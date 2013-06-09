package me.bazhenov.aot.lucene;

import com.google.common.base.Objects;
import me.bazhenov.aot.PartOfSpeech;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Morph {

	@Nonnull
	private final String word;
	private final PartOfSpeech pos;

	public Morph(String word, PartOfSpeech pos) {
		this.word = checkNotNull(word);
		this.pos = checkNotNull(pos);
	}

	public String getWord() {
		return word;
	}

	public PartOfSpeech getPos() {
		return pos;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Morph)) return false;

		Morph morph = (Morph) o;

		return pos == morph.pos && word.equals(morph.word);
	}

	@Override
	public int hashCode() {
		int result = word.hashCode();
		result = 31 * result + pos.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("word", word)
			.add("pos", pos)
			.toString();
	}
}
