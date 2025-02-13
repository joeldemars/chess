# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## Server Design (phase 2)

[Sequence diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLlqOJpEuGFocjA3K8gagrCjAQGhqRbqdph5QGjAABmvicI6zoEjhLLlJ63pBgGQbGjAjFMsxkYUdGMCxsG2hSWaEbQU68oKUGvGJsmkElshPLZrmmD-iC6kAUBszTrO6Dfle5mFNkPYwP2g69FZMA2Y2dlmDxnjeH4gReCg6B7gevjMMe6SZJgzkXkU1DXtIACiu4pfUKXNC0D6qE+3TeXO7a-mcQIltO0BIAAXvEiSmfpJWsTA8H2JFSERb6qEYhhcpYfxTGCTA5JgIpYlxtoJHSdKsnlFRMbidoQphIV6AqWRSaXjB5SKZx3ErphGoCaSRgoNwmSjdOIautNhSWjIp0UoYinxr1nZmeUyGRcZCB5g1LEOVc9lJY5XY5GAfYDkOS7+WuQUBJCtq7tCMAAOKjqy0WnnF57MBZaYVCjGXZfYo4FUGtloMVbLvV5QaVTVBB1WZbJNcNaOjKosIoxS7NqF16E6X1MhHe6Q0UhdC3yJNqnkbNPLzeN8hLbTdY+ZT13mhtiUabBWl+rtPg8Qd2EDcdbPo7C0vrbdFHc8wwDKqjo6ChAOgAFYoOAGga2pm06+UvOslxhv7a9Ws03bgffb9ZUgz+Vwkxz5QVEOicoAAkhwswnpkBp3LMOgIKADZ56OsxpwAcqOezTKno6Z9nMX6kRb5CkXIAly3wFDaOVejHs3SNEDxxa-F4OuZDvRp6LKdT-XWcwDnzcjq3hfF6XreV9Xtdz6MDeL03G-d2vHdH+XvfV4P0MrgF66BNgPhQNg3DwLqmRO6MKRN9jYMswDlS1AaMTUmwRyZqzrqMPuKBh4dj0rHcoFUoDVVqmgWYPQt793qrHP+W0YDCUyLzWEcA34oF5vzLEgtDqm1FsNCWitgBWxkjbOWvJnqLToitdWIt-q4J2sHI2Yd+pTTNjzUcsIMEoEYTddkXJ5YfxQLRMIEi1oyXUuCGAiplSUJNsI0W+DSFiIkVIzWzCFI2nkQ6H2PD-byO0XA1M5RiFegIaOaOWDUzWIAmndO0gYEgzHhDdy6D66+L8jfWGG5LCnXgskGAAApCAPJ5GBBPg2H+5gcElGTlUSkd4WhpzJqrOcQ5n7ACiVAOAEB4JQHPnvUJP5qYNQQXTJBDMnxoNKeUyp1TakZ1CczNRmk3aJLQIQhJPIyEoDRN1bRQjVJ4RGqJS6E0VHSLunNPWSklYcLAUVKxWsmp8L2rM4W1CFmEO8dIYxEZTEbN5oonudTVma0GbrXmBsBEwSobo8ofgtAuNGOIkJ74i5dKqdAayQYXBAWubLPk2B-mGHucpY29jgTlHGaM1xagTLuKgnjQGVNR440CVDMJq5Aobi8GUrsXpYDAGwM-QgKCv5YzHpk5KaUMpZVaMYIlf4ml4tgX7dRIBuB4Eticn2QlxVwlhTNWleB2TPN9trcEdiBXwMVVmHFP0hVx3-j0PxnYAkTyCQcclQA)

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
