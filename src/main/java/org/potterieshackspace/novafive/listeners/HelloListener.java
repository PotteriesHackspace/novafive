package org.potterieshackspace.novafive.listeners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class HelloListener extends ListenerAdapter {
    private final HashMap<String, String> responders;
    public HelloListener() {
        responders = new HashMap<>();
        reloadResponders();
    }

    private void reloadResponders(){
        try {
            Stream<String> lines = Files.lines(Paths.get("responders.txt"));
            lines.forEach(x -> responders.put(x.split(":", 2)[0], x.split(":", 2)[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        String message = event.getMessage().split(" ", 2)[0];
        //When someone says ?helloworld respond with "Hello World"
        if (message.equalsIgnoreCase("?helloworld")) {
            event.respond("Hello world!");
        } else if (message.equalsIgnoreCase("?respond")){
            String[] split = event.getMessage().split(" ", 3);
            if (split.length == 3) {
                responders.put(split[1], split[2]);
                List<String> strings = new ArrayList<>();
                responders.forEach((x, y) -> strings.add(String.format("%s:%s", x, y)));
                try {
                    Files.write(Paths.get("responders.txt"), strings);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                event.respond("Responder added: " + split[1] + " - " + split[2]);
            }
        } else if(message.equalsIgnoreCase("?responders")){
            final String[] response = {"Responders: "};
            responders.keySet().stream().map(s -> s + ", ").forEachOrdered(s1 -> response[0] +=s1);
            event.respond(response[0].substring(0, response[0].length() - 2));
        } else if (message.equalsIgnoreCase("?reloadresponders")) {
            reloadResponders();
            event.respond("Done!");
        }else {
            Optional<String> s = responders.keySet().stream().filter(s3 -> s3.equalsIgnoreCase(message)).findAny();
            s.ifPresent(x -> event.respond(responders.get(x.toLowerCase())));
        }
    }
}
