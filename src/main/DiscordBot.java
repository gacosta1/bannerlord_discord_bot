package main;

import main.listeners.AnnotationListener;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

public class DiscordBot {
    private static final String TOKEN = "MjgxMTkwNTU3NDM3NTI2MDE4.C4UWvQ.DS1Bs85rP9HDwK8Si0nfxTk1anc";

    public static void main(String[] args) {
        IDiscordClient client = createClient(TOKEN, true);
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new AnnotationListener());
    }

    private static IDiscordClient createClient(String token, boolean login) {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        try {
            if (login) {
                return clientBuilder.login();
            } else {
                return clientBuilder.build();
            }
        } catch (DiscordException e) {
            e.printStackTrace();
            return null;
        }
    }
}
