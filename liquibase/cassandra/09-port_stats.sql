use dhtspace;

drop table port_stats;

create table port_stats(
 port int primary key,
 hits counter
);
