package cn.paindar.academymonster.core;

import cn.paindar.academymonster.core.support.terminal.ui.AIMScannerUI;
import cn.paindar.academymonster.entity.EntityLoader;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;


/**
 * Created by Paindar on 2017/2/13.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        EntityLoader.registerRenders();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        //ClientRegistry.registerKeyBinding(showTime);
        super.init(event);
        AIMScannerUI.__init();

    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }
}
