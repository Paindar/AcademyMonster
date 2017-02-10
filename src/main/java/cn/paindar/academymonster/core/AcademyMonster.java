package cn.paindar.academymonster.core;

import cn.paindar.academymonster.entity.EntityLoader;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by Paindar on 2017/2/9.
 */

@Mod(modid =AcademyMonster.MODID , name = AcademyMonster.NAME, version = AcademyMonster.VERSION,
        dependencies = "required-after:LambdaLib@@LL_VERSION@") // LambdaLib is currently unstable. Supports only one version.
public class AcademyMonster
{
    public static final String MODID = "academy-zombie";
    public static final String NAME = "Academy Monster";
    public static final String VERSION = "@VERSION@";
    public static final Logger log = LogManager.getLogger("AcademyMonster");
    public static Configuration config;
    @Instance
    public static AcademyMonster instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        new EntityLoader();
        NetworkManager.init(event);
        config=new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        config.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
    }


}
