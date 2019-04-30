package main;

enum ServerMessage {
    PLAYER_LIST, PLAYER_CONNECT, PLAYER_DISCONNECT, START_GAME, DEAL_HAND, START_TURN, QUERY, QUERY_RESPONSE, ANNOUNCE_WINNER;

    String toUTF(){
        return toString() + ":";
    }
}
