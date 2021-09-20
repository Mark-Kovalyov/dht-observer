create keyspace geolite with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

use geolite;

drop table asn_blocks_ipv6;

-- network,autonomous_system_number,autonomous_system_organization
--   2001:200::/37,2500,"WIDE Project"
   --2001:200:800::/40,2500,"WIDE Project"
   --2001:200:900::/40,7660,"Asia Pacific Advanced Network - Japan"


create table asn_blocks_ipv6(
    network text primary key,
    autonomous_system_number int,
    autonomous_system_organization text
);

--network,autonomous_system_number,autonomous_system_organization
--1.0.0.0/24,13335,"Cloudflare, Inc."
--1.0.4.0/22,56203,Gtelecom-AUSTRALIA
--1.0.16.0/24,2519,"ARTERIA Networks Corporation"
--1.0.64.0/18,18144,"Energia Communications,Inc."
--1.0.128.0/17,23969,"TOT Public Company Limited"

create table asn_blocks_ipv4(
    network text primary key,
    autonomous_system_number int,
    autonomous_system_organization text
);
