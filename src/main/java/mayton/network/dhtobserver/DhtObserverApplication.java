package mayton.network.dhtobserver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.lang.System.getProperty;
import static java.util.concurrent.TimeUnit.SECONDS;

public class DhtObserverApplication {

    static Logger logger = LogManager.getLogger(DhtObserverApplication.class);

    public static Injector injector = Guice.createInjector(DhtObserverModule.dhtObserverModule);

    /*public DhtListener dhtListenerVuze16680() {
        return new DhtListener("Vuze", 16680, "VZ1");
    }
    public DhtListener dhtListenerVuze49001() {
        return new DhtListener("Vuze", 49001, "VZ2");
    }
    public DhtListener dhtListenerAmule4665() {
        return new DhtListener("A-Mule", 4665, "AM1");
    }
    public DhtListener dhtListenerAmule4672() {
        return new DhtListener("A-Mule", 4672, "AM2");
    }
    public DhtListener dhtListenerTorrent46434() {
        return new DhtListener("Transm", 46434, "TR2");
    }*/

    public static DhtListener dhtListenerTransmission51413() {  return new DhtListener("Transm", 51413, "TR1");    }

    public static DhtListener dhtListenerTransmission48529() {
        return new DhtListener("Transm", 48529, "TR3");
    }

    public static List<DhtListener> dhtListenerList = new ArrayList<>() {{
        add(dhtListenerTransmission51413());
        /*add(dhtListenerTorrent46434());
        add(dhtListenerAmule4672());
        add(dhtListenerAmule4665());
        add(dhtListenerVuze16680());
        add(dhtListenerVuze49001());*/
        add(dhtListenerTransmission48529());
    }};

    public DhtObserverApplication() {
        dhtListenerList.forEach(thread -> injector.getInstance(ExecutorServiceProvider.class).executorService().execute(thread));
    }

	public static void main(String[] args) {

        System.setProperty("log4j.configurationFile","log4j2.xml");
        System.out.printf("LogManager.context = %s\n", LogManager.getContext(true));

        Thread shutdownHook = new Thread(() -> {
            logger.warn("Shutdown hook called!");
            ExecutorService executorService = injector.getInstance(ExecutorServiceProvider.class).executorService();
            executorService.shutdown();
            logger.warn(":: signalling stop for all threads");
            dhtListenerList.forEach(item -> item.askStop());
            logger.warn(":: waiting for cachedthreadpool shutdown");
            try {
                logger.warn(":: waiting 7 s a while for existing tasks to terminate");
                if (!executorService.awaitTermination(7, SECONDS)) {
                    logger.warn(":: trying to shut down now!");
                    executorService.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!executorService.awaitTermination(3, SECONDS)) {
                        logger.warn(":: shutdown is not OK");
                    } else {
                        logger.warn(":: shutdown OK");
                    }
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logger.warn(ie);
                // (Re-)Cancel if current thread also interrupted
                executorService.shutdownNow();
                // Preserve interrupt status
            }
            try {
                injector.getInstance(Chronicler.class).close();
                injector.getInstance(Reporter.class).close();
            } catch (Exception ex) {
                logger.warn("!", ex);
            }
            logger.warn("Shutdown finished!");
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        logger.info(":: start with user.dir = {}", getProperty("user.dir"));
        new DhtObserverApplication();
	}



}
