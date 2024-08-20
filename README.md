# MessageLogger
Spigot Message Logging Plugin
***
## What is it?

MessageLogger is a simple Spigot plugin which store inside a database all the messages sent by players.

All database operation are completely asynchronous, meaning that the main server thread will not be slown down when performing database requests.

## Commands
- ```/messagelogger info``` Get a simple message containing the version of the plugin and the author's name
- ```/messagelogger log <player> [page]``` Print the list of messages sent by the given player from the more recent to the less recent
- ```/messagelogger reload``` Reload the plugin
  
**Aliases:**
- ```/msglog info```
- ```/msglog log <player> [page]``` or ```/log <player> [page]```
- ```/msglog reload```

## API

MessageLogger comes with an API, to use it just run ```MessageLoggerAPI = MessageLoggerApiProvider.getAPI();```

The api contains some methods to get the list of the messages sent by players as a ```List<Message>```, the ```Message``` class contains a field called "message" and a field called "date", you can obtain them by calling ```getMessage()``` and ```getDate()```.

## Libraries Used
- [Basic](https://github.com/Asintotoo/Basic): My Personal library to create Minecraft Plugins
- [ColorLib](https://github.com/Asintotoo/ColorLib): My Color Parsing Library for Spigot (built into Basic)
- [Lamp](https://github.com/Revxrsal/Lamp/): A modern annotations-driven commands framework for Java and Kotlin
