## aot-lematizer -- Лемматизатор

### Краткое описание.
Библиотека принимает на вход слово и выдает:
* Все возможные леммы этого слова.
* Вдобавок каждой лемме идёт набор грамматической информации,  
   который может соответствовать этому слову.  
   
Например, рассмотрим слово "дорогой". Есть два варианта:
* *исходная лемма*: дорога,  
  *часть речи*: существительное (кто?),  
  *падеж*: творительный(чем?),  
  *единственное число*
* *исходная лемма*: дорогая,  
  *часть речи*: прилагательное (какой?)  
  *падеж*: дательный (кому?) или предложный (о ком?) или творительный (кем?)  
  *единственное число*

### Пример использования в коде.

```
var flexionStorage = new FlexionStorage();
var flexions = flexionStorage.get("дорога");
for(var flexion: flexions) {

  // исходная лемма из которой было образовано слово
  var sourceLemmaString = flexion.lemma;

  // массив грамматической информации слова
  // (если применить ее к лемме, 
  //  по правилам русского языка получается искомое слово "дорога")
  var grammarInfoArray = flexion.grammarInfo;
  
  // выводим на экран лемму
  System.out.println(lemma);
  // делаем разные действия 
  // в зависимости от грамматических характеристик слова
  for(var inf: grammarInfoArray) {
    case GrammarInfo.Noun:
      // *** //
    case GrammarInfo.ShortAdjective:
      // *** //
    default: 
      // *** //
  }
  
}
```

### Cкорость работы лемматизатора. Микробенчмарк:

Benchmark                                             Mode  Cnt     Score    Error   Units
LookupBenchmark.lookup                               thrpt   10  1565,426 ±  7,470  ops/ms
LookupBenchmark.lookup:·gc.alloc.rate                thrpt   10   457,435 ±  2,063  MB/sec
LookupBenchmark.lookup:·gc.alloc.rate.norm           thrpt   10   357,823 ±  0,004    B/op
LookupBenchmark.lookup:·gc.churn.G1_Eden_Space       thrpt   10   458,436 ± 26,284  MB/sec
LookupBenchmark.lookup:·gc.churn.G1_Eden_Space.norm  thrpt   10   358,619 ± 21,093    B/op
LookupBenchmark.lookup:·gc.churn.G1_Old_Gen          thrpt   10     0,001 ±  0,001  MB/sec
LookupBenchmark.lookup:·gc.churn.G1_Old_Gen.norm     thrpt   10     0,001 ±  0,001    B/op
LookupBenchmark.lookup:·gc.count                     thrpt   10   136,000           counts
LookupBenchmark.lookup:·gc.time                      thrpt   10   171,000               ms


### Полезные ссылки:
* [Описание исходного формата словаря, из которого взяты слова](http://phpmorphy.sourceforge.net/dokuwiki/manual-graminfo)  
* [Аббревиатуры, использованые в перечислении GrammarInfo](https://sourceforge.net/p/seman/svn/HEAD/tree/trunk/Docs/Morph_UNIX.txt)
* [Консольное приложение использующее aot-lemmatizer](https://github.com/demidko/aot-lematizer/blob/master/testapp/src/main/java/com/farpost/aot/testapp/TestApplication.java)
 
