package org.potterieshackspace.novafive;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Config {
    public String server;
    public int port;
    public String nick;
    public String real;
    public String channel;
    public String sasl;
    public String webhookSecret;

    public Config() {
        Field[] methods = this.getClass().getFields();
        Path path = Paths.get("nova.conf");
        //When filteredLines is closed, it closes underlying stream as well as underlying file.

        for (Field method : methods) {
            try(Stream<String> filteredLines = Files.lines(path).filter(s -> s.startsWith(method.getName().toLowerCase()))){
                Optional<String> hasThing = filteredLines.findFirst();
                if(hasThing.isPresent()){
                    String s = hasThing.get().split("=", 2)[1];
                    if (method.getType().getName().equals("java.lang.String")){
                        method.set(this, s);
                    } else if (method.getType().getName().equals("int")){
                        method.setInt(this, Integer.parseInt(s));
                    }
                }
            } catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }


    }
}
