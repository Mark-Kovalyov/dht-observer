package mayton.network.dhtobserver;

import com.google.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

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

    private LinkedHashMap<String, Object> application;

    @Inject
    public void init() {
        logger.info("init");
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.yaml");
        root = yaml.load(inputStream);
        application = (LinkedHashMap<String, Object>) ((LinkedHashMap<String, Object>) root).get("application");
        logger.info("init done");
    }

    public List<Pair<Integer, String >> threadConfig() {
        Object listenersArr = application.get("listeners");

        ArrayList<LinkedHashMap> res = (ArrayList) listenersArr;

        // TODO:
        return Collections.unmodifiableList(
                res.stream()
                        .map(item -> mapToTriple((LinkedHashMap<String, Object>) item))
                        .collect(Collectors.toList()));
    }

    @Override
    public String getNodeId() {
        return (String) application.get("peer_id");
    }

    public static Pair<Integer, String> mapToTriple(LinkedHashMap<String, Object> lhm) {
        return Pair.of((Integer) lhm.get("port"), (String) lhm.get("shortCode"));
    }

}
