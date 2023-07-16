package mayton.network.dhtobserver.dht.handlers;

import mayton.network.dhtobserver.chain.BasicHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnouncePeerHandler extends BasicHandler {

    static Logger logger = LoggerFactory.getLogger(AnnouncePeerHandler.class);

    public AnnouncePeerHandler(String description) {
        super(description);
        logger.info("constr");
    }
}
