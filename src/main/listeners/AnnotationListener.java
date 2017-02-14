package main.listeners;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * Created by Vincent on 2/14/2017.
 */
public class AnnotationListener {

    @EventSubscriber
    public void onReadyEvent(ReadyEvent e) {
        // do something?
    }

    @EventSubscriber
    public void onMessagReceivedEvent(MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        String response = "";
        switch (e.getMessage().getContent().toLowerCase()) {
            case "!baka" :
                response = "NANI DESU KA?!?!";
                break;
            case "!test" :
                response = "rm -rf /";
                break;
            case "!roll" :
                response = e.getMessage().getAuthor().getName() + " rolled " + Integer.toString((int)(Math.random() * 100));
                break;
        }
        if (response != "")
            e.getMessage().getChannel().sendMessage(response);
    }
}
