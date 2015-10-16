package org.potterieshackspace.novafive;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.SASLCapHandler;
import org.potterieshackspace.novafive.listeners.HelloListener;
import org.potterieshackspace.novafive.listeners.ResponderListener;

import javax.net.ssl.SSLSocketFactory;

public class NovaFive {
    private static PircBotX bot;

    public static void main(String[] args) throws Exception {
        Config conf = new Config();
        //Configure what we want our bot to do
        Configuration configuration = new Configuration.Builder()
                .setName(conf.nick)
                .setRealName(conf.real)
                .setServerHostname(conf.server) //Join the freenode network
                .setServerPort(conf.port)
                .setSocketFactory(SSLSocketFactory.getDefault())
                .addAutoJoinChannel(conf.channel) //Join the channel
                .addCapHandler(new SASLCapHandler(conf.sasl.split(":", 2)[0], conf.sasl.split(":", 2)[1]))
                .addListener(new HelloListener()) //Add our listener that will be called on Events
                .addListener(new ResponderListener())
                .buildConfiguration();

        //Create our bot with the configuration
        bot = new PircBotX(configuration);
        Web web = new Web(bot, conf);
        web.start();
        //Connect to the server
        bot.startBot();
    }

    public static PircBotX getBot() {
        return bot;
    }
}
