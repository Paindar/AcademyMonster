package cn.paindar.academymonster.network;

import cn.paindar.academymonster.core.AcademyMonster;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/2/9.
 */
public class NetworkManager
{
    public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(AcademyMonster.MODID);
    private static int nextID = 0;
    public static void init(FMLPreInitializationEvent event)
    {
        registerMessage(MessageSound.Handler.class, MessageSound.class, Side.CLIENT);
        registerMessage(MessageMdRayEffect.Handler.class, MessageMdRayEffect.class, Side.CLIENT);
        registerMessage(MessageFleshRippingEffect.Handler.class, MessageFleshRippingEffect.class, Side.CLIENT);
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

    public static void sendTo(Vec3 str,Vec3 end, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageMdRayEffect msg = new MessageMdRayEffect(str,end);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }

    public static void sendTo(EntityLivingBase target, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageFleshRippingEffect msg = new MessageFleshRippingEffect(target);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }
}
