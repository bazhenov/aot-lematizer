package me.bazhenov.aot;

import com.google.common.base.Objects;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Lemma {

	private final String prefix;
	private final List<Flexion> flexions;
	private final PartOfSpeech posTag;

	public Lemma(String prefix, List<Flexion> flexions) {
		this.prefix = prefix;
		this.flexions = flexions;
		String posCode = flexions.get(0).getAncode().split(" ", 3)[1];
		try {
			posTag = PosTag.fromString(posCode);
		} catch (Exception e) {
			throw new RuntimeException("Invalid POS: " + prefix + "/" + posCode, e);
		}
	}

	public String getWord() {
		return prefix + flexions.get(0).getEnding();
	}

	public PartOfSpeech getPosTag() {
		return posTag;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("word", prefix)
			.toString();
	}

	public Set<String> derivate(String... tags) {
		Set<String> result = newHashSet();
		for (Flexion f : flexions)
			if (isAncodeMatchAllTags(f, tags))
				result.add(prefix + f.getEnding());
		return result;
	}

	private boolean isAncodeMatchAllTags(Flexion f, String[] tags) {
		for (String t : tags)
			if (!f.getAncode().contains(t))
				return false;
		return true;
	}
}
