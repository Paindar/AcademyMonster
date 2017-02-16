package cn.paindar.academymonster.network;

import cn.academy.vanilla.meltdowner.entity.EntityMdRaySmall;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/2/10.
 */
public class MessageMdRayEffect implements IMessage
{
    public static class Handler implements IMessageHandler<MessageMdRayEffect, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageMdRayEffect message, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                EntityPlayer player=Minecraft.getMinecraft().thePlayer;
                EntityMdRaySmall raySmall  = new EntityMdRaySmall(player.worldObj);
                raySmall.setFromTo(message.strX, message.strY,message.strZ, message.endX, message.endY, message.endZ);
                raySmall.viewOptimize = false;
                player.worldObj.spawnEntityInWorld(raySmall);
            }
            return null;
        }
    }
    public double strX,strY,strZ;
    public double endX,endY,endZ;

    public MessageMdRayEffect(){}

    public MessageMdRayEffect(Vec3 str,Vec3 end) {
        strX=str.xCoord;
        strY=str.yCoord;
        strZ=str.zCoord;
        endX=end.xCoord;
        endY=end.yCoord;
        endZ=end.zCoord;
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        strX=buf.readDouble();
        strY=buf.readDouble();
        strZ=buf.readDouble();
        endX=buf.readDouble();
        endY=buf.readDouble();
        endZ=buf.readDouble();
    }

    /**
     * Deconstruct your message into the supplied byte buffer
     *
     * @param buf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(strX);
        buf.writeDouble(strY);
        buf.writeDouble(strZ);
        buf.writeDouble(endX);
        buf.writeDouble(endY);
        buf.writeDouble(endZ);
    }
}
