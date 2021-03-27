package mayton.network.dhtobserver.security;

import com.google.inject.Inject;
import mayton.network.NetworkUtils;
import mayton.network.dhtobserver.IpFilter;
import mayton.network.dhtobserver.geo.GeoDbImpl;
import mayton.network.dhtobserver.geo.GeoRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//000.000.000.000 - 000.255.255.255 , 000 , Bogon
//001.002.004.000 - 001.002.004.255 , 000 , China Internet Information Center (CNNIC)
//001.002.008.000 - 001.002.008.255 , 000 , China Internet Information Center (CNNIC)
//001.009.096.105 - 001.009.096.105 , 000 , Botnet on Telekom Malaysia
//001.009.102.251 - 001.009.102.251 , 000 , Botnet on Telekom Malaysia
//001.009.106.186 - 001.009.106.186 , 000 , Botnet on Telekom Malaysia
//001.016.000.000 - 001.019.255.255 , 000 , Korea Internet & Security Agency (KISA) - IPv6 Policy
public class IpFilterEmule implements IpFilter {

    private static Logger logger = LogManager.getLogger(IpFilterEmule.class);

    private List<IpSecurityEntity> ipSecurityEntities = new ArrayList<>();

    private String guardingPath = "/storage/db/amule/guarding.p2p";

    @Inject
    public void init() {
        logger.info("init()");
        try (Stream<String> stream = Files.lines(Paths.get(guardingPath))) {
            stream.forEach(item -> {
                IpSecurityEntity ipSecurityEntity = new IpSecurityEntity(
                        NetworkUtils.parseIpV4(item.substring(0,15)),
                        NetworkUtils.parseIpV4(item.substring(18,18 + 15)),
                        item.substring(43));
                logger.debug("entity = {}", ipSecurityEntity.toString());
                ipSecurityEntities.add(ipSecurityEntity);
            });
            logger.info("init done with {} enitities", ipSecurityEntities.size());
        } catch (IOException e) {
            logger.error("!", e);
        }
    }

    @Override
    public boolean isAllowedIpv4(String ipv4) {
        long ip = NetworkUtils.parseIpV4(ipv4);
        for(IpSecurityEntity ipSecurityEntity : ipSecurityEntities) {
            if (ip >= ipSecurityEntity.beginIp && ip <= ipSecurityEntity.endIp) {
                return true;
            }
        }
        return false;
    }
}
