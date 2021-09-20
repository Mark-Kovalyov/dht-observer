package mayton.network.dhtobserver.geo;

import com.google.inject.Inject;
import mayton.network.NetworkUtils;
import mayton.network.dhtobserver.GeoDb;
import mayton.network.dhtobserver.Utils;
import mayton.network.dhtobserver.jfr.GeoEnrichmentEvent;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GeoDbCsvImpl implements GeoDb {

    private static Logger logger = LoggerFactory.getLogger(GeoRecord.class);

    private List<GeoRecord> geoRecords;

    private String csvPath = "/bigdata/GeoIPCity.utf-8.csv";

    @Inject
    public void init() {
        Profiler profiler = new Profiler("GeoDbImpl::init");
        profiler.setLogger(logger);
        profiler.start("loading from csv");
        List<GeoRecord> geoRecordsTemp = new ArrayList<>();
        try(CSVParser csvParser = new CSVParser(new FileReader(csvPath), CSVFormat.DEFAULT.withSkipHeaderRecord(true))) {
            Iterator<CSVRecord> i = csvParser.iterator();
            int cnt = 0;
            i.next();
            while(i.hasNext()) {
                CSVRecord record = i.next();
                String country = record.get(2);
                String region = record.get(3);
                String city = record.get(4);
                long begin = NetworkUtils.parseIpV4(record.get(0));
                long end = NetworkUtils.parseIpV4(record.get(1));
                geoRecordsTemp.add(new GeoRecord(country, city,region, begin, end));
                cnt++;
            }
            //logger.info("init CSV records loaded. Sorting..");
            profiler.start("sorting");
            geoRecordsTemp.sort(GeoRecord.beginIpComparator);
            //logger.info("init done, {} records loaded and sorted", cnt);
            geoRecords = Collections.unmodifiableList(geoRecordsTemp);
            TimeInstrument report = profiler.stop();
            report.log();
            //saveToAvro(geoRecords);
        } catch (NumberFormatException | IOException ex) {
            logger.error("Exception GeoDbImpl", ex);
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
        GeoEnrichmentEvent geoEnrichmentEvent = new GeoEnrichmentEvent();
        geoEnrichmentEvent.begin();
        Optional<GeoRecord> result = findFirstStuped(ipv4, geoRecords);
        geoEnrichmentEvent.ip = NetworkUtils.formatIpV4(ipv4);
        geoEnrichmentEvent.commit();
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
