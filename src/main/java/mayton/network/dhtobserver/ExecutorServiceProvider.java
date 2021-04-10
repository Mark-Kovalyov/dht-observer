package mayton.network.dhtobserver;

import dagger.Component;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;

@Singleton
@Component(modules = DhtObserverModule.class)
public interface ExecutorServiceProvider {

    ExecutorService executorService();

}
