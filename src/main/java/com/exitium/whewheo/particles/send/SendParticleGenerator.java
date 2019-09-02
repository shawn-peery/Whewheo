package com.exitium.whewheo.particles.send;

import com.exitium.whewheo.Main;
import com.exitium.whewheo.init.ConfigLoader;
import com.exitium.whewheo.init.ServerSelectionHandler;
import com.exitium.whewheo.particles.ParticleGenerator;
import com.exitium.whewheo.particles.receive.ReceiveParticleGenerator;
import com.exitium.whewheo.particles.receive.ValidReceiveGenerators;
import com.exitium.whewheo.teleportobjects.WarpTP;
import com.exitium.whewheo.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * General Send Particle Generator class that all Send Particle Generators must
 * extend.
 * 
 * @author Cloaking_Ocean date Apr 1, 2017
 * @version 1.0
 */
public class SendParticleGenerator extends ParticleGenerator {

	protected WarpTP warp;

	public static double delay;
	protected int currentSecond = -1;

	private ConfigLoader configLoader;
	private Main main;
	private ServerSelectionHandler serverSel;

	public SendParticleGenerator(Player player, int tickDelay, WarpTP warp, Main main) {
		super(player, tickDelay);

		this.configLoader = main.getConfigLoader();
		this.main = main;
		this.serverSel = main.getServerSel();

		this.warp = warp;

		if (!this.configLoader.getConfig().contains("general")) {
			Bukkit.getServer().getLogger()
					.severe("Couldn't load SendParticleGeneartors because there is no \"general\" section in config.");
			delay = -1;
		}

		if (!this.configLoader.getConfig().getConfigurationSection("general").contains("teleportDelay")) {
			Bukkit.getServer().getLogger().severe(
					"Couldn't load SendParticleGeneartors because there is no \"teleportDelay\" section in config.");
			delay = -1;
		}

		delay = this.configLoader.getConfig().getInt("general.teleportDelay");
	}

	protected void handleLocationDetails(Player player) {
		Bukkit.getServer().getLogger().severe("HandlingLocationDetails!");
		if (warp.getLocation() == null) {
			// Not specified. No need to teleport

			Bukkit.getServer().getLogger().severe("Warp " + warp.getName() + " does not have a valid Location! Please verify configuration!");
			return;
		}
		Bukkit.getServer().getLogger().severe("warp location is set to: " + Util.serializeLocation(warp.getLocation()));
		teleportPlayer(player, warp.getReceive());
	}

	protected void checkTeleporation() {
		if (secondsPassed < delay) {
			if (currentSecond < (int) secondsPassed) {
				currentSecond = (int) secondsPassed;
				player.sendMessage(this.configLoader.getPrefix() + this.configLoader.msg("teleportationWillCommenceIn").replace("%time%",
						((int) (delay - currentSecond)) + ""));
			}
			currentSecond = (int) secondsPassed;
		} else {
			serverSel.removeTeleportingPlayer(player.getUniqueId().toString());
			handleLocationDetails(player);
			cancel();

		}
	}

	protected void teleportPlayer(Player player, ValidReceiveGenerators generator) {
		serverSel.removeTeleportingPlayer(player.getUniqueId().toString());

		warp.receivePlayer(player, generator);
	}

	@Override
	public void run() {
		Bukkit.getServer().getLogger().severe("This SendParticleGenorator doesn't have a specific animation.");
	}

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		SendParticleGenerator.delay = delay;
	}

	public ReceiveParticleGenerator getGenerator(Player player, WarpTP warp) {
		Bukkit.getServer().getLogger().severe("Starting Switch Statement");

		return main.getReceiveGeneratorFromEnum(warp.getReceive(), player);
	}
}
