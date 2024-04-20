package dev.osowiz.speedrunstats.listeners;


import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.HandlerList;
public class SpeedrunListenerBase implements Listener {

    protected Plugin plugin;

    public final String name;
    public SpeedrunListenerBase(Plugin plugin, String name)
    {
        this.plugin = plugin;
        this.name = name;
    }

    public void register()
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void unregister()
    {
        HandlerList.unregisterAll(this);
    }

}
