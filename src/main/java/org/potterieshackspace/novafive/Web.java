package org.potterieshackspace.novafive;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.pircbotx.PircBotX;
import static spark.Spark.*;

public class Web extends Thread {
    private final PircBotX bot;
    private final Config conf;

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
            if (request.headers("X-Github-Event").equalsIgnoreCase("push")){
                String s = object.getAsJsonObject("repository").getAsJsonPrimitive("full_name").getAsString();
                String diff = object.getAsJsonPrimitive("compare").getAsString();
                bot.sendIRC().message(conf.channel, "[GitHub]: Push to " + s + " - " + diff);
            }
            response.status(200);
            response.body("OK");
            return response;
        });
    }
}
