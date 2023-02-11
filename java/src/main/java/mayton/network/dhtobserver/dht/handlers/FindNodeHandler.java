package mayton.network.dhtobserver.dht.handlers;

import mayton.network.dhtobserver.chain.BasicHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindNodeHandler extends BasicHandler {

    static Logger logger = LoggerFactory.getLogger(FindNodeHandler.class);

    public FindNodeHandler(String description) {
        super(description);
        logger.info("constr");
    }
}
