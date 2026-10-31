
package com.fserver.lobbyspawn;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public final class LobbySpawnPaper extends JavaPlugin
        implements PluginMessageListener, Listener {

    private static final String CHANNEL = "fserver:lobbyspawn";

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getMessenger()
                .registerIncomingPluginChannel(this, CHANNEL, this);

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("FServerLobbySpawn Paper listener enabled.");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger()
                .unregisterIncomingPluginChannel(this, CHANNEL, this);
    }

    // Successful AuthMe login
    @EventHandler
    public void onAuthMeLogin(LoginEvent event) {
        Player player = event.getPlayer();

        getLogger().info("AuthMe login detected for " + player.getName());

        teleportToSpawn(player);
    }

    // Velocity server-switch message
    @Override
    public void onPluginMessageReceived(
            String channel, Player player, byte[] message) {

        getLogger().info("Plugin message received! Channel: " + channel
                + ", Player: " + player.getName()
                + ", Bytes: " + message.length);

        if (!CHANNEL.equals(channel)) {
            getLogger().warning("Unexpected channel: " + channel);
            return;
        }

        if (message.length != 1 || message[0] != 1) {
            getLogger().warning("Invalid plugin message.");
            return;
        }

        teleportToSpawn(player);
    }

    private void teleportToSpawn(Player player) {
        getServer().getScheduler().runTaskLater(this, () -> {
            if (!player.isOnline()) {
                return;
            }

            String worldName = getConfig().getString("spawn.world", "world");
            World world = getServer().getWorld(worldName);

            if (world == null) {
                getLogger().warning("Spawn world not loaded: " + worldName);
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

            boolean success = player.teleport(spawn);

            getLogger().info("Teleport result for "
                    + player.getName() + ": " + success);

        }, 20L);
    }
}
