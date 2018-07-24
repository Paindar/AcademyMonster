package cn.paindar.academymonster.config;

import cn.lambdalib.util.generic.RegistryUtils;
import cn.paindar.academymonster.core.AcademyMonster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by Paindar on 2017/2/16.
 */
public class AMConfig
{
    private static Config config;
    private static Logger log= AcademyMonster.log;

    public static void init(FMLPreInitializationEvent event)
    {
        ResourceLocation defaultRes = new ResourceLocation("academymonster:config/default.cfg");

        File configFile = event.getSuggestedConfigurationFile();
        if (!configFile.exists())//check if config file exists.
        {
            try
            {
                Files.copy(RegistryUtils.getResourceStream(defaultRes), configFile.toPath());
            } catch (IOException ex)
            {
                log.error("Error when copying config template to config folder", ex);
            }
            configFile = new File(defaultRes.getResourcePath());
        }

        try
        {
            String s=new String(Files.readAllBytes(configFile.toPath()));
            config = ConfigFactory.parseString(s.toLowerCase());
        } catch (Exception exception) {
            log.error("An error occured parsing custom config", exception);
        }
    }

    public static boolean getBoolean(String path,boolean defaultValue)
    {
        path = path.toLowerCase();
        if(!config.hasPath(path))
        {
            log.debug("Cannot find path: "+path);
            return defaultValue;
        }
        else
            return config.getBoolean(path);
    }

    public static int getInt(String path,int defaultValue)
    {
        path = path.toLowerCase();
        if(!config.hasPath(path))
        {
            log.debug("Cannot find path: "+path);
            return defaultValue;
        }
        else
            return config.getInt(path);
    }

    public static double getDouble(String path,double defaultValue)
    {
        path = path.toLowerCase();
        if(!config.hasPath(path))
        {
            log.debug("Cannot find path: "+path);
            return defaultValue;
        }
        else
            return config.getDouble(path);
    }

    public static List<String> getStringArray(String path, List<String> defaultValue)
    {
        path = path.toLowerCase();
        if(!config.hasPath(path))
        {
            log.debug("Cannot find path: "+path);
            return defaultValue;
        }
        else
            return config.getStringList(path);
    }
}
