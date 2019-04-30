package main;

import view.ClientGUI;

public class ClientLauncher {

	private static ClientController client;
	private static ServerController server;
	private static ClientGUI gui;
	
	public static void main(String[] args) {
		ClientGUI.launchGUI(args);
	}

	public static void setGui(ClientGUI gui){
		ClientLauncher.gui = gui;
	}

	public static ServerController launchServer(){
		return server = new ServerController(gui);
	}

	public static ClientController launchClient(){
		return client = new ClientController(gui);
	}
}
