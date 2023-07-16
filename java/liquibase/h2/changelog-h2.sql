--liquibase formatted sql

--changeset mayton:001
create table nodes_hosts(
 node_id text primary key,
 last_ip_port text,
 last_update_time timestamp,
 last_country text,
 last_city text
);

--changeset mayton:002
create table targets(
  target_id text,
  node_id text,
  x int,
  PRIMARY KEY(target_id, node_id)
);

--changeset mayton:003
create table nodes_stats(
 node_id text primary key,
 pings_requests int,
 get_peeers_requests int,
 find_nodes_requests int,
 announce_requests int
);

--changeset mayton:004
create table geoipcity(
  begin_ip text,
  end_ip text,
  PRIMARY KEY(begin_ip, end_ip)
);

--changeset mayton:005
create table info_hash(
 node_id text primary key,
 info_hash text
);

--changeset mayton:006
create table announces(
    info_hash text primary key,
    node_id text,
    token_value text,
    port int,
    last_update_time timestamp
);

--changeset mayton:007
create table banned_hosts (
 last_ip_port text primary key,
 last_update_time timestamp,
 last_country text,
 last_city text
);

--changeset mayton:008
create table port_stats(
 port int primary key,
 hits int
);


CREATE TABLE known_peers (
    seq int,
    last_update_time timestamp,
    host text,
    PRIMARY KEY (seq)
);
