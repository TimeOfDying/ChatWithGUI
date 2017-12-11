public interface TCPconnectionListener {

    void onConnectionReady(TCPconnection tcpconnection);
    void onReceiveString(TCPconnection tcpconnection, String value);
    void onDisconnect(TCPconnection tcpconnection);
    void onException(TCPconnection tcPconnection, Exception e);


}
