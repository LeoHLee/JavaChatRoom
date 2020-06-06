package chatbean;

import java.net.Socket;

public interface Parser {
    /**
     * parse a ChatBean
     * @param bean the ChatBean to parse
     * @param source the client socket to send reply to (ignore this on a client
     */
    public void parse(ChatBean bean, Socket source);
}
