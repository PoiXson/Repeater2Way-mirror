package com.poixson.repeater2way;

import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.event.HandlerList;

import com.poixson.tools.xJavaPlugin;


public class Repeater2WayPlugin extends xJavaPlugin {
	@Override public int getSpigotPluginID() { return 107123; }
	@Override public int getBStatsID() {       return 17260;  }
	public static final String LOG_PREFIX  = "[Repeater2Way] ";

	protected final AtomicReference<RedstoneRepeaterListener> repeaterListener = new AtomicReference<RedstoneRepeaterListener>(null);



	public Repeater2WayPlugin() {
		super(Repeater2WayPlugin.class);
	}



	@Override
	public void onEnable() {
		super.onEnable();
		// redstone repeater listener
		{
			final RedstoneRepeaterListener listener = new RedstoneRepeaterListener(this);
			final RedstoneRepeaterListener previous = this.repeaterListener.getAndSet(listener);
			if (previous != null)
				HandlerList.unregisterAll(previous);
			listener.start();
		}
		this.saveConfigs();
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
	}



}
