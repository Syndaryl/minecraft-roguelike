package greymerk.roguelike.dungeon.settings;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import greymerk.roguelike.config.RogueConfig;
import greymerk.roguelike.treasure.ITreasureChest;
import greymerk.roguelike.treasure.Treasure;
import greymerk.roguelike.treasure.TreasureManager;
import greymerk.roguelike.util.WeightedChoice;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IWorldEditor;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SettingsResolverTest {

	@Before
	public void setUp() throws Exception {
		Bootstrap.register();
		RogueConfig.testing = true;
	}

	@Test
	public void ResolveInheritOneLevel() throws Exception {
		
		DungeonSettings main = new DungeonSettings();
		DungeonSettings toInherit = new DungeonSettings();
		
		SettingsContainer settings = new SettingsContainer();
		
		main.id = new SettingIdentifier("main");
		toInherit.id = new SettingIdentifier("inherit");
		settings.put(main);
		settings.put(toInherit);
		
		main.inherit.add(toInherit.id);
		
		ItemStack stick = new ItemStack(Items.STICK);
		
		toInherit.lootRules.add(Treasure.REWARD, new WeightedChoice<ItemStack>(stick , 1), 0, true, 1);
		
		DungeonSettings assembled = SettingsResolver.processInheritance(main, settings);
		
		TreasureManager treasure = new TreasureManager();
		MockChest chest = new MockChest(Treasure.REWARD, 0);
		treasure.add(chest);
		
		assertTrue(chest.count(stick) == 0);
		
		assembled.lootRules.process(new Random(), treasure);
		
		assertTrue(chest.count(stick) == 1);
		
		
	}
	
	@Test
	public void ResolveInheritTwoLevel() throws Exception{
		
		DungeonSettings main = new DungeonSettings();
		DungeonSettings child = new DungeonSettings();
		DungeonSettings grandchild = new DungeonSettings();
		
		SettingsContainer settings = new SettingsContainer();
		
		main.id = new SettingIdentifier("main");
		child.id = new SettingIdentifier("child");
		grandchild.id = new SettingIdentifier("grandchild");
		settings.put(main);
		settings.put(child);
		settings.put(grandchild);
		
		main.inherit.add(child.id);
		child.inherit.add(grandchild.id);
		
		ItemStack stick = new ItemStack(Items.STICK);
		ItemStack diamond = new ItemStack(Items.DIAMOND);
		
		child.lootRules.add(Treasure.REWARD, new WeightedChoice<ItemStack>(stick , 1), 0, true, 1);
		grandchild.lootRules.add(Treasure.REWARD, new WeightedChoice<ItemStack>(diamond , 1), 0, true, 1);
		
		DungeonSettings assembled = SettingsResolver.processInheritance(main, settings);
		
		TreasureManager treasure = new TreasureManager();
		MockChest chest = new MockChest(Treasure.REWARD, 0);
		treasure.add(chest);
		
		assertTrue(chest.count(stick) == 0);
		assertTrue(chest.count(diamond) == 0);
		
		assembled.lootRules.process(new Random(), treasure);
		
		assertTrue(chest.count(stick) == 1);
		assertTrue(chest.count(new ItemStack(Items.BOAT)) == 0);
		assertTrue(chest.count(diamond) == 1);

	}

	@Test
	public void ResolveInheritTwoLevelMultiple() throws Exception{
		DungeonSettings main = new DungeonSettings();
		DungeonSettings child = new DungeonSettings();
		DungeonSettings sibling = new DungeonSettings();
		DungeonSettings grandchild = new DungeonSettings();
		
		SettingsContainer settings = new SettingsContainer();
		
		main.id = new SettingIdentifier("main");
		child.id = new SettingIdentifier("child");
		sibling.id = new SettingIdentifier("sibling");
		grandchild.id = new SettingIdentifier("grandchild");
		settings.put(main);
		settings.put(child);
		settings.put(sibling);
		settings.put(grandchild);
		
		main.inherit.add(child.id);
		main.inherit.add(sibling.id);
		child.inherit.add(grandchild.id);
		
		ItemStack stick = new ItemStack(Items.STICK);
		ItemStack coal = new ItemStack(Items.COAL);
		ItemStack diamond = new ItemStack(Items.DIAMOND);
		
		child.lootRules.add(Treasure.REWARD, new WeightedChoice<ItemStack>(stick , 1), 0, true, 1);
		sibling.lootRules.add(Treasure.REWARD, new WeightedChoice<ItemStack>(coal , 1), 0, true, 1);
		grandchild.lootRules.add(Treasure.REWARD, new WeightedChoice<ItemStack>(diamond , 1), 0, true, 1);
		
		DungeonSettings assembled = SettingsResolver.processInheritance(main, settings);
		
		TreasureManager treasure = new TreasureManager();
		MockChest chest = new MockChest(Treasure.REWARD, 0);
		treasure.add(chest);
		
		assertTrue(chest.count(stick) == 0);
		assertTrue(chest.count(coal) == 0);
		assertTrue(chest.count(diamond) == 0);
		
		assembled.lootRules.process(new Random(), treasure);
		
		assertTrue(chest.count(new ItemStack(Items.BOAT)) == 0);
		assertTrue(chest.count(coal) == 1);
		assertTrue(chest.count(stick) == 1);
		assertTrue(chest.count(diamond) == 1);
		
	}
	
	private class MockChest implements ITreasureChest{
		
		Treasure type;
		int level;
		Map<Integer, ItemStack> loot;
		
		public MockChest(Treasure type, int level){
			this.type = type;
			this.level = level;
			loot = new HashMap<Integer, ItemStack>();
		}
		
		@Override
		public ITreasureChest generate(IWorldEditor editor, Random rand, Coord pos, int level, boolean trapped) {
			return this;
		}

		@Override
		public boolean setSlot(int slot, ItemStack item) {
			this.loot.put(slot, item);
			return true;
		}

		@Override
		public boolean setRandomEmptySlot(ItemStack item) {
			for(int i = 0; i < this.getSize(); ++i){
				if(!this.isEmptySlot(i)) continue;
				this.setSlot(i, item);
				return true;
			}
			
			return false;
		}

		@Override
		public boolean isEmptySlot(int slot) {
			return !this.loot.containsKey(slot);
		}

		@Override
		public Treasure getType() {
			return this.type;
		}

		@Override
		public int getSize() {
			return 27;
		}

		@Override
		public int getLevel() {
			return this.level;
		}
		
		public int count(ItemStack type){
			int count = 0;
			
			for(int i = 0; i < this.getSize(); ++i){
				if(!this.loot.containsKey(i)) continue;
				ItemStack item = this.loot.get(i);
				if(item.getItem() != type.getItem()) continue;
				count += item.getCount();
			}
			
			return count;
		}
		
	}
	
}
