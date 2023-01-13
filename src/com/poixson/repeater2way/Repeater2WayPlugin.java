package com.poixson.repeater2way;

import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.event.HandlerList;

import com.poixson.commonmc.tools.plugin.xJavaPlugin;


public class Repeater2WayPlugin extends xJavaPlugin {
	protected static final String LOG_PREFIX  = "[Repeater2Way] ";
	protected static final int SPIGOT_PLUGIN_ID = 107123;
	protected static final int BSTATS_PLUGIN_ID = 17260;

	protected static final AtomicReference<Repeater2WayPlugin> instance = new AtomicReference<Repeater2WayPlugin>(null);

	protected final AtomicReference<RedstoneRepeaterListener> repeaterListener = new AtomicReference<RedstoneRepeaterListener>(null);



	public Repeater2WayPlugin() {
		super(Repeater2WayPlugin.class);
	}



	@Override
	public void onEnable() {
		if (!instance.compareAndSet(null, this))
			throw new RuntimeException("Plugin instance already enabled?");
		super.onEnable();
		// redstone repeater listener
		{
			final RedstoneRepeaterListener listener = new RedstoneRepeaterListener(this);
			final RedstoneRepeaterListener previous = this.repeaterListener.getAndSet(listener);
			if (previous != null)
				HandlerList.unregisterAll(previous);
			listener.start();
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
		// restore repeaters
		{
			final RedstoneRepeaterListener listener = this.repeaterListener.getAndSet(null);
			if (listener != null)
				listener.unload();
		}
		if (!instance.compareAndSet(this, null))
			(new RuntimeException("Disable wrong instance of plugin?")).printStackTrace();
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
