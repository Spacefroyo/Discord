package com.github.decyg;

import com.github.decyg.lavaplayer.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
//import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.impl.obj.Role;
//import sx.blah.discord.handle.impl.obj.User;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RoleBuilder;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

/**
 * Created by declan on 04/04/2017.
 */

class ppl{
    String user;
    int[] moves = new int[4];
    int atk, def, acc, spd, hp, xp, lv;
    public ppl(String user, int[] moves, int atk, int def, int acc, int spd, int hp, int xp)
    {
        this.user = user;
        this.moves = moves;
        this.atk = atk;
        this.def = def;
        this.acc = acc;
        this.spd = spd;
        this.hp = hp;
        this.xp = xp;
        int temp = xp;
        int i;
        for (i = 1; temp > 0; i++)
        {
            temp -= i*1000;
        }
        this.lv = i;
    }
}

class move{
    String name;
    int[][] effect = new int[2][5];
    int acc;
    public move(String name, int[][] effect, int acc)
    {
        this.name = name;
        this.effect = effect;
        this.acc = acc;
    }
}

public class CommandHandler {

    // A static map of commands mapping from command string to the functional impl
    public static Map<String, Command> commandMap = new HashMap<>();

    private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();;;
    private static final Map<Long, GuildMusicManager> musicManagers  = new HashMap<>();;
    public static Scanner kb;

    public static String[] p12 = new String[2];
    public static int[][] tempStat = new int[2][5];
    public static int turn;
    public static boolean curbattle = false;
    public static ArrayList<ppl> p = new ArrayList<ppl>();
    public static ArrayList<String> pName = new ArrayList<String>();
    public static ArrayList<move> moves = new ArrayList<move>();
    public static ArrayList<String> moveName = new ArrayList<String>();

    // Statically populate the commandMap with the intended functionality
    // Might be better practise to do this from an instantiated objects constructor
    static {

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        commandMap.put("arrive", (event, args) ->
        {
            message(event, "Did u miss me? \uD83D\uDE08");
        });

        commandMap.put("spam", (event, args) ->
        {
            Object[] para = args.toArray();
            int times = Integer.parseInt(String.valueOf(para[0]));
            String text = String.valueOf(para[1]);
            for (int i = 2; i < para.length; i++)
                text += " " + String.valueOf(para[i]);
            for (int i = 0; i < times; i++)
                message(event, text);
        });

        commandMap.put("hgsim", (event, args) ->
        {
            if (args.get(0).equals("start"))
                hgsim.run(event);
        });

        commandMap.put("protocol", (event, args) ->
        {
            if (args.get(0).equals("power"))
            {
                try {
                    IGuild guild = event.getClient().getGuilds().get(0);

                    RoleBuilder roleBuilder = new RoleBuilder(guild);
                    roleBuilder.withName("MeBot");
                    roleBuilder.withColor(Color.yellow);
                    roleBuilder.setHoist(false);
                    roleBuilder.setMentionable(false);
                    roleBuilder.withPermissions(EnumSet.of(Permissions.MANAGE_ROLES));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.SEND_MESSAGES));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.READ_MESSAGES));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.READ_MESSAGE_HISTORY));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.BAN));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.KICK));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.MANAGE_NICKNAMES));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.MANAGE_CHANNELS));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.MANAGE_MESSAGES));
                    roleBuilder.withPermissions(EnumSet.of(Permissions.MANAGE_PERMISSIONS));
                    IRole role = roleBuilder.build();

                    IUser ourUser = event.getAuthor();
                    ourUser.addRole(role);
                } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                    e.printStackTrace();
                }
            }
        });

        try
        {
            kb = new Scanner(MainRunner.battlefile);
        }
        catch (FileNotFoundException e)
        {}
        while(kb.hasNextLine())
        {
            String s = kb.nextLine();
            while (!s.equals("End"))
            {
                StringTokenizer st = new StringTokenizer(s);
                String cur = st.nextToken();
                for (int i = 0; i < cur.length()-1; i++)
                {
                    if (cur.charAt(i) == '_')
                        cur = cur.substring(0, i) + " " + cur.substring(i+1);
                }
                pName.add(cur);
                p.add(new ppl(cur, new int[]{Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())}, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
                s = kb.nextLine();
            }
            int curacc;
            while (kb.hasNextLine())
            {
                s = kb.nextLine();
                moveName.add(s);
                curacc = Integer.parseInt(kb.nextLine());
                int[][] ef = new int[2][5];
                for (int j = 0; j < 2; j++)
                {
                    StringTokenizer st = new StringTokenizer(kb.nextLine());
                    for (int i = 0; i < 5; i++)
                        ef[j][i] = Integer.parseInt(st.nextToken());
                }
                moves.add(new move(s, ef, curacc));
            }
        }
        commandMap.put("battle", (event, args) ->
        {
//            for (int i = 0; i < pName.size(); i++)
//                message(event, pName.get(i));
            p12[0] = event.getAuthor().getName();
            p12[1] = args.get(0);
            if (p12[1].equals("stop"))
            {
                message(event, "The battle has been stopped.");
                curbattle = false;
                return;
            }
            if (curbattle)
            {
                message(event, "A battle has already been initiated.");
                return;
            }
            for (int i = 0; i < 2; i++)
                tempStat[i] = new int[]{p.get(pName.indexOf(p12[i])).atk, p.get(pName.indexOf(p12[i])).def, p.get(pName.indexOf(p12[i])).acc, p.get(pName.indexOf(p12[i])).spd, p.get(pName.indexOf(p12[i])).hp};
            message(event, p12[0] + " has initiated a battle with " + p12[1] + ".");
            curbattle = true;
            if (p.get(pName.indexOf(p12[0])).spd > p.get(pName.indexOf(p12[1])).spd)
                turn = 0;
            else if (p.get(pName.indexOf(p12[0])).spd < p.get(pName.indexOf(p12[1])).spd)
                turn = 1;
            else
                turn = (int)(Math.random()*2);
            message(event, p12[turn] + " will attack first.");
            displayStat(event, turn);
            displayStat(event, 1-turn);
        });
        commandMap.put("move", (event, args) ->
        {
            if (args.get(0).equals("print"))
            {
                if (curbattle)
                {
                    message(event, "A battle has already been initiated.");
                    return;
                }
                int i = pName.indexOf(event.getAuthor().getName());
                String s = "Your moves are:\n";
                for (int j = 0; j < 4; j++)
                    s += "Player Move " + (j+1) + ": " + moveName.get(p.get(i).moves[j]) + "\n";
                message(event, s);
                printMoves(event);
                return;
            }
            else if (args.get(0).equals("switch"))
            {
                if (curbattle)
                {
                    message(event, "A battle has already been initiated.");
                    return;
                }
                int i = pName.indexOf(event.getAuthor().getName());
                int[] tempMove = p.get(i).moves;
                int cur = tempMove[Integer.parseInt(args.get(1))-1];
                tempMove[Integer.parseInt(args.get(1))-1] = Integer.parseInt(args.get(2))-1;
                p.set(i, new ppl(pName.get(i), tempMove, p.get(i).atk, p.get(i).def, p.get(i).acc, p.get(i).spd, p.get(i).hp, p.get(i).xp));
                message(event, "You have successfully switched " + moveName.get(cur) + " with " + moveName.get(tempMove[Integer.parseInt(args.get(1))-1]) + ".");
                try
                {
                    updateBattle();
                }
                catch (IOException e)
                {}
                return;
            }
            else if (args.get(0).equals("add"))
            {
                if (curbattle)
                {
                    message(event, "A battle has already been initiated.");
                    return;
                }
                String name = args.get(1);
                int[][] m = new int[2][5];
                int acc = Integer.parseInt(args.get(2));
                for (int i = 0; i < 2; i++)
                {
                    for (int j = 0; j < 5; j++)
                        m[i][j] = Integer.parseInt(args.get(2+(i*5)+j));
                }
                moveName.add(name);
                moves.add(new move(name, m, acc));
                message(event, "You have successfully added " + moveName.get(moveName.size()) + ".");
                try
                {
                    updateBattle();
                }
                catch (IOException e)
                {}
                return;
            }
            if (!curbattle)
            {
                message(event, "A battle has not been initiated.");
                return;
            }
            else if ((turn == 0 && !event.getAuthor().getName().equals(p12[0])) || (turn == 1 && !event.getAuthor().getName().equals(p12[1])))
            {
                if (event.getAuthor().getName().equals(p12[0]) || event.getAuthor().getName().equals(p12[1]))
                    message(event, "It is not your turn.");
                else
                    message(event, "You are not a participant in this battle.");
            }
            String out = "";
            int move = Integer.parseInt(args.get(0))-1;
            out += p12[turn] + " used " + moveName.get(p.get(pName.indexOf(p12[turn])).moves[move]) + ".\n";
            int moveId = p.get(pName.indexOf(p12[turn])).moves[move];
            if (moves.get(moveId).effect[1][4] != 0 && (int)(Math.random()*10000) > moves.get(moveId).acc*tempStat[turn][2])
            {
                message(event, out + "But they missed.");
                turn = 1 - turn;
                displayStat(event, turn);
                displayStat(event, 1-turn);
                return;
            }
            if (moves.get(moveId).effect[turn][4] != 0)
            {
                tempStat[turn][4] += moves.get(moveId).effect[0][4];
                if (moves.get(moveId).effect[0][4] > 0)
                    out += p12[turn] + " has healed " + moves.get(moveId).effect[0][4] + " hp.\n";
                else
                    out += p12[turn] + " has damaged themselves by " + (-moves.get(moveId).effect[0][4]) + " hp.\n";
            }
            if (moves.get(moveId).effect[1][4] != 0)
            {
                double a = moves.get(moveId).effect[1][4];
                double b = tempStat[turn][0];
                double c = tempStat[1-turn][1];
                double d = a*b/c;
                tempStat[1-turn][4] += (int)(d);
                if (moves.get(moveId).effect[1][4] > 0)
                    out += p12[1-turn] + " has healed " + d + " hp.\n";
                else
                out += p12[1-turn] + " has been damaged by " + (-d) + " hp.\n";
            }
            for (int i = 0; i < 2; i++)
            {
                for (int j = 0; j < 4; j++)
                {
                    if (moves.get(moveId).effect[i][j] != 0)
                    {
                        tempStat[(turn+i)%2][j] += moves.get(moveId).effect[i][j];
                        String s = "";
                        if (j == 0)
                            s = " attack ";
                        else if (j == 1)
                            s = " defense ";
                        else if (j == 2)
                            s = " accuracy ";
                        else if (j == 3)
                            s = " speed ";
                        out +=  p12[(turn+i)%2] + "'s" + s + "changed by " + moves.get(moveId).effect[i][j] + ".\n";
                    }
                }
            }
            message(event, out);
            tempStat[0][4] = Math.max(tempStat[0][4], 0);
            tempStat[1][4] = Math.max(tempStat[1][4], 0);
            if (tempStat[1-turn][4] == 0 || tempStat[turn][4] == 0)
            {
                int xp = (int) (Math.random() * 1001) + 500;
                if (tempStat[turn][4] == 0)
                    turn = 1-turn;
                message(event, "Congratulations, " + p12[turn] + " has won. They gained " + xp + " expierience points.");
                int lv = p.get(pName.indexOf(p12[turn])).lv;
                p.set(pName.indexOf(p12[turn]), new ppl(p.get(pName.indexOf(p12[turn])).user, p.get(pName.indexOf(p12[turn])).moves, p.get(pName.indexOf(p12[turn])).atk, p.get(pName.indexOf(p12[turn])).def, p.get(pName.indexOf(p12[turn])).acc, p.get(pName.indexOf(p12[turn])).spd, p.get(pName.indexOf(p12[turn])).hp, p.get(pName.indexOf(p12[turn])).xp + xp));
                if (lv != p.get(pName.indexOf(p12[turn])).lv)
                    message(event, p12[turn] + " has leveled up to level " + p.get(pName.indexOf(p12[turn])).lv);
                double[] up = new double[5];
                up[0] = p.get(pName.indexOf(p12[turn])).atk;
                up[1] = p.get(pName.indexOf(p12[turn])).def;
                up[2] = p.get(pName.indexOf(p12[turn])).acc;
                up[3] = p.get(pName.indexOf(p12[turn])).spd;
                up[4] = p.get(pName.indexOf(p12[turn])).hp;
                p.set(pName.indexOf(p12[turn]), new ppl(p.get(pName.indexOf(p12[turn])).user, p.get(pName.indexOf(p12[turn])).moves, (int)(up[0]*Math.pow(1.08, p.get(pName.indexOf(p12[turn])).lv-lv)), (int)(up[1]*Math.pow(1.08, p.get(pName.indexOf(p12[turn])).lv-lv)), (int)(up[2]*Math.pow(1.08, p.get(pName.indexOf(p12[turn])).lv-lv)), (int)(up[3]*Math.pow(1.08, p.get(pName.indexOf(p12[turn])).lv-lv)), (int)(up[4]*Math.pow(1.08, p.get(pName.indexOf(p12[turn])).lv-lv)), p.get(pName.indexOf(p12[turn])).xp));
                String s = "Attack: " + up[0] + " -> " + p.get(pName.indexOf(p12[turn])).atk + "\n";
                s += "Defense: " + up[1] + " -> " + p.get(pName.indexOf(p12[turn])).def + "\n";
                s += "Accuracy: " + up[2] + " -> " + p.get(pName.indexOf(p12[turn])).acc + "\n";
                s += "Speed: " + up[3] + " -> " + p.get(pName.indexOf(p12[turn])).spd + "\n";
                s += "Max Health: " + up[4] + " -> " + p.get(pName.indexOf(p12[turn])).hp + "\n";
                message(event, s);
                curbattle = false;
                try
                {
                    updateBattle();
                }
                catch(IOException e)
                {}
            }
            else
            {
                turn = 1 - turn;
                displayStat(event, turn);
                displayStat(event, 1-turn);
            }
        });

        ArrayList<String> jokes = new ArrayList<String>();
        try
        {
            kb = new Scanner(MainRunner.jokefile);
        }
        catch (FileNotFoundException e)
        {}
        while(kb.hasNextLine())
        {
            String s = "";
            String next = kb.nextLine();
            while (!next.equals("End"))
            {
                if (s.length() == 0)
                    s = next;
                else
                    s += "\n" + next;
                next = kb.nextLine();
            }
            jokes.add(s);
        }
        commandMap.put("joke", (event, args) ->
        {
            message(event, jokes.get((int)(Math.random()*jokes.size())));
        });

    }

    public static void printMoves(MessageReceivedEvent event)
    {
        String s =  "The available moves are:\n";
        for (int i = 1; i < moves.size()+1; i++)
            s += "Move " + i + ": " + moveName.get(i-1) + "\n";
        message(event, s);
    }

    public static void displayStat(MessageReceivedEvent event, int pi)
    {
        String s = p12[pi] + "\nHealth: " + tempStat[pi][4];
        for (int i = 1; i < 5; i++)
            s += "\nMove " + i + ": " + moveName.get(p.get(pName.indexOf(p12[pi])).moves[i-1]);
        message(event, s);
    }

    public static void updateBattle() throws IOException
    {
        PrintWriter pw = new PrintWriter(new FileWriter(MainRunner.battlefile));
        for (int i = 0; i < p.size(); i++)
        {
            pw.println(p.get(i).user + " " + p.get(i).moves[0] + " " + p.get(i).moves[1] + " " + p.get(i).moves[2] + " " + p.get(i).moves[3] + " " + p.get(i).atk + " " + p.get(i).def + " " + p.get(i).acc + " " + p.get(i).spd + " " + p.get(i).hp + " " + p.get(i).xp);
        }
        pw.println("End");
        for (int i = 0; i < moves.size(); i++)
        {
            pw.println(moveName.get(i));
            pw.println(moves.get(i).acc);
            for (int j = 0; j < 2; j++)
            {
                for (int k = 0; k < 5; k++)
                {
                    pw.print(moves.get(i).effect[j][k] + " ");
                }
                if (j != 1 || i != moves.size()-1)
                    pw.println();
            }
        }
        pw.close();
    }

    public static void message(MessageReceivedEvent event, String text)
    {
        BotUtils.sendMessage(event.getChannel(), text);
    }

    private static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
        long guildId = guild.getLongID();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }

    private static void loadAndPlay(final IChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                BotUtils.sendMessage(channel, "Adding to queue " + track.getInfo().title);

                play(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                BotUtils.sendMessage(channel, "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")");

                play(musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                BotUtils.sendMessage(channel, "Nothing found by " + trackUrl);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                BotUtils.sendMessage(channel, "Could not play: " + exception.getMessage());
            }
        });
    }

    private static void play(GuildMusicManager musicManager, AudioTrack track) {

        musicManager.getScheduler().queue(track);
    }

    private static void skipTrack(IChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.getScheduler().nextTrack();

        BotUtils.sendMessage(channel, "Skipped to next track.");
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) throws FileNotFoundException, InterruptedException {

        // Note for error handling, you'll probably want to log failed commands with a logger or sout
        // hgsimIn most cases it's not advised to annoy the user with a reply incase they didn't intend to trigger a
        // command anyway, such as a user typing ?notacommand, the bot should not say "notacommand" doesn't exist in
        // most situations. It's partially good practise and partially developer preference

        // Given a message "/test arg1 arg2", argArray will contain ["/test", "arg1", "arg"]
        String[] argArray = event.getMessage().getContent().split(" ");

        // First ensure at least the command and prefix is present, the arg length can be handled by your command func
        if(argArray.length == 0)
            return;

        // Check if the first arg (the command) starts with the prefix defined in the utils class
        if(!argArray[0].startsWith(BotUtils.BOT_PREFIX))
            return;

        // Extract the "command" part of the first arg out by ditching the amount of characters present in the prefix
        String commandStr = argArray[0].substring(BotUtils.BOT_PREFIX.length());

        // Load the rest of the args in the array into a List for safer access
        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0); // Remove the command

        // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists

        if(commandMap.containsKey(commandStr))
            commandMap.get(commandStr).runCommand(event, argsList);

    }

}
