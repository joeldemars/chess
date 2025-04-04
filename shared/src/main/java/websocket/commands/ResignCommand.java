package websocket.commands;

public class ResignCommand extends UserGameCommand {
    public String user;

    public ResignCommand(String authToken, Integer gameID, String user) {
        super(CommandType.RESIGN, authToken, gameID);
        this.user = user;
    }
}
