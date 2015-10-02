/**
 * Bukkit plugin which moves the mobs closer to the players.
 * Copyright (C) 2015 Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.hordes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Contains all settings for the world.
 * 
 * @author Jakub Sapalski
 */
public class WorldSettings {
	
	private double height;
	private ArrayList<EntityType> entities = new ArrayList<>();
	private String world;
	private double multiplier;

	/**
	 * Loads the settings for a world.
	 * 
	 * @param plugin
	 *            plugin instance
	 * @param world
	 *            name of the world to load
	 * @throws LoadingException
	 *             when the configuration is incorrect
	 */
	public WorldSettings(Hordes plugin, String world) throws LoadingException {
		this.world = world;
		height = plugin.getConfig().getDouble(world + ".height", 24);
		multiplier = plugin.getConfig().getDouble(world + ".multi", 1);
		for (String entity : plugin.getConfig()
				.getStringList(world + ".mobs")) {
			try {
				entities.add(EntityType.valueOf(
						entity.toUpperCase().replace(' ', '_')));
			} catch (IllegalArgumentException e) {
				plugin.getLogger().warning("Unknown mob type: " + entity);
			}
		}
	}
	
	/**
	 * Checks if the entity is withing range of the player or if it has custom
	 * name. In other words, if it should be removed or left alone.
	 * 
	 * @param entity
	 *            entity to check
	 * @return true if the entity should not be removed, false if it should be
	 */
	public boolean shouldExist(Entity entity) {
		if (entity.getCustomName() != null) return true;
		if (!entities.contains(entity.getType())) return true;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getGameMode() == GameMode.CREATIVE ||
					player.getGameMode() == GameMode.SPECTATOR) continue; 
			if (!player.getWorld().getName().equals(world)) continue;
			double vDist = verticalDist(player.getLocation(), entity.getLocation());
			if (vDist <= height) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calculates vertical distance between the two locations.
	 * 
	 * @param loc1
	 *            first location
	 * @param loc2
	 *            second location
	 * @return vertical distance
	 */
	private double verticalDist(Location loc1, Location loc2) {
		double yDist = loc1.getY() - loc2.getY();
		return (yDist < 0) ? -yDist : yDist;
	}
	
	/**
	 * @return the list of EntityTypes which should be handled by the plugin on
	 *         this world
	 */
	public ArrayList<EntityType> getEntities() {
		return entities;
	}

	/**
	 * @return the health multiplier
	 */
	public double getMultiplier() {
		return multiplier;
	}

}