use dhtspace;

drop table nodes_stats;
drop table nodes_hosts;

create table nodes_hosts(
 node_id text primary key,
 last_ip_port text,
 last_update_time timestamp,
 last_country text,
 last_city text
);

create table nodes_stats(
 node_id text primary key,
 pings_requests counter,
 get_peeers_requests counter,
 find_nodes_requests counter
);

-- Example for ttl-records

update nodes set find_nodes_requests = find_nodes_requests + 1 where node_id = '7d2c835df2f49462a73c00988c7eadcf722c57e1';

update nodes_stats set pings_requests = pings_requests + 1 where node_id = '7d2c835df2f49462a73c00988c7eadcf722c57e1';

insert into nodes(node_id) values('7d2c835df2f49462a73c00988c7eadcf722c57e1') using ttl 86400;

-- For update

update nodes using ttl 1000 set last_ip = '192.168.1.1', last_port = 8080 where node_id = '7d2c835df2f49462a73c00988c7eadcf722c57e1';

-- For select

select node_id, last_ip, ttl(last_ip) from nodes where node_id = '7d2c835df2f49462a73c00988c7eadcf722c57e1';

