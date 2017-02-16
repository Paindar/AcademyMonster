package cn.paindar.academymonster.network;

import cn.academy.core.client.ACRenderingHelper;
import cn.academy.core.client.sound.ACSounds;
import cn.academy.vanilla.generic.entity.EntityBloodSplash;
import cn.lambdalib.util.generic.RandUtils;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Paindar on 2017/2/11.
 */
public class MessageFleshRippingEffect implements IMessage
{
    public static class Handler implements IMessageHandler<MessageFleshRippingEffect, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageFleshRippingEffect message, MessageContext ctx)
        {
            EntityLivingBase target = (EntityLivingBase)Minecraft.getMinecraft().theWorld.getEntityByID(message.nbt.getInteger("i"));

            if (ctx.side == Side.CLIENT && target!=null)
            {
                ACSounds.playClient(target, "tp.guts", 0.6f);
                for(int i=0;i< RandUtils.rangei(4, 6);i++)
                {
                    Double y = target.posY + RandUtils.ranged(0, 1) * target.height;
                    if(target instanceof EntityPlayer)
                        y += ACRenderingHelper.getHeightFix((EntityPlayer)target);

                    Double theta = RandUtils.ranged(0, Math.PI * 2);
                    Double r  = 0.5 * RandUtils.ranged(0.8 * target.width, target.width);
                    EntityBloodSplash splash = new EntityBloodSplash(target.worldObj);
                    splash.setPosition(target.posX + r * Math.sin(theta), y, target.posZ + r * Math.cos(theta));
                    target.worldObj.spawnEntityInWorld(splash);
                }
            }
            return null;
        }
    }
    NBTTagCompound nbt;

    public MessageFleshRippingEffect(){}

    public MessageFleshRippingEffect(EntityLivingBase target)
    {
        nbt=new NBTTagCompound();
        nbt.setInteger("i",target.getEntityId());
    }

    /**
     * Convert from the supplied buffer into your specific message type
     *
     * @param buf
     */
    @Override
    public void fromBytes(ByteBuf buf)
    {
        nbt=ByteBufUtils.readTag(buf);
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
