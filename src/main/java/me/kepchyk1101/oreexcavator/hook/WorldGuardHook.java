package me.kepchyk1101.oreexcavator.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorldGuardHook {

    WorldGuard worldGuard;

    public boolean canBreak(Player player, Block block) {
        if (player.isOp()) return true;

        ApplicableRegionSet regions = worldGuard
                .getPlatform()
                .getRegionContainer()
                .createQuery()
                .getApplicableRegions(
                        BukkitAdapter.adapt(block.getLocation())
                );

        if (regions.size() == 0) return true;

        for (ProtectedRegion region : regions) {
            UUID playerUuid = player.getUniqueId();
            DefaultDomain owners = region.getOwners();
            DefaultDomain members = region.getMembers();
            if (owners.contains(playerUuid) || members.contains(playerUuid)) {
                return true;
            }
        }

        return false;

    }

    public List<Block> removeBlocksThatPlayerCantBreak(List<Block> list, Player player) {
        List<Block> result = new ArrayList<>();
        for (Block block : list) {
            if (canBreak(player, block)) {
                result.add(block);
            }
        }
        return result;
    }

}
