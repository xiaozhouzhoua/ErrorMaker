package org.me;

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
    @GetMapping("/oom")
    public void oom() {
        while(true) {
            data.add(IntStream.rangeClosed(1, 10_000)
                    .mapToObj(i -> "a")
                    .collect(Collectors.joining("")));

        }
    }
}
