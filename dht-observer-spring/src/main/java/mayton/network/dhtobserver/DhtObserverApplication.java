package mayton.network.dhtobserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class DhtObserverApplication {

    static Logger logger = LoggerFactory.getLogger(DhtObserverApplication.class);

	public static void main(String[] args) {
	    logger.info(":: start");
		SpringApplication.run(DhtObserverApplication.class, args);
	}

    @PreDestroy
    public void onExit() {
        logger.info(":: stopping");
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            logger.error("", e);;
        }
        logger.info(":: STOP FROM THE LIFECYCLE");
    }

}
