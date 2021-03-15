package mayton.network.dhtobserver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "dhtconfig")
public class DhtConfig {

    Map<String, DhtConfigParams> paramsMap;

}
