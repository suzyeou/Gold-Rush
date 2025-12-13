# Gold Rush game server

The project consists of four modules:

- **game_client** — client application.
  Designed as a thin client responsible for handling player entities and their behavior, rendering the game board,
  and performing basic client-side actions.

- **game_connector_lib** — a client-side library.
  Provides communication between the client application and the game server

- **game_common** — shared module.
  Contains common classes and definitions for commands exchanged between
  the client and the server

- **game_server** — server application.
  Handles game sessions, player connections, and command processing


## Folders with nice stuff

- **bin** — contains binaries; currently includes a single file: the server application (see below)
- **lib** — compiled libraries (JAR files)
- **public** — documentation covering all three subprojects; published at https://gold-rush-srv-930089.gitlab.io/


# Library and server app versions

- **1.2.6** — simple server test with a “thumbs-up” board; board square labels represented as characters
- **1.2.10** — full board implementation in the server app; board square labels represented as strings


# How to run the server?
In order to run the server, you need the server application binaries.

You can **build them yourself** by running the `shadowJar` Gradle task
from the root directory of the main project _gold_rush_srv_:
```
$ ./gradlew shadowJar

or

> gradlew.bat shadowJar
```

Alternatively, you can use the **ready-to-run binary** available in the repository’s _bin/_ folder.

Once you have the binary, run the server with:
```
java -jar game_server-1.x.y-all.jar
```
