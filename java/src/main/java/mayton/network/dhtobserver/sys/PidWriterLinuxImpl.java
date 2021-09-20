package mayton.network.dhtobserver.sys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

public class PidWriterLinuxImpl implements PidWriter {

    static Logger logger = LogManager.getLogger(PidWriterLinuxImpl.class);

    @Override
    public void write() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.substring(0, name.indexOf("@")); // Plattform dependent method.
        logger.info("name = {}, pid = {}", name, pid);
        try(OutputStream pidStream = new FileOutputStream("dht-observer-app.pid")) {
            pidStream.write(pid.getBytes(StandardCharsets.UTF_8));
            logger.info("done");
        } catch (IOException e) {
            logger.error("IOException in PidWriterLinuxImpl", e);
        }
    }
}
