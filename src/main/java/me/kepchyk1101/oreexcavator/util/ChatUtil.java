package me.kepchyk1101.oreexcavator.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@UtilityClass
public class ChatUtil {

    public void sendColorizedMessage(CommandSender target, String message) {
        target.sendMessage(colorize(message));
    }

    public String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
