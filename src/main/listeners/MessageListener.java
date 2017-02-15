package main.listeners;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Arrays;
import java.util.List;

public class MessageListener {
    private List<String> factionList = Arrays.asList("Sturgians", "Aserai", "Khuzaits", "Baltanians", "Vlandians");

    @EventSubscriber
    public void onMessagReceivedEvent(MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (e.getMessage().getContent().equalsIgnoreCase("who the best?")) {
            e.getMessage().reply("\nyou da best");
        }
        String msg[] = e.getMessage().getContent().split(" ");
        switch (msg[0]) {
            case "!factions" :
                e.getMessage().reply("\nAvailable factions: sturgians, aserai, khuzaits, baltanians, vlandians\n" +
                        "To join a faction, use !faction [faction name]");
                break;
            case "!faction" :
                setFaction(msg, e);
                break;
            case "!baka" :
                sendMessage("NANI DESU KA?!?!?", e);
                break;
            case "!test" :
                sendMessage("rm -rf /", e);
                break;
            case "!roll" :
                sendMessage(e.getMessage().getAuthor().getName() + " rolled " + Integer.toString((int)(Math.random() * 100)), e);
                break;
        }

    }

    private void setFaction(String msg[], MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        String faction = msg[1].substring(0, 1).toUpperCase() + msg[1].substring(1).toLowerCase();
        if (!factionList.contains(faction)) {
            e.getMessage().reply("\nInvalid faction!");
            return;
        }
        List<IRole> roles = e.getMessage().getClient().getRoles();
        for (IRole role : roles) {
            if (factionList.contains(role.getName())) {
                if (role.getName().equalsIgnoreCase(faction)) {
                    e.getMessage().getAuthor().addRole(role);
                    e.getMessage().reply("\nFaction set - " + role.getName());
                } else {
                    e.getMessage().getAuthor().removeRole(role);
                }
            }
        }
    }

    private void sendMessage(String msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        e.getMessage().getChannel().sendMessage(msg);
    }
}
