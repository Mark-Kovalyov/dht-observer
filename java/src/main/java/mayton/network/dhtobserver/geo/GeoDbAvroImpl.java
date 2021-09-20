package mayton.network.dhtobserver.geo;

import com.google.inject.Inject;
import mayton.network.dhtobserver.GeoDb;
import mayton.probeavro.geoip.GeoIpCityAvroEntityV2;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Range;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Performance check #1
//
// 2021-07-14T18:38:18,303 [INFO ] 15  : GeoDbImpl init from /bigdata/GeoIPCity.utf-8.csv with objectId = 651673999
// 2021-07-14T18:38:30,972 [INFO ] 15  : GeoDbImpl init CSV records loaded. Sorting..
// 2021-07-14T18:38:31,051 [INFO ] 15  : GeoDbImpl init done, 5748952 records loaded and sorted
//
public class GeoDbAvroImpl implements GeoDb {

    private List<GeoIpCityAvroEntityV2> entityList = new ArrayList<>();

    private static Logger logger = LogManager.getLogger(GeoDbAvroImpl.class);

    @Inject
    public void init() throws IOException {
        logger.info("start");
        //Profiler profiler = new Profiler("GeoDbAvroImpl::init");
        //profiler.setLogger(logger);
        //profiler.start("loading from avro");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        DatumReader<GeoIpCityAvroEntityV2> datumReader = new SpecificDatumReader<>();
        DataFileReader<GeoIpCityAvroEntityV2> dataFileReader = new DataFileReader<>(new File("/bigdata/GeoIPCity-V2-snappy.avro"), datumReader);
        logger.info("Schema detected : {}", dataFileReader.getSchema().toString());
        logger.info("Blocksize  : {}, blockcount : {}",   dataFileReader.getBlockSize(), dataFileReader.getBlockCount());

        int datarows = 0;
        while(dataFileReader.hasNext()) {
            GeoIpCityAvroEntityV2 geo = dataFileReader.next();
            entityList.add(geo);
            datarows++;
        }

        //TimeInstrument report = profiler.stop();
        //report.log();
        stopWatch.stop();
        logger.info("Finished with elapsed time = {} ms and {} datarows", stopWatch.getTime(TimeUnit.MILLISECONDS), datarows);


    }

    @Override
    public Optional<GeoRecord> findFirst(@Range(from = 0, to = Integer.MAX_VALUE) long ipv4) {
        Optional<GeoRecord> res = Optional.empty();
        return res;
    }
}
