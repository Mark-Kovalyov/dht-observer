use dhtspace;

create table nodes(
 node_id text primary key,
 last_ip text,
 last_port int,
 pings_requests int,
 get_peeers_requests int,
 find_nodes_requests int
);

-- Example for ttl-records

insert into nodes(node_id) values('7d2c835df2f49462a73c00988c7eadcf722c57e1') using ttl 86400;

-- For update

update nodes using ttl 1000 set last_ip = '192.168.1.1', last_port = 8080 where node_id = '7d2c835df2f49462a73c00988c7eadcf722c57e1';

-- For select

select node_id, last_ip, ttl(last_ip) from nodes where node_id = '7d2c835df2f49462a73c00988c7eadcf722c57e1';

