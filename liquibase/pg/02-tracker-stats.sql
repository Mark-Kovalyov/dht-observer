drop table tracker_stats;

create table tracker_stats(
  application          text primary key,
  port                 integer check (port >= 0 and port < 65536),
  udp_packets_received integer,
  udp_packets_parsed   integer
) tablespace dhtspace;
