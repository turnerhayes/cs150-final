package edu.tufts.hrilab.boxbot;

import java.io.IOException;

import edu.tufts.hrilab.socket.SocketConnection;
import edu.tufts.hrilab.boxbot.actions.Active;

public class GamePlay {
    SocketConnection sock;
    BoxBotObservation observation;
    
    public GamePlay(int socketPort){
        try {
            this.sock = new SocketConnection(socketPort);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    
    public void perform(GameAction action) {
        this.sock.sendCommand(action.getCommand());
        String response = this.sock.waitedResponse(1000);

        action.insertResponse(response);

        this.observation = ((Active)action).getObservation();
    }
}
