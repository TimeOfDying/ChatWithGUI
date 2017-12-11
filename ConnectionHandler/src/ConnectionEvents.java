public interface ConnectionEvents {

    void ConnectionReady(Connection connection);
    void ReceiveString(Connection connection, String value);
    void Disconnect(Connection connection);
    void Exception(Connection connection, Exception ex);

}
