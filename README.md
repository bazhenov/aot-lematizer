## aot-lematizer -- Библиотека морфологического анализа на Java

### Решаемые задачи:
* Лемматизация (получение нормальной формы слова)
* Получение грамматической информации для слова (часть речи, падеж, спряжение и т.д.)

### Краткое описание:
Библиотека принимает на вход форму слова и возвращает набор пар вида:  
* Лемма (нормальная форма слова)
* Грамматическая информация о переданной форме слова, в соответствии с леммой

Например, рассмотрим слово "дорогой". Есть (как минимум) два варианта:
* *исходная лемма*: дорога,  
  *часть речи*: существительное (кто?),  
  *падеж*: творительный(чем?),  
  *единственное число*
* *исходная лемма*: дорогая,  
  *часть речи*: прилагательное (какой?)  
  *падеж*: дательный (кому?) или предложный (о ком?) или творительный (кем?)  
  *единственное число*

### Пример использования в коде:

```
// Инициализируем хранилище информации
var flexionStorage = new FlexionStorage();

// Запрашиваем информацию по слову "дорогой"
var flexions = flexionStorage.get("дорогой");

// Обходим все варианты, чем может являться это слово
for(var flexion: flexions) {

  // исходное слово, от которого образовано слово "дорогой" (лемма)
  var sourceLemmaString = flexion.lemma;

  // Набор всей грамматической информации о слове
  // (если применить ее к лемме,
  //  по правилам русского языка получается слово "дорогой")
  var grammarInfoArray = flexion.allGrammarInfo;
  
  // делаем разные действия 
  // в зависимости от грамматических характеристик слова

  if(flexion.is(GrammarInfo.Noun) {
    // *** //
  }

  if(flexion.isNot(GrammarInfo.ShortAdjective) {
      // *** //
  }

  // *** //
}
```

### Особенности реализации:
* Вввиду частого взаимозаменяемого использования в речи,  
  буква ё рассматривается наравне с буквой е.
* Для некоторых словоформ возвращаются дублирующиеся пары.  
  Например, результат для слова "замок":  
  ```
  <замок, [С, мр, ед, им], [С, мр, ед, вн]>
  <замок, [С, мр, ед, им], [С, мр, ед, вн]>
  ```
  Это означает что в выдаче присутствуют две разные, но сопадающие по написанию леммы.  
  В данном случае это зАмок (строение) и замОк (устройство для запирания дверей).

### Полезные ссылки:
* [Аббревиатуры использованые в перечислении GrammarInfo](http://phpmorphy.sourceforge.net/dokuwiki/manual-graminfo)  
* [Описание исходного формата словаря](https://sourceforge.net/p/seman/svn/HEAD/tree/trunk/Docs/Morph_UNIX.txt)
* [Консольное приложение использующее aot-lemmatizer](https://github.com/demidko/aot-lematizer/blob/master/testapp/src/main/java/com/farpost/aot/TestApplication.java)

### TODO:

1. Изменить выдачу следующим образом (пример для слова замок):  
   <замок, [С, мр, ед, им], [С, мр, ед, вн]>  
   <замок, [С, мр, ед, им], [С, мр, ед, вн]>  
   <замокнуть, [Г, дст, прш, мр, ед]>  
   
2. Новая функция:  
   На вход подается лемма, на выходе все словоформы с характеристиками.
   
3. Согласовать процесс компиляции:  
   редактируемая база данных -> mrd формат -> бинарный формат.

 
