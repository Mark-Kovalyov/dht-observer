use dhtspace;

drop table targets;

create table targets(
  target_id text,
  node_id text,
  x int,
  PRIMARY KEY(target_id, node_id)
);

create table targets(
  target_id text primary key,
  who_is_looking set<text>
);

-- Sample

insert into targets(target_id) values ('2ecb156e5b97c9b0f5eda2d7a89a84b1dcf9b4a3');
update targets set who_is_looking = who_is_looking + { '2ecb156e5b97c9b0f5eda2d7a89a84b1dcf9b4a3' } where target_id = '2ecb156e5b97c9b0f5eda2d7a89a84b1dcf9b4a3';
update targets set who_is_looking = who_is_looking + { '881319a480fb7022236b501216190b7662aa423a' } where target_id = '2ecb156e5b97c9b0f5eda2d7a89a84b1dcf9b4a3';

