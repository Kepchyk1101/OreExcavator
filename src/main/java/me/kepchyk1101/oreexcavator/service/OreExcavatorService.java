package me.kepchyk1101.oreexcavator.service;

import lombok.*;
import lombok.experimental.FieldDefaults;
import me.kepchyk1101.oreexcavator.hook.WorldGuardHook;
import me.kepchyk1101.oreexcavator.util.BlockUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OreExcavatorService {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    final Plugin plugin;

    WorldGuardHook worldGuardHook;
    DiggingMode diggingMode;
    int limit;
    int msDelay;
    boolean enableParticles;

    /**
     * Allows you to dig up the entire ore vein at once
     * @param initialBlock the block from which the search for ores begins
     * @param player used to dig out a vein from a specific player (his tool), as well as for compatibility with WorldGuard, optional
     */
    public void excavate(@NotNull Block initialBlock, @Nullable Player player) {

        Validate.notNull(initialBlock);

        List<Block> foundOres = findConnectedBlocks(
                initialBlock,
                Math.max(limit, 2),
                diggingMode != null ? diggingMode : DiggingMode.SINGLE
        );

        if (worldGuardHook != null && player != null) {
            foundOres = worldGuardHook.removeBlocksThatPlayerCantBreak(foundOres, player);
        }

        ItemStack tool = null;
        int blocksToBreakAmount = foundOres.size();

        if (player != null) {

            if (player.getGameMode() == GameMode.SURVIVAL) {
                tool = player.getInventory().getItemInMainHand();
                ItemMeta toolMeta = tool.getItemMeta();
                if (toolMeta instanceof Damageable) {
                    Damageable damageable = (Damageable) toolMeta;
                    int maxToolDurability = tool.getType().getMaxDurability();
                    int currentToolDurability = damageable.getDamage();
                    int maxAllegedDurability = maxToolDurability - currentToolDurability;
                    blocksToBreakAmount = Math.min(blocksToBreakAmount, maxAllegedDurability);
                    damageable.setDamage(damageable.getDamage() + blocksToBreakAmount);
                    tool.setItemMeta((ItemMeta) damageable);
                }
            }

        }

        List<Block> finalFoundOres = foundOres;
        ItemStack finalTool = tool;
        BukkitScheduler scheduler = Bukkit.getScheduler();
        int finalBlocksToBreakAmount = blocksToBreakAmount;
        scheduler.runTaskAsynchronously(plugin, () -> {

            AtomicInteger blocksBroken = new AtomicInteger();

            for (Block block : finalFoundOres) {

                if (blocksBroken.get() >= finalBlocksToBreakAmount) break;

                scheduler.runTask(plugin, () -> {

                    block.breakNaturally(finalTool);
                    blocksBroken.getAndIncrement();

                    World blockWorld = block.getWorld();
                    if (enableParticles) {
                        blockWorld.spawnParticle(
                                Particle.FIREWORKS_SPARK,
                                block.getLocation().add(0, 0.5, 0),
                                3, 0, 0, 0, 0.1);
                    }

                    blockWorld.playSound(
                            block.getLocation(),
                            block.getBlockData().getSoundGroup().getBreakSound(),
                            1f, 1f);

                });

                if (msDelay > 0) {
                    try {
                        Thread.sleep(msDelay);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        });

    }

    private List<Block> findConnectedBlocks(Block mainBlock, int limit, DiggingMode diggingMode) {
        List<Block> connectedBlocks = new ArrayList<>();
        connectedBlocks.add(mainBlock);

        Material mainType = mainBlock.getType();

        findConnectedBlocksRecursive(mainBlock.getLocation(), connectedBlocks, mainType, limit, diggingMode);

        return connectedBlocks;
    }

    private boolean findConnectedBlocksRecursive(Location location, List<Block> connectedBlocks,
                                                 Material mainType, int limit, DiggingMode diggingMode) {
        if (connectedBlocks.size() >= limit) {
            return true;
        }

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    if (xOffset == 0 && yOffset == 0 && zOffset == 0) {
                        continue;
                    }

                    Location currentLoc = location.clone().add(xOffset, yOffset, zOffset);
                    Block currentBlock = currentLoc.getBlock();

                    if (((diggingMode == DiggingMode.SINGLE && currentBlock.getType() == mainType)
                            || (diggingMode == DiggingMode.MULTI && BlockUtil.isOre(currentBlock.getType())))
                            && !connectedBlocks.contains(currentBlock)) {
                        connectedBlocks.add(currentBlock);
                        if (findConnectedBlocksRecursive(currentLoc, connectedBlocks, mainType, limit, diggingMode)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public enum DiggingMode {

        SINGLE,

        MULTI;

        @Nullable
        public static DiggingMode byName(@Nullable String name) {
            for (DiggingMode dm : DiggingMode.values()) {
                if (dm.name().equals(name)) {
                    return dm;
                }
            }
            return null;
        }

    }

}
