use dhtspace;

drop table event_stats;

create table event_stats(
 event text primary key,
 hits counter
);