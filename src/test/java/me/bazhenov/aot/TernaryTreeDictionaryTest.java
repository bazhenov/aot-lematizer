package me.bazhenov.aot;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.getFirst;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.regex.Pattern.*;
import static me.bazhenov.aot.Lemma.retireveWord;
import static me.bazhenov.aot.PartOfSpeech.Noun;
import static me.bazhenov.aot.TernaryTreeDictionary.loadDictionary;
import static me.bazhenov.aot.TernaryTreeDictionary.mergeIntersect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TernaryTreeDictionaryTest {

	private TernaryTreeDictionary dictionary;

	@BeforeClass
	public void setUp() throws Exception {
		dictionary = loadDictionary();
	}

	@Test
	public void lookup() throws IOException, InterruptedException {
		Set<Lemma> lemmas = dictionary.lookupWord("люди");
		Lemma l = getFirst(lemmas, null);
		assertThat(l.getPosTag(), is(Noun));
		Set<String> derivations = l.derivate("ед", "вн");
		assertThat(derivations, hasSize(1));
		assertThat(derivations, hasItem("человека"));
	}

	@Test
	public void prefixesShouldBeResolved() {
		List<String> lemmas = from(dictionary.lookupWord("полезай")).transform(retireveWord).toList();
		assertThat(lemmas, hasItem("лезть"));
	}

	@Test
	public void foo() {
		Set<Lemma> lemmas = dictionary.lookupWord("покос");
		assertThat(lemmas, hasSize(1));
	}

	@Test
	public void intersect() {
		IntArrayList a = new IntArrayList(new int[]{1, 2, 4, 6});
		IntArrayList b = new IntArrayList(new int[]{0, 2, 5, 6});

		IntArrayList result = mergeIntersect(a, b);
		assertThat(result.toIntArray(), is(new int[]{2, 6}));
	}

	@Test
	public void testPerformance() throws IOException {
		TernaryTreeDictionary dict = TernaryTreeDictionary.loadDictionary();
		String text = "Здравствуйте , дорогие покупатели!\n" +
			"***Наша компания предлагает вам свежие цветы по самым низким ценам*** \n" +
			"Мы занимаемся продажей роз и хризантем из Эквадора, Кении, Голландии !!!\n" +
			"Поставки 2 раза в неделю, по этому цветок всегда в наличии и самый свежий !!!\n" +
			"***Нас очень легко найти и удобно подъехать и забрать покупку***\n" +
			"Цены:\n" +
			"\n" +
			"***Кенийская роза 40см - 35 рублей !!!***\n" +
			"***Кустовая роза 50см - 100 рублей !!!***\n" +
			"***Хризантема 70см - 80 рублей !!!***\n" +
			"\n" +
			"Роза Эквадор 60-70см (цена зависит от количества)\n" +
			"\n" +
			"***15 роз - 1125 рублей (75 рублей за штуку)\n" +
			"***25роз - 1875 рублей ( 75 рублей за штуку)\n" +
			"***51 роза - 3672 рублей ( 72 рубля за штуку) \n" +
			"***101 роза - 6565 рублей ( 65 РУБЛЕЙ ЗА ШТУКУ)\n" +
			"!!!!!!!!!!!! Эквадорскую розу мы заказываем только на лучших плантациях !!! Роза всегда отличного качества и имеет крупный красивый бутон и долго стоит в домашних условиях !!!!!!!!!!!!!!!!\n" +
			"\n" +
			"***Так же мы занимаемся продажей гелиевых шаров***\n" +
			"Цена на шары:\n" +
			"12\"- 50 РУБЛЕЙ\n" +
			"14\" – 60 РУБЛЕЙ\n" +
			"16\" – 70 РУБЛЕЙ\n" +
			"18\" – 80 РУБЛЕЙ\n" +
			"******* У нас не отличаются шарики с надписью и без по цене !!! А так же обработка уже включена в стоимость шаров !!! С обработкой шары летают около недели, без обработки максимум сутки ************\n" +
			"\n" +
			"А еще у нас постоянно проходят АКЦИИ и не редко мы раздаем лепестки роз совсем даром =)\n" +
			"\n" +
			"*************************Мы находимся на Военном шоссе 17, 2 этаж **********************";

		Pattern words = compile("[а-я]+", CASE_INSENSITIVE | UNICODE_CASE);

		Matcher matcher = words.matcher(text);

		warmUp(dict);

		long totalTimeSpent = 0;
		long wordCount = 0;
		while (matcher.find()) {
			wordCount++;
			String word = matcher.group().toLowerCase();
			long start = System.nanoTime();
			dict.lookupWord(word);
			long end = System.nanoTime();
			long timeSpent = NANOSECONDS.toMicros(end - start);
			totalTimeSpent += timeSpent;
			//System.out.printf("%5d - %s\n", timeSpent, word);
		}
		System.out.printf("Total time spent: %d (%.1f per word)", totalTimeSpent, ((double) totalTimeSpent) / wordCount);
	}

	private void warmUp(TernaryTreeDictionary dict) {
		for (int i = 0; i < 1000; i++) {
			dict.lookupWord("клеенный");
		}
	}
}

