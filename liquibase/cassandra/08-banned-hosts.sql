use dhtspace;

drop table banned_hosts;

create table banned_hosts (
 last_ip_port text primary key,
 last_update_time timestamp,
 last_country text,
 last_city text
);
