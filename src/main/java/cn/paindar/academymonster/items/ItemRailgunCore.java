package cn.paindar.academymonster.items;

import cn.paindar.academymonster.ability.AILocManip;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/6/5.
 */
public class ItemRailgunCore extends Item
{
    public ItemRailgunCore()
    {
        super();
        this.setUnlocalizedName("railgunCore");
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        AILocManip ai=new AILocManip(player,1);
        ai.spell();
        return itemStack;
    }
}
