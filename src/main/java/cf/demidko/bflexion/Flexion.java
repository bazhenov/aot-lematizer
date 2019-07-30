package cf.demidko.bflexion;

public class Flexion {

	public final String lemma;
	public final GrammarInfo[] grammarInfo;

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(lemma).append('(');
		for(int i = 0; i < grammarInfo.length; ++i) {
			builder.append(grammarInfo[i]);
			if(i != grammarInfo.length - 1) {
				builder.append(", ");
			}
		}
		return builder.append(')').toString();
	}

	public Flexion(final String lemma, final GrammarInfo[] grammarInfo) {
		this.lemma = lemma;
		this.grammarInfo = grammarInfo;
	}
}
