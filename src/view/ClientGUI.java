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
	
	//@@@Non Scene Change Methods@@@
	
	public boolean validateName(String name)
	{
		for(char c: name.toCharArray())
			if(!(Character.isLetterOrDigit(c)||Character.isSpaceChar(c)))	//If it is not the case that the character is a-zA-Z0-9 or ' '
				return false;
		return true;
						
				
	}
	
	private String getCardImage(Card card){
		return "resources\\" + card.toString() + ".png";
	}

	private void drawCard(Card card, double posx, double posy, double width, double height) throws FileNotFoundException {
		Image image = new Image(new FileInputStream(getCardImage(card)));
		ImageView view = new ImageView(image);
		view.setX(posx);
		view.setY(posy);
		view.setFitWidth(width);
		view.setFitHeight(height);
		root.getChildren().add(view);
	}

	void drawCard(Card card, double posx, double posy) throws FileNotFoundException {
		drawCard(card, posx, posy, STD_CARD_WIDTH, STD_CARD_HEIGHT);
	}
	
	
	//Buttons
	
	/**
	 * Initializes the array of card buttons the player presses
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
					userSelect[3] = numberToSymbol(temp);
					
				}
			});
		}
	}
	
	void initializePlayerButtons(int playerAmt) throws FileNotFoundException
	{
		playerButtons = new Button[playerAmt];
		Image image = new Image(new FileInputStream("resources\\playa.png"));
		ImageView imgV = new ImageView(image);
		imgV.setFitHeight(STD_CARD_HEIGHT);
		imgV.setFitWidth(STD_CARD_HEIGHT);
		
		for(int i = 0; i < playerAmt; ++i)
		{
			playerButtons[i] = new Button();
			playerButtons[i].setGraphic(imgV);
			
			final int temp = i;
			
			playerButtons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					userSelect[2] = game.clientLabels.get(temp);
					
				}
			});
		}
	}
	
	void updateCardButtons()
	{
		List<Card> uCards = yourCards.getUCards();
		double height = root.getScene().getHeight();
		double cardGap = (root.getScene().getWidth());
		
		double posX;
		int index;
		
		//Clear all the buttons currently
		for(int i = 0; i < cardButtons.length; ++i)
			root.getChildren().remove(cardButtons[i]);
		
		
		for(Card uCard: uCards)
		{
			index = uCard.getVal().toInt()-1;
			posX = (double)index*cardGap+20;
		}
		
	}
	
	/**
	 * COnverts a card int value to its String Symbol form
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
	
	
}//end of GUI



