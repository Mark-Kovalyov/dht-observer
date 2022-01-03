use dhtspace;

create table geoipcity(
  begin_ip text,
  end_ip text,
  PRIMARY KEY(begin_ip, end_ip)
);