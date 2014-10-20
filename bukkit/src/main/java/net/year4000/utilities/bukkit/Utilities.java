package net.year4000.utilities.bukkit;

import com.ewized.utilities.bukkit.BukkitPlugin;
import lombok.Getter;
import net.year4000.utilities.bukkit.bossbar.BarAPIListener;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;

public class Utilities extends BukkitPlugin {
    @Getter
    private static Utilities inst;

    @Override
    public void onLoad() {
        inst = this;
    }

    @Override
    public void onEnable() {
        // Register boss bar
        Bukkit.getPluginManager().registerEvents(new BarAPIListener(), this);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                BossBar.handleTeleport(player, player.getLocation());
            });
        }, 20L, 20L);
    }
}
