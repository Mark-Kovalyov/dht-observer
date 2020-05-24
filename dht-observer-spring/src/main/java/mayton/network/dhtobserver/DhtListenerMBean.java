package mayton.network.dht;

// TODO: Migrate to Spring JMX
@Deprecated
public interface DhtListenerMBean {

    int getPacketsReceived();

    int getPacketsParsed();

    int getPacketsRejected();

}
