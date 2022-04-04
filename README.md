# 优化排查

获取Java进程

`jps -l`

根据进程获取线程信息并保存为文件

`jstack pid > pid.txt`

从服务器下载到本地

`sz pid.txt`

服务器上运行 `top -Hp pid` 命令，通过查看该进程下的所有的线程数据，来查看进程中哪个线程 CPU 使用高，然后，输入大写的 P 将线程按照 CPU 使用率排序

获取的PID是十进制的，需要查找十六进制的printf “%x\n” 十进制的线程id

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/347EAC52-BDDA-421F-9F5B-415B1367A36D_2/yAXXQTealFLgTq5L6dOONjWA4U7JKZOQ0z0oE6kKeDwz/Image.png)

最后，在 jstack 命令输出的线程栈中搜索这个线程 ID，定位出问题的线程当时的调用栈

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/AE6D2921-02CB-4456-976C-E0B8D2022667_2/Ft64X114HDSTMptFpSw1fUkCSmQPwdnQ6PEo7hMClxMz/Image.png)

[](https://coding.imooc.com/lesson/241.html#mid=15676)

调整运行堆为1G，即1 * 1024 * 1024 * 1024字节

java -Xms1g -Xmx1g -jar ErrorMaker-0.0.1-SNAPSHOT.jar

jps -l

jinfo pid查看

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/C3585E15-8352-439A-85A9-8C5F1BFA66F7_2/8qunVDIZ8lH4vaNnB4MXGQAMbV04m4GG9fgkj5vrweQz/Image.png)

服务器上主要使用命令行工具，如果希望看到GC趋势的话，可以使用jstat工具，jstat工具允许以固定的监控频次输出JVM的各种监控指标，比如使用-gcutil输出GC和内存占用汇总信息，每隔5秒输出一次，输出100次，可以看到Young GC比较频繁，而Full GC基本10秒一次：

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/636D7E06-4A21-45CE-8B90-927BFF3C65A5_2/Jc6mbTy1hC6YVd3KDuvFcy0JZvoOWcm1mURy0j7Zom0z/Image.png)

其中，S0 表示 Survivor0 区占用百分比，S1 表示 Survivor1 区占用百分比，E 表示Eden 区占用百分比，O 表示老年代占用百分比，M 表示元数据区占用百分比，YGC 表示年轻代回收次数，YGCT 表示年轻代回收耗时，FGC 表示老年代回收次数，FGCT 表示老年代回收耗时。

通过命令行工具 jstack，也可以实现抓取线程栈的操作：

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/44C01411-F040-41B2-83F9-21BA97C5D382_2/APoYxxc3JA3VKe77IDmwneCsnsTxyEUJkxxDJ3UT3Ykz/Image.png)

抓取后可以使用类似fastthread这样的在线分析工具来分析线程栈。

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/CA89C5D7-4FD6-4ECE-B061-468AE86EA25E_2/X3eDC0XJvJhFXkleoCScoYli1kNGipRdHFy4HzkxeeIz/Image.png)

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/24CE489D-613D-4FCA-906A-17608A0910E0_2/KGEIAxENza5zJFmyGGLkRQXo7UUgTKHrRs2vHiijxAcz/Image.png)

长度最长的Mysql本地查询进行分析

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/59EE35A9-3FDF-4D4C-B0BC-5CE5C8108D3F_2/igkojx0yOA32cj4nZfl1PXgtYe3NWV5VD8vQxtz6OJ8z/Image.png)

