package mayton.network.dhtobserver;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import mayton.network.dhtobserver.dht.Ping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.lang.System.getProperty;
import static java.lang.System.in;
import static java.util.concurrent.TimeUnit.SECONDS;

//@SpringBootApplication
public class DhtObserverApplication {

    static Logger logger = LogManager.getLogger(DhtObserverApplication.class);

    //@Autowired
    public DhtListener dhtListenerVuze16680() {
        return new DhtListener("Vuze", 16680, "VZ1");
    }

    //@Autowired
    public DhtListener dhtListenerVuze49001() {
        return new DhtListener("Vuze", 49001, "VZ2");
    }

    //@Autowired
    public DhtListener dhtListenerAmule4665() {
        return new DhtListener("A-Mule", 4665, "AM1");
    }

    //@Autowired
    public DhtListener dhtListenerAmule4672() {
        return new DhtListener("A-Mule", 4672, "AM2");
    }

    //@Autowired
    public DhtListener dhtListenerTransmission51413() {
        return new DhtListener("Transm", 51413, "TR1");
    }

    public DhtListener dhtListenerTransmission48529() {
        return new DhtListener("Transm", 48529, "TR3");
    }

    //@Autowired
    public DhtListener dhtListenerTorrent46434() {
        return new DhtListener("Transm", 46434, "TR2");
    }

    //@Autowired
    public ExecutorService executorService() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("DHTWorker-%d")
                .build();
        return Executors.newCachedThreadPool(threadFactory); // Thread pool with 2 billion of maximum threads
    }

    @SuppressWarnings("java:S1104")
    public List<DhtListener> dhtListenerList = new ArrayList<>() {{
        add(dhtListenerTransmission51413());
        add(dhtListenerTorrent46434());
        add(dhtListenerAmule4672());
        add(dhtListenerAmule4665());
        add(dhtListenerVuze16680());
        add(dhtListenerVuze49001());
        add(dhtListenerTransmission48529());
    }};

    public DhtObserverApplication() {
        dhtListenerList.forEach(thread -> executorService().execute(thread));
    }

    //@Override
    public void run(String... args) throws Exception {

    }

	public static void main(String[] args) {
        System.setProperty("log4j.configurationFile","log4j2.xml");
        System.out.printf("LogManager.context = %s\n", LogManager.getContext(true));
        Injector injector = Guice.createInjector(new DhtObserverModule());
        //Chronicler chronicler = injector.getInstance(Chronicler.class);
        //chronicler.onPing(new Ping(UUID.randomUUID().toString()));
        logger.info(":: start with user.dir = {}", getProperty("user.dir"));
        //SpringApplication springApplication = new SpringApplication(DhtObserverApplication.class);
        //springApplication.addListeners(new ApplicationPidFileWriter("./dht-observer-app.pid"));
        //springApplication.run(args);
        new DhtObserverApplication();
	}

    //@PreDestroy
    public void preDestroy() {
        logger.info(":: signalling stop for all threads");
        dhtListenerList.forEach(item -> item.askStop());
        logger.info(":: waiting for cachedthreadpool shutdown");
        executorService().shutdown();
        try {
            logger.info(":: waiting 7 s a while for existing tasks to terminate");
            if (!executorService().awaitTermination(7, SECONDS)) {
                logger.info(":: trying to shut down now!");
                executorService().shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executorService().awaitTermination(3, SECONDS)) {
                    logger.warn(":: shutdown is not OK");
                } else {
                    logger.info(":: shutdown OK");
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.warn(ie);
            // (Re-)Cancel if current thread also interrupted
            executorService().shutdownNow();
            // Preserve interrupt status
        }
        logger.info(":: STOP FROM THE LIFECYCLE");
    }


}
