package mayton.network.dhtobserver.db.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class CassandraConnectionComponent {

    private static Logger logger = LoggerFactory.getLogger(CassandraConnectionComponent.class);

    protected CqlSession session;

    protected String keyspace = "dhtspace";

    protected boolean sessionAction(String cqlCommand, Object... arguments) {
        PreparedStatement pst = session.prepare(cqlCommand);
        ResultSet res = session.execute(pst.bind(arguments));
        if (!res.wasApplied()) {
            logger.warn("Warning. Something going wrong during {}", cqlCommand);
        }
        return res.wasApplied();
    }

}
