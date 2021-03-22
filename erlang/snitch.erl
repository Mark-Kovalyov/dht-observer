-module(snitch).



{ok, Socket} = gen_udp:open(51413, [binary, {active, true}]).