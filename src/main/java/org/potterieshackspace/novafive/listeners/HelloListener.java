package org.potterieshackspace.novafive.listeners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class HelloListener extends ListenerAdapter {
    private final HashMap<String, String> responders;
    public HelloListener() {
        responders = new HashMap<>();
        try {
            Stream<String> lines = Files.lines(Paths.get("responders.txt"));
            lines.forEach(x -> responders.put(x.split(":", 2)[0], x.split(":", 2)[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        //When someone says ?helloworld respond with "Hello World"
        if (event.getMessage().startsWith("?helloworld")) {
            event.respond("Hello world!");
        } else if (event.getMessage().startsWith("?respond")){
            String[] split = event.getMessage().split(" ", 3);
            if (split.length == 3) {
                responders.put(split[1], split[2]);
                List<String> strings = new ArrayList<>();
                responders.forEach((x, y) -> {
                    strings.add(String.format("%s:%s", x, y));
                });
                try {
                    Files.write(Paths.get("responders.txt"), strings);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                event.respond("Responder added: " + split[1] + " - " + split[2]);
            }
        }
    }
}
