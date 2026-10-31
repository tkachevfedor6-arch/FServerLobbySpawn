
package com.fserver.lobbyspawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public final class LobbySpawnPaper extends JavaPlugin implements PluginMessageListener {
    private static final String CHANNEL = "fserver:lobbyspawn";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, this);
        getLogger().info("FServerLobbySpawn Paper listener enabled.");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL, this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!CHANNEL.equals(channel) || message.length != 1 || message[0] != 1) return;
        if (!player.isOnline()) return;

        String worldName = getConfig().getString("spawn.world", "world");
        World world = getServer().getWorld(worldName);

        if (world == null) {
            getLogger().warning("Configured spawn world '" + worldName + "' is not loaded.");
            return;
        }

        Location spawn = new Location(
                world,
                getConfig().getDouble("spawn.x"),
                getConfig().getDouble("spawn.y"),
                getConfig().getDouble("spawn.z"),
                (float) getConfig().getDouble("spawn.yaw"),
                (float) getConfig().getDouble("spawn.pitch")
        );

        player.teleport(spawn);
    }
}
