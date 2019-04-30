package view;

import javafx.geometry.Pos;
import main.ClientController;
import main.ClientLauncher;
import main.ServerController;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
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

import java.util.HashMap;

public class ClientGUI extends Application {
	
	private ClientController client = null;
	private ServerController server = null;

	private static final double STD_CARD_WIDTH = 90.0;
	private static final double STD_CARD_HEIGHT = 135.0;
	private static final double STD_MINI_CARD_WIDTH = 30.0;
	private static final double STD_MINI_CARD_HEIGHT = 45.0;

	//GUI stuff
	private Scene main;
    private HashMap<String, Pane> sceneMap;
    private String[] userSelect;
    private boolean canSelect;

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
		VBox mainMenu = new VBox(20);
		mainMenu.setAlignment(Pos.CENTER);
		Text menuLabel = new Text("Main Menu");
		Button hostButton = new Button("Host Game");
		hostButton.setMinWidth(200);
		hostButton.setMinHeight(50);
		hostButton.setOnAction(event -> preHost());
		Button joinButton = new Button("Join Game");
        joinButton.setMinWidth(200);
        joinButton.setMinHeight(50);
		joinButton.setOnAction(event -> loadScene("Join"));
		Button exitButton = new Button("Exit");
        exitButton.setMinWidth(200);
        exitButton.setMinHeight(50);
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

	private void setupGameScene() throws FileNotFoundException {
		Pane gameScene = new VBox();
		double boxHeight = main.getHeight() / 3.0;
		Pane otherPlayers = new HBox(30);
		otherPlayers.setMinHeight(boxHeight);
		otherPlayers.setId("Players");
		Pane board = new HBox();
		board.setMinHeight(boxHeight);
		board.setId("Board");
		double boxWidth = main.getWidth() / 3.0;
		VBox deckBox = new VBox();
		deckBox.setMinWidth(boxWidth);
		deckBox.setAlignment(Pos.CENTER);
		deckBox.setId("DeckBox");
		VBox playBox = new VBox(15);
		playBox.setMinWidth(boxWidth);
		playBox.setAlignment(Pos.CENTER);
		playBox.setId("PlayBox");
		Pane pairBox = new Pane();
		pairBox.setMinWidth(boxWidth);
		pairBox.setId("PairBox");
		board.getChildren().addAll(deckBox, playBox, pairBox);
		HBox cards = new HBox(15);
		cards.setAlignment(Pos.CENTER);
		cards.setMinHeight(boxHeight);
		cards.setId("Cards");
		gameScene.getChildren().addAll(otherPlayers, board, cards);
		sceneMap.put("Game", gameScene);
		userSelect = new String[2];
        placePlayerButtons(initializePlayerButtons());
        drawDeck();
        placeCenterField();
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

	public void startGame() throws FileNotFoundException {
        setupGameScene();
        loadScene("Game");
    }

	private void game() {
		if(server != null)
		    server.startGame();
	}

	public void updateGame() throws FileNotFoundException {
        userSelect[0] = "<player>";
        userSelect[1] = "<card>";

        updateUserSelect(canSelect);
        updateCardButtons();
        drawPlayerPairsAndHands();
    }

	public void displayTurn() {
        Button queryButton = (Button) sceneMap.get("Game").lookup("#QueryButton");
        queryButton.setDisable(false);
        queryButton.setVisible(true);
        canSelect = true;
    }

    private void onQuery() {
        if(!validateUserSelect()) return;
        Button queryButton = (Button) sceneMap.get("Game").lookup("#QueryButton");
        client.sendQuery(userSelect[1], userSelect[0]);
        queryButton.setDisable(true);
        queryButton.setVisible(false);
        canSelect = false;
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

    private boolean validateUserSelect()
	{
		if(userSelect[0].equals("<player>"))
		{
			showAlert("Please select a player!");
			return false;
		}
		else if(userSelect[1].equals("<card>"))
		{
			showAlert("Please select a card!");
			return false;
		}
		return true;
	}

	private void updateUserSelect(boolean canUpdate)
	{
		if(canUpdate) {
		    Text userSelectLabel = (Text) sceneMap.get("Game").lookup("#UserSelect");
            userSelectLabel.setText("Ask "+userSelect[0]+" for any "+symbolToWord(userSelect[1])+"'s");
        }
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

	private ImageView drawCard(Card card, boolean mini, double posx, double posy) throws FileNotFoundException {
		return drawCard(card, posx, posy, (mini)?STD_MINI_CARD_WIDTH:STD_CARD_WIDTH, (mini)?STD_MINI_CARD_HEIGHT:STD_CARD_HEIGHT);
	}


	private ImageView drawFaceDownCard(String color, double posx, double posy, double width, double height) throws FileNotFoundException {
		Image image = new Image(new FileInputStream("resources\\"+color+"_back.png"));
		ImageView view = new ImageView(image);
		view.setX(posx);
		view.setY(posy);
		view.setFitWidth(width);
		view.setFitHeight(height);
		return view;
	}

	private ImageView drawFaceDownCard(String color, boolean mini, double posx, double posy) throws FileNotFoundException
	{
		return drawFaceDownCard(color, posx, posy, (mini)?STD_MINI_CARD_WIDTH:STD_CARD_WIDTH, (mini)?STD_MINI_CARD_HEIGHT:STD_CARD_HEIGHT);
	}

	/**
	 * Removes all the cards drawn that aren't associated to the cardButtons
	 */
    private void clearCardsInPlay()
	{
	    Pane cards = (HBox) sceneMap.get("Game").lookup("#Cards");
	    cards.getChildren().clear();
	}

	private void placeCenterField()
	{
		double offset = main.getWidth()/1920*100;
		double startY = main.getHeight()/3+offset;
		double startX = main.getWidth()/3;

		VBox playBox = (VBox) sceneMap.get("Game").lookup("#PlayBox");
		playBox.setLayoutX(startX);
		playBox.setLayoutY(startY);

		Text turnLabel = new Text();
		Text infoLabel = new Text();
		Text userSelectLabel = new Text();
		userSelectLabel.setId("UserSelect");
		Button queryButton = new Button("Ask");
		queryButton.setOnAction(event -> onQuery());
		queryButton.setId("QueryButton");
		queryButton.setDisable(true);
		queryButton.setVisible(false);

		playBox.getChildren().addAll(turnLabel, infoLabel, userSelectLabel, queryButton);
	}

	/**
	 * Draws the cards in deck, each one offset by a tiny amount to kind of see
	 * how many cards there are.
	 * Should call clearCardsInPlay() before calling this again.
	 * @throws FileNotFoundException TODO
	 */
    private void drawDeck() throws FileNotFoundException
	{
		double offset = main.getWidth()/1920*100;
		double posY = main.getHeight()/3 + offset;
		VBox deckBox = (VBox) sceneMap.get("Game").lookup("#DeckBox");
		int i = 0;
		deckBox.getChildren().add(drawFaceDownCard("green", false, offset+i*2, posY+i/5));
	}

	/**
	 * Initializes the array of card buttons the player presses
	 * Each one sets userSelect[1] to the character symbol of their respective card value
	 * @author Chris
     */
    private List<Button> initializeCardButtons(List<Card> uCards)
	{
		List<Button> cardButtons = new ArrayList<>();
		for(Card card : uCards)
		{
		    Card.Value value = card.getVal();
		    Button button = new Button();
			button.setOnAction(event -> {
                userSelect[1] = value.toChar();
                updateUserSelect(canSelect);
            });
            cardButtons.add(button);
		}
		return cardButtons;
	}

	/**
	 * Sets up the player buttons
	 * @author Chris
	 * @throws FileNotFoundException TODO
	 */
    private List<Button> initializePlayerButtons() throws FileNotFoundException
	{
		List<Button> playerButtons = new ArrayList<>();
		Image image = new Image(new FileInputStream("resources\\playa.png"));
        List<String> otherPlayers = new ArrayList<>(client.getPlayers());
        otherPlayers.remove(client.getPlayer().getName());

		for(String player : otherPlayers)
		{
			Button button = new Button(player);
            ImageView imgV = new ImageView(image);
            imgV.setFitHeight(STD_CARD_HEIGHT);
            imgV.setFitWidth(STD_CARD_HEIGHT);
			button.setGraphic(imgV);
			button.setOnAction(event -> {
			    userSelect[0] = player;
			    updateUserSelect(canSelect);
            });
			playerButtons.add(button);
		}
		return playerButtons;
	}

	/**
	 * Removes and redraws each card button as their availability
	 * is updated within the client's hand.
	 * Each card is shifted towards the left side of the screen
	 * NOTE: Need to make a call to clearCardsInPlay() before calling this again (for the duplicate cards drawn below the face up card).
	 * @author Chris
	 * @throws FileNotFoundException
	 */
    private void updateCardButtons() throws FileNotFoundException
	{
		List<Card> uCards = client.getPlayer().getUCards();

		Pane cards = (HBox) sceneMap.get("Game").lookup("#Cards");
		cards.getChildren().clear();
		List<Button> cardButtons = initializeCardButtons(uCards);

		double height = main.getHeight();
		double width = main.getWidth();

		double offset = width/1920*50;
		double cardGap = ( (width-offset) - offset - STD_CARD_WIDTH)/uCards.size();
		double posY = height/3*2;
		double posX;

		for(int i = 0; i < uCards.size(); i++)
		{
			posX = (double)i*cardGap+offset;
			//drawExtraHandCards(yourCards.getDuplicityAmount(uCard.getVal()), posX, posY+STD_CARD_HEIGHT/5*4);
			//cardButtons[index].setText("x"+yourCards.getDuplicityAmount(uCard.getVal()));
			placeCardButton(cardButtons.get(i), uCards.get(i), posX, posY);
		}
	}

	/**
	 * Places the player buttons.
	 * Should only be used once in the game.
	 */
    private void placePlayerButtons(List<Button> playerButtons)
	{
		double width = main.getWidth();

		double offset = width/1920*50;
		double playerGap = ( (width-offset) - offset - STD_CARD_WIDTH)/playerButtons.size();

		double posX;

		for(int i = 0; i < playerButtons.size(); ++i)
		{
            posX = (double)i*playerGap+offset;

            playerButtons.get(i).setLayoutX(posX);
		}
        Pane players = (HBox) sceneMap.get("Game").lookup("#Players");
		players.getChildren().addAll(playerButtons);
	}

	/**
	 * Just a rehash of An
	 * @param cardButton TODO
	 * @param card TODO
	 * @param posx TODO
	 * @param posy TODO
	 * @param width TODO
	 * @param height TODO
	 * @throws FileNotFoundException TODO
	 */
	private void placeCardButton(Button cardButton, Card card, double posx, double posy, double width, double height) throws FileNotFoundException {
		Image image = new Image(new FileInputStream(getCardImage(card)));
		ImageView view = new ImageView(image);
		view.setFitWidth(width);
		view.setFitHeight(height);
		cardButton.setGraphic(view);
		cardButton.setLayoutX(posx);
		cardButton.setLayoutY(posy);

		Pane cards = (HBox) sceneMap.get("Game").lookup("#Cards");
		cards.getChildren().add(cardButton);
	}
	void placeCardButton(Button cardButton, Card card, double posx, double posy) throws FileNotFoundException {
		placeCardButton(cardButton, card, posx, posy, STD_CARD_WIDTH, STD_CARD_HEIGHT);
	}

	/**
	 * Draws the cards for each player's pairs and a representation of their cards in hand face-down
	 * NOTE: Need to make a call to clearCardsInPlay() before calling this again.
	 * @author Chris
     */
    private void drawPlayerPairsAndHands() {

		double height = main.getHeight();
		double width = main.getWidth();
		double offset = width/1920*50;
		double playerGap = ( (width-offset) - offset - STD_CARD_WIDTH)/(client.getPlayers().size());
		double posY = STD_CARD_HEIGHT;
		double posX;

		//Used to hold the i in case we are working on players above the client's id
		int tempIndex;

		//TODO
		/*
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
		*/
	}
	/**
	 * Draws the pairs a given player has.
	 * @author Chris
	 * @param pairs	- The pairs the player has
	 * @param startX - Where we will start draw the players cards on the X Pane
	 * @param startY - Where we will start draw the players cards on the Y Pane
	 * @throws FileNotFoundException TODO
	 */
	private void drawPairs(List<Card> pairs, double startX, double startY) throws FileNotFoundException
	{
		int columns = 4;

		double width = startX + STD_CARD_WIDTH;
		double cardGap = (width)/(client.getPlayers().size());
		double posX;
		Pane board = (HBox) sceneMap.get("Game").lookup("#Board");

		int i = 0;
		int j = 0;
		for(Card pair: pairs)
		{
			posX = (double)j*cardGap+startX;
			board.getChildren().add(drawCard(pair, true, posX, startY+STD_MINI_CARD_HEIGHT*i));

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
	 * @throws FileNotFoundException TODO
	 */
	private void drawHand(int cardAmount, double startX, double startY) throws FileNotFoundException
	{
        Pane board = (HBox) sceneMap.get("Game").lookup("#Board");
		for(int i = cardAmount; i > 0; --i)
		{
			board.getChildren().add(drawFaceDownCard("red", true, startX+15*(i-1), startY));
		}
	}

	private void drawExtraHandCards(int cardAmount, double startX, double startY) throws FileNotFoundException
	{
        Pane board = (HBox) sceneMap.get("Game").lookup("#Board");
		for(int i = cardAmount - 1; i > 0; --i)
		{
			board.getChildren().add(drawFaceDownCard("green", false, startX, startY+10*i));
		}
	}

	private ImageView drawCard(Card card, double posx, double posy) throws FileNotFoundException {
		return drawCard(card, posx, posy, STD_CARD_WIDTH, STD_CARD_HEIGHT);
	}

}