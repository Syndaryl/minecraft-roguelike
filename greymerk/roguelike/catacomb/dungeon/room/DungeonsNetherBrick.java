package greymerk.roguelike.catacomb.dungeon.room;

import greymerk.roguelike.catacomb.Catacomb;
import greymerk.roguelike.catacomb.dungeon.IDungeon;
import greymerk.roguelike.catacomb.theme.ITheme;
import greymerk.roguelike.config.RogueConfig;
import greymerk.roguelike.treasure.TreasureChest;
import greymerk.roguelike.worldgen.BlockRandomizer;
import greymerk.roguelike.worldgen.IBlockFactory;
import greymerk.roguelike.worldgen.MetaBlock;
import greymerk.roguelike.worldgen.Spawner;
import greymerk.roguelike.worldgen.WorldGenPrimitive;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class DungeonsNetherBrick implements IDungeon {
	

	public DungeonsNetherBrick() {
	}

	public boolean generate(World world, Random rand, ITheme theme, int x, int y, int z) {
		
		int height = 3;
		int length = 2 + rand.nextInt(3);
		int width = 2 + rand.nextInt(3);
		
		IBlockFactory walls = theme.getPrimaryWall();
		WorldGenPrimitive.fillRectHollow(world, rand, x - length - 1, y - 1, z - width - 1, x + length + 1, y + height + 1, z + width + 1, walls, false, true);
		
		BlockRandomizer floor = new BlockRandomizer(new MetaBlock(Block.netherBrick.blockID));
		floor.addBlock(new MetaBlock(Block.netherrack.blockID), 3);
		floor.addBlock(new MetaBlock(Block.oreNetherQuartz.blockID), 5);
		floor.addBlock(new MetaBlock(Block.blockRedstone.blockID), 10);
		if (RogueConfig.getBoolean(RogueConfig.PRECIOUSBLOCKS)) floor.addBlock(new MetaBlock(Block.blockGold.blockID), 500);
		if (RogueConfig.getBoolean(RogueConfig.PRECIOUSBLOCKS)) floor.addBlock(new MetaBlock(Block.blockDiamond.blockID), 1000);
		WorldGenPrimitive.fillRectSolid(world, rand, x - length - 1, y - 1, z - width - 1, x + length + 1, y - 1, z + width + 1, floor);

		// lava crap under the floor
		BlockRandomizer subFloor = new BlockRandomizer(new MetaBlock(Block.lavaStill.blockID));
		subFloor.addBlock(new MetaBlock(Block.obsidian.blockID), 3);
		WorldGenPrimitive.fillRectSolid(world, rand, x - length, y - 5, z - width, x + length, y - 2, z + width, subFloor);
		
		BlockRandomizer ceiling = new BlockRandomizer(new MetaBlock(Block.netherFence.blockID));
		ceiling.addBlock(new MetaBlock(0), 2);
		WorldGenPrimitive.fillRectSolid(world, rand, x - length, y + height, z - width, x + length, y + height, z + width, ceiling);
		
		TreasureChest.createChests(world, rand, 1, WorldGenPrimitive.getRectSolid(x - length, y, z - width, x + length, y, z + width));

		Spawner.generate(world, rand, x - length - 1, y + rand.nextInt(2), z - width - 1);
		Spawner.generate(world, rand, x - length - 1, y + rand.nextInt(2), z + width + 1);
		Spawner.generate(world, rand, x + length + 1, y + rand.nextInt(2), z - width - 1);
		Spawner.generate(world, rand, x + length + 1, y + rand.nextInt(2), z + width + 1);

		return true;
	}
	
	public int getSize(){
		return 6;
	}
}