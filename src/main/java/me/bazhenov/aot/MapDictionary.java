package me.bazhenov.aot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;

/**
 * Имплементация словаря на основе хранения лексем в нескольких {@link HashMap}
 * с использованием избыточной информации (такие как все основы словаря) для обеспечения быстрой выборки. <br />
 * <p>
 * Данный класс использует следующий формат словаря: <br />
 * <ol>
 * <li>
 * <b>Секция склонений</b><br />
 * В начале данной секции идет число n - количество склонений в секции, далее идет
 * n блоков вида
 * <pre>
 *       m
 *       posTag
 *       f1
 *       ancode1
 *       f2
 *       ancode2
 *       ...
 *       fm
 *       ancodem
 *     </pre>
 * где m - количество склонений, posTag - один из ключей {@link PosTag#posTags},
 * общий для всей парадигмы, fi - склонение, которое представлено либо в виде
 * *%s*%s, где первая строка - это префикс, а вторая - суффикс, либо просто буквенной строкой %s (в таком случае
 * префикс считается равным пустой строке). Также для каждого склонения на следующей за ним строке идет его ancode.
 * </li>
 * <li>
 * <b>Секция лемм</b><br />
 * Данная секция также начинается с количества блоков n. Эти блоки группируют основания
 * лемм по индексу парадигмы. Формат блоков следующий
 * <pre>
 *       paradigm
 *       m
 *       base1
 *       base2
 *       ...
 *       basem
 *     </pre>
 * Где paradigm - номер парадигмы склонения (индекс блока из секции склонений),
 * m - количество оснований, basei - основание леммы, у которой номер парадигмы равен paradigm
 * </li>
 * </ol>
 * Также стоит обратить внимание на то, что данная имплементация не использует tab-файл, храня вместо этого его
 * в памяти в {@link MapDictionary#tabDescriptors}
 * <p>
 * {@link #lookupWord(String)} не модифицирует состояние внутренних объектов, и как следствие потокобезопасный.
 */
public class MapDictionary implements Dictionary {

	private static final Map<String, String> tabDescriptors;

	static {
		{
			Map<String, String> tabs = new HashMap<>();
			tabs.put("аа", "A С мр,ед,им");
			tabs.put("аб", "A С мр,ед,рд");
			tabs.put("Эф", "A С мр,ед,рд,2");
			tabs.put("ав", "A С мр,ед,дт");
			tabs.put("аг", "A С мр,ед,вн");
			tabs.put("ад", "A С мр,ед,тв");
			tabs.put("ае", "A С мр,ед,пр");
			tabs.put("Эх", "A С мр,ед,пр,2");
			tabs.put("ас", "A С мр,ед,зв,");
			tabs.put("аж", "A С мр,мн,им");
			tabs.put("аз", "A С мр,мн,рд");
			tabs.put("аи", "A С мр,мн,дт");
			tabs.put("ай", "A С мр,мн,вн");
			tabs.put("ак", "A С мр,мн,тв");
			tabs.put("ал", "A С мр,мн,пр");
			tabs.put("ам", "B С мр,0");
			tabs.put("ан", "B С мр,ед,0");
			tabs.put("Юо", "A С мр,ед,им,разг ");
			tabs.put("Юп", "A С мр,ед,рд,разг ");
			tabs.put("Юр", "A С мр,ед,дт,разг ");
			tabs.put("Юс", "A С мр,ед,вн,разг ");
			tabs.put("Ют", "A С мр,ед,тв,разг ");
			tabs.put("Юф", "A С мр,ед,пр,разг ");
			tabs.put("Юх", "A С мр,ед,зв,разг ");
			tabs.put("Яб", "A С мр,мн,им,разг ");
			tabs.put("Яа", "A С мр,мн,рд,разг");
			tabs.put("Яв", "A С мр,мн,дт,разг");
			tabs.put("Яг", "A С мр,мн,вн,разг");
			tabs.put("Яд", "A С мр,мн,тв,разг");
			tabs.put("Яж", "A С мр,мн,пр,разг");
			tabs.put("го", "A С мр,ед,им,арх ");
			tabs.put("гп", "A С мр,ед,рд,арх ");
			tabs.put("гр", "A С мр,ед,дт,арх ");
			tabs.put("гс", "A С мр,ед,вн,арх ");
			tabs.put("гт", "A С мр,ед,тв,арх ");
			tabs.put("гу", "A С мр,ед,пр,арх ");
			tabs.put("гф", "A С мр,мн,им,арх ");
			tabs.put("гх", "A С мр,мн,рд,арх");
			tabs.put("гц", "A С мр,мн,дт,арх");
			tabs.put("гч", "A С мр,мн,вн,арх");
			tabs.put("гш", "A С мр,мн,тв,арх");
			tabs.put("гщ", "A С мр,мн,пр,арх");
			tabs.put("ва", "E С мр-жр,ед,им");
			tabs.put("вб", "E С мр-жр,ед,рд");
			tabs.put("вв", "E С мр-жр,ед,дт");
			tabs.put("вг", "E С мр-жр,ед,вн");
			tabs.put("вд", "E С мр-жр,ед,тв");
			tabs.put("ве", "E С мр-жр,ед,пр");
			tabs.put("вж", "E С мр-жр,мн,им");
			tabs.put("вз", "E С мр-жр,мн,рд");
			tabs.put("ви", "E С мр-жр,мн,дт");
			tabs.put("вй", "E С мр-жр,мн,вн");
			tabs.put("вк", "E С мр-жр,мн,тв");
			tabs.put("вл", "E С мр-жр,мн,пр");
			tabs.put("вм", "F С мр-жр,0");
			tabs.put("вн", "F С мр-жр,ед,0");
			tabs.put("во", "E С арх,мр-жр,ед,им");
			tabs.put("вп", "E С арх,мр-жр,ед,рд");
			tabs.put("вр", "E С арх,мр-жр,ед,дт");
			tabs.put("вс", "E С арх,мр-жр,ед,вн");
			tabs.put("вт", "E С арх,мр-жр,ед,тв");
			tabs.put("ву", "E С арх,мр-жр,ед,пр");
			tabs.put("вф", "E С арх,мр-жр,мн,им");
			tabs.put("вх", "E С арх,мр-жр,мн,рд");
			tabs.put("вц", "E С арх,мр-жр,мн,дт");
			tabs.put("вч", "E С арх,мр-жр,мн,вн");
			tabs.put("вш", "E С арх,мр-жр,мн,тв");
			tabs.put("вщ", "E С арх,мр-жр,мн,пр");
			tabs.put("га", "G С жр,ед,им");
			tabs.put("гб", "G С жр,ед,рд");
			tabs.put("гв", "G С жр,ед,дт");
			tabs.put("гг", "G С жр,ед,вн");
			tabs.put("гд", "G С жр,ед,тв");
			tabs.put("ге", "G С жр,ед,пр");
			tabs.put("Эч", "G С жр,ед,пр,2");
			tabs.put("Йш", "G С жр,ед,зв");
			tabs.put("гж", "G С жр,мн,им");
			tabs.put("гз", "G С жр,мн,рд");
			tabs.put("ги", "G С жр,мн,дт");
			tabs.put("гй", "G С жр,мн,вн");
			tabs.put("гк", "G С жр,мн,тв");
			tabs.put("гл", "G С жр,мн,пр");
			tabs.put("гм", "H С жр,0");
			tabs.put("гн", "H С жр,ед,0");
			tabs.put("Йа", "G С арх,жр,ед,им");
			tabs.put("Йб", "G С арх,жр,ед,рд");
			tabs.put("Йв", "G С арх,жр,ед,дт");
			tabs.put("Йг", "G С арх,жр,ед,вн");
			tabs.put("Йд", "G С арх,жр,ед,тв");
			tabs.put("Йе", "G С арх,жр,ед,пр");
			tabs.put("Йж", "G С арх,жр,мн,им");
			tabs.put("Йз", "G С арх,жр,мн,рд");
			tabs.put("Йи", "G С арх,жр,мн,дт");
			tabs.put("Йй", "G С арх,жр,мн,вн");
			tabs.put("Йк", "G С арх,жр,мн,тв");
			tabs.put("Йл", "G С арх,жр,мн,пр");
			tabs.put("Йм", "G С разг,жр,ед,им");
			tabs.put("Йн", "G С разг,жр,ед,рд");
			tabs.put("Йо", "G С разг,жр,ед,дт");
			tabs.put("Йп", "G С разг,жр,ед,вн");
			tabs.put("Йр", "G С разг,жр,ед,тв");
			tabs.put("Йс", "G С разг,жр,ед,пр");
			tabs.put("Йт", "G С разг,жр,мн,им");
			tabs.put("Йу", "G С разг,жр,мн,рд");
			tabs.put("Йф", "G С разг,жр,мн,дт");
			tabs.put("Йх", "G С разг,жр,мн,вн");
			tabs.put("Йц", "G С разг,жр,мн,тв");
			tabs.put("Йч", "G С разг,жр,мн,пр");
			tabs.put("еа", "K С ср,ед,им");
			tabs.put("еб", "K С ср,ед,рд");
			tabs.put("ев", "K С ср,ед,дт");
			tabs.put("ег", "K С ср,ед,вн");
			tabs.put("ед", "K С ср,ед,тв");
			tabs.put("ее", "K С ср,ед,пр");
			tabs.put("еж", "K С ср,мн,им");
			tabs.put("ез", "K С ср,мн,рд");
			tabs.put("еи", "K С ср,мн,дт");
			tabs.put("ей", "K С ср,мн,вн");
			tabs.put("ек", "K С ср,мн,тв");
			tabs.put("ел", "K С ср,мн,пр");
			tabs.put("ем", "L С ср,0");
			tabs.put("ен", "L С ср,ед,0");
			tabs.put("Эя", "K С ср,ед,рд,аббр");
			tabs.put("Яз", "K С разг,ср,ед,им");
			tabs.put("Яи", "K С разг,ср,ед,рд");
			tabs.put("Як", "K С разг,ср,ед,дт");
			tabs.put("Ял", "K С разг,ср,ед,вн");
			tabs.put("Ям", "K С разг,ср,ед,тв");
			tabs.put("Ян", "K С разг,ср,ед,пр");
			tabs.put("Яо", "K С разг,ср,мн,им");
			tabs.put("Яп", "K С разг,ср,мн,рд");
			tabs.put("Яр", "K С разг,ср,мн,дт");
			tabs.put("Яс", "K С разг,ср,мн,вн");
			tabs.put("Ят", "K С разг,ср,мн,тв");
			tabs.put("Яу", "K С разг,ср,мн,пр");
			tabs.put("иж", "Q С мн,мн,им");
			tabs.put("из", "Q С мн,мн,рд");
			tabs.put("ии", "Q С мн,мн,дт");
			tabs.put("ий", "Q С мн,мн,вн");
			tabs.put("ик", "Q С мн,мн,тв");
			tabs.put("ил", "Q С мн,мн,пр");
			tabs.put("им", "R С мн,0");
			tabs.put("ао", "B С мр,аббр,0,");
			tabs.put("ап", "B С мр,ед,аббр,0");
			tabs.put("ат", "H С жр,аббр,0");
			tabs.put("ау", "H С жр,ед,аббр,0");
			tabs.put("ац", "H С ср,аббр,0");
			tabs.put("ач", "H С ср,ед,аббр,0");
			tabs.put("аъ", "R С мн,аббр,0");
			tabs.put("бо", "C С мр,имя,ед,им");
			tabs.put("бп", "C С мр,имя,ед,рд");
			tabs.put("бр", "C С мр,имя,ед,дт");
			tabs.put("бс", "C С мр,имя,ед,вн");
			tabs.put("бт", "C С мр,имя,ед,тв");
			tabs.put("бу", "C С мр,имя,ед,пр");
			tabs.put("бь", "C С мр,имя,ед,зв,разг");
			tabs.put("бф", "C С мр,имя,мн,им");
			tabs.put("бх", "C С мр,имя,мн,рд");
			tabs.put("бц", "C С мр,имя,мн,дт");
			tabs.put("бч", "C С мр,имя,мн,вн");
			tabs.put("бш", "C С мр,имя,мн,тв");
			tabs.put("бщ", "C С мр,имя,мн,пр");
			tabs.put("бН", "I С мр,имя,0");
			tabs.put("вН", "E С мр-жр,имя,0");
			tabs.put("вО", "E С мр-жр,имя,ед,им");
			tabs.put("вП", "E С мр-жр,имя,ед,рд");
			tabs.put("вР", "E С мр-жр,имя,ед,дт");
			tabs.put("вС", "E С мр-жр,имя,ед,вн");
			tabs.put("вТ", "E С мр-жр,имя,ед,тв");
			tabs.put("вУ", "E С мр-жр,имя,ед,пр");
			tabs.put("вЬ", "E С мр-жр,имя,ед,зв,разг");
			tabs.put("вФ", "E С мр-жр,имя,мн,им");
			tabs.put("вХ", "E С мр-жр,имя,мн,рд");
			tabs.put("вЦ", "E С мр-жр,имя,мн,дт");
			tabs.put("вЧ", "E С мр-жр,имя,мн,вн");
			tabs.put("вШ", "E С мр-жр,имя,мн,тв");
			tabs.put("вЩ", "E С мр-жр,имя,мн,пр");
			tabs.put("до", "I С жр,имя,ед,им");
			tabs.put("дп", "I С жр,имя,ед,рд");
			tabs.put("др", "I С жр,имя,ед,дт");
			tabs.put("дс", "I С жр,имя,ед,вн");
			tabs.put("дт", "I С жр,имя,ед,тв");
			tabs.put("ду", "I С жр,имя,ед,пр");
			tabs.put("дь", "I С жр,имя,ед,зв,разг");
			tabs.put("дф", "I С жр,имя,мн,им");
			tabs.put("дх", "I С жр,имя,мн,рд");
			tabs.put("дц", "I С жр,имя,мн,дт");
			tabs.put("дч", "I С жр,имя,мн,вн");
			tabs.put("дш", "I С жр,имя,мн,тв");
			tabs.put("дщ", "I С жр,имя,мн,пр");
			tabs.put("дН", "I С жр,имя,0");
			tabs.put("Ра", "Q С мр,отч,ед,им,");
			tabs.put("Рб", "Q С мр,отч,ед,рд,");
			tabs.put("Рв", "Q С мр,отч,ед,дт,");
			tabs.put("Рг", "Q С мр,отч,ед,вн,");
			tabs.put("Рд", "Q С мр,отч,ед,тв,");
			tabs.put("Ре", "Q С мр,отч,ед,пр,");
			tabs.put("Рн", "Q С мр,отч,мн,им,");
			tabs.put("Ро", "Q С мр,отч,мн,рд,");
			tabs.put("Рп", "Q С мр,отч,мн,дт,");
			tabs.put("Рр", "Q С мр,отч,мн,вн,");
			tabs.put("Рс", "Q С мр,отч,мн,тв,");
			tabs.put("Рт", "Q С мр,отч,мн,пр,");
			tabs.put("Рж", "Q С жр,отч,ед,им,");
			tabs.put("Рз", "Q С жр,отч,ед,рд,");
			tabs.put("Ри", "Q С жр,отч,ед,дт,");
			tabs.put("Рк", "Q С жр,отч,ед,вн,");
			tabs.put("Рл", "Q С жр,отч,ед,тв,");
			tabs.put("Рм", "Q С жр,отч,ед,пр,");
			tabs.put("Ру", "Q С жр,отч,мн,им,");
			tabs.put("Рф", "Q С жр,отч,мн,рд,");
			tabs.put("Рх", "Q С жр,отч,мн,дт,");
			tabs.put("Рц", "Q С жр,отч,мн,вн,");
			tabs.put("Рч", "Q С жр,отч,мн,тв,");
			tabs.put("Рш", "Q С жр,отч,мн,пр,");
			tabs.put("Та", "Q С мр,отч,разг,ед,им,");
			tabs.put("Тб", "Q С мр,отч,разг,ед,рд,");
			tabs.put("Тв", "Q С мр,отч,разг,ед,дт,");
			tabs.put("Тг", "Q С мр,отч,разг,ед,вн,");
			tabs.put("Тд", "Q С мр,отч,разг,ед,тв,");
			tabs.put("Те", "Q С мр,отч,разг,ед,пр,");
			tabs.put("Тн", "Q С мр,отч,разг,мн,им,");
			tabs.put("То", "Q С мр,отч,разг,мн,рд,");
			tabs.put("Тп", "Q С мр,отч,разг,мн,дт,");
			tabs.put("Тр", "Q С мр,отч,разг,мн,вн,");
			tabs.put("Тс", "Q С мр,отч,разг,мн,тв,");
			tabs.put("Тт", "Q С мр,отч,разг,мн,пр,");
			tabs.put("Тж", "Q С жр,отч,разг,ед,им,");
			tabs.put("Тз", "Q С жр,отч,разг,ед,рд,");
			tabs.put("Ти", "Q С жр,отч,разг,ед,дт,");
			tabs.put("Тк", "Q С жр,отч,разг,ед,вн,");
			tabs.put("Тл", "Q С жр,отч,разг,ед,тв,");
			tabs.put("Тм", "Q С жр,отч,разг,ед,пр,");
			tabs.put("Ту", "Q С жр,отч,разг,мн,им,");
			tabs.put("Тф", "Q С жр,отч,разг,мн,рд,");
			tabs.put("Тх", "Q С жр,отч,разг,мн,дт,");
			tabs.put("Тц", "Q С жр,отч,разг,мн,вн,");
			tabs.put("Тч", "Q С жр,отч,разг,мн,тв,");
			tabs.put("Тш", "Q С жр,отч,разг,мн,пр,");
			tabs.put("йа", "Y П мр,ед,им,од,но");
			tabs.put("йб", "Y П мр,ед,рд,од,но");
			tabs.put("йв", "Y П мр,ед,дт,од,но");
			tabs.put("йг", "Y П мр,ед,вн,од");
			tabs.put("Рщ", "Y П мр,ед,вн,но");
			tabs.put("йд", "Y П мр,ед,тв,од,но");
			tabs.put("йе", "Y П мр,ед,пр,од,но");
			tabs.put("йж", "Y П жр,ед,им,од,но");
			tabs.put("йз", "Y П жр,ед,рд,од,но");
			tabs.put("йи", "Y П жр,ед,дт,од,но");
			tabs.put("йй", "Y П жр,ед,вн,од,но");
			tabs.put("йк", "Y П жр,ед,тв,од,но");
			tabs.put("йл", "Y П жр,ед,пр,од,но");
			tabs.put("йм", "Y П ср,ед,им,од,но");
			tabs.put("йн", "Y П ср,ед,рд,од,но");
			tabs.put("йо", "Y П ср,ед,дт,од,но");
			tabs.put("йп", "Y П ср,ед,вн,од,но");
			tabs.put("йр", "Y П ср,ед,тв,од,но");
			tabs.put("йс", "Y П ср,ед,пр,од,но");
			tabs.put("йт", "Y П мн,им,од,но");
			tabs.put("йу", "Y П мн,рд,од,но");
			tabs.put("йф", "Y П мн,дт,од,но");
			tabs.put("йх", "Y П мн,вн,од");
			tabs.put("Рь", "Y П мн,вн,но");
			tabs.put("йц", "Y П мн,тв,од,но");
			tabs.put("йч", "Y П мн,пр,од,но");
			tabs.put("йш", "Y КР_ПРИЛ мр,ед,од,но");
			tabs.put("йщ", "Y КР_ПРИЛ жр,ед,од,но");
			tabs.put("йы", "Y КР_ПРИЛ ср,ед,од,но");
			tabs.put("йэ", "Y КР_ПРИЛ мн,од,но");
			tabs.put("йю", "Y П сравн,од,но");
			tabs.put("йъ", "Y П сравн,2,од,но");
			tabs.put("йь", "Y П сравн,од,но,разг");
			tabs.put("йя", "Z П 0,од,но");
			tabs.put("иа", "Y П прев,мр,ед,им,од,но");
			tabs.put("иб", "Y П прев,мр,ед,рд,од,но");
			tabs.put("ив", "Y П прев,мр,ед,дт,од,но");
			tabs.put("иг", "Y П прев,мр,ед,вн,од");
			tabs.put("ид", "Y П прев,мр,ед,вн,но");
			tabs.put("ие", "Y П прев,мр,ед,тв,од,но");
			tabs.put("Гб", "Y П прев,мр,ед,пр,од,но");
			tabs.put("Гв", "Y П прев,жр,ед,им,од,но");
			tabs.put("Гг", "Y П прев,жр,ед,рд,од,но");
			tabs.put("Гд", "Y П прев,жр,ед,дт,од,но");
			tabs.put("Ге", "Y П прев,жр,ед,вн,од,но");
			tabs.put("Гж", "Y П прев,жр,ед,тв,од,но");
			tabs.put("Гз", "Y П прев,жр,ед,пр,од,но");
			tabs.put("ин", "Y П прев,ср,ед,им,од,но");
			tabs.put("ио", "Y П прев,ср,ед,рд,од,но");
			tabs.put("ип", "Y П прев,ср,ед,дт,од,но");
			tabs.put("ир", "Y П прев,ср,ед,вн,од,но");
			tabs.put("ис", "Y П прев,ср,ед,тв,од,но");
			tabs.put("ит", "Y П прев,ср,ед,пр,од,но");
			tabs.put("иу", "Y П прев,мн,им,од,но");
			tabs.put("иф", "Y П прев,мн,рд,од,но");
			tabs.put("их", "Y П прев,мн,дт,од,но");
			tabs.put("иц", "Y П прев,мн,вн,од");
			tabs.put("ич", "Y П прев,мн,вн,но");
			tabs.put("иш", "Y П прев,мн,тв,од,но");
			tabs.put("ищ", "Y П прев,мн,пр,од,но");
			tabs.put("нр", "a ИНФИНИТИВ безл");
			tabs.put("нс", "a Г безл,буд ");
			tabs.put("нт", "a Г безл,прш");
			tabs.put("ну", "a Г безл,нст");
			tabs.put("ка", "a ИНФИНИТИВ дст");
			tabs.put("кб", "a Г дст,нст,1л,ед");
			tabs.put("кв", "a Г дст,нст,1л,мн");
			tabs.put("кг", "a Г дст,нст,2л,ед");
			tabs.put("кд", "a Г дст,нст,2л,мн");
			tabs.put("ке", "a Г дст,нст,3л,ед");
			tabs.put("кж", "a Г дст,нст,3л,мн");
			tabs.put("кз", "a Г дст,прш,мр,ед");
			tabs.put("ки", "a Г дст,прш,жр,ед");
			tabs.put("кй", "a Г дст,прш,ср,ед");
			tabs.put("кк", "a Г дст,прш,мн");
			tabs.put("кп", "a Г дст,буд,1л,ед");
			tabs.put("кр", "a Г дст,буд,1л,мн");
			tabs.put("кс", "a Г дст,буд,2л,ед");
			tabs.put("кт", "a Г дст,буд,2л,мн");
			tabs.put("ку", "a Г дст,буд,3л,ед");
			tabs.put("кф", "a Г дст,буд,3л,мн");
			tabs.put("Ръ", "a Г дст,нст,1л,ед,разг");
			tabs.put("Ры", "a Г дст,нст,1л,мн,разг");
			tabs.put("Рэ", "a Г дст,нст,2л,ед,разг");
			tabs.put("Рю", "a Г дст,нст,2л,мн,разг");
			tabs.put("Ря", "a Г дст,нст,3л,ед,разг");
			tabs.put("кю", "a Г дст,нст,3л,мн,разг");
			tabs.put("кя", "a Г дст,прш,мн,разг");
			tabs.put("кэ", "a Г дст,буд,1л,ед,разг");
			tabs.put("Эа", "a Г дст,буд,1л,мн,разг");
			tabs.put("Эб", "a Г дст,буд,2л,ед,разг");
			tabs.put("Эв", "a Г дст,буд,2л,мн,разг");
			tabs.put("Эг", "a Г дст,буд,3л,ед,разг");
			tabs.put("Эд", "a Г дст,буд,3л,мн,разг");
			tabs.put("Эе", "a Г дст,нст,1л,ед,арх");
			tabs.put("Эж", "a Г дст,нст,1л,мн,арх");
			tabs.put("Эз", "a Г дст,нст,2л,ед,арх");
			tabs.put("Эи", "a Г дст,нст,2л,мн,арх");
			tabs.put("Эй", "a Г дст,нст,3л,ед,арх");
			tabs.put("Эк", "a Г дст,нст,3л,мн,арх");
			tabs.put("Эл", "a Г дст,прш,мн,арх");
			tabs.put("Эм", "a Г дст,буд,1л,ед,арх");
			tabs.put("Эн", "a Г дст,буд,1л,мн,арх");
			tabs.put("Эо", "a Г дст,буд,2л,ед,арх");
			tabs.put("Эп", "a Г дст,буд,2л,мн,арх");
			tabs.put("Эр", "a Г дст,буд,3л,ед,арх");
			tabs.put("Эс", "a Г дст,буд,3л,мн,арх");
			tabs.put("кн", "a ДЕЕПРИЧАСТИЕ дст,нст");
			tabs.put("ко", "a ДЕЕПРИЧАСТИЕ дст,прш");
			tabs.put("Эт", "a ДЕЕПРИЧАСТИЕ дст,нст,арх");
			tabs.put("Эу", "a ДЕЕПРИЧАСТИЕ дст,прш,арх");
			tabs.put("нп", "a Г дст,пвл,1л,мн");
			tabs.put("къ", "a Г дст,пвл,1л,ед");
			tabs.put("кл", "a Г дст,пвл,2л,ед");
			tabs.put("км", "a Г дст,пвл,2л,мн");
			tabs.put("ль", "a Г дст,пвл,2л,ед,разг");
			tabs.put("кь", "a Г дст,пвл,2л,мн,разг");
			tabs.put("Эю", "a Г дст,пвл,2л,ед,аббр ");
			tabs.put("фъ", "a Г дст,пвл,2л,ед,арх");
			tabs.put("фю", "a Г дст,пвл,2л,мн,арх");
			tabs.put("ла", "a ПРИЧАСТИЕ од,но,нст,дст,ед,мр,им");
			tabs.put("лб", "a ПРИЧАСТИЕ од,но,нст,дст,ед,мр,рд");
			tabs.put("лв", "a ПРИЧАСТИЕ од,но,нст,дст,ед,мр,дт");
			tabs.put("лг", "a ПРИЧАСТИЕ од,нст,дст,ед,мр,вн");
			tabs.put("Ла", "a ПРИЧАСТИЕ но,нст,дст,ед,мр,вн");
			tabs.put("лд", "a ПРИЧАСТИЕ од,но,нст,дст,ед,мр,тв");
			tabs.put("ле", "a ПРИЧАСТИЕ од,но,нст,дст,ед,мр,пр");
			tabs.put("лз", "a ПРИЧАСТИЕ од,но,нст,дст,ед,жр,им");
			tabs.put("ли", "a ПРИЧАСТИЕ од,но,нст,дст,ед,жр,рд");
			tabs.put("лй", "a ПРИЧАСТИЕ од,но,нст,дст,ед,жр,дт");
			tabs.put("лк", "a ПРИЧАСТИЕ од,но,нст,дст,ед,жр,вн");
			tabs.put("лл", "a ПРИЧАСТИЕ од,но,нст,дст,ед,жр,тв");
			tabs.put("лм", "a ПРИЧАСТИЕ од,но,нст,дст,ед,жр,пр");
			tabs.put("ло", "a ПРИЧАСТИЕ од,но,нст,дст,ед,ср,им");
			tabs.put("лп", "a ПРИЧАСТИЕ од,но,нст,дст,ед,ср,рд");
			tabs.put("лр", "a ПРИЧАСТИЕ од,но,нст,дст,ед,ср,дт");
			tabs.put("лс", "a ПРИЧАСТИЕ од,но,нст,дст,ед,ср,вн");
			tabs.put("лт", "a ПРИЧАСТИЕ од,но,нст,дст,ед,ср,тв");
			tabs.put("лу", "a ПРИЧАСТИЕ од,но,нст,дст,ед,ср,пр");
			tabs.put("лх", "a ПРИЧАСТИЕ од,но,нст,дст,мн,им");
			tabs.put("лц", "a ПРИЧАСТИЕ од,но,нст,дст,мн,рд");
			tabs.put("лч", "a ПРИЧАСТИЕ од,но,нст,дст,мн,дт");
			tabs.put("лш", "a ПРИЧАСТИЕ од,нст,дст,мн,вн");
			tabs.put("Лй", "a ПРИЧАСТИЕ но,нст,дст,мн,вн");
			tabs.put("лщ", "a ПРИЧАСТИЕ од,но,нст,дст,мн,тв");
			tabs.put("лы", "a ПРИЧАСТИЕ од,но,нст,дст,мн,пр");
			tabs.put("ма", "a ПРИЧАСТИЕ од,но,прш,дст,ед,мр,им");
			tabs.put("мб", "a ПРИЧАСТИЕ од,но,прш,дст,ед,мр,рд");
			tabs.put("мв", "a ПРИЧАСТИЕ од,но,прш,дст,ед,мр,дт");
			tabs.put("мг", "a ПРИЧАСТИЕ од,прш,дст,ед,мр,вн");
			tabs.put("Лб", "a ПРИЧАСТИЕ но,прш,дст,ед,мр,вн");
			tabs.put("мд", "a ПРИЧАСТИЕ од,но,прш,дст,ед,мр,тв");
			tabs.put("ме", "a ПРИЧАСТИЕ од,но,прш,дст,ед,мр,пр");
			tabs.put("мз", "a ПРИЧАСТИЕ од,но,прш,дст,ед,жр,им");
			tabs.put("ми", "a ПРИЧАСТИЕ од,но,прш,дст,ед,жр,рд");
			tabs.put("мй", "a ПРИЧАСТИЕ од,но,прш,дст,ед,жр,дт");
			tabs.put("мк", "a ПРИЧАСТИЕ од,но,прш,дст,ед,жр,вн");
			tabs.put("мл", "a ПРИЧАСТИЕ од,но,прш,дст,ед,жр,тв");
			tabs.put("мм", "a ПРИЧАСТИЕ од,но,прш,дст,ед,жр,пр");
			tabs.put("мо", "a ПРИЧАСТИЕ од,но,прш,дст,ед,ср,им");
			tabs.put("мп", "a ПРИЧАСТИЕ од,но,прш,дст,ед,ср,рд");
			tabs.put("мр", "a ПРИЧАСТИЕ од,но,прш,дст,ед,ср,дт");
			tabs.put("мс", "a ПРИЧАСТИЕ од,но,прш,дст,ед,ср,вн");
			tabs.put("мт", "a ПРИЧАСТИЕ од,но,прш,дст,ед,ср,тв");
			tabs.put("му", "a ПРИЧАСТИЕ од,но,прш,дст,ед,ср,пр");
			tabs.put("мх", "a ПРИЧАСТИЕ од,но,прш,дст,мн,им");
			tabs.put("мц", "a ПРИЧАСТИЕ од,но,прш,дст,мн,рд");
			tabs.put("мч", "a ПРИЧАСТИЕ од,но,прш,дст,мн,дт");
			tabs.put("мш", "a ПРИЧАСТИЕ од,прш,дст,мн,вн");
			tabs.put("Лк", "a ПРИЧАСТИЕ но,прш,дст,мн,вн");
			tabs.put("мщ", "a ПРИЧАСТИЕ од,но,прш,дст,мн,тв");
			tabs.put("мы", "a ПРИЧАСТИЕ од,но,прш,дст,мн,пр");
			tabs.put("па", "b ПРИЧАСТИЕ од,но,нст,стр,ед,мр,им");
			tabs.put("пб", "b ПРИЧАСТИЕ од,но,нст,стр,ед,мр,рд");
			tabs.put("пв", "b ПРИЧАСТИЕ од,но,нст,стр,ед,мр,дт");
			tabs.put("пг", "b ПРИЧАСТИЕ од,нст,стр,ед,мр,вн");
			tabs.put("Лг", "b ПРИЧАСТИЕ но,нст,стр,ед,мр,вн");
			tabs.put("пд", "b ПРИЧАСТИЕ од,но,нст,стр,ед,мр,тв");
			tabs.put("пе", "b ПРИЧАСТИЕ од,но,нст,стр,ед,мр,пр");
			tabs.put("пж", "b КР_ПРИЧАСТИЕ од,но,нст,стр,ед,мр");
			tabs.put("пз", "b ПРИЧАСТИЕ од,но,нст,стр,ед,жр,им");
			tabs.put("пи", "b ПРИЧАСТИЕ од,но,нст,стр,ед,жр,рд");
			tabs.put("пй", "b ПРИЧАСТИЕ од,но,нст,стр,ед,жр,дт");
			tabs.put("пк", "b ПРИЧАСТИЕ од,но,нст,стр,ед,жр,вн");
			tabs.put("пл", "b ПРИЧАСТИЕ од,но,нст,стр,ед,жр,тв");
			tabs.put("пм", "b ПРИЧАСТИЕ од,но,нст,стр,ед,жр,пр");
			tabs.put("пн", "b КР_ПРИЧАСТИЕ од,но,нст,стр,ед,жр");
			tabs.put("по", "b ПРИЧАСТИЕ од,но,нст,стр,ед,ср,им");
			tabs.put("пп", "b ПРИЧАСТИЕ од,но,нст,стр,ед,ср,рд");
			tabs.put("пр", "b ПРИЧАСТИЕ од,но,нст,стр,ед,ср,дт");
			tabs.put("пс", "b ПРИЧАСТИЕ од,но,нст,стр,ед,ср,вн");
			tabs.put("пт", "b ПРИЧАСТИЕ од,но,нст,стр,ед,ср,тв");
			tabs.put("пу", "b ПРИЧАСТИЕ од,но,нст,стр,ед,ср,пр");
			tabs.put("пф", "b КР_ПРИЧАСТИЕ од,но,нст,стр,ед,ср");
			tabs.put("пх", "b ПРИЧАСТИЕ од,но,нст,стр,мн,им");
			tabs.put("пц", "b ПРИЧАСТИЕ од,но,нст,стр,мн,рд");
			tabs.put("пч", "b ПРИЧАСТИЕ од,но,нст,стр,мн,дт");
			tabs.put("пш", "b ПРИЧАСТИЕ од,нст,стр,мн,вн");
			tabs.put("Лм", "b ПРИЧАСТИЕ но,нст,стр,мн,вн");
			tabs.put("пщ", "b ПРИЧАСТИЕ од,но,нст,стр,мн,тв");
			tabs.put("пы", "b ПРИЧАСТИЕ од,но,нст,стр,мн,пр");
			tabs.put("пэ", "b КР_ПРИЧАСТИЕ од,но,нст,стр,мн");
			tabs.put("са", "b ПРИЧАСТИЕ од,но,прш,стр,ед,мр,им");
			tabs.put("сб", "b ПРИЧАСТИЕ од,но,прш,стр,ед,мр,рд");
			tabs.put("св", "b ПРИЧАСТИЕ од,но,прш,стр,ед,мр,дт");
			tabs.put("сг", "b ПРИЧАСТИЕ од,прш,стр,ед,мр,вн");
			tabs.put("Ле", "b ПРИЧАСТИЕ но,прш,стр,ед,мр,вн");
			tabs.put("сд", "b ПРИЧАСТИЕ од,но,прш,стр,ед,мр,тв");
			tabs.put("се", "b ПРИЧАСТИЕ од,но,прш,стр,ед,мр,пр");
			tabs.put("сж", "b КР_ПРИЧАСТИЕ од,но,прш,стр,ед,мр");
			tabs.put("сз", "b ПРИЧАСТИЕ од,но,прш,стр,ед,жр,им");
			tabs.put("си", "b ПРИЧАСТИЕ од,но,прш,стр,ед,жр,рд");
			tabs.put("сй", "b ПРИЧАСТИЕ од,но,прш,стр,ед,жр,дт");
			tabs.put("ск", "b ПРИЧАСТИЕ од,но,прш,стр,ед,жр,вн");
			tabs.put("сл", "b ПРИЧАСТИЕ од,но,прш,стр,ед,жр,тв");
			tabs.put("см", "b ПРИЧАСТИЕ од,но,прш,стр,ед,жр,пр");
			tabs.put("сн", "b КР_ПРИЧАСТИЕ од,но,прш,стр,ед,жр");
			tabs.put("со", "b ПРИЧАСТИЕ од,но,прш,стр,ед,ср,им");
			tabs.put("сп", "b ПРИЧАСТИЕ од,но,прш,стр,ед,ср,рд");
			tabs.put("ср", "b ПРИЧАСТИЕ од,но,прш,стр,ед,ср,дт");
			tabs.put("сс", "b ПРИЧАСТИЕ од,но,прш,стр,ед,ср,вн");
			tabs.put("ст", "b ПРИЧАСТИЕ од,но,прш,стр,ед,ср,тв");
			tabs.put("су", "b ПРИЧАСТИЕ од,но,прш,стр,ед,ср,пр");
			tabs.put("сф", "b КР_ПРИЧАСТИЕ од,но,прш,стр,ед,ср");
			tabs.put("сх", "b ПРИЧАСТИЕ од,но,прш,стр,мн,им");
			tabs.put("сц", "b ПРИЧАСТИЕ од,но,прш,стр,мн,рд");
			tabs.put("сч", "b ПРИЧАСТИЕ од,но,прш,стр,мн,дт");
			tabs.put("сш", "b ПРИЧАСТИЕ од,прш,стр,мн,вн");
			tabs.put("Ло", "b ПРИЧАСТИЕ но,прш,стр,мн,вн");
			tabs.put("сщ", "b ПРИЧАСТИЕ од,но,прш,стр,мн,тв");
			tabs.put("сы", "b ПРИЧАСТИЕ од,но,прш,стр,мн,пр");
			tabs.put("сэ", "b КР_ПРИЧАСТИЕ од,но,прш,стр,мн");
			tabs.put("ча", "e МС 1л,ед,им");
			tabs.put("чб", "e МС 1л,ед,рд");
			tabs.put("чв", "e МС 1л,ед,дт");
			tabs.put("чг", "e МС 1л,ед,вн");
			tabs.put("чд", "e МС 1л,ед,тв");
			tabs.put("че", "e МС 1л,ед,пр");
			tabs.put("чж", "e МС 1л,мн,им");
			tabs.put("чз", "e МС 1л,мн,рд");
			tabs.put("чи", "e МС 1л,мн,дт");
			tabs.put("чй", "e МС 1л,мн,вн");
			tabs.put("чк", "e МС 1л,мн,тв");
			tabs.put("чл", "e МС 1л,мн,пр");
			tabs.put("чм", "e МС 2л,ед,им");
			tabs.put("чн", "e МС 2л,ед,рд");
			tabs.put("чо", "e МС 2л,ед,дт");
			tabs.put("чп", "e МС 2л,ед,вн");
			tabs.put("чр", "e МС 2л,ед,тв");
			tabs.put("чс", "e МС 2л,ед,пр");
			tabs.put("чт", "e МС 2л,мн,им");
			tabs.put("чу", "e МС 2л,мн,рд");
			tabs.put("чф", "e МС 2л,мн,дт");
			tabs.put("чх", "e МС 2л,мн,вн");
			tabs.put("чц", "e МС 2л,мн,тв");
			tabs.put("чч", "e МС 2л,мн,пр");
			tabs.put("ша", "e МС 3л,мр,ед,им");
			tabs.put("шб", "e МС 3л,мр,ед,рд");
			tabs.put("шв", "e МС 3л,мр,ед,дт");
			tabs.put("шг", "e МС 3л,мр,ед,вн");
			tabs.put("шд", "e МС 3л,мр,ед,тв");
			tabs.put("ше", "e МС 3л,мр,ед,пр");
			tabs.put("шж", "e МС 3л,жр,ед,им");
			tabs.put("шз", "e МС 3л,жр,ед,рд");
			tabs.put("ши", "e МС 3л,жр,ед,дт");
			tabs.put("шй", "e МС 3л,жр,ед,вн");
			tabs.put("шк", "e МС 3л,жр,ед,тв");
			tabs.put("шл", "e МС 3л,жр,ед,пр");
			tabs.put("шм", "e МС 3л,ср,ед,им");
			tabs.put("шн", "e МС 3л,ср,ед,рд");
			tabs.put("шо", "e МС 3л,ср,ед,дт");
			tabs.put("шп", "e МС 3л,ср,ед,вн");
			tabs.put("шр", "e МС 3л,ср,ед,тв");
			tabs.put("шс", "e МС 3л,ср,ед,пр");
			tabs.put("шт", "e МС 3л,мн,им");
			tabs.put("шу", "e МС 3л,мн,рд");
			tabs.put("шф", "e МС 3л,мн,дт");
			tabs.put("шх", "e МС 3л,мн,вн");
			tabs.put("шц", "e МС 3л,мн,тв");
			tabs.put("шч", "e МС 3л,мн,пр");
			tabs.put("ща", "e МС мр,ед,им");
			tabs.put("щб", "e МС мр,ед,рд");
			tabs.put("щв", "e МС мр,ед,дт");
			tabs.put("щг", "e МС мр,ед,вн");
			tabs.put("щд", "e МС мр,ед,тв");
			tabs.put("ще", "e МС мр,ед,пр");
			tabs.put("щж", "e МС жр,ед,им");
			tabs.put("щз", "e МС жр,ед,рд");
			tabs.put("щи", "e МС жр,ед,дт");
			tabs.put("щй", "e МС жр,ед,вн");
			tabs.put("щк", "e МС жр,ед,тв");
			tabs.put("щл", "e МС жр,ед,пр");
			tabs.put("щм", "e МС ср,ед,им");
			tabs.put("щн", "e МС ср,ед,рд");
			tabs.put("що", "e МС ср,ед,дт");
			tabs.put("щп", "e МС ср,ед,вн");
			tabs.put("щр", "e МС ср,ед,тв");
			tabs.put("щс", "e МС ср,ед,пр");
			tabs.put("щт", "e МС мн,им");
			tabs.put("щу", "e МС мн,рд");
			tabs.put("щф", "e МС мн,дт");
			tabs.put("щх", "e МС мн,вн");
			tabs.put("щц", "e МС мн,тв");
			tabs.put("щч", "e МС мн,пр");
			tabs.put("щщ", "e МС рд");
			tabs.put("щы", "e МС дт");
			tabs.put("щэ", "e МС вн");
			tabs.put("щю", "e МС тв");
			tabs.put("щя", "e МС пр");
			tabs.put("ыа", "f МС-П мр,ед,им,од,но");
			tabs.put("ыб", "f МС-П мр,ед,рд,од,но");
			tabs.put("ыв", "f МС-П мр,ед,дт,од,но");
			tabs.put("ыг", "f МС-П мр,ед,вн,но");
			tabs.put("Лф", "f МС-П мр,ед,вн,од");
			tabs.put("ыд", "f МС-П мр,ед,тв,од,но");
			tabs.put("ые", "f МС-П мр,ед,пр,од,но");
			tabs.put("ыж", "f МС-П жр,ед,им,од,но");
			tabs.put("ыз", "f МС-П жр,ед,рд,од,но");
			tabs.put("ыи", "f МС-П жр,ед,дт,од,но");
			tabs.put("ый", "f МС-П жр,ед,вн,од,но");
			tabs.put("ык", "f МС-П жр,ед,тв,од,но");
			tabs.put("ыл", "f МС-П жр,ед,пр,од,но");
			tabs.put("ым", "f МС-П ср,ед,им,од,но");
			tabs.put("ын", "f МС-П ср,ед,рд,од,но");
			tabs.put("ыо", "f МС-П ср,ед,дт,од,но");
			tabs.put("ып", "f МС-П ср,ед,вн,од,но");
			tabs.put("ыр", "f МС-П ср,ед,тв,од,но");
			tabs.put("ыс", "f МС-П ср,ед,пр,од,но");
			tabs.put("ыт", "f МС-П мн,им,од,но");
			tabs.put("ыу", "f МС-П мн,рд,од,но");
			tabs.put("ыф", "f МС-П мн,дт,од,но");
			tabs.put("ых", "f МС-П мн,вн,но");
			tabs.put("Лх", "f МС-П мн,вн,од");
			tabs.put("ыц", "f МС-П мн,тв,од,но");
			tabs.put("ыч", "f МС-П мн,пр,од,но");
			tabs.put("ыш", "f МС-П 0,од,но");
			tabs.put("ыщ", "g МС-ПРЕДК ед,рд");
			tabs.put("ыы", "g МС-ПРЕДК ед,дт");
			tabs.put("ыэ", "g МС-ПРЕДК ед,вн");
			tabs.put("ыю", "g МС-ПРЕДК ед,тв");
			tabs.put("ыь", "g МС-ПРЕДК ед,пр");
			tabs.put("ыя", "g МС-ПРЕДК");
			tabs.put("эа", "h ЧИСЛ им");
			tabs.put("эб", "h ЧИСЛ рд");
			tabs.put("эв", "h ЧИСЛ дт");
			tabs.put("эг", "h ЧИСЛ вн");
			tabs.put("эд", "h ЧИСЛ тв");
			tabs.put("эе", "h ЧИСЛ пр");
			tabs.put("Ца", "h ЧИСЛ им,арх");
			tabs.put("Цб", "h ЧИСЛ рд,арх");
			tabs.put("Цв", "h ЧИСЛ дт,арх");
			tabs.put("Цг", "h ЧИСЛ вн,арх");
			tabs.put("Цд", "h ЧИСЛ тв,арх");
			tabs.put("Це", "h ЧИСЛ пр,арх");
			tabs.put("эж", "h ЧИСЛ мр,им");
			tabs.put("эз", "h ЧИСЛ мр,рд");
			tabs.put("эи", "h ЧИСЛ мр,дт");
			tabs.put("эй", "h ЧИСЛ мр,вн");
			tabs.put("эк", "h ЧИСЛ мр,тв");
			tabs.put("эл", "h ЧИСЛ мр,пр");
			tabs.put("эм", "h ЧИСЛ жр,им");
			tabs.put("эн", "h ЧИСЛ жр,рд");
			tabs.put("эо", "h ЧИСЛ жр,дт");
			tabs.put("эп", "h ЧИСЛ жр,вн");
			tabs.put("эр", "h ЧИСЛ жр,тв");
			tabs.put("эс", "h ЧИСЛ жр,пр");
			tabs.put("эт", "h ЧИСЛ ср,им");
			tabs.put("эу", "h ЧИСЛ ср,рд");
			tabs.put("эф", "h ЧИСЛ ср,дт");
			tabs.put("эх", "h ЧИСЛ ср,вн");
			tabs.put("эц", "h ЧИСЛ ср,тв");
			tabs.put("эч", "h ЧИСЛ ср,пр");
			tabs.put("эш", "h ЧИСЛ сравн");
			tabs.put("юа", "i ЧИСЛ-П мр,ед,им,од,но");
			tabs.put("юб", "i ЧИСЛ-П мр,ед,рд,од,но");
			tabs.put("юв", "i ЧИСЛ-П мр,ед,дт,од,но");
			tabs.put("юг", "i ЧИСЛ-П мр,ед,вн,но");
			tabs.put("Лт", "i ЧИСЛ-П мр,ед,вн,од");
			tabs.put("юд", "i ЧИСЛ-П мр,ед,тв,од,но");
			tabs.put("юе", "i ЧИСЛ-П мр,ед,пр,од,но");
			tabs.put("юж", "i ЧИСЛ-П жр,ед,им,од,но");
			tabs.put("юз", "i ЧИСЛ-П жр,ед,рд,од,но");
			tabs.put("юи", "i ЧИСЛ-П жр,ед,дт,од,но");
			tabs.put("юй", "i ЧИСЛ-П жр,ед,вн,од,но");
			tabs.put("юк", "i ЧИСЛ-П жр,ед,тв,од,но");
			tabs.put("юл", "i ЧИСЛ-П жр,ед,пр,од,но");
			tabs.put("юм", "i ЧИСЛ-П ср,ед,им,од,но");
			tabs.put("юн", "i ЧИСЛ-П ср,ед,рд,од,но");
			tabs.put("юо", "i ЧИСЛ-П ср,ед,дт,од,но");
			tabs.put("юп", "i ЧИСЛ-П ср,ед,вн,од,но");
			tabs.put("юр", "i ЧИСЛ-П ср,ед,тв,од,но");
			tabs.put("юс", "i ЧИСЛ-П ср,ед,пр,од,но");
			tabs.put("ют", "i ЧИСЛ-П мн,им,од,но");
			tabs.put("юу", "i ЧИСЛ-П мн,рд,од,но");
			tabs.put("юф", "i ЧИСЛ-П мн,дт,од,но");
			tabs.put("юх", "i ЧИСЛ-П мн,вн,но");
			tabs.put("Лу", "i ЧИСЛ-П мн,вн,од");
			tabs.put("юц", "i ЧИСЛ-П мн,тв,од,но");
			tabs.put("юч", "i ЧИСЛ-П мн,пр,од,но");
			tabs.put("ющ", "i ЧИСЛ-П рд,од,но");
			tabs.put("яа", "j Н");
			tabs.put("ян", "j Н вопр");
			tabs.put("яо", "j Н указат");
			tabs.put("яп", "j Н разг");
			tabs.put("яб", "k ПРЕДК нст");
			tabs.put("як", "k ПРЕДК прш");
			tabs.put("ял", "k ПРЕДК ");
			tabs.put("яр", "k ПРЕДК сравн,нст");
			tabs.put("ям", "k ПРЕДК 0");
			tabs.put("яв", "l ПРЕДЛ");
			tabs.put("яг", "m ПОСЛ");
			tabs.put("яд", "n СОЮЗ");
			tabs.put("яе", "o МЕЖД");
			tabs.put("яё", "o МЕЖД разг");
			tabs.put("яж", "p ЧАСТ");
			tabs.put("яз", "q ВВОДН");
			tabs.put("яй", "s ФРАЗ");
			tabs.put("Пп", "b Г стр,буд,1л,ед");
			tabs.put("Пр", "b Г стр,буд,1л,мн");
			tabs.put("Пс", "b Г стр,буд,2л,ед");
			tabs.put("Пт", "b Г стр,буд,2л,мн");
			tabs.put("Пу", "b Г стр,буд,3л,ед");
			tabs.put("Пф", "b Г стр,буд,3л,мн");
			tabs.put("Уа", "a * лок");
			tabs.put("Уе", "a * кач");
			tabs.put("Уж", "a * дфст");
			tabs.put("Уз", "a * дфст,орг");
			tabs.put("Уи", "a * дфст,лок");
			tabs.put("Ул", "a * св,пе");
			tabs.put("Ум", "a * св,нп");
			tabs.put("Ун", "a * нс,пе");
			tabs.put("Уо", "a * нс,нп");
			tabs.put("Уп", "a * св,нс,пе");
			tabs.put("Ур", "a * св,нс,нп");
			tabs.put("Ус", "a * нс");
			tabs.put("Ут", "a * св");
			tabs.put("Уф", "a * жарг");
			tabs.put("Ух", "a * опч");
			tabs.put("Уч", "a * жарг,опч");
			tabs.put("Уц", "a * орг,жарг");
			tabs.put("Уш", "a * лок,жарг");
			tabs.put("Ущ", "a * но,лок");
			tabs.put("Уь", "a * но,орг");
			tabs.put("Уы", "a * од,фам");
			tabs.put("Уъ", "a * но,дфст,лок");
			tabs.put("Уэ", "a * но,дфст,орг");
			tabs.put("Ую", "a * но,жарг");
			tabs.put("Уя", "a * но,опч,");
			tabs.put("Фа", "a * но,");
			tabs.put("Фб", "a * од,");
			tabs.put("Фв", "a * орг,жарг,но");
			tabs.put("Фг", "a * дфст,но");
			tabs.put("Фд", "a * дфст,од");
			tabs.put("Фж", "a * од,жарг");
			tabs.put("Фз", "a * имя,притяж");
			tabs.put("Фи", "a * притяж");
			tabs.put("Фк", "a * св,пе,разг");
			tabs.put("Фл", "a * св,нп,разг");
			tabs.put("Фн", "a * нс,пе,разг");
			tabs.put("Фо", "a * нс,нп,разг");
			tabs.put("Фп", "a * но,разг");
			tabs.put("Фр", "a * од,разг");
			tabs.put("Фс", "a * св,пе,жарг");
			tabs.put("Фт", "a * св,нп,жарг");
			tabs.put("Фу", "a * нс,пе,жарг");
			tabs.put("Фф", "a * нс,нп,жарг");
			tabs.put("Фх", "a * разг");
			tabs.put("Фц", "a * арх");
			tabs.put("Фч", "a * св,пе,арх");
			tabs.put("Фш", "a * св,нп,арх");
			tabs.put("Фщ", "a * нс,пе,арх");
			tabs.put("Фь", "a * нс,нп,арх");
			tabs.put("Фы", "a * но,арх");
			tabs.put("Фъ", "a * од,арх");
			tabs.put("Фэ", "a * нс,арх");
			tabs.put("Фю", "a * св,арх");
			tabs.put("Фя", "a * кач,арх");
			tabs.put("Фё", "a * но,од");
			tabs.put("Ха", "a * од,опч,");
			tabs.put("Хб", "a * лок,опч,");
			tabs.put("яю", "F С мр,жр,ср,ед,им,рд,дт,вн,тв,пр");
			tabs.put("яя", "F С мр,жр,ср,,ед,мн,им,рд,дт,вн,тв,пр");
			tabDescriptors = unmodifiableMap(tabs);
		}
	}

	private final Set<String> allPrefixes = new HashSet<>();
	private final Set<String> allEndings = new HashSet<>();
	private final LemmaRepository lemmaRepository;

	private MapDictionary() throws IOException {
		this.lemmaRepository = new LemmaRepository();

		InputStream is = getClass().getResourceAsStream("/mrd");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8));

		int sectionLength = parseInt(reader.readLine());
		List<List<Flexion>> paradigms = new ArrayList<>(sectionLength);
		List<Set<String>> paradigmEndings = new ArrayList<>(sectionLength);
		Map<Integer, Set<String>> paradigmPrefixes = new HashMap<>();
		List<String> paradigmAncodes = new ArrayList<>(sectionLength);

		for (int paradigm = 0; paradigm < sectionLength; paradigm++) {
			int paradigmSize = parseInt(reader.readLine());
			paradigmAncodes.add(paradigm, reader.readLine());

			Set<String> prefixes = new HashSet<>();
			Set<String> endings = new HashSet<>();
			List<Flexion> flexions = new ArrayList<>(paradigmSize);

			for (int i = 0; i < paradigmSize; i++) {
				String flexion = reader.readLine();
				String ancode = reader.readLine();

				String affix;
				String prefix;
				if (!flexion.isEmpty() && flexion.charAt(0) == '*') {
					String[] parts = flexion.split("\\*", 3);
					prefix = parts[1];
					affix = parts[2];
				} else {
					affix = flexion;
					prefix = "";
				}
				flexions.add(new Flexion(affix, prefix, tabDescriptors.get(ancode)));
				if (!prefix.isEmpty()) {
					prefixes.add(prefix);
				}
				endings.add(affix);
			}

			if (!prefixes.isEmpty()) {
				paradigmPrefixes.put(paradigm, prefixes);
				allPrefixes.addAll(prefixes);
			}
			allEndings.addAll(endings);
			paradigmEndings.add(paradigm, endings);
			paradigms.add(flexions);
		}

		sectionLength = parseInt(reader.readLine());
		for (int i = 0; i < sectionLength; i++) {
			int paradigm = parseInt(reader.readLine());
			int paradigmSize = parseInt(reader.readLine());

			List<Flexion> flexions = paradigms.get(paradigm);
			Set<String> endings = paradigmEndings.get(paradigm);
			Set<String> prefixes = paradigmPrefixes.getOrDefault(paradigm, new HashSet<>(singleton("")));
			for (int j = 0; j < paradigmSize; j++) {
				String base = reader.readLine();
				base = base.equals("#") ? "" : base;
				Lemma l = new Lemma(base, flexions, paradigmAncodes.get(paradigm));
				l.setEndings(endings);
				l.setPrefixes(prefixes);
				lemmaRepository.insert(l);
			}
		}
	}

	public static MapDictionary loadDictionary() {
		try {
			return new MapDictionary();
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public Set<Lemma> lookupWord(String word) {
		String lowercaseWord = word.toLowerCase().replace('ё', 'е');

		return IntStream.range(0, word.length())
			.boxed()
			.filter(i -> i == 0 || allPrefixes.contains(lowercaseWord.substring(0, i)))
			.map(i -> lookupWithoutPrefix(lowercaseWord.substring(0, i), lowercaseWord.substring(i, word.length())))
			.flatMap(Collection::stream)
			.collect(toSet());

	}

	private Set<Lemma> lookupWithoutPrefix(String preffix, String sufix) {
		Map<String, String> prefixesPostfixes = rangeClosed(0, sufix.length())
			.boxed()
			.filter(i -> allEndings.contains(sufix.substring(i, sufix.length())))
			.collect(Collectors.toMap(
				index -> sufix.substring(0, index),
				index -> sufix.substring(index, sufix.length())));

		Set<Lemma> byBaseIn = lemmaRepository.findByBaseIn(prefixesPostfixes.keySet());
		return byBaseIn.stream()
			.filter(l -> (isNullOrEmpty(preffix) || l.getPrefixes().contains(preffix)) &&
				l.getEndings().contains(prefixesPostfixes.get(l.getBase())))
			.collect(toSet());
	}
}
