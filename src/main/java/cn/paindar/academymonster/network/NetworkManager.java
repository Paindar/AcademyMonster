package cn.paindar.academymonster.network;

import cn.paindar.academymonster.core.AcademyMonster;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * Created by Paindar on 2017/2/9.
 */
public class NetworkManager
{
    private static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(AcademyMonster.MODID);
    private static int nextID = 0;
    public static void init(FMLPreInitializationEvent event)
    {
        registerMessage(MessageSound.Handler.class, MessageSound.class, Side.CLIENT);
        registerMessage(MessageMdRayEffect.Handler.class, MessageMdRayEffect.class, Side.CLIENT);
        registerMessage(MessageFleshRippingEffect.Handler.class, MessageFleshRippingEffect.class, Side.CLIENT);
        registerMessage(MessageRailgunEffect.Handler.class, MessageRailgunEffect.class, Side.CLIENT);
        registerMessage(MessageSkillInfoSync.Handler.class, MessageSkillInfoSync.class,Side.CLIENT);
        registerMessage(MessageArcGenEffect.Handler.class, MessageArcGenEffect.class,Side.CLIENT);
        registerMessage(MessageThunderBolt.Handler.class, MessageThunderBolt.class,Side.CLIENT);
    }


    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
            Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
    {
        instance.registerMessage(messageHandler, requestMessageType, nextID++, side);
    }

    public static void sendSoundTo(String sound,EntityLivingBase source,float vol, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageSound msg = new MessageSound(sound,source,vol);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }

    public static void sendSoundTo(String sound,double x,double y,double z,float vol,float pitch, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageSound msg = new MessageSound(sound,x,y,z,vol,pitch);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }

    public static void sendMdRayEffectTo(Vec3 str,Vec3 end, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageMdRayEffect msg = new MessageMdRayEffect(str,end);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }

    public static void sendFleshRippingEffectTo(EntityLivingBase target, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageFleshRippingEffect msg = new MessageFleshRippingEffect(target);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }

    public static void sendRailgunEffectTo(EntityLivingBase target,int dist, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageRailgunEffect msg = new MessageRailgunEffect(target,dist);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }

    public static void sendEntitySkillInfoTo(EntityLivingBase entity, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageSkillInfoSync msg = new MessageSkillInfoSync(entity);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }

    public static void sendArcGenTo(EntityLivingBase speller,float range, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageArcGenEffect msg = new MessageArcGenEffect(speller,range);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }

    public static void sendThunderBoltTo(EntityLivingBase ori,EntityLivingBase target,List<Entity> list, EntityPlayerMP player)
    {
        if(!player.getEntityWorld().isRemote)
        {
            MessageThunderBolt msg = new MessageThunderBolt(ori,target,list);
            instance.sendTo(msg, player);
        }
        else
            throw new IllegalStateException("Wrong context side!");
    }
}
