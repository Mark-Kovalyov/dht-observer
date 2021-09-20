package mayton.network.dhtobserver;

import mayton.network.dhtobserver.geo.GeoDbCsvImpl;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(value = Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GeoIpComponentBenchmark {

    private GeoDb geoDb;
    private Random r = new Random();

    @Setup(value = Level.Iteration)
    public void setup() {
        geoDb = new GeoDbCsvImpl();
        ((GeoDbCsvImpl)geoDb).init();
    }

    @Benchmark
    @Warmup(iterations = 1, time = 10, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 2, batchSize = 2)
    public void testFindFirst() {
        geoDb.findFirst(r.nextInt(Integer.MAX_VALUE));
    }


}
