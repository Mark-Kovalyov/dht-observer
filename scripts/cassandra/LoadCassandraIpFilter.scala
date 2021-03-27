import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.datastax.oss.driver.api.core.cql.ResultSet

object LoadCassandraIpFilter {
  def main(args : Array[String]) : Unit = {
    val session : CqlSession = CqlSession.builder.withKeyspace(keyspace).build

  }
}