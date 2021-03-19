-- drop table ipv4peers;

create table ipv4peers(
    ip             text primary key,
    registered     timestamp,
    last_seen      timestamp,
    interactions   integer,
    port           integer,
    associated_app text,
    peer_id        text
) tablespace dhtspace;

-- select * ipv4peers;