package com.poixson.repeater2way;

import java.util.concurrent.atomic.AtomicReference;

import com.poixson.tools.xJavaPlugin;


public class Repeater2WayPlugin extends xJavaPlugin {
	@Override public int getBStatsID() { return 17260; }

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
				previous.unload();
			listener.start();
		}
		// save
		this.setConfigChanged();
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
