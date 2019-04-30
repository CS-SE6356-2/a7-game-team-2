package view;

import main.ClientController;
import main.ClientLauncher;
import main.ServerController;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class ClientGUI extends Application{
	
	private ClientController client = null;
	private ServerController server = null;

	private static final double STD_CARD_WIDTH = 150.0;
	private static final double STD_CARD_HEIGHT = 210.0;

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

	private ImageView drawCard(Card card, double posx, double posy) throws FileNotFoundException {
		return drawCard(card, posx, posy, STD_CARD_WIDTH, STD_CARD_HEIGHT);
	}

}



