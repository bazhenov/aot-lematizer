package me.bazhenov.aot;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class PosTag {

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
		put("СОЮЗ", PartOfSpeech.Preposition);
		put("ФРАЗ", PartOfSpeech.Preposition);
		put("ЧАСТ", PartOfSpeech.Preposition);
		put("ЧИСЛ", PartOfSpeech.Numeral);
		put("ЧИСЛ-П", PartOfSpeech.Numeral);
		put("ВВОДН", PartOfSpeech.Preposition);
		put("ПРЕДК", PartOfSpeech.Preposition);
		put("ПРЕДЛ", PartOfSpeech.Preposition);
		put("ИНФИНИТИВ", PartOfSpeech.Verb);
		put("ПРИЧАСТИЕ", PartOfSpeech.Participle);
		put("ДЕЕПРИЧАСТИЕ", PartOfSpeech.Participle);
	}};

	public static PartOfSpeech fromString(String code) {
		PartOfSpeech posTag = posTags.get(code);
		checkArgument(posTag != null, "Invalid POS descriptor %s", code);
		return posTag;
	}
}
