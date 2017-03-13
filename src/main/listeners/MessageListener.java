package main.listeners;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class MessageListener {
    private List<String> factionList = Arrays.asList("Sturgians", "Aserai", "Khuzaits", "Baltanians", "Vlandians");

    private String URL = "jdbc:mysql://************/gjengli";
    private String db = "CATS";
    private String pass = "********";

    @EventSubscriber
    public void onMessagReceivedEvent(MessageReceivedEvent e) throws IllegalAccessException, InstantiationException, InterruptedException, RateLimitException, DiscordException, MissingPermissionsException {
        String msg[] = e.getMessage().getContent().split(" ");
        ArrayList<String> arr = new ArrayList<>();
        switch (msg[0]) {
            case "!message":
                String ID = e.getMessage().getAuthor().getID();
                sendPrivateMessage(e.getClient(), ID);
                break;
            case "!factions":
                e.getMessage().reply("\nAvailable factions: sturgians, aserai, khuzaits, baltanians, vlandians\n" +
                        "To join a faction, use !faction [faction name]");
                break;
            case "!faction":
                setFaction(msg, e);
                break;
            case "!baka":
                sendMessage("NANI DESU KA?!?!?", e);
                break;
            case "!test":
                sendMessage("rm -rf /", e);
                break;
            case "!roll":
                sendMessage(e.getMessage().getAuthor().getName() + " rolled " + Integer.toString((int) (Math.random() * 100)), e);
                break;
            case "!user":
                if (msg.length > 1) {
                    arr = viewOtherUser(msg, e);
                    sendUserStat(arr, e);
                } else {
                    arr = databaseUser(e);
                    sendUserStat(arr, e);
                }
                break;
            case "!role":
                String role = databaseRole(e);
                if (msg.length > 1) {
                    updateRole(e.getMessage().getAuthor().getName(), msg[1]);
                    e.getMessage().reply("\nYour role has been updated!");
                } else {
                    sendRole(role, e);
                }
                break;
            case "!signup":
                boolean inSys = signUp(e);
                if (!inSys)
                    e.getMessage().reply("\nYou have now signed up!");
                else
                    e.getMessage().reply("\nYou are already in the System!");
                break;
            case "!clear":
                if (msg.length > 1) {
                    clearEvent(msg);
                    sendMessage("Event has been cleared :smile:", e);
                } else {
                    e.getMessage().reply("Incorrect Use of !clear... Try: !clear [Event Name]");
                }
                break;
            case "!update":
                if (!(msg.length > 2)) {
                    e.getMessage().reply("Incorrect Use of !update... Try: !update [Event Name] + " + "<Description>");
                } else {
                    updateEvent(msg);
                    e.getMessage().reply("Successfully updated Event");
                }
                break;
            case "!coach":
                if (!(msg.length > 1)) {
                    sendMessage("Incorrect Use of !coach... Try: !coach [Want] or [Available] or [Reputation]", e);
                    break;
                }
                else {
                    switch (msg[1]) {
                        case "Wanted":
                        case "wanted":
                        case "Want":
                        case "want":
                        case "W":
                        case "w":
                            coachWant(e);
                            break;
                        case "Available":
                        case "available":
                        case "Avail":
                        case "avail":
                        case "A":
                        case "a":
                            arr.clear();
                            arr = coachAvail();
                            sendAvailable(arr, e);
                            break;
                        case "Reputation":
                        case "reputation":
                        case "Rep":
                        case "rep":
                        case "R":
                        case "r":
                            if(!(msg.length > 2)) {
                                sendMessage("Try !coach rep [Username]", e);
                                break;
                            }
                            else {
                                arr.clear();
                                arr = coachRep(msg);
                                sendRep(arr, e);
                            }
                            break;
                    }
                }
                break;
        }

    }


    private ArrayList<String> coachRep(String msg[]) throws InstantiationException, IllegalAccessException {
        ArrayList<String> repArr = new ArrayList<>();
        try {
            String rep;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM Coaching WHERE Username = '" + msg[2] + "'");
            while(result.next()){
                rep = result.getString("Reputation");
                repArr.add("Reputation:");
                repArr.add(rep);
            }
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
        }
        return repArr;
    }

    private void coachWant(MessageReceivedEvent m) throws IllegalAccessException, InstantiationException, RateLimitException, DiscordException, MissingPermissionsException {
        m.getMessage().getChannel().sendMessage("Here are the available coaches:");
        ArrayList<String> coach = coachAvail();
        sendAvailable(coach, m);
    }

    private ArrayList<String> coachAvail() throws InstantiationException, IllegalAccessException {
       ArrayList<String> coach = new ArrayList<>();
        try {
            String user, rep;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT * FROM Coaching");
            while(result.next()){
                user = result.getString("Username");
                rep = result.getString("Reputation");
                coach.add("Username:\t");
                coach.add(user);
                coach.add("\t");
                coach.add("\nReputation:\t");
                coach.add(rep);
                coach.add("\n");
                coach.add("\n");
            }
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
        }
        return coach;
    }


    private void updateEvent(String msg[]) throws InstantiationException, IllegalAccessException {
        try {
            String str = Arrays.toString(msg);
            str = str.substring(10, str.length() - 1);
            str = str.replace(",", "");
            str = str.substring(str.indexOf("<") + 1, str.indexOf(">"));

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            state.executeUpdate("UPDATE Users SET Description = '" + str + "' WHERE Event = '" + msg[1] + "'");
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
        }
    }

    private void clearEvent(String msg[]) throws InstantiationException, IllegalAccessException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            state.executeUpdate("UPDATE Users SET Description = '" + "No Description" + "' WHERE Event = '" + msg[1] + "'");
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        }
    }

    private void updateRole(String author, String role) throws IllegalAccessException, InstantiationException{
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            state.executeUpdate("UPDATE Users SET Roles = '" + role + "' WHERE Username = '" + author + "'");
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        }
    }

    private boolean signUp(MessageReceivedEvent m) throws IllegalAccessException, InstantiationException{
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT Username from Users WHERE Username = '" + m.getMessage().getAuthor().getName() + "'");
            if(result.next()){
                return true;
            }
            state.executeUpdate("INSERT IGNORE INTO `Users` (`Username`, `User_id`, `Roles`) VALUES (" + "'" + m.getMessage().getAuthor().getName() + "'" + "," + "'" + m.getMessage().getAuthor().getID() + "'" + "," + "'Samurai'" + ")");
            state.executeUpdate("INSERT IGNORE INTO `Stats` (`Username`, `User_id`, `Wins`, `Losses`, `Kills`, `Deaths`, `Ranks`) VALUES (" + "'" + m.getMessage().getAuthor().getName() + "'" + "'" + m.getMessage().getAuthor().getID() + "'" + ",'0'" + ",'0'" + ",'0'" + ",'0'" + ",'0'" + ")");
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
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

    private ArrayList<String> viewOtherUser(String otherUser[], MessageReceivedEvent m) throws IllegalAccessException, InstantiationException, InterruptedException, RateLimitException, DiscordException, MissingPermissionsException {
        String user, win, loss, kills, death;
        ArrayList<String> stats = new ArrayList<>();

        String userName = Arrays.toString(otherUser);
        userName = userName.substring(8, userName.length()-1);
        userName = userName.replace(",", "");
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            ResultSet result = null;

            if(userName.substring(1, 3).equals("@!")) {
                userName = userName.substring(userName.indexOf("<") + 1, userName.indexOf(">"));
                userName = userName.substring(2, userName.length()).trim();
                result = state.executeQuery("SELECT * from Stats WHERE User_id = '" + userName + "'");
            }
            else if(userName.substring(1, 2).equals("@")){
                userName = userName.substring(userName.indexOf("<") + 1, userName.indexOf(">"));
                userName = userName.substring(1, userName.length()).trim();
                result = state.executeQuery("SELECT * from Stats WHERE User_id = '" + userName + "'");
            }
            else {
                result = state.executeQuery("SELECT * from Stats WHERE Username = '" + userName + "'");
            }

            if(!result.isBeforeFirst()) {
                m.getMessage().reply("No User with that name! Sorry :frowning:");
                return null;
            }
            else {

                while (result.next()) {
                    user = result.getString("Username");
                    win = result.getString("Wins");
                    loss = result.getString("Losses");
                    kills = result.getString("Kills");
                    death = result.getString("Deaths");
                    stats.add("Username:");
                    stats.add(user);
                    stats.add("\nWins:");
                    stats.add(win);
                    stats.add("\nLosses:");
                    stats.add(loss);
                    stats.add("\nKills:");
                    stats.add(kills);
                    stats.add("\nDeaths:");
                    stats.add(death);
                }
            }

        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        }
        return stats;
    }

    private ArrayList<String> databaseUser(MessageReceivedEvent m) throws IllegalAccessException, InstantiationException {
        String user, win, loss, kills, death;
        ArrayList<String> stats = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT * from Stats WHERE Username = '" + m.getMessage().getAuthor().getName() + "'");
            while (result.next()) {
                user = result.getString("Username");
                win = result.getString("Wins");
                loss = result.getString("Losses");
                kills = result.getString("Kills");
                death = result.getString("Deaths");
                stats.add("Username:");
                stats.add(user);
                stats.add("\nWins:");
                stats.add(win);
                stats.add("\nLosses:");
                stats.add(loss);
                stats.add("\nKills:");
                stats.add(kills);
                stats.add("\nDeaths:");
                stats.add(death);
            }

        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        }
        return stats;
    }

    private String databaseRole(MessageReceivedEvent m) throws IllegalAccessException, InstantiationException{
        String role = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = URL;
            Connection conn = DriverManager.getConnection(url, db, pass);
            Statement state = conn.createStatement();
            ResultSet result = state.executeQuery("SELECT Roles from Users WHERE Username = '" + m.getMessage().getAuthor().getName() + "'");

            while (result.next()) {
                role = result.getString("Roles");

            }
        } catch (SQLException e) {
            System.out.println("WE NOT GETTING INTO DA DATABASE");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("NO DRIVER DAWG");
            System.out.println(e.getMessage());
        }
        return role;
    }

    private void sendMessage(String msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        e.getMessage().getChannel().sendMessage(msg);
    }
    private void sendRole(String msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        e.getMessage().getChannel().sendMessage(msg);
    }
    private void sendUserStat(ArrayList<String> msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (msg != null) {
            String app = String.join("\t", msg);
            e.getMessage().getChannel().sendMessage(app);
        }
    }
    private void sendAvailable(ArrayList<String> msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (msg != null) {
            String app = String.join("", msg);
            e.getMessage().getChannel().sendMessage(app);
        }
    }

    private void sendRep(ArrayList<String> msg, MessageReceivedEvent e) throws RateLimitException, DiscordException, MissingPermissionsException {
        if(msg != null) {
            String app = String.join("\t", msg);
            e.getMessage().getChannel().sendMessage(app);
        }
    }

    private void sendPrivateMessage(IDiscordClient client, String user) throws DiscordException, RateLimitException, MissingPermissionsException{
        IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(user));
        channel.sendMessage("Hello User");
    }
}

