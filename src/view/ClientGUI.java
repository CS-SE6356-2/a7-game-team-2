package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Card;
import model.Hand;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

public class ClientGUI extends Application{
	
	ClientController game = null;

	private double STD_CARD_WIDTH = 150.0;
	private double STD_CARD_HEIGHT = 210.0;

	String state;
	String yourName;
	//server to client variables
	int yourID;
	Hand yourCards;
	int deckCount;
	int cardCounts[];
	List<Card>[] playerPairs;



	//GUI stuff
	private Scene main;
	private Pane root;
	private HashMap<String, Pane> sceneMap;
	
	@Override
	public void start(Stage stage) {
		game = ClientLauncher.game;
		ClientLauncher.gui = this;
		game.gui = ClientLauncher.gui;
		
		state = "";
		yourName = "Player";
		yourCards = new Hand();

		root = new Pane();
		main = new Scene(root, 1280, 720);
		sceneMap = new HashMap<>();
		setupMainMenuScene();
		setupHostSetupScene();
		setupJoinScene();
		
		main();
		
		stage.setTitle("Card Game Project");
		stage.setScene(main);
        stage.show();
	}
	private void setupMainMenuScene() {
		Pane mainMenu = new VBox(10);
		Text menuLabel = new Text("Main Menu");
		Button hostButton = new Button("Host Game");
		hostButton.setOnAction(event -> preHost());
		Button joinButton = new Button("Join Game");
		joinButton.setOnAction(event -> join());
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
		Button backButton = new Button("Back");
		backButton.setOnAction(event -> main());
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
		Button backButton = new Button("Back");
		backButton.setOnAction(event -> main());
		join.getChildren().addAll(menuLabel, infoLabel, addressInput, nameInput, connectButton, backButton);
		sceneMap.put("Join", join);
	}
	private void setupLobbyScene(){
		Pane lobby = new VBox(10);
		Text menuLabel = new Text("Lobby");
		Text addressLabel = new Text(game.server.toString());
		Text infoLabel = new Text(yourName);
		Button startButton = new Button("Start Game");
		startButton.setOnAction(event -> game());
		startButton.setDisable(!game.isServer);
		Button leaveButton = new Button("Leave Lobby");
		leaveButton.setOnAction(event -> main());
		lobby.getChildren().addAll(menuLabel, addressLabel, infoLabel, startButton, leaveButton);
		sceneMap.put("Lobby", lobby);
	}
	private void setupGameScene() {
		Pane gameScene = new Pane();
		gameScene.getChildren().addAll();
		sceneMap.put("Game", gameScene);
	}

	private void loadScene(String scene){
		if(sceneMap.keySet().contains(scene))
			main.setRoot(sceneMap.get(scene));
		else {
			System.out.println(scene + " not found.");
		}
	}
	
	//methods called by buttons
	private void main() {
		if(state.equals("hosting") || state.equals("lobby"))
			game.closeSocks(state);
		state = "main";
		loadScene("MainMenu");
	}
	
	private void preHost() {
		state = "host";
		loadScene("HostSetup");
	}
	
	private void hosting() {
		String name = ((TextField) main.lookup("#NameInput")).getText();

		if(name.isEmpty()) {
			new Alert(Alert.AlertType.ERROR, "Enter your name!").show();
			return;
		}
		else if(!validateName(name))
		{
			new Alert(Alert.AlertType.ERROR, "Please use only the characters a-zA-Z0-9 or ' '").show();
			return;
		}
		
		game.setupHost();
		if(!game.isHosting()) {
			new Alert(Alert.AlertType.ERROR, "Unable to establish Host").show();
			return;
		}
		
		String result = game.connectToHost(game.getServerAddress(), name);
		if(!result.equals("Connected!")) {
			new Alert(Alert.AlertType.ERROR, result).show();
			return;
		}
		
		setupLobbyScene();
		
		state = "hosting";
		
		game.isServer = true;
		game.serverThread.start();
		game.clientThread.start();
	}
	
	private void join() {
		state = "join";
		loadScene("Join");
	}
	
	private void connect() {
		String name = ((TextField) main.lookup("#NameInput")).getText();
		String address = ((TextField) main.lookup("#AddressInput")).getText();

		if(name.isEmpty()) {
			new Alert(Alert.AlertType.ERROR, "Enter your name!").show();
			return;
		}
		else if(!validateName(name))
		{
			new Alert(Alert.AlertType.ERROR, "Please use only the characters a-zA-Z0-9 or ' '").show();
			return;
		}
		if(address.isEmpty()) {
			new Alert(Alert.AlertType.ERROR, "Enter the host address!").show();
			return;
		}
		if(address.split(":", 0).length != 2) {
			new Alert(Alert.AlertType.ERROR, "Invalid host name!").show();
			return;
		}

		String result = game.connectToHost(address, name);

		if(!result.equals("Connected!")) {
			new Alert(Alert.AlertType.ERROR, result).show();
			return;
		}
		lobby();
	}
	
	private void lobby(){
		setupLobbyScene();
		state = "lobby";
		game.clientThread.start();
	}
	
	void game() {
		setupGameScene();
		state = "game";
	}
	
	/*private void play() {
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
	//returns true if the message was successfully sent without error
	boolean endTurn(String messageToServer) {
		boolean success = false;
		int attempts = 0;//keeps track of attempts
		while(!success && attempts++ < 10){//tries ten times talk to server
			try {
				game.writeToServer(messageToServer);
				success = true;
			} catch (IOException ignored) {}
		}
		return success;
	}*/

	
	void launchGUI() {
		launch();
	}
	
	private boolean validateName(String name)
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

}



