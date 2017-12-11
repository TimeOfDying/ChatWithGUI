public interface TCPconnectionListener {

    void ConnectionReady(TCPconnection tcpconnection);
    void ReceiveString(TCPconnection tcpconnection, String value);
    void Disconnect(TCPconnection tcpconnection);
    void Exception(TCPconnection tcPconnection, Exception e);

}
