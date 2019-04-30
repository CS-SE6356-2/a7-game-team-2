package view;

import main.ClientController;
import main.ClientLauncher;
import main.ServerController;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Card;
import model.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class ClientGUI extends Application{
	
	private ClientController client = null;
	private ServerController server = null;

	private static final double STD_CARD_WIDTH = 60.0;
	private static final double STD_CARD_HEIGHT = 90.0;
	private static final double STD_MINI_CARD_WIDTH = 30.0;
	private static final double STD_MINI_CARD_HEIGHT = 45.0;

	//GUI stuff
	private Scene main;
    private HashMap<String, Pane> sceneMap;

    public static void launchGUI(String args[]) {
        launch(args);
    }

	@Override
	public void start(Stage stage) {
        ClientLauncher.setGui(this);

        Pane root = new Pane();
		main = new Scene(root, 1280, 720);
		sceneMap = new HashMap<>();
		setupMainMenuScene();
		setupHostSetupScene();
		setupJoinScene();
		
		main();
		
		stage.setTitle("Go Fish");
		stage.setScene(main);
        stage.show();
	}
	private void setupMainMenuScene() {
		Pane mainMenu = new VBox(10);
		Text menuLabel = new Text("Main Menu");
		Button hostButton = new Button("Host Game");
		hostButton.setOnAction(event -> preHost());
		Button joinButton = new Button("Join Game");
		joinButton.setOnAction(event -> loadScene("Join"));
		Button exitButton = new Button("Exit");
		exitButton.setOnAction(event -> Platform.exit());
		mainMenu.getChildren().addAll(menuLabel, hostButton, joinButton, exitButton);
		sceneMap.put("MainMenu", mainMenu);
	}
	private void setupHostSetupScene() {
		Pane hostSetup = new VBox(10);
		Text menuLabel = new Text("Host a Game");
		Text infoLabel = new Text("Enter your name and click \"Create Server\"");
		TextField nameInput = new TextField();
		nameInput.setPromptText("Enter Your Name");
		nameInput.setId("NameInput");
		Button serverButton = new Button("Create Server");
		serverButton.setOnAction(event -> hosting());
		serverButton.setDefaultButton(true);
		Button backButton = new Button("Back");
		backButton.setOnAction(event -> main());
		backButton.setCancelButton(true);
		hostSetup.getChildren().addAll(menuLabel, infoLabel, nameInput, serverButton, backButton);
		sceneMap.put("HostSetup", hostSetup);
	}
	private void setupJoinScene() {
		Pane join = new VBox(10);
		Text menuLabel = new Text("Join a Game");
		Text infoLabel = new Text("Enter details below");
		TextField addressInput = new TextField();
		addressInput.setPromptText("Enter Host Address");
		addressInput.setId("AddressInput");
		TextField nameInput = new TextField();
		nameInput.setPromptText("Enter Your Name");
		nameInput.setId("NameInput");
		Button connectButton = new Button("Connect");//part of join screen
		connectButton.setOnAction(event -> {
			connectButton.setDisable(true);
			connect();
			connectButton.setDisable(false);
		});
		connectButton.setDefaultButton(true);
		Button backButton = new Button("Back");
		backButton.setOnAction(event -> main());
		backButton.setCancelButton(true);
		join.getChildren().addAll(menuLabel, infoLabel, addressInput, nameInput, connectButton, backButton);
		sceneMap.put("Join", join);
	}
	private void setupLobbyScene(){
		Pane lobby = new VBox(10);
		Text menuLabel = new Text("Lobby");
		Text addressLabel = new Text();
		addressLabel.setId("AddressLabel");
		Pane nameList = new VBox(10);
		nameList.setId("NameList");
		Button startButton = new Button("Start Game");
		startButton.setOnAction(event -> game());
		startButton.setDisable(server == null);
		Button leaveButton = new Button("Leave Lobby");
		leaveButton.setOnAction(event -> main());
		leaveButton.setCancelButton(true);
		lobby.getChildren().addAll(menuLabel, addressLabel, nameList, startButton, leaveButton);
		sceneMap.put("Lobby", lobby);
	}
	public void updateLobbyScene(){
        Text addressLabel = (Text) sceneMap.get("Lobby").lookup("#AddressLabel");
	    addressLabel.setText(client.getServerAddress());
        Pane nameList = (VBox) sceneMap.get("Lobby").lookup("#NameList");
        nameList.getChildren().clear();
        for(String name : client.getPlayers()){
            Text nameField = new Text(name);
            nameList.getChildren().add(nameField);
        }
    }
	private void setupGameScene() {
		Pane gameScene = new VBox();
		Pane otherPlayers = new HBox();
		for(String player : client.getPlayers()){
		    Button selectPlayer = new Button(player);
		    selectPlayer.setOnAction(event -> System.out.println(player));
		    otherPlayers.getChildren().add(selectPlayer);
        }
		Pane board = new HBox();
		board.setId("Board");
		Pane cards = new HBox();
		cards.setId("Cards");
		gameScene.getChildren().addAll(otherPlayers, board, cards);
		sceneMap.put("Game", gameScene);
	}

	private void loadScene(String scene){
		if(sceneMap.keySet().contains(scene))
			main.setRoot(sceneMap.get(scene));
		else {
			System.out.println(scene + " not found.");
		}
	}

	public void showAlert(String message){
        new Alert(Alert.AlertType.ERROR, message).show();
    }

	private void main() {
		loadScene("MainMenu");
	}

	private void preHost() {
		server = ClientLauncher.launchServer();
		loadScene("HostSetup");
	}

	private void hosting() {
		String name = ((TextField) main.lookup("#NameInput")).getText();
		if(!validateName(name)) return;

		if(!server.setupHost()) return;

		lobby(server.getServerAddress(), name);
	}

	private void connect() {
		String name = ((TextField) main.lookup("#NameInput")).getText();
		String address = ((TextField) main.lookup("#AddressInput")).getText();
        if(!validateName(name)) return;
        if(!validateAddress(address)) return;
		lobby(address, name);
	}

	private void lobby(String address, String name) {
        setupLobbyScene();
	    client = ClientLauncher.launchClient();
        if(!client.connectToHost(address, name)) return;
		loadScene("Lobby");
	}

	private void game() {
		setupGameScene();
		loadScene("Game");
		server.startGame();
	}

	public void updateGame(Player player){
        Pane cards = (HBox) sceneMap.get("Game").lookup("#Cards");
        double minX = 100;
        double maxX = cards.getMaxWidth() - 100;
        int pos = 0;
        for(Card card : player.getActiveCards()){
            try {
                cards.getChildren().add(drawCard(card, pos++ * (maxX - minX) / player.getActiveCards().size(), 0));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

	public void displayTurn() {
        System.out.println("Display Turn");
    }

    private void onQuery() {
        if(!validateUserSelect()) return;

        boolean success = endTurn(userSelect[0]+" "+userSelect[1]);

        if(success) {
            playButton.setVisible(false);
            //gamePane.getChildren().remove(playButton);
        }
        else {
            infoLabel.setText("Failed to reach server, try again");
        }
    }
	
	private boolean validateName(String name)
	{
        if(name.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Enter your name!").show();
            return false;
        }
		for(char c: name.toCharArray())
			if(!(Character.isLetterOrDigit(c) || Character.isSpaceChar(c))){
                new Alert(Alert.AlertType.ERROR, "Please use only the characters a-z, A-Z, 0-9, or ' '").show();
                return false;
            }
		return true;
	}

	private boolean validateAddress(String address){
        if(address.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Enter the host address!").show();
            return false;
        }
        String[] addressParts = address.split("[.:]");
        if(addressParts.length != 5) {
            new Alert(Alert.AlertType.ERROR, "Invalid host name!").show();
            return false;
        }
        return true;
    }

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
		return view;
	}

	//Makes sure the user made a selection
	boolean validateUserSelect()
	{
		if(userSelect[0] == "<player>")
		{
			infoLabel.setText("Please select a player!");
			return false;
		}
		else if(userSelect[1] == "<card>")
		{
			infoLabel.setText("Please select a card!");
			return false;
		}
		return true;
	}

	void updateUserSelect(boolean canUpdate)
	{
		if(canUpdate)
			userSelectLabel.setText("Ask player "+userSelect[0]+" for any "+symbolToWord(userSelect[1])+"'s");
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
	String symbolToWord(String symbol)
	{
		String word = "<card>";
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
				word = "Six";
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

	//###Initialize buttons & Update GameScreen###

	/**
	 * Sets up the initial game gui elements, the ones that don't change throughout the game
	 * @throws FileNotFoundException
	 */
	void setupGameGUI() throws FileNotFoundException
	{
		gamePane.getChildren().clear();
		root.getChildren().add(gamePane);
		//Initialize
		initializeCardButtons();
		initializePlayerButtons(cardCounts.length);
		userSelect[0] = "<player>";
		userSelect[1] = "<card>";

		//Initialize the list that holds references to all cards in play
		cardsInPlay = new ArrayList<>();

		//Place buttons and fields that don't need to be updated
		placePlayerButtons();
		placeCenterField();
	}

	/**
	 * Updates GUI elements like cards. Should be called after everyone gets their cards
	 * @author Chris
	 * @throws FileNotFoundException
	 */
	void updateGameGUI() throws FileNotFoundException
	{
		//Get rid of any card that is not involved with a button
		clearCardsInPlay();
		userSelect[0] = "<player>";
		userSelect[1] = "<card>";
		updateUserSelect(canSelect);

		//Redraw the deck
		drawDeck();
		//Update the cardButtons or the UI representation of the player's activeCards
		updateCardButtons();
		//Update each player's pairs
		drawPlayerPairsAndHands();

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
		gamePane.getChildren().add(view);
		return view;
	}

	ImageView drawCard(Card card, boolean mini, double posx, double posy) throws FileNotFoundException {
		return drawCard(card, posx, posy, (mini)?STD_MINI_CARD_WIDTH:STD_CARD_WIDTH, (mini)?STD_MINI_CARD_HEIGHT:STD_CARD_HEIGHT);
	}


	private ImageView drawFaceDownCard(String color, double posx, double posy, double width, double height) throws FileNotFoundException {
		Image image = new Image(new FileInputStream("resources\\"+color+"_back.png"));
		//ImageView view = new ImageView(faceDownCard);
		ImageView view = new ImageView(image);
		view.setX(posx);
		view.setY(posy);
		view.setFitWidth(width);
		view.setFitHeight(height);
		gamePane.getChildren().add(view);
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
			gamePane.getChildren().remove(view);
		cardsInPlay.clear();
	}

	//@@@Draw Deck and Center screen information@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

	void placeCenterField()
	{
		double offset = root.getScene().getWidth()/1920*100;
		double startY = root.getScene().getHeight()/3+offset;
		double startX = root.getScene().getWidth()/3;

		VBox center = new VBox();
		center.setLayoutX(startX);
		center.setLayoutY(startY);

		//Set each label in
		center.getChildren().addAll(turnLabel, infoLabel, userSelectLabel, playButton);
		//Make the playbutton invisible
		playButton.setVisible(false);
		//Add the above labels except for the play button
		gamePane.getChildren().addAll(center);
	}

	/**
	 * Draws the cards in deck, each one offset by a tiny amount to kind of see
	 * how many cards there are.
	 * Should call clearCardsInPlay() before calling this again.
	 * @throws FileNotFoundException
	 */
	void drawDeck() throws FileNotFoundException
	{
		double offset = root.getScene().getWidth()/1920*100;
		double posY = root.getScene().getHeight()/3 + offset;

		for(int i = deckCount; i > 0; --i)
		{
			cardsInPlay.add(drawFaceDownCard("green", false, offset+i*2, posY+i/5));
		}
	}

	//@@@Button drawing@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

	/**
	 * Initializes the array of card buttons the player presses
	 * Each one sets userSelect[1] to the character symbol of their respective card value
	 * @author Chris
	 */
	void initializeCardButtons()
	{
		cardButtons = new Button[13];	//There are 13 card values
		for(int i = 0; i < cardButtons.length; ++i)
		{
			cardButtons[i] = new Button();
			final int temp = i+1;

			cardButtons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.out.println(numberToSymbol(temp));
					userSelect[1] = numberToSymbol(temp);
					updateUserSelect(canSelect);
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
		ImageView imgV;

		for(int i = 0; i < playerAmt; ++i)
		{
			imgV = new ImageView(image);
			imgV.setFitHeight(STD_CARD_HEIGHT);
			imgV.setFitWidth(STD_CARD_HEIGHT);

			playerButtons[i] = new Button(game.clientLabels.get(i));
			playerButtons[i].setGraphic(imgV);

			final int temp = i;

			playerButtons[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					userSelect[0] = game.clientLabels.get(temp);
					updateUserSelect(canSelect);
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

		double offset = width/1920*50;
		double cardGap = ( (width-offset) - offset - STD_CARD_WIDTH)/uCards.size();
		double posY = height/3*2;

		double posX;
		int index;

		//Clear all the buttons currently, If the button is not there, no Ill effects occur
		for(int i = 0; i < cardButtons.length; ++i)
			gamePane.getChildren().remove(cardButtons[i]);

		int i = 0;
		for(Card uCard: uCards)
		{
			index = uCard.getVal().toInt()-1;
			posX = (double)(i++)*cardGap+offset;
			System.out.println(uCard.toString());
			drawExtraHandCards(yourCards.getDuplicityAmount(uCard.getVal()), posX, posY+STD_CARD_HEIGHT/5*4);
			cardButtons[index].setText("x"+yourCards.getDuplicityAmount(uCard.getVal()));
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

		double offset = width/1920*50;
		double playerGap = ( (width-offset) - offset - STD_CARD_WIDTH)/playerButtons.length;

		double posX;

		int tempIndex;
		for(int i = 0; i < playerButtons.length; ++i)
		{
			//Get the proper index for the other player's excluding you
			tempIndex = (i>yourID)?i-1:i;
			if(i!=yourID)
			{
				posX = (double)tempIndex*playerGap+offset;

				playerButtons[i].setLayoutX(posX);
				gamePane.getChildren().add(playerButtons[i]);
			}
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

		gamePane.getChildren().add(cardButton);
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
		double offset = width/1920*50;
		double playerGap = ( (width-offset) - offset - STD_CARD_WIDTH)/playerButtons.length;
		double posY = STD_CARD_HEIGHT;
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
				drawPairs(playerPairs[i], posX, posY + STD_MINI_CARD_HEIGHT);
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
		int columns = 4;

		double width = startX + STD_CARD_WIDTH;
		double cardGap = (width)/playerButtons.length;
		double posX;

		int i = 0;
		int j = 0;
		for(Card pair: pairs)
		{
			posX = (double)j*cardGap+startX;
			cardsInPlay.add(drawCard(pair, true, posX, startY+STD_MINI_CARD_HEIGHT*i));

			++j;
			if(j == columns)
			{
				j = 0;
				++i;
			}
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
		for(int i = cardAmount; i > 0; --i)
		{
			cardsInPlay.add(drawFaceDownCard("red", true, startX+15*(i-1), startY));
		}
	}

	private void drawExtraHandCards(int cardAmount, double startX, double startY) throws FileNotFoundException
	{
		for(int i = cardAmount - 1; i > 0; --i)
		{
			cardsInPlay.add(drawFaceDownCard("green", false, startX, startY+10*i));
		}
	}


}//end of GUI

	private ImageView drawCard(Card card, double posx, double posy) throws FileNotFoundException {
		return drawCard(card, posx, posy, STD_CARD_WIDTH, STD_CARD_HEIGHT);
	}

}