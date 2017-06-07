package cn.paindar.academymonster.items;

import cn.paindar.academymonster.ability.AIPlasmaCannon;
import cn.paindar.academymonster.ability.AIVecReflect;
import cn.paindar.academymonster.entity.EntityPlasmaBodyEffect;
import cn.paindar.academymonster.entity.EntityTornadoEffect;
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
        this.setUnlocalizedName("railgun_core");
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        AIPlasmaCannon skill=new AIPlasmaCannon(player,1);
        skill.spell();
        return itemStack;
    }
}
