package org.me;

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
