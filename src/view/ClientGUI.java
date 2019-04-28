package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Card;
import model.Hand;

public class ClientGUI extends Application{
	
	ClientController game = null;
	
	private final int STD_CARD_WIDTH = 120;
	private final int STD_CARD_HEIGHT = 180;
	private final int STD_MINI_CARD_WIDTH = 30;
	private final int STD_MINI_CARD_HEIGHT = 45;
	
	String state;
	String yourName;
	//server to client variables
	int yourID;
	Hand yourCards;
	int deckCount;
	int cardCounts[];
	//Holds references to all cards that represent each player's hand and pairs
	List<Card>[] playerPairs;
	
	
	//String array to easily hold the clients choices for their play on their turn
	//userSelect[0] = The client's name, this get changed per turn
	//userSelect[1] = The player the client is asking for cards
	//userSelect[2] = The card value the user is asking for
	String[] userSelect;
	
	
	//GUI stuff
	VBox root = new VBox();
	Button hostButton;
	Button joinButton;
	Button connectButton;
	Button serverButton;
	Button backButton;
	Button exitButton;
	Button playButton;
	Button startButton;
	Text menuLabel;
	Text addressLabel;
	Text infoLabel;
	Text turnLabel;
	Text testLabel;
	TextField addressInput;
	TextField nameInput;
	TextField gameInput;
	//Label realLabel;
	
	//Card button array
	Button[] cardButtons;
	//OtherPlayer button array
	Button[] playerButtons;
	//Holds a collective list of all faceup cards and facedown cards 
	//that are not a part of the card buttons
	List<ImageView> cardsInPlay;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		game = ClientLauncher.game;
		ClientLauncher.gui = this;
		game.gui = ClientLauncher.gui;
		
		state = "";
		yourName = "model.Player";
		
		//Initialize the user selection array
		//userSelect[0] = The client's name, this get changed per turn
		//userSelect[1] = The player the client is asking for cards
		//userSelect[2] = The card value the user is asking for
		userSelect = new String[3];
		
		root = new VBox();
		hostButton = new Button("Host Game");//part of main menu screen
		joinButton = new Button("Join Game");//part of main menu screen
		connectButton = new Button("Connect");//part of join screen
		serverButton = new Button("Create Server");//part of host screen
		backButton = new Button("Back");//part of host and join screens
		exitButton = new Button("Exit");//part of main menu screen
		playButton = new Button("Play");//part of game screen
		startButton = new Button("Start Game");//part of hosting screen
		menuLabel = new Text("Main Menu");//part of all screens
		addressLabel = new Text("Starting server...");//part of host screen
		infoLabel = new Text("Enter details below");//part of join and game screen
		turnLabel = new Text();//part of game screen
		testLabel = new Text();		//Used to test if a message is recieved
		addressInput = new TextField();//part of join screen
		nameInput = new TextField();//part of host and join screen
		gameInput = new TextField();//part of game screen
		//realLabel = new Label();
		
		Image image = new Image(new FileInputStream("resources\\2C.png"));
		ImageView imgV = new ImageView(image);
		imgV.setFitHeight(STD_CARD_HEIGHT);
		imgV.setFitWidth(STD_CARD_WIDTH);
		hostButton.setGraphic(imgV);
		
		//setup buttons and what-not
		hostButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				preHost();
			}
		});
		
		serverButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				hosting();
			}
		});
		
		joinButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				join();
			}
		});
		
		connectButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				connectButton.setDisable(true);
				
				connect();
				
				connectButton.setDisable(false);
			}
		});
		
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				main();
			}
		});
		
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();//closes the window
			}
		});
		
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				play();
			}
		});
		
		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				game();
			}
		});
		
		main();
		
		stage.setTitle("Client Test");
		Scene scene = new Scene(root, 1280, 720);
		//Scene scene = new Scene(root, 600, 500);
		stage.setScene(scene);
        stage.show();
	}//end of start
	
	
	//methods called by buttons
	void main() {
		if(state.equals("hosting") || state.equals("lobby"))
			game.closeSocks(state);
		
		mainScreen();
		
		state = "main";
	}
	void mainScreen() {
		root.getChildren().clear();
		root.getChildren().addAll(/*realLabel,*/ menuLabel, hostButton, joinButton, exitButton);
		menuLabel.setText("Main Menu");
	}
	
	void preHost() {
		preHostScreen();
		state = "host";
	}
	void preHostScreen() {
		root.getChildren().clear();
		root.getChildren().addAll(menuLabel, infoLabel, nameInput, serverButton, backButton);
		menuLabel.setText("Host a Game");
		infoLabel.setText("Enter your name and click \"Create Server\"");
	}
	
	void hosting() {
		
		if(nameInput.getText().isEmpty()) {
			infoLabel.setText("Enter your name!");
			connectButton.setDisable(false);
			return;
		}
		else if(!validateName(nameInput.getText()))
		{
			infoLabel.setText("Please use only the characters a-zA-Z0-9 or ' '");
			connectButton.setDisable(false);
			return;
		}
		
		yourName = nameInput.getText();
		
		String address = game.setupHost();
		if(address.equals("Unable to establish host")) {
			infoLabel.setText(address);
			return;
		}
		
		String result = game.connectToHost(address, yourName);
		if(!result.equals("Connected!")) {
			infoLabel.setText(result);
			return;
		}
		addressLabel.setText(address);
		
		hostingScreen();
		
		state = "hosting";
		
		game.isServer = true;
		game.serverThread.start();
		game.clientThread.start();
	}
	void hostingScreen() {
		root.getChildren().clear();
		root.getChildren().addAll(menuLabel, addressLabel, infoLabel, startButton, backButton);
		menuLabel.setText("Host a Game");
		infoLabel.setText(yourName);
	}
	
	void join() {
		joinScreen();
		state = "join";
	}
	void joinScreen() {
		root.getChildren().clear();
		root.getChildren().addAll(menuLabel, infoLabel, addressInput, nameInput, connectButton, backButton);
		menuLabel.setText("Join a Game");
		infoLabel.setText("Enter details below");
		addressInput.setPromptText("Enter Host Address");
		nameInput.setPromptText("Enter Your Name");
	}
	
	void connect() {
		if(addressInput.getText().isEmpty()) {
			infoLabel.setText("Enter the host address!");
			connectButton.setDisable(false);
			return;
		}
		if(nameInput.getText().isEmpty()) {
			infoLabel.setText("Enter your name!");
			connectButton.setDisable(false);
			return;
		}
		if(addressInput.getText().split(":", 0).length != 2) {
			infoLabel.setText("Invalid host name!");
			connectButton.setDisable(false);
			return;
		}
		
		String result = game.connectToHost(addressInput.getText(), nameInput.getText());
		
		if(result.equals("Connected!")) {
			yourName = nameInput.getText();
			lobby();
		}
		infoLabel.setText(result);
	}
	
	void lobby(){
		lobbyScreen();
		state = "lobby";
		game.clientThread.start();
	}
	void lobbyScreen(){
		root.getChildren().clear();
		root.getChildren().addAll(menuLabel, infoLabel, backButton);
		menuLabel.setText("Lobby");
		infoLabel.setText(yourName);
	}
	
	void game() {
		gameScreen();
		state = "game";
	}
	void gameScreen() {
		root.getChildren().clear();
		root.getChildren().addAll(menuLabel, infoLabel, turnLabel, testLabel, gameInput);
		menuLabel.setText("Game");
		gameInput.setPromptText("Write your move here");
	}
	
	void play() {
		if(gameInput.getText().isEmpty()) {
			infoLabel.setText("Enter your move");
			return;
		}
		
		boolean success = endTurn(gameInput.getText());
		
		if(success) {
			gameInput.setText("");
			root.getChildren().remove(playButton);
		}
		else {
			infoLabel.setText("Failed to reach server, try again");
		}
	}
	
	//call this to end the clients turn
	//pass the string to write to the server
	//returns true if the message was sucesesfully sent without error
	boolean endTurn(String messageToServer) {
		boolean success = false;
		int attempts = 0;//keeps track of attempts
		while(!success && attempts++ < 10){//tries ten times talk to server
			try {
				game.writeToServer(messageToServer);
				success = true;
			} catch (IOException e) {}
		}
		return success;
	}

	
	public void launchGUI() {
		launch();
	}
	
	//@@@Non Scene Change Methods@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	public boolean validateName(String name)
	{
		for(char c: name.toCharArray())
			if(!(Character.isLetterOrDigit(c)||Character.isSpaceChar(c)))	//If it is not the case that the character is a-zA-Z0-9 or ' '
				return false;
		return true;
						
				
	}
	
	/**
	 * Converts a card int value to its String Symbol form
	 * @param value
	 * @return
	 */
	String numberToSymbol(int value)
	{
		String val;
		switch(value)
		{
			case 1:
				val = "A";
				break;	
			case 10:
				val = "T";
				break;
			case 11:
				val = "J";
				break;
			case 12:
				val = "Q";
				break;
			case 13:
				val = "K";
				break;
			default:
				val = value+"";
				break;
		}
		return val;
	}
	/**
	 * Converts a Card value symbol to the long string form
	 * @param symbol
	 * @return
	 */
	String SymbolToWord(String symbol)
	{
		String word = "Joker";
		switch(symbol)
		{
			case "A":
				word = "Ace";
				break;		
			case "2":
				word = "Two";
				break;	
			case "3":
				word = "Three";
				break;	
			case "4":
				word = "Four";
				break;	
			case "5":
				word = "Five";
				break;	
			case "6":
				word = "Sixe";
				break;	
			case "7":
				word = "Seven";
				break;	
			case "8":
				word = "Eight";
				break;	
			case "9":
				word = "Nine";
				break;
			case "T":
				word = "Ten";
				break;
			case "J":
				word = "Jack";
				break;
			case "Q":
				word = "Queen";
				break;
			case "K":
				word = "King";
				break;
		}
		return word;
	}
	
	
	//@@@Drawing plain cards@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	private String getCardImage(Card card){
		return "resources\\" + card.toString() + ".png";
	}

	private ImageView drawCard(Card card, double posx, double posy, double width, double height) throws FileNotFoundException {
		Image image = new Image(new FileInputStream(getCardImage(card)));
		ImageView view = new ImageView(image);
		view.setX(posx);
		view.setY(posy);
		view.setFitWidth(width);
		view.setFitHeight(height);
		root.getChildren().add(view);
		return view;
	}

	ImageView drawCard(Card card, boolean mini, double posx, double posy) throws FileNotFoundException {
		return drawCard(card, posx, posy, (mini)?STD_MINI_CARD_WIDTH:STD_CARD_WIDTH, (mini)?STD_MINI_CARD_HEIGHT:STD_CARD_HEIGHT);
	}
	
	
	private ImageView drawFaceDownCard(String color, double posx, double posy, double width, double height) throws FileNotFoundException {
		Image image = new Image(new FileInputStream("resources\\"+color+"_back.png"));
		ImageView view = new ImageView(image);
		view.setX(posx);
		view.setY(posy);
		view.setFitWidth(width);
		view.setFitHeight(height);
		root.getChildren().add(view);
		return view;
	}

	ImageView drawFaceDownCard(String color, boolean mini, double posx, double posy) throws FileNotFoundException 
	{
		return drawFaceDownCard(color, posx, posy, (mini)?STD_MINI_CARD_WIDTH:STD_CARD_WIDTH, (mini)?STD_MINI_CARD_HEIGHT:STD_CARD_HEIGHT);
	}
	
	/**
	 * Removes all the cards drawn that aren't associated to the cardButtons
	 */
	void clearCardsInPlay()
	{
		for(ImageView view: cardsInPlay)
			root.getChildren().remove(view);
	}
	
	//@@@Draw Deck@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	/**
	 * Draws the cards in deck, each one offset by a tiny amount to kind of see 
	 * how many cards there are.
	 * Should call clearCardsInPlay() before calling this again.
	 * @throws FileNotFoundException
	 */
	void drawDeck() throws FileNotFoundException
	{
		double offset = root.getScene().getWidth()*(20/1920);
		double posY = root.getScene().getHeight()/3 + offset;
		
		for(int i = 0; i < deckCount; ++i)
		{
			cardsInPlay.add(drawFaceDownCard("green", false, offset+i/100, posY+i/100));
		}
	}
	
	//@@@Button drawing@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	/**
	 * Initializes the array of card buttons the player presses
	 * Each one sets userSelect[2] to the character symbol of their respective card value
	 * @author Chris
	 */
	void initializeCardButtons()
	{
		cardButtons = new Button[13];	//There are 13 card values
		for(int i = 0; i < cardButtons.length; ++i)
		{
			cardButtons[i] = new Button();
			final int temp = i;
			
			cardButtons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					userSelect[2] = numberToSymbol(temp);
					
				}
			});
		}
	}
	
	/**
	 * Sets up the player buttons
	 * @author Chris
	 * @param playerAmt
	 * @throws FileNotFoundException
	 */
	void initializePlayerButtons(int playerAmt) throws FileNotFoundException
	{
		playerButtons = new Button[playerAmt];
		Image image = new Image(new FileInputStream("resources\\playa.png"));
		ImageView imgV = new ImageView(image);
		imgV.setFitHeight(STD_CARD_HEIGHT);
		imgV.setFitWidth(STD_CARD_HEIGHT);
		
		for(int i = 0; i < playerAmt; ++i)
		{
			playerButtons[i] = new Button(game.clientLabels.get(i));
			playerButtons[i].setGraphic(imgV);
			
			final int temp = i;
			
			playerButtons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					userSelect[1] = game.clientLabels.get(temp);
					
				}
			});
		}
	}
	
	/**
	 * Removes and redraws each card button as their availability 
	 * is updated within the client's hand.
	 * Each card is shifted towards the left side of the screen
	 * NOTE: Need to make a call to clearCardsInPlay() before calling this again (for the duplicate cards drawn below the face up card).
	 * @author Chris
	 * @throws FileNotFoundException 
	 */
	void updateCardButtons() throws FileNotFoundException
	{
		List<Card> uCards = yourCards.getUCards();
		double height = root.getScene().getHeight();
		double width = root.getScene().getWidth();
		
		double offset = width*(20/1920);
		double cardGap = ( (width-offset) - offset - STD_CARD_WIDTH)/uCards.size();
		double posY = height/3*2 + offset;
		
		double posX;
		int index;
		
		//Clear all the buttons currently, If the button is not there, no Ill effects occur
		for(int i = 0; i < cardButtons.length; ++i)
			root.getChildren().remove(cardButtons[i]);
		
		
		for(Card uCard: uCards)
		{
			index = uCard.getVal().toInt()-1;
			posX = (double)index*cardGap+offset;
			
			drawExtraHandCards(yourCards.getDuplicityAmount(uCard.getVal()), posX, posY);
			//cardButtons[index].setText("x"+yourCards.getDuplicityAmount(uCard.getVal()));
			placeCardButton(cardButtons[index], uCard, posX, posY);
		}
		
	}
	
	/**
	 * Places the player buttons.
	 * Should only be used once in the game.
	 */
	void placePlayerButtons()
	{
		double width = root.getScene().getWidth();
		
		double offset = width*(20/1920);
		double playerGap = ( (width-offset) - offset - STD_CARD_WIDTH)/playerButtons.length;
		double posY = offset;
		
		double posX;
		
		for(int i = 0; i < playerButtons.length; ++i)
		{
			posX = (double)i*playerGap+offset;
			
			playerButtons[i].setLayoutX(posX);
			playerButtons[i].setLayoutY(posY);
			root.getChildren().add(playerButtons[i]);
		}
	}
	
	/**
	 * Just a rehash of An
	 * @param cardButton
	 * @param card
	 * @param posx
	 * @param posy
	 * @param width
	 * @param height
	 * @throws FileNotFoundException
	 */
	private void placeCardButton(Button cardButton, Card card, double posx, double posy, double width, double height) throws FileNotFoundException {
		Image image = new Image(new FileInputStream(getCardImage(card)));
		ImageView view = new ImageView(image);
		view.setFitWidth(width);
		view.setFitHeight(height);
		cardButton.setGraphic(view);
		cardButton.setLayoutX(posx);
		cardButton.setLayoutY(posy);
		
		root.getChildren().add(cardButton);
	}
	void placeCardButton(Button cardButton, Card card, double posx, double posy) throws FileNotFoundException {
		placeCardButton(cardButton, card, posx, posy, STD_CARD_WIDTH, STD_CARD_HEIGHT);
	}
	
	//@@@Player cards in hand and their pairs@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	/**
	 * Draws the cards for each player's pairs and a representation of their cards in hand face-down
	 * NOTE: Need to make a call to clearCardsInPlay() before calling this again.
	 * @author Chris
	 * @throws FileNotFoundException
	 */
	void drawPlayerPairsAndHands() throws FileNotFoundException
	{
		
		double height = root.getScene().getHeight();
		double width = root.getScene().getWidth();
		double offset = width*(20/1920);
		double playerGap = ( (width-offset) - offset - STD_CARD_WIDTH)/playerButtons.length;
		double posY = 2*offset + STD_CARD_HEIGHT;
		double posX;
		
		//Used to hold the i in case we are working on players above the client's id
		int tempIndex;
		
		//Update the list of each player's pairs, and cards in hand
		for(int i = 0; i < cardCounts.length; ++i)
		{
			//If we are updating this client's pairs
			if(i == yourID)
			{
				//Draw only your pairs
				drawPairs(playerPairs[i], width/3*2, height/3 + offset);
			}
			else //If we are updating some other player's pairs and hand
			{
				//Get the proper index for the other player's excluding you
				tempIndex = (i>yourID)?i-1:i;
				//Get the X position for the current player
				posX = (double)tempIndex*playerGap+offset;
				
				drawHand(cardCounts[i], posX, posY);
				drawPairs(playerPairs[i], posX, posY + offset);
			}
			
		}
	}
	/**
	 * Draws the pairs a given player has.
	 * @author Chris
	 * @param pairs	- The pairs the player has
	 * @param startX - Where we will start draw the players cards on the X Pane
	 * @param startY - Where we will start draw the players cards on the Y Pane
	 * @throws FileNotFoundException
	 */
	private void drawPairs(List<Card> pairs, double startX, double startY) throws FileNotFoundException
	{		
		int columns = 3;
		
		double width = startX + STD_CARD_WIDTH;
		double cardGap = ( width - startX - STD_MINI_CARD_WIDTH)/playerButtons.length;
		double posX;
		
		int i = 0;
		int j = 0;
		for(Card pair: pairs)
		{
			posX = (double)(j%columns)*cardGap+startX;
			cardsInPlay.add(drawCard(pair, true, posX, STD_MINI_CARD_HEIGHT*i));
			
			i = j/columns;
			++j;
		}
	}
	/**
	 * Draws a group of facedown cards 
	 * @author Chris
	 * @param cardAmount - The number of cards this player has
	 * @param startX - Where we will start draw the players cards on the X Pane
	 * @param startY - Where we will start draw the players cards on the Y Pane
	 * @throws FileNotFoundException
	 */
	private void drawHand(int cardAmount, double startX, double startY) throws FileNotFoundException
	{
		for(int i = 0; i < cardAmount; ++i)
		{
			cardsInPlay.add(drawFaceDownCard("red", true, startX+2*i, startY));
		}
	}
	
	private void drawExtraHandCards(int cardAmount, double startX, double startY) throws FileNotFoundException
	{
		for(int i = 0; i < cardAmount; ++i)
		{
			cardsInPlay.add(drawFaceDownCard("green", true, startX, startY+2*i));
		}
	}
	
	
}//end of GUI



