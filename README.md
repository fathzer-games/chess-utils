[![Maven Central](https://img.shields.io/maven-central/v/com.fathzer/chess-utils)](https://central.sonatype.com/artifact/com.fathzer/chess-utils)
[![License](https://img.shields.io/badge/license-Apache%202.0-brightgreen.svg)](https://github.com/fathzer-games/chess-utils/blob/master/LICENSE)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fathzer_chess-utils&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fathzer-games_chess-utils)
[![javadoc](https://javadoc.io/badge2/com.fathzer/chess-utils/javadoc.svg)](https://javadoc.io/doc/com.fathzer/chess-utils)

# chess-utils

Some general utilities (Move comparator, basic evaluators, etc...) to implement a Java chess engine.

To implement a chess engine, you basically need the following:
- A move generator.  
You can find a lot on the Internet (example: [chesslib](https://github.com/bhlangonijr/chesslib)).

- An implementation of tree search algorithm like Negamax.  
[games-core](https://github.com/fathzer-games/games-core) provides you with a generic game engine library. As it is not dedicated to a particular game, it does not contain any things like evaluation functions.

- An evaluation function, an *a priori* comparator of moves (usually, move generators does not sort moves, but it improves a lot tree search), and some other little things (example: An estimation of how many moves it remains before the end game, to implement a time management strategy).  
**This library provides you with some of these building blocks**.

- Optionally, you will need to implement a communication protocol between your engine and a chess user interface.  
the [jchess-uci library](https://en.wikipedia.org/wiki/Universal_Chess_Interface) will help you to implement [UCI communication protocol]().
