package com.github.decyg;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.User;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by declan on 04/04/2017.
 */
public interface Command {

    // Interface for a command to be implemented in the command map
    void runCommand(MessageReceivedEvent event, List<String> args) throws FileNotFoundException, InterruptedException;

}
