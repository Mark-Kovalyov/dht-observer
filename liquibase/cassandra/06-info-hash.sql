use dhtspace;

drop table info_hash;

create table info_hash(
 node_id text primary key,
 info_hash text
);