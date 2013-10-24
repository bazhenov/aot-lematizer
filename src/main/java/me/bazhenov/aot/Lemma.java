package me.bazhenov.aot;

import com.google.common.base.Function;
import com.google.common.base.Objects;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Lemma {

	public static Function<Lemma, String> retireveWord = new Function<Lemma, String>() {
		@Override
		public String apply(Lemma lemma) {
			return lemma.getWord();
		}
	};

	private final String base;
	private final List<Flexion> flexions;
	private final PartOfSpeech posTag;

	public Lemma(String base, List<Flexion> flexions) {
		this.base = base;
		this.flexions = flexions;
		String posCode = flexions.get(0).getAncode().split(" ", 3)[1];
		try {
			posTag = PosTag.fromString(posCode);
		} catch (Exception e) {
			throw new RuntimeException("Invalid POS: " + base + "/" + posCode, e);
		}
	}

	public String getWord() {
		return base + flexions.get(0).getEnding();
	}

	public PartOfSpeech getPosTag() {
		return posTag;
	}

	public String getBase() {
		return base;
	}

	public List<Flexion> getFlexions() {
		return flexions;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("word", base)
			.toString();
	}

	public Set<String> derivate(String... tags) {
		Set<String> result = newHashSet();
		for (Flexion f : flexions)
			if (isAncodeMatchAllTags(f, tags))
				result.add(base + f.getEnding());
		return result;
	}

	private boolean isAncodeMatchAllTags(Flexion f, String[] tags) {
		for (String t : tags)
			if (!f.getAncode().contains(t))
				return false;
		return true;
	}
}
