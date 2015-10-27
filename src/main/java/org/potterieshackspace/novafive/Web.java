package org.potterieshackspace.novafive;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.pircbotx.PircBotX;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static spark.Spark.*;

public class Web extends Thread {
    private final PircBotX bot;
    private final Config conf;
    private final ArrayList<Lease> leases = new ArrayList<>();
    private String lastLogin = "";
    public Web(PircBotX bot, Config conf) {
        this.bot = bot;
        this.conf = conf;
    }

    @Override
    public void run() {
        post("/webhook-github", (request, response) -> {
//            bot.sendIRC().message(conf.channel, );
            for (String s : request.headers()) {
                System.out.println(s + ": " + request.headers(s));
            }

            System.out.println(request.body());
            JsonParser parser = new JsonParser();
            JsonElement parse = parser.parse(request.body());
            JsonObject object = parse.getAsJsonObject();
            if (request.headers("X-Github-Event").equalsIgnoreCase("push")) {
                String s = object.getAsJsonObject("repository").getAsJsonPrimitive("full_name").getAsString();
                String diff = object.getAsJsonPrimitive("compare").getAsString();
                bot.sendIRC().message(conf.channel, "[GitHub]: Push to " + s + " - " + diff);
            }
            response.status(200);
            response.body("OK");
            return response;
        });

        post("/webhook-door", (request, response) -> {
//            bot.sendIRC().message(conf.channel, );
            for (String s : request.headers()) {
                System.out.println(s + ": " + request.headers(s));
            }

            System.out.println(request.body());
            String[] split = request.body().split("\n");
            String s = split[0];
            if (!s.isEmpty() && !s.equals(lastLogin)) {
                lastLogin = s;
                bot.sendIRC().message(conf.channel, "[Door]: New login from " + lastLogin);
            }
            response.status(200);
            response.body("OK");
            return response;
        });

        post("/webhook-dhcpd", (request, response) -> {
//            bot.sendIRC().message(conf.channel, );
            for (String s : request.headers()) {
                System.out.println(s + ": " + request.headers(s));
            }
            ArrayList<Lease> newLeases = new ArrayList<>();
            System.out.println(request.body());
            boolean print = true;
            if (leases.isEmpty()) {
                print = false;
            }

            for (String s : request.body().split("\n")) {
                String[] split = s.replaceAll(" ", "").split(Pattern.quote("|"));
                Lease lease = new Lease(split[0], split[1], split[2]);
                newLeases.add(lease);
                if (print && !leases.contains(lease)) {
                    bot.sendIRC().message(conf.channel, "[DHCPD]: New lease found! Client name: " + lease.getClientName() + ", IP: " + lease.getIp() + ", MAC: " + lease.getMac());
                }
            }

            ((ArrayList<Lease>) leases.clone()).forEach(x -> {
                if (!newLeases.contains(x)) {
                    leases.remove(x);
                }
            });
            ((ArrayList<Lease>) newLeases.clone()).forEach(x -> {
                if (leases.contains(x)) {
                    newLeases.remove(x);
                }
            });
            leases.addAll(newLeases);
            leases.forEach(System.out::println);

            response.status(200);
            response.body("OK");
            return response;
        });
    }
}

class Lease {
    final private String ip;
    final private String mac;
    final private String clientName;

    Lease(String ip, String mac, String clientName) {
        this.ip = ip;
        this.mac = mac;
        this.clientName = clientName;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lease lease = (Lease) o;

        return getIp().equals(lease.getIp()) && getMac().equals(lease.getMac()) && getClientName().equals(lease.getClientName());

    }

    @Override
    public int hashCode() {
        int result = getIp().hashCode();
        result = 31 * result + getMac().hashCode();
        result = 31 * result + getClientName().hashCode();
        return result;
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public String toString() {
        return "Lease{" +
                "ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", clientName='" + clientName + '\'' +
                '}';
    }
}
