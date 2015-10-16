package org.potterieshackspace.novafive.listeners;

import org.pircbotx.hooks.types.GenericMessageEvent;

public class HelloListener extends ListenerBase {

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        String message = event.getMessage().split(" ", 2)[0];
        //When someone says ?helloworld respond with "Hello World"
        if (message.equalsIgnoreCase("?helloworld")) {
            event.respond("Hello world!");
        }
    }
}
