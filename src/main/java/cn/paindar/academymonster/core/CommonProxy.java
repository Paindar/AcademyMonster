package cn.paindar.academymonster.core;

import cn.academy.crafting.ModuleCrafting;
import cn.academy.terminal.AppRegistry;
import cn.academy.terminal.item.ItemApp;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.core.support.terminal.AppAIMScanner;
import cn.paindar.academymonster.entity.EntityLoader;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Paindar on 2017/2/13.
 */
public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        new EntityLoader();
        AMConfig.init(event);
        EntityLoader.registerEntity();
        AcademyMonster.instance.initSkill();
        NetworkManager.init(event);
        AppRegistry.register(AppAIMScanner.instance);
    }

    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new GlobalEventHandle());

    }

    public void postInit(FMLPostInitializationEvent event)
    {
        GameRegistry.addShapedRecipe(new ItemStack(ItemApp.getItemForApp(AppAIMScanner.instance)), "#* ",
                " . ",
                " ^ ",
                '#', ModuleCrafting.brainComp, '*', Blocks.redstone_block,'.',ModuleCrafting.dataChip,'^',ModuleCrafting.infoComp);
    }
}
