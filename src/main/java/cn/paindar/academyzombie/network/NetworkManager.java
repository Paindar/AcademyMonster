package cn.paindar.academyzombie.network;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by Paindar on 2017/2/9.
 */
public class NetworkManager
{

    public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel("AcademyZombie");
    private static int nextID = 0;
    public static void init(FMLPreInitializationEvent event)
    {
        registerMessage(MessageSound.Handler.class, MessageSound.class, Side.CLIENT);
    }


    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
            Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
    {
        instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
    }

    public static void sendTo(String sound, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageSound msg = new MessageSound(sound);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }
}
