package cn.paindar.academymonster.core.support.terminal;

import cn.academy.terminal.App;
import cn.academy.terminal.AppEnvironment;
import cn.academy.terminal.AppRegistry;
import cn.academy.terminal.registry.AppRegistration.RegApp;
import cn.paindar.academymonster.core.support.terminal.ui.AIMScannerUI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

/**
 * Created by Paindar on 2017/2/13.
 */
public class AppAIMScanner extends App
{

    public static AppAIMScanner instance = new AppAIMScanner();
    /**
     * Register in preInit stage at  CommonProxy
     */


    private AppAIMScanner()
    {
        super("AIM_Scanner");
    }

    @Override
    public AppEnvironment createEnvironment()
    {
        return  new AppEnvironment()
        {
            @Override
            @SideOnly(Side.CLIENT)
            public void onStart()
            {
                AIMScannerUI.keyHandler.onKeyUp();
            }
        };
    }
}
