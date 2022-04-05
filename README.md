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

内存溢出OOM

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class OOMMakerController {

    List<String> data = new ArrayList<>();

    /**
     * 加启动参数：-Xms256m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=.
     */
    @GetMapping("oom")
    public void oom() {
        while(true) {
            data.add(IntStream.rangeClosed(1, 10_000)
                    .mapToObj(i -> "a")
                    .collect(Collectors.joining("")));

        }
    }
}
```

加启动参数：-Xms256m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=.

MAT工具查看Spring容器的加载bean

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/C5086437-F60F-417D-A2D9-9764F1958726_2/eVqbxSQOxkPD0NtZ77w1wJPFQbpjhphk3VwubEiwpdYz/Image.png)

超多的对象

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/FD903DB9-1794-464C-8956-E0AEECDF0643_2/oZmK3Gxih37WvzNw8UYzdxMNVJAfWUHUIiyypFdH9JQz/Image.png)

OQL查询

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/EA4E16EB-1437-469A-A29A-7096CEF45431_2/wv4kNFF2i2kvvFvyjjVYGK7YXQ3oG4fQ2rxMcDyk0mIz/Image.png)

Arthas定位问题

curl -O [https://alibaba.github.io/arthas/arthas-boot.jar](https://alibaba.github.io/arthas/arthas-boot.jar)

```java
import org.springframework.util.DigestUtils;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HighCPUApplication {

    private static int ADMIN_ID = 0;

    private static byte[] payload = IntStream.rangeClosed(1, 10_000)
            .mapToObj(i -> "a")
            .collect(Collectors.joining(""))
            .getBytes(StandardCharsets.UTF_8);

    private static Random random = new Random();

    private static void doTask(int i) {
        if (i != ADMIN_ID) {
            IntStream.rangeClosed(1, 10_000).parallel().forEach(j -> DigestUtils.md5DigestAsHex(payload));
        }
    }

    public static void main(String[] args) {
        System.out.println("HighCPUApplication VM options");
        System.out.println(ManagementFactory.getRuntimeMXBean().getInputArguments()
                .stream()
                .collect(Collectors.joining(System.lineSeparator())));
        System.out.println("HighCPUApplication Program arguments");
        System.out.println(Arrays.stream(args).collect(Collectors.joining(System.lineSeparator())));


        while (true) {
            doTask(random.nextInt(100));
        }
    }
}
```

使用Java8打印的线程信息更清晰

/Users/zhouguangping/Library/Java/JavaVirtualMachines/corretto-1.8.0_302/Contents/Home/bin/java -jar ErrorMaker-1.0-SNAPSHOT.jar

/Users/zhouguangping/Library/Java/JavaVirtualMachines/corretto-1.8.0_302/Contents/Home/bin/java -jar arthas-boot.jar

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/CBA9D1CB-2BE7-4AD3-8AC4-B67902904193_2/Q2MbWtr3v5GZmyKLq3ZvZ8m14WCjXQYFSy01yPbyVuEz/Image.png)

线程信息

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/332C7DA1-9FD9-4DBF-9910-D1D6290126D4_2/7n7bA6YOLd8CIrIHU5ybFr6GZ72jwkyKd3Pgc8cGmXMz/Image.png)

主线程

![Image.png](https://res.craft.do/user/full/4bb18089-5d79-fff1-b7b0-44beb356c085/doc/7BA9FEDF-0544-44B0-9A3E-9E7DBA9B02D1/619B12EF-34BC-475F-978D-EC14A70FC79A_2/Uy6q37CeDDea9Il4yLuh47u9vtqyk7hgEkk3Kw92ZnAz/Image.png)

可以看到，由于这些线程都在处理 MD5 的操作，所以占用了大量 CPU 资源。我们希望分析出代码中哪些逻辑可能会执行这个操作，所以需要从方法栈上找出我们自己写的类，并重点关注。

使用 jad 命令直接对 HighCPUApplication 类反编译:
jad org.me.HighCPUApplication

[快速入门 — Arthas 3.6.0 文档](https://arthas.aliyun.com/doc/quick-start.html)