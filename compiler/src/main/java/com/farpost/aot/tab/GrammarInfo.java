package com.farpost.aot.tab;

import java.util.Arrays;

public enum GrammarInfo {

	Plural("мн"),
	Male("мр"),
	Nominative("им"),
	Pronoun("МС"),
	Abbreviation("аббр"),
	ShortAdjective("КР_ПРИЛ"),
	Comparative("сравн"),
	Dative("дт"),
	PresentTense("нст"),
	IndicativeAdverb("указат"),
	PerfectVerb("св"),
	MiddleName("отч"),
	Verb("Г"),
	PastTime("прш"),
	Interjection("МЕЖД"),
	Adverb("Н"),
	Singular("ед"),
	NeuterGender("ср"),
	Adjective("П"),
	Noun("С"),
	Inanimate("но"),
	Organization("орг"),
	Introduction("ВВОДН"),
	Transitive("нп"),
	Imperfect("нс"),
	Archaism("арх"),
	Toponym("лок"),
	MaleFemale("мр-жр"),
	PronounPredicative("МС-ПРЕДК"),
	FirstPerson("1л"),
	ImpersonalVerb2("*"),
	PassiveParticiple("стр"),
	Ablative("тв"),
	Immutable("0"),
	SecondGenetive("2"),
	ColloquialSpeech("разг"),
	Animated("од"),
	Typo("опч"),
	PronounAdjective("МС-П"),
	Participle("ПРИЧАСТИЕ"),
	SuperlativeAdjective("прев"),
	Surname("фам"),
	QualitativeAdjective("кач"),
	ActiveVoice("дст"),
	VerbalParticiple("ДЕЕПРИЧАСТИЕ"),
	BreifComunion("КР_ПРИЧАСТИЕ"),
	AdjectiveUnusedType("дфст"),
	SecondPerson("2л"),
	Female("жр"),
	Accusative("вн"),
	FutureTime("буд"),
	InterrogativeAdverb("вопр"),
	POSL_PART_OF_SPEECH("ПОСЛ"),
	Intransive("пе"),
	Slang("жарг"),
	Imperative("пвл"),
	Numeral("ЧИСЛ"),
	Predicative("ПРЕДК"),
	Particle("ЧАСТ"),
	Pretext("ПРЕДЛ"),
	Vocative("зв"),
	Name("имя"),
	Prepositional("пр"),
	ThirdPerson("3л"),
	Infinitive("ИНФИНИТИВ"),
	InpersonalVerb("безл"),
	Superlative("притяж"),
	OrdinalNumber("ЧИСЛ-П"),
	Idiom("ФРАЗ"),
	Genitive("рд"),
	Union("СОЮЗ");

	private final String token;

	@Override
	public String toString() {
		return token;
	}

	public static GrammarInfo fromString(final String token) {
		for (final var info : values()) {
			if (info.token.equals(token)) {
				return info;
			}
		}
		throw new IllegalArgumentException("Invalid token: " + token);
	}

	public static GrammarInfo fromByte(final byte i) {
		return values()[i];
	}

	public byte toByte() {
		return (byte) Arrays.binarySearch(values(), this);
	}

	GrammarInfo(final String token) {
		this.token = token;
	}
}
