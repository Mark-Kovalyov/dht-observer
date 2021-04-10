package mayton.network.dhtobserver;

import mayton.network.dhtobserver.db.Chronicler;
import mayton.network.dhtobserver.db.Reporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static java.lang.System.getProperty;
import static java.util.concurrent.TimeUnit.SECONDS;

public class DhtObserverApplication {

    static Logger logger = LogManager.getLogger(DhtObserverApplication.class);

    public static List<DhtListener> dhtListenerList;

    public DhtObserverApplication() {
        ConfigProvider configProvider = ConfigProviderService.create();

        dhtListenerList = configProvider.threadConfig()
                .stream()
                .map(i -> new DhtListener(i.getLeft(), i.getRight())).collect(Collectors.toList());

        dhtListenerList.forEach(thread -> {
            ExecutorServiceProvider executorServiceProvider = ExecutorServiceProviderService.create();
            executorServiceProvider.executorService().execute(thread);
        });
    }

	public static void main(String[] args) {

        System.setProperty("log4j.configurationFile","log4j2.xml");
        System.out.printf("LogManager.context = %s\n", LogManager.getContext(true));
        System.out.printf("User dir = %s\n", System.getProperty("user.dir"));
        System.out.printf("Java version = %s\n", System.getProperty("java.version"));
        System.out.printf("Java VM name = %s\n", System.getProperty("java.vm.name"));
        List<GarbageCollectorMXBean> gcMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        System.out.printf("GC info:\n");
        for (GarbageCollectorMXBean gcMxBean : gcMxBeans) {
            System.out.printf(" - GC bean name : %s, objectName %s\n", gcMxBean.getName(),gcMxBean.getObjectName());
        }
        Thread shutdownHook = new Thread(() -> {
            logger.warn("Shutdown hook called!");
            ExecutorService executorService = DaggerExecutorService.create();
            executorService.shutdown();
            logger.warn(":: signalling stop for all threads");
            dhtListenerList.forEach(DhtListener::askStop);
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
                Chronicler chronicler = ChroniclerService.create().close();
                Reporter reporter = ReporterService.create().close();
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
