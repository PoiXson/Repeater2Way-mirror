package com.poixson.repeater2way;

import static com.poixson.repeater2way.RepeaterDAO.ValidateRepeater;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class RedstoneRepeaterListener extends BukkitRunnable implements Listener {

	protected final Repeater2WayPlugin plugin;

	protected final LinkedList<RepeaterDAO> queueOn  = new LinkedList<RepeaterDAO>();
	protected final LinkedList<RepeaterDAO> queueOff = new LinkedList<RepeaterDAO>();

	protected final HashMap<Location, RepeaterDAO> active = new HashMap<Location, RepeaterDAO>();



	public RedstoneRepeaterListener(final Repeater2WayPlugin plugin) {
		super();
		this.plugin = plugin;
	}



	public void start() {
		Bukkit.getPluginManager()
			.registerEvents(this, this.plugin);
		this.runTaskTimer(this.plugin, 20, 3);
	}
	public void unload() {
		HandlerList.unregisterAll(this);
		try {
			this.cancel();
		} catch (IllegalStateException ignore) {}
		// restore repeaters
		this.queueOn.clear();
		this.queueOff.clear();
		{
			Entry<Location, RepeaterDAO> entry;
			final Iterator<Entry<Location, RepeaterDAO>> it = this.active.entrySet().iterator();
			while (it.hasNext()) {
				entry = it.next();
				entry.getValue().restore();
				it.remove();
			}
		}
	}



	@Override
	public void run() {
		RepeaterDAO dao;
		while (true) {
			dao = this.queueOn.pollFirst();
			if (dao == null) break;
			if (dao.activate())
				this.active.put(dao.blockA.getLocation(), dao);
		}
		while (true) {
			dao = this.queueOff.pollFirst();
			if (dao == null) break;
			if (this.active.remove(dao.blockA.getLocation()) != null)
				dao.restore();
		}
	}



	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onBlockRedstone(final BlockRedstoneEvent event) {
		// turning on
		if (event.getOldCurrent() == 0
		&&  event.getNewCurrent() >  0) {
			if (ValidateRepeater(event.getBlock(), null)) {
				final Block blockA = event.getBlock();
				final Repeater repeater = (Repeater) blockA.getBlockData();
				final BlockFace facingA = repeater.getFacing();
				final Block blockB = blockA.getRelative( facingA.getOppositeFace() );
				if (ValidateRepeater(blockB, facingA.getOppositeFace())) {
					this.queueOn.addLast(
						new RepeaterDAO(blockA)
					);
				}
			}
		// turning off
		} else
		if (event.getOldCurrent() >  0
		&&  event.getNewCurrent() == 0) {
			final RepeaterDAO dao = this.active.get(event.getBlock().getLocation());
			if (dao != null) {
				this.queueOff.addLast(dao);
			}
		}
	}



}
