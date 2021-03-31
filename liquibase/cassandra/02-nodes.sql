use dhtspace;

drop table nodes_hosts;

create table nodes_hosts(
 node_id text primary key,
 last_ip_port text,
 last_update_time timestamp,
 last_country text,
 last_city text
);
