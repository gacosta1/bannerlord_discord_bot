package main.listeners;

import sun.plugin2.message.Message;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;
import java.sql.*;

public class MessageListener {
    private List<String> factionList = Arrays.asList("Sturgians", "Aserai", "Khuzaits", "Baltanians", "Vlandians");

    @EventSubscriber
    public void onMessagReceivedEvent(MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
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
            case "!user":
                String usr = databaseUser(e);
                sendMessage2(usr ,e);
                break;
            case "!role":
                String role = databaseRole(e);
                if(msg.length == 2) {
                    updateRole(e.getMessage().getAuthor().getName(), msg[1]);
                    e.getMessage().reply("\nYour role has been updated!");
                }
                else {
                    sendRole(role, e);
                }
                break;
            case "!signup":
                boolean inSys = signUp(e);
                if(inSys == false)
                    e.getMessage().reply("\nYou have now signed up!");
                else
                    e.getMessage().reply("\nYou are already in the System!");
                break;
        }

    }
    public void updateRole(String author, String role){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://192.168.0.143/gjengli";
            Connection conn = DriverManager.getConnection(url, "CATS", "ragar375");
            Statement state = conn.createStatement();
            state.executeUpdate("UPDATE Users SET Roles = '" + role + "' WHERE Username = '" + author + "'");
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean signUp(MessageReceivedEvent m){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://192.168.0.143/gjengli";
            Connection conn = DriverManager.getConnection(url, "CATS", "ragar375");
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT Username from Users WHERE Username = '" + m.getMessage().getAuthor().getName().toString() + "'");
            if(result.next()){
                return true;
            }
            state.executeUpdate("INSERT IGNORE INTO `Users` (`Username`, `Roles`) VALUES (" + "'" + m.getMessage().getAuthor().getName().toString() + "'" + "," + "'Samurai'" + ")");
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
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

    public String databaseUser(MessageReceivedEvent m) {
        String user = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://192.168.0.143/gjengli";
            Connection conn = DriverManager.getConnection(url, "CATS", "ragar375");
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT Username from Users WHERE Username = '" + m.getMessage().getAuthor().getName().toString() + "'");

            while (result.next()) {
                user = result.getString("Username");
            }

        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String databaseRole(MessageReceivedEvent m) {
        String role = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://192.168.0.143/gjengli";
            Connection conn = DriverManager.getConnection(url, "CATS", "ragar375");
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT Roles from Users WHERE Username = '" + m.getMessage().getAuthor().getName().toString() + "'");

            while (result.next()) {
                role = result.getString("Roles");

            }
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return role;
    }

    private void sendMessage(String msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        e.getMessage().getChannel().sendMessage(msg);
    }

    private void sendMessage2(String msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        e.getMessage().getChannel().sendMessage(msg);
    }
    private void sendRole(String msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        e.getMessage().getChannel().sendMessage(msg);
    }
}

