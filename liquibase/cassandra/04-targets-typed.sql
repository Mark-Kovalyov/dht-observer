use dhtspace;

create type who_is_looking_type(
  who_is_looking_id text,
  last_action_time timestamp
);

create table targets_typed(
  target_id text primary key,
  who_is_looking_set set<frozen <who_is_looking_type>>
);

--insert into targets_typed(target_id, who_is_looking_set) values ('2ecb156e5b97c9b0f5eda2d7a89a84b1dcf9b4a3', [{ who_is_looking_id : '2ecb156e5b97c9b0f5eda2d7a89a84b1dcf9b4a3', last_action_time : toTimestamp(now()) }]);

insert into targets_typed(target_id, who_is_looking_set) values ('2ecb156e5b97c9b0f5eda2d7a89a84b1dcf9b4a3', null);


