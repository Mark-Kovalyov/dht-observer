# DHT

## UFW

```
ufw ..
``` 

## Netstat 

```
netstat -plnu | grep -E "16680|49001|43567|4665|4672|51413|46434|48529"
```

## JFR and Schenandoah

```
[1256.819s][warning][jfr] LeakProfiler is currently not supported in combination with Shenandoah GC
```

## JFR

https://docs.oracle.com/en/java/javase/11/tools/jcmd.html

```
jcmd [pid | main-class] command...|PerfCounter.print| 
-f filename
```