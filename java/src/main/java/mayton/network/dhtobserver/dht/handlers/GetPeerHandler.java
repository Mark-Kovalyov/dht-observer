package mayton.network.dhtobserver.dht.handlers;

import mayton.network.dhtobserver.chain.BasicHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetPeerHandler extends BasicHandler {

    static Logger logger = LoggerFactory.getLogger(GetPeerHandler.class);

    public GetPeerHandler(String description) {
        super(description);
        logger.info("constr");
    }
}
