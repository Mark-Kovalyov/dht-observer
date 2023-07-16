use dhtspace;

drop table nodes_stats;

create table nodes_stats(
 node_id text primary key,
 pings_requests counter,
 get_peeers_requests counter,
 find_nodes_requests counter,
 announce_requests counter
);