package cn.paindar.academymonster.core;

import cn.paindar.academymonster.core.command.CommandTest;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Created by Paindar on 2017/2/9.
 */

@Mod(modid =AcademyMonster.MODID , name = AcademyMonster.NAME, version = AcademyMonster.VERSION,
        dependencies = "required-after:academy-craft@@AC_VERSION@") // LambdaLib is currently unstable. Supports only one version.
public class AcademyMonster
{
    public static final String MODID = "academy-monster";
    public static final String NAME = "Academy Monster";
    public static final String VERSION = "@VERSION@";
    public static final Logger log = LogManager.getLogger("AcademyMonster");
    @SidedProxy(clientSide = "cn.paindar.academymonster.core.ClientProxy",
            serverSide = "cn.paindar.academymonster.core.CommonProxy")
    private static CommonProxy proxy;
    @Instance
    public static AcademyMonster instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandTest());
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
    }







}
