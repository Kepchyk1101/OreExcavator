package me.kepchyk1101.oreexcavator.command;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.oreexcavator.OreExcavator;
import me.kepchyk1101.oreexcavator.service.CustomBooksGiver;
import me.kepchyk1101.oreexcavator.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OreExcavatorCommand implements TabExecutor {

    OreExcavator plugin;
    CustomBooksGiver customBooksGiver;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        Configuration config = plugin.getConfig();

        if (!(sender instanceof Player)) {
            ChatUtil.sendColorizedMessage(sender, config.getString("Messages.OnlyPlayer"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            ChatUtil.sendColorizedMessage(player, config.getString("Messages.CommandSyntax"));
            return true;
        }

        String subCommand = args[0];
        switch (subCommand) {
            case "give":

                if (!player.hasPermission("oe.give")) {
                    ChatUtil.sendColorizedMessage(player, config.getString("Messages.NoPerms"));
                    return true;
                }

                Player target = null;
                if (args.length >= 2) {
                    target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        ChatUtil.sendColorizedMessage(player, config.getString("Messages.PlayerNotFound"));
                        return true;
                    }
                }

                Player finalTarget = target != null ? target : player;
                customBooksGiver.give(finalTarget, OreExcavator.getOreExcavationEnch(), 1,
                        Collections.singletonList(ChatUtil.colorize(config.getString("General.EnchantmentName"))));
                break;

            case "reload":

                if (!player.hasPermission("oe.reload")) {
                    ChatUtil.sendColorizedMessage(player, config.getString("Messages.NoPerms"));
                    return true;
                }

                plugin.reload();
                ChatUtil.sendColorizedMessage(player, config.getString("Messages.ConfigReloaded"));

                break;
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            if (sender.hasPermission("oe.give")) result.add("give");
            if (sender.hasPermission("oe.reload")) result.add("reload");
            return result;
        }
        if (args.length == 2 && args[0].equals("give") && sender.hasPermission("oe.give")) {
            return null;
        }

        return Collections.emptyList();
    }
}
