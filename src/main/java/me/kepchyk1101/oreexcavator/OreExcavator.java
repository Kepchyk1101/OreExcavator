package me.kepchyk1101.oreexcavator;

import com.sk89q.worldguard.WorldGuard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.oreexcavator.command.OreExcavatorCommand;
import me.kepchyk1101.oreexcavator.enchantment.OreExcavationEnch;
import me.kepchyk1101.oreexcavator.hook.WorldGuardHook;
import me.kepchyk1101.oreexcavator.listener.AnvilListener;
import me.kepchyk1101.oreexcavator.service.CustomBooksGiver;
import me.kepchyk1101.oreexcavator.service.OreExcavatorService;
import me.kepchyk1101.oreexcavator.util.ChatUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class OreExcavator extends JavaPlugin {

    @Getter
    private static boolean worldGuardEnabled;

    @Getter
    private static Enchantment oreExcavationEnch;

    WorldGuardHook worldGuardHook;
    OreExcavatorService oreExcavatorService;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        PluginManager pluginManager = getServer().getPluginManager();

        worldGuardEnabled = pluginManager.isPluginEnabled("WorldGuard");
        if (worldGuardEnabled) {
            worldGuardHook = new WorldGuardHook(WorldGuard.getInstance());
        }

        oreExcavatorService = new OreExcavatorService(this);
        applySettingForOES(oreExcavatorService);

        String enchName = ChatUtil.colorize(getConfig().getString("General.EnchantmentName"));
        oreExcavationEnch = new OreExcavationEnch(
                NamespacedKey.minecraft("ore_excavator"), enchName, oreExcavatorService);

        registerEnchantment(oreExcavationEnch);

        getCommand("oreexcavator").setExecutor(new OreExcavatorCommand(this, new CustomBooksGiver()));

        pluginManager.registerEvents((Listener) oreExcavationEnch, this);
        pluginManager.registerEvents(new AnvilListener(this), this);

    }

    public void reload() {
        reloadConfig();
        applySettingForOES(oreExcavatorService);
    }

    private void applySettingForOES(OreExcavatorService oreExcavatorService) {
        FileConfiguration config = getConfig();
        oreExcavatorService.setDiggingMode(OreExcavatorService.DiggingMode.byName(config.getString("General.Mode")));
        oreExcavatorService.setLimit(config.getInt("General.Limit"));
        oreExcavatorService.setMsDelay(config.getInt("General.DiggingDelay"));
        oreExcavatorService.setEnableParticles(config.getBoolean("General.Particles"));
        oreExcavatorService.setWorldGuardHook(worldGuardHook);
    }

    @SneakyThrows
    private void registerEnchantment(Enchantment enchantment) {

        // if enchantment already registered - return
        if (Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(enchantment)) return;

        Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
        acceptingNew.setAccessible(true);
        acceptingNew.set(null, true);
        Enchantment.registerEnchantment(enchantment);

    }

}
