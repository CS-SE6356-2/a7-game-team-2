package view;

enum ServerMessage {
    PLAYER_LIST, PLAYER_CONNECT, PLAYER_DISCONNECT, START_GAME;

    String toUTF(){
        return toString() + ":";
    }
}
