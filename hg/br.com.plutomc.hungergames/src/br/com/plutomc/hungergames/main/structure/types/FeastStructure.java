package br.com.plutomc.hungergames.main.structure.types;

import br.com.plutomc.core.common.CommonConst;
import br.com.plutomc.hungergames.engine.GameAPI;
import br.com.plutomc.hungergames.main.structure.Chestable;
import br.com.plutomc.hungergames.main.structure.Structure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class FeastStructure implements Structure, Chestable {

	private int radius;
	private int maxSpawnDistance;

	public FeastStructure() {
		this(25, 150);
	}

	public FeastStructure(int radius, int maxSpawnDistance) {
		this.radius = radius;
		this.maxSpawnDistance = maxSpawnDistance;
	}

	@Override
	public Location findPlace() {
		World w = Bukkit.getWorld("world");
		Random r = new Random();
		int x = -maxSpawnDistance + r.nextInt(2 * maxSpawnDistance);
		int z = -maxSpawnDistance + r.nextInt(2 * maxSpawnDistance);
		int y = w.getHighestBlockYAt(x, z);
		return new Location(w, x, y, z);
	}

	@Override
	public void spawnChest(Location location) {
		List<Chest> chests = new ArrayList<>();
		location.clone().add(0, 1, 0).getBlock().setType(Material.ENCHANTMENT_TABLE);
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if (!(x == 0 && z == 0) && ((x % 2 == 0 && z % 2 == 0) || (x % 2 != 0 && z % 2 != 0))) {
					Location loc = location.clone().add(x, 1, z);
					loc.getBlock().setType(Material.CHEST);

					if (loc.getBlock().getType() == Material.CHEST) {
						Block b = loc.getBlock();
						if (b.getState() instanceof Chest)
							chests.add((Chest) loc.getBlock().getState());
					}
				}
			}
		}

		if (chests.size() <= 0)
			return;

		List<ItemStack> items = generateItems();
		Iterator<ItemStack> iterator = items.iterator();
		int chestNumber = 0;

		while (iterator.hasNext() && chests.size() > 0) {
			if (chestNumber >= chests.size())
				chestNumber = 0;

			Chest chest = chests.get(chestNumber);

			if (chest == null) {
				chestNumber = 0;
				continue;
			}

			Inventory inv = chest.getBlockInventory();

			if (inv.firstEmpty() == -1) {
				chests.remove(chestNumber);
				continue;
			}

			ItemStack item = iterator.next();

			if (item.getAmount() >= 0)
				inv.addItem(item);

			chest.update();
			iterator.remove();
			++chestNumber;
		}
	}

	@Override
	public void spawnStructure(Location central) {
		System.out.println("New Feast: " + central.getBlockX() + " " + central.getBlockY() + " " + central.getBlockZ());
		central.getChunk().load(true);

		Material material = Material.GRASS;
		Biome biome = central.getBlock().getBiome();

		if (biome == Biome.FOREST)
			material = Material.STONE;

		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				Location feastBlock = central.clone().add(x, 0, z);
				if (central.distance(feastBlock) < radius) {
					feastBlock.getBlock().setType(material);
					feastBlock.getBlock().setMetadata("unbreakableBlock",
							new FixedMetadataValue(GameAPI.getInstance(), true));

					for (int i = 1; i < 10; i++) {
						Location airBlock = feastBlock.clone().add(0, i, 0);
						airBlock.getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}

	public List<ItemStack> generateItems() {
		List<ItemStack> feastItems = new ArrayList<>();
		feastItems.addAll(addItem(Material.DIAMOND, 2, 6));
		feastItems.addAll(addItem(Material.DIAMOND_SWORD, 0, 2));
		feastItems.addAll(addItem(Material.DIAMOND_HELMET, 0, 2));
		feastItems.addAll(addItem(Material.DIAMOND_CHESTPLATE, 1, 2));
		feastItems.addAll(addItem(Material.DIAMOND_LEGGINGS, 0, 2));
		feastItems.addAll(addItem(Material.DIAMOND_BOOTS, 0, 2));

		feastItems.addAll(addItem(Material.IRON_SWORD, 0, 3));
		feastItems.addAll(addItem(Material.IRON_HELMET, 0, 1));
		feastItems.addAll(addItem(Material.IRON_CHESTPLATE, 0, 1));
		feastItems.addAll(addItem(Material.IRON_LEGGINGS, 0, 1));
		feastItems.addAll(addItem(Material.IRON_BOOTS, 0, 1));

		feastItems.addAll(addItem(Material.COOKED_BEEF, 4, 32));
		feastItems.addAll(addItem(Material.COOKED_CHICKEN, 4, 24));
		feastItems.addAll(addItem(Material.BREAD, 4, 24));
		feastItems.addAll(addItem(Material.WATER_BUCKET, 2, 4));
		feastItems.addAll(addItem(Material.LAVA_BUCKET, 2, 4));
		feastItems.addAll(addItem(Material.MUSHROOM_SOUP, 10, 25));
		feastItems.addAll(addItem(Material.WEB, 10, 20));
		feastItems.addAll(addItem(Material.ENDER_PEARL, 5, 15));
		feastItems.addAll(addItem(Material.TNT, 16, 64));
		feastItems.addAll(addItem(Material.FLINT_AND_STEEL, 2, 6));
		feastItems.addAll(addItem(Material.ARROW, 12, 36));
		feastItems.addAll(addItem(Material.BOW, 1, 6));
		feastItems.addAll(addItem(Material.EXP_BOTTLE, 32, 64));
		feastItems.addAll(addItem(Material.GOLDEN_APPLE, 6, 12));
		feastItems.addAll(addItem(Material.POTION, (short) 16385, 0, 5));
		feastItems.addAll(addItem(Material.POTION, (short) 16386, 0, 5));
		feastItems.addAll(addItem(Material.POTION, (short) 16387, 0, 5));
		feastItems.addAll(addItem(Material.POTION, (short) 16388, 0, 5));
		feastItems.addAll(addItem(Material.POTION, (short) 16389, 0, 5));
		feastItems.addAll(addItem(Material.POTION, (short) 16394, 0, 5));
		feastItems.addAll(addItem(Material.POTION, (short) 16396, 0, 5));
		Collections.shuffle(feastItems);
		return feastItems;
	}

	private List<ItemStack> addItem(Material mat, int min, int max) {
		return addItem(mat, (short) 0, min, max);
	}

	private List<ItemStack> addItem(Material mat, short durability, int min, int max) {
		List<ItemStack> items = new ArrayList<>();

		for (int i = 0; i <= min + CommonConst.RANDOM.nextInt(max - min); i++) {
			items.add(new ItemStack(mat, 1, durability));
		}

		return items;
	}

	public List<Item> getItems() {
		List<Item> itemList = new ArrayList<>();

		itemList.add(new Item(Material.DIAMOND_SWORD, (short) 0, 1, 1, 12));
		itemList.add(new Item(Material.DIAMOND_BOOTS, (short) 0, 1, 1, 12));
		itemList.add(new Item(Material.DIAMOND_LEGGINGS, (short) 0, 1, 1, 12));
		itemList.add(new Item(Material.DIAMOND_CHESTPLATE, (short) 0, 1, 1, 12));
		itemList.add(new Item(Material.DIAMOND_HELMET, (short) 0, 1, 1, 12));

		itemList.add(new Item(Material.DIAMOND_HELMET, (short) 0, 1, 1, 12));

		return itemList;

	}

	@Getter
	@AllArgsConstructor
	public class Item {

		private Material material;
		private short data;

		private int min;
		private int max;

		private int chance;

	}

}