package greymerk.roguelike.dungeon.settings.builtin;

import java.util.ArrayList;

import greymerk.roguelike.dungeon.settings.DungeonSettings;
import greymerk.roguelike.dungeon.settings.SettingIdentifier;
import greymerk.roguelike.dungeon.settings.SettingsContainer;

public class SettingsBase extends DungeonSettings{
	
	public static final SettingIdentifier ID = new SettingIdentifier(SettingsContainer.BUILTIN_NAMESPACE, "base");
	
	public SettingsBase(){
		
		this.id = ID;
		this.inherit = new ArrayList<SettingIdentifier>();
		this.inherit.add(SettingsRooms.ID);
		this.inherit.add(SettingsSecrets.ID);
		this.inherit.add(SettingsSegments.ID);
		this.inherit.add(SettingsSize.ID);
		this.inherit.add(SettingsTheme.ID);
		this.inherit.add(SettingsGenerator.ID);
		this.inherit.add(SettingsLootRules.ID);
		
	}
}
