package cn.paindar.academymonster.network;

import cn.paindar.academymonster.entity.EntityMDRayNative;
import cn.paindar.academymonster.entity.EntityRailgunFXNative;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Paindar on 2017/3/13.
 */
public class MessageMeltdownerEffect implements IMessage
{
    public static class Handler implements IMessageHandler<MessageMeltdownerEffect, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageMeltdownerEffect message, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                EntityLivingBase player= (EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(message.nbt.getInteger("i"));
                int dist=message.nbt.getInteger("dst");
                player.worldObj.spawnEntityInWorld(new EntityMDRayNative(player, dist));
            }
            return null;
        }
    }

    NBTTagCompound nbt;
    public MessageMeltdownerEffect(){}

    public MessageMeltdownerEffect(EntityLivingBase speller,int dist)
    {
        nbt=new NBTTagCompound();
        nbt.setInteger("i",speller.getEntityId());
        nbt.setInteger("dst",dist);
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf)
    {
        nbt= ByteBufUtils.readTag(buf);
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, nbt);
    }
}
