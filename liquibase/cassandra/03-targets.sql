use dhtspace;

drop table targets;

create table targets(
  target_id text,
  node_id text,
  x int,
  PRIMARY KEY(target_id, node_id)
);

