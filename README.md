# aot-lematizer
aot-lematizer

Библиотека принимает на вход слово и выдает:
1) Все возможные леммы этого слова.  
2) Вдобавок каждой лемме идёт набор грамматической информации, который может соответствовать этому слову.  
Например:  
дорогой -> {
            (исходная лемма: дорога, часть речи: существительное (кто?), падеж: творительный(чем?), единственное число),
            (исходная лемма: дорогой, часть речи: прилагательное (какой?), падеж: именительный(она какая?), единственное число)
        }
