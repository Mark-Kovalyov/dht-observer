use dhtspace;

drop table announces;

create table announces(
    info_hash text primary key,
    node_id text,
    token_value text,
    port int,
    last_update_time timestamp
);