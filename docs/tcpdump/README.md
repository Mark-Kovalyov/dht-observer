# TCP dump

## General help

```
tcpdump --help
tcpdump version 4.9.3
libpcap version 1.9.1 (with TPACKET_V3)
OpenSSL 1.1.1f  31 Mar 2020
Usage: tcpdump [-aAbdDefhHIJKlLnNOpqStuUvxX#] [ -B size ] [ -c count ]
	[ -C file_size ] [ -E algo:secret ] [ -F file ] [ -G seconds ]
	[ -i interface ] [ -j tstamptype ] [ -M secret ] [ --number ]
	[ -Q in|out|inout ]
	[ -r file ] [ -s snaplen ] [ --time-stamp-precision precision ]
	[ --immediate-mode ] [ -T type ] [ --version ] [ -V file ]
	[ -w file ] [ -W filecount ] [ -y datalinktype ] [ -z postrotate-command ]
	[ -Z user ] [ expression ]
```

## Examples
```
$ tcpdump -i enp7s0 udp port 4665 or port 51413 -vv -X
$ tcpdump -i enp7s0 udp portrange 1000-65536 -vv -X
$ tshark -i enp7s0 -f "udp port 51413"
$ tshark -i enp7s0 -R "bittorrent" -any_other_options
```

### TPCDump : Listen all outbound UDP traf from Transmission (UDP:51413)

```
tcpdump -i wlp7s0 -Qout udp port 51413 -vv -X

where:
  -v 	Вывод подробной информации (TTL; ID; общая длина заголовка, а также его параметры; производит проверку контрольных сумм IP и ICMP-заголовков)
 -vv 	Вывод ещё более полной информации, в основном касается NFS и SMB.
-vvv 	Вывод максимально подробной информации.

-T тип 	Интерпретация пакетов заданного типа. Поддерживаются типы aodv, cnfp, rpc, rtp, rtcp, snmp, tftp, vat, wb.

-x 	Делает распечатку пакета в шестнадцатеричной системе, полезно для более детального анализа пакета. Количество отображаемых данных зависит от параметра -s
-xx 	То же, что и предыдущий параметр -x, но включает в себя заголовок канального уровня
-X 	Выводит пакет в ASCII- и hex-формате. Полезно в случае анализа инцидента связанного со взломом, так как позволяет просмотреть какая текстовая информация передавалась во время соединения.
-XX 	То же, что и предыдущий параметр -X, но включает заголовок канального уровня.  
```

