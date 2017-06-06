package cn.paindar.academymonster.items;

import cn.paindar.academymonster.core.AcademyMonster;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

/**
 * Created by Paindar on 2017/6/5.
 */
public class ItemLoader
{
    public static Item railgunCore = new ItemRailgunCore();
    public static void registerItems()
    {
        //registerItem(railgunCore,"railgun_core");

    }

    public static void registerItem(Item item,String name)
    {
        GameRegistry.registerItem(item, name);
        item.setTextureName(GameData.getItemRegistry().getNameForObject(item));
        AcademyMonster.log.info(GameData.getItemRegistry().getNameForObject(item));
    }

}
