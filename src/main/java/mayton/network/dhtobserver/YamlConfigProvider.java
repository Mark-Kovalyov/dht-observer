package mayton.network.dhtobserver;

import com.google.inject.Inject;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Refactor with @InjectConfig / @BindConfig
public class YamlConfigProvider implements ConfigProvider{

    static Logger logger = LogManager.getLogger(YamlConfigProvider.class);

    private Object root;

    @Inject
    public void init() {
        logger.info("init");
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.yaml");
        root = yaml.load(inputStream);
        logger.info("init done");
    }

    public List<Triple<String, Integer, String >> threadConfig() {
        Object listenersArr = ((LinkedHashMap<String, Object>)(((LinkedHashMap<String, Object>)root).get("application"))).get("listeners");

        ArrayList<LinkedHashMap> res = (ArrayList) listenersArr;

        // TODO:
        return Collections.unmodifiableList(
                res.stream()
                        .map(item -> mapToTriple((LinkedHashMap<String, Object>) item))
                        .collect(Collectors.toList()));
    }

    public static Triple<String, Integer, String> mapToTriple(LinkedHashMap<String, Object> lhm) {
        return Triple.of((String) lhm.get("threadName"), (Integer) lhm.get("port"), (String) lhm.get("shortCode"));
    }

}
