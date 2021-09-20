package mayton.network.dhtobserver;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutorServiceProviderImpl implements ExecutorServiceProvider {

    private ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("DHTWorker-%d")
            .build();

    private final ExecutorService executorService;

    public ExecutorServiceProviderImpl() {
        executorService = Executors.newCachedThreadPool(threadFactory);
    }

    public ExecutorService executorService() {
        return executorService;
    }

}
