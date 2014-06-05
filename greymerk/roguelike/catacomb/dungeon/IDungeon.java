package greymerk.roguelike.catacomb.dungeon;

import greymerk.roguelike.catacomb.theme.ITheme;

import java.util.Random;

import net.minecraft.src.World;

public interface IDungeon {

	public boolean generate(World world, Random rand, ITheme theme, int x, int y, int z);
		
	public int getSize();
	
}