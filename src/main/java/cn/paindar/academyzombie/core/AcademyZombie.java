package cn.paindar.academyzombie.core;

import cn.lambdalib.annoreg.core.Registrant;
import cn.lambdalib.annoreg.core.RegistrationManager;
import cn.lambdalib.annoreg.core.RegistrationMod;
import cn.paindar.academyzombie.entity.EntityAcademyZombie;
import cn.paindar.academyzombie.entity.EntityLoader;
import cn.paindar.academyzombie.network.NetworkManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cn.paindar.academyzombie.entity.EntityLoader.registerEntitySpawn;

/**
 * Created by Paindar on 2017/2/9.
 */

@Mod(modid = "academy-zombie", name = "AcademyZombie", version = AcademyZombie.VERSION,
        dependencies = "required-after:LambdaLib@@LL_VERSION@") // LambdaLib is currently unstable. Supports only one version.
public class AcademyZombie
{
    public static final String VERSION = "@VERSION@";
    public static final Logger log = LogManager.getLogger("AcademyZombie");
    public static Configuration config;
    @Instance
    public static AcademyZombie instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        new EntityLoader();
        NetworkManager.init(event);
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
