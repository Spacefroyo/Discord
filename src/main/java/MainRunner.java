package com.github.decyg;

import sx.blah.discord.api.IDiscordClient;

import java.io.File;

/**
 * Created by declan on 03/04/2017.
 */
public class MainRunner {

    public static File hgsimfile = new File("C:\\Users\\peter\\Documents\\GitHub\\d4jexamplebot\\hgsimIn");
    public static File jokefile = new File("C:\\Users\\peter\\Documents\\GitHub\\d4jexamplebot\\jokeIn");
    public static File battlefile = new File("C:\\Users\\peter\\Documents\\GitHub\\d4jexamplebot\\battleIn");
    public static void main(String[] args){

        if(args.length != 1){
            System.out.println("Please enter the bots token as the first argument e.g java -jar thisjar.jar tokenhere");
            return;
        }

        IDiscordClient cli = BotUtils.getBuiltDiscordClient(args[0]);

        // Register a listener via the EventSubscriber annotation which allows for organisation and delegation of events
        cli.getDispatcher().registerListener(new CommandHandler());

        // Only login after all events are registered otherwise some may be missed.
        cli.login();

    }

}
