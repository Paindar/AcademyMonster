package cn.paindar.academymonster.network;

import cn.paindar.academymonster.entity.EntityPlasmaBodyEffect;
import cn.paindar.academymonster.entity.EntityTornadoEffect;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Paindar on 2017/6/7.
 */
public class MessagePlasmaEffectSync implements IMessage
{
    public static class Handler implements IMessageHandler<MessagePlasmaEffectSync, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessagePlasmaEffectSync message, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Entity player=  Minecraft.getMinecraft().theWorld.getEntityByID(message.nbt.getInteger("i"));
                if(player!=null)
                    if(player instanceof EntityPlasmaBodyEffect)
                        ((EntityPlasmaBodyEffect)player).changeState();
                    else if(player instanceof EntityTornadoEffect)
                        ((EntityTornadoEffect)player).changeState();
            }
            return null;
        }
    }
    NBTTagCompound nbt;

    public MessagePlasmaEffectSync(){}

    public MessagePlasmaEffectSync(Entity speller)
    {
        nbt=new NBTTagCompound();
        nbt.setInteger("i",speller.getEntityId());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        nbt= ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, nbt);
    }
}
