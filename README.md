# OOP Course - Excercise 2 - Ariel University 2020
#### This is excercise 1 in Object Oriented Programming course, which is an implementation of a directed weighted graphs in Java,
#### to use in a Pokemon game.
![graph picture](/icons/logo.png)

## Table of content
### [Graph API:](#graph-api)
* [General info](#general-info)
* [Dependencies](#dependencies)
* [Setup](#setup)

### [Pokemon game:](#pokemon-game)
* [How to play](#how-to-play)
* [Score](#scores)

---

### Graph API:  
  
### General info
This structure is capable of creating directed weighted graphs, and using a few algorithms on them:

- Copy a graph
- Check if a graph is connected
- Find the shortest path between 2 nodes and its total weight
- Save and load a graph from a JSON-format file

---

### Dependencies
This project is using Java version 14  
The tests included in this projects are based on JUnit version 5.7

---

### Setup
To run this project, download th EX2.jar and /data folder, and place them in the same folder.
* Option 1: Run from GUI  
  After you have downloaded the files neccessary (Ex2.jar, /data) run Ex2.jar,
  select a level and login using an ID.
* Option 2: run form CLI  
  After you have downloaded the files neccessary (Ex2.jar, /data), open a terminal at the current folder, and type:  
  > "java -jar Ex2.jar [ID] [LEVEL]",  
    
  where [ID] is your ID, and [LEVEL] is the level you want to play(0-23).

link to this Git: https://github.com/shaharhayon/OOP_Ex1.git

---

### Pokemon game:

# How to play
- After the game has opened and you have selected a level, you need to choose where to place the Agents.  
  You can choose to place the Agents manually where you want them to be, or press "Place agents automatically" to place them at the optimal position.  
  The agents then run around chasing pokemons, until the time (in the upper left corner) is up.  
  
# Score
- The score you have achieved will be sent automatically to the server, assuming you have signed in.









