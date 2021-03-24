package mayton.network.dhtobserver;

import mayton.network.NetworkUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.annotation.concurrent.ThreadSafe;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

//@ThreadSafe
//@ManagedResource(objectName="bean:name=mayton.network.dhtobserver.GeoDb")
//@Component
public class GeoDb {

    private static Logger logger = LogManager.getLogger(GeoDb.class);

    private static List<GeoRecord> geoRecords = new ArrayList<>();

    private AtomicInteger requests = new AtomicInteger();
    private AtomicLong cumulativeResponseTimeMs = new AtomicLong();

    private String csvPath;


    public void postConstruct() {
        logger.info("init from {}" , csvPath);
        try(CSVParser csvParser = new CSVParser(new FileReader(csvPath), CSVFormat.DEFAULT.withSkipHeaderRecord(true))) {
            Iterator<CSVRecord> i = csvParser.iterator();
            int cnt = 0;
            i.next();
            while(i.hasNext()) {
                CSVRecord record = i.next();
                String country = record.get(2);
                String city = record.get(4);
                long begin = NetworkUtils.parseIpV4(record.get(0));
                long end = NetworkUtils.parseIpV4(record.get(1));
                geoRecords.add(new GeoRecord(country, city, begin, end));
                cnt++;
            }
            logger.info("init CSV records loaded. Sorting..");
            geoRecords.sort(GeoRecord.beginIpComparator);
            logger.info("init done, {} records loaded and sorted", cnt);
        } catch (NumberFormatException | IOException ex) {
            logger.error(ex);
        }
    }

    private static Optional<GeoRecord> findFirstFast(long ipv4, List<GeoRecord> geoRecordsArg) {
        // TODO: Implement
        int length = geoRecordsArg.size();
        int upperIndex = 0;
        int lowerIndex = length - 1;
        GeoRecord upperBound = geoRecordsArg.get(upperIndex);
        GeoRecord lowerBound = geoRecordsArg.get(lowerIndex);
        boolean upperLocated = false;
        boolean lowerLocated = false;
        do {

        } while (!upperLocated || !lowerLocated);

        return Optional.empty();
    }

    @Deprecated
    public synchronized Optional<GeoRecord> findFirstStuped(long ipv4, List<GeoRecord> geoRecordsArg) {
        for (GeoRecord gr : geoRecordsArg) {
            if (ipv4 >= gr.beginIp && ipv4 <= gr.endIp) {
                return Optional.of(gr);
            }
        }
        return Optional.empty();
    }

    public Optional<GeoRecord> findFirst(long ipv4) {
        requests.incrementAndGet();
        long begin = System.currentTimeMillis();
        Optional<GeoRecord> result = findFirstStuped(ipv4, geoRecords);
        long end = System.currentTimeMillis();
        cumulativeResponseTimeMs.addAndGet(end - begin);
        return result;
    }

    public String decodeCountryCity(String ip) {
        logger.trace("ip={}", ip);
        long ipv4 = NetworkUtils.parseIpV4(ip);
        logger.trace("ip(integer)={}", ipv4);
        Optional<GeoRecord> res = findFirst(ipv4);
        String stringResult = res.isPresent() ? res.get().toString() : "Uknown";
        logger.trace("resolved country city = {}", stringResult);
        return stringResult;
    }



}
