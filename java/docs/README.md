# Netcat

Usecases:

Send UDP packet
```
echo -n "foo" | nc -u -w1 192.168.1.100 3030
```

Receive single UDP message
```
nc -u localhost 7777
```

Cyclic receive UDP messages
```
while true; do nc -u localhost 7777; done
```