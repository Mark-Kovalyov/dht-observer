package mayton.network.dhtobserver.geo;

import mayton.network.NetworkUtils;
import mayton.network.dhtobserver.GeoDb;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GeoDbImpl implements GeoDb {

    private static Logger logger = LogManager.getLogger(GeoDbImpl.class);

    private List<GeoRecord> geoRecords;

    private String csvPath = "/bigdata/GeoIPCity.utf-8.csv";

    public GeoDbImpl() {
        logger.info("init from {} with objectId = {}" , csvPath, System.identityHashCode(this));
        List<GeoRecord> geoRecordsTemp = new ArrayList<>();
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
                geoRecordsTemp.add(new GeoRecord(country, city, begin, end));
                cnt++;
            }
            logger.info("init CSV records loaded. Sorting..");
            geoRecordsTemp.sort(GeoRecord.beginIpComparator);
            logger.info("init done, {} records loaded and sorted", cnt);
            geoRecords = Collections.unmodifiableList(geoRecordsTemp);
        } catch (NumberFormatException | IOException ex) {
            logger.error(ex);
        }
    }

    public Optional<GeoRecord> findFirstFast(long ipv4, List<GeoRecord> geoRecordsArg) {
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
        Optional<GeoRecord> result = findFirstStuped(ipv4, geoRecords);
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
