package cn.paindar.academymonster.network;

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
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/2/12.
 */
public class MessageRailgunEffect implements IMessage
{
    public static class Handler implements IMessageHandler<MessageRailgunEffect, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageRailgunEffect message, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                EntityLivingBase player= (EntityLivingBase)Minecraft.getMinecraft().theWorld.getEntityByID(message.nbt.getInteger("id"));

                Vec3 str=Vec3.createVectorHelper(message.nbt.getDouble("str_x"),
                        message.nbt.getDouble("str_y"),
                        message.nbt.getDouble("str_z"));
                Vec3 end=Vec3.createVectorHelper(message.nbt.getDouble("end_x"),
                        message.nbt.getDouble("end_y"),
                        message.nbt.getDouble("end_z"));
                player.worldObj.spawnEntityInWorld(new EntityRailgunFXNative(player, str, end));
            }
            return null;
        }
    }

    NBTTagCompound nbt;

    public MessageRailgunEffect(){}

    public MessageRailgunEffect(EntityLivingBase speller, Vec3 str, Vec3 end)
    {
        nbt=new NBTTagCompound();
        nbt.setInteger("id", speller.getEntityId());
        nbt.setDouble("str_x", str.xCoord);
        nbt.setDouble("str_y", str.yCoord);
        nbt.setDouble("str_z", str.zCoord);
        nbt.setDouble("end_x", end.xCoord);
        nbt.setDouble("end_y", end.yCoord);
        nbt.setDouble("end_z", end.zCoord);
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
