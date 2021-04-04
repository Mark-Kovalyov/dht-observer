use dhtspace;

drop table known_peers;

CREATE TABLE dhtspace.known_peers (
    seq int,
    last_update_time timestamp,
    host text,
    PRIMARY KEY (seq, last_update_time)
);
