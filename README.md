Team: 
Antonio
Cameron
Chris
Matthew

Compilation Instructions:
Make sure you have a Java Development Kit (JDK) installed
Find the JDK (should be in c:\Program Files\Java
Enther this line in command line set path=%path%;C:\Program Files\Java\jdk-9.0.1\bin
Change Directory to the location of this project, into the src folder

To compile and run the project: 
javac view.ClientLauncher.java view.ClientController.java view.ClientGUI.java model.Card.java model.CardGame.java model.Cardpile.java model.Deck.java model.Hand.java model.Player.java model.PlayerQueue.java
java view.ClientLauncher
 
To compile and run model.CardpileTest
javac model.CardpileTest.java model.Cardpile.java model.Card.java
java model.CardpileTest
 
To compile and run model.DeckTest
javac model.DeckTest.java model.Deck.java model.Card.java
java model.DeckTest
 
To compile and run model.PlayerQueueTest
Javac model.PlayerQueueTest.java model.Player.java model.Hand.java model.Card.java
Java model.PlayerQueueTest
 
To compile and run model.CardGameTest
Javac model.CardGameTest.java model.CardGame.java model.Deck.java model.Cardpile.java model.PlayerQueue.java model.Player.java model.Hand.java model.Card.java
Java model.CardGameTest
 
To compile and run model.PlayerHandTest
Javac model.PlayerHandTest.java model.Player.java model.Hand.java model.Card.java model.Deck.java
Java model.PlayerQueueTest
