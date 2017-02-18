package cn.paindar.academymonster.network;


import cn.academy.core.client.sound.ACSounds;
import cn.academy.vanilla.electromaster.client.effect.ArcPatterns;
import cn.lambdalib.util.entityx.handlers.Life;
import cn.paindar.academymonster.entity.EntityArcNative;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/2/17.
 */
public class MessageArcGenEffect implements IMessage
{
    public static class Handler implements IMessageHandler<MessageArcGenEffect, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageArcGenEffect msg, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                World world=Minecraft.getMinecraft().theWorld;
                EntityLivingBase target=(EntityLivingBase) world.getEntityByID(msg.nbt.getInteger("id"));
                EntityArcNative arc = new EntityArcNative(target, ArcPatterns.weakArc);
                arc.texWiggle = 0.7;
                arc.showWiggle = 0.1;
                arc.hideWiggle = 0.4;
                arc.addMotionHandler(new Life(10));
                arc.lengthFixed = false;
                arc.length = msg.nbt.getFloat("range");

                world.spawnEntityInWorld(arc);
                ACSounds.playClient(target, "em.arc_weak", 0.5f);
            }
            return null;
        }
    }
    NBTTagCompound nbt;
    public MessageArcGenEffect(){}

    public MessageArcGenEffect(EntityLivingBase speller, float range)
    {
        nbt=new NBTTagCompound();
        nbt.setInteger("id",speller.getEntityId());
        nbt.setFloat("range",range);
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
