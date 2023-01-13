package com.poixson.repeater2way;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.poixson.commonbukkit.pxnCommonPlugin;
import com.poixson.tools.AppProps;


public class Repeater2WayPlugin extends JavaPlugin {
	public static final String LOG_PREFIX  = "[Repeater2Way] ";
	public static final String CHAT_PREFIX = ChatColor.AQUA + LOG_PREFIX + ChatColor.WHITE;
	public static final Logger log = Logger.getLogger("Minecraft");
	public static final int SPIGOT_PLUGIN_ID = 107123;
	public static final int BSTATS_PLUGIN_ID = 17260;

	protected static final AtomicReference<Repeater2WayPlugin> instance = new AtomicReference<Repeater2WayPlugin>(null);
	protected static final AtomicReference<Metrics>            metrics  = new AtomicReference<Metrics>(null);
	protected final AppProps props;

	protected final AtomicReference<RedstoneRepeaterListener> repeaterListener = new AtomicReference<RedstoneRepeaterListener>(null);



	public Repeater2WayPlugin() {
		try {
			this.props = AppProps.LoadFromClassRef(Repeater2WayPlugin.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}



	@Override
	public void onEnable() {
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
		// bStats
		System.setProperty("bstats.relocatecheck","false");
		metrics.set(new Metrics(this, BSTATS_PLUGIN_ID));
		// update checker
		pxnCommonPlugin.GetPlugin()
			.getUpdateCheckManager()
				.addPlugin(this, SPIGOT_PLUGIN_ID, this.getPluginVersion());
	}

	@Override
	public void onDisable() {
		// update checker
		pxnCommonPlugin.GetPlugin()
			.getUpdateCheckManager()
				.removePlugin(SPIGOT_PLUGIN_ID);
		// stop schedulers
		try {
			Bukkit.getScheduler()
				.cancelTasks(this);
		} catch (Exception ignore) {}
		// stop listeners
		HandlerList.unregisterAll(this);
		if (!instance.compareAndSet(this, null))
			throw new RuntimeException("Disable wrong instance of plugin?");
		// restore repeaters
		{
			final RedstoneRepeaterListener listener = this.repeaterListener.get();
			if (listener != null)
				listener.unload();
		}
	}



	public String getPluginVersion() {
		return this.props.version;
	}



}
