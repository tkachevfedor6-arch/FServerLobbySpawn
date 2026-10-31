package com.fserver.lobbyspawn;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

@Plugin(id = "fserverlobbyspawn", name = "FServerLobbySpawn", version = "1.0.0")
public final class LobbySpawnVelocity {
    private static final MinecraftChannelIdentifier CHANNEL =
            MinecraftChannelIdentifier.from("fserver:lobbyspawn");

    private final ProxyServer proxy;
    private final Logger logger;

    @Inject
    public LobbySpawnVelocity(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
        proxy.getChannelRegistrar().register(CHANNEL);
        logger.info("FServerLobbySpawn enabled; channel registered.");
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        if (event.getPreviousServer() == null) return;
        String from = event.getPreviousServer().getServerInfo().getName();
        if (!"minecraft".equalsIgnoreCase(from)) return;

        ServerConnection current = event.getPlayer().getCurrentServer().orElse(null);
        if (current == null) return;
        String to = current.getServerInfo().getName();
        if (!"lobby".equalsIgnoreCase(to)) return;

        // Sent only after a real minecraft -> lobby switch; first login/Limbo is untouched.
        current.sendPluginMessage(CHANNEL, new byte[] { 1 });
        logger.info("Requested lobby spawn for {} after minecraft -> lobby switch.",
                event.getPlayer().getUsername());
    }
}
