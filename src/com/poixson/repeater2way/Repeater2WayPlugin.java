package com.poixson.repeater2way;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;

import com.poixson.commonmc.tools.plugin.xJavaPlugin;


public class Repeater2WayPlugin extends xJavaPlugin {
	public static final String LOG_PREFIX  = "[Repeater2Way] ";
	public static final String CHAT_PREFIX = ChatColor.AQUA + LOG_PREFIX + ChatColor.WHITE;
	public static final Logger log = Logger.getLogger("Minecraft");
	public static final int SPIGOT_PLUGIN_ID = 107123;
	public static final int BSTATS_PLUGIN_ID = 17260;

	protected static final AtomicReference<Repeater2WayPlugin> instance = new AtomicReference<Repeater2WayPlugin>(null);

	protected final AtomicReference<RedstoneRepeaterListener> repeaterListener = new AtomicReference<RedstoneRepeaterListener>(null);



	public Repeater2WayPlugin() {
		super(Repeater2WayPlugin.class);
	}



	@Override
	public void onEnable() {
		super.onEnable();
		if (!instance.compareAndSet(null, this))
			throw new RuntimeException("Plugin instance already enabled?");
		{
			final RedstoneRepeaterListener listener = new RedstoneRepeaterListener(this);
			final RedstoneRepeaterListener previous = this.repeaterListener.getAndSet(listener);
			if (previous != null)
				HandlerList.unregisterAll(previous);
			Bukkit.getPluginManager()
				.registerEvents(listener, this);
			listener.start();
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
		// restore repeaters
		{
			final RedstoneRepeaterListener listener = this.repeaterListener.get();
			if (listener != null)
				listener.unload();
		}
	}



	// -------------------------------------------------------------------------------



	@Override
	protected int getSpigotPluginID() {
		return SPIGOT_PLUGIN_ID;
	}
	@Override
	protected int getBStatsID() {
		return BSTATS_PLUGIN_ID;
	}



}
