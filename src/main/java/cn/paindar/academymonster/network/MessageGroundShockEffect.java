package cn.paindar.academymonster.network;

import cn.academy.core.client.sound.ACSounds;
import cn.academy.vanilla.generic.client.effect.SmokeEffect;
import cn.academy.vanilla.vecmanip.skill.IVec;
import cn.lambdalib.util.mc.RichEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import static cn.lambdalib.util.generic.RandUtils.*;

/**
 * Created by Paindar on 2017/3/21.
 */
public class MessageGroundShockEffect implements IMessage
{
    public static class Handler implements IMessageHandler<MessageGroundShockEffect, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageGroundShockEffect msg, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                int[] bytes=msg.nbt.getIntArray("vecs");
                IVec[] vecs=new IVec[bytes.length/3];
                World world=Minecraft.getMinecraft().theWorld;
                ACSounds.playClient(Minecraft.getMinecraft().thePlayer, "vecmanip.groundshock", 2);
                for(int i=0;i<vecs.length;i++)
                {
                    vecs[i]=IVec.apply(bytes[i*3],bytes[i*3+1],bytes[i*3+2]);
                }
                for(IVec pt:vecs)
                    {
                    for (int i=0;i<rangei(4, 8);i++)
                    {
                        double randvel = ranged(-0.2, 0.2);
                        EntityDiggingFX entity = new EntityDiggingFX(
                                world,
                                pt.x() + nextDouble(), pt.y() + 1 + nextDouble() * 0.5 + 0.2, pt.z() + nextDouble(),
                                randvel, 0.1 + nextDouble() * 0.2, randvel,
                                world.getBlock(pt.x(), pt.y(), pt.z()),
                                ForgeDirection.UP.ordinal());

                        Minecraft.getMinecraft().effectRenderer.addEffect(entity);
                    }

                    if (nextFloat() < 0.5f)
                    {
                        SmokeEffect eff = new SmokeEffect(world);
                        Vec3 pos = Vec3.createVectorHelper(pt.x() + 0.5 + ranged(-.3, .3), pt.y() + 1 + ranged(0, 0.2), pt.z() + 0.5 + ranged(-.3, .3));
                        Vec3 vel = Vec3.createVectorHelper(ranged(-.03, .03), ranged(.03, .06), ranged(-.03, .03));
                        RichEntity richEntity=new RichEntity(eff);
                        richEntity.setPos(pos);
                        richEntity.setVel(vel);
                        world.spawnEntityInWorld(eff);
                    }
               }

            }
            return null;
        }
    }
    NBTTagCompound nbt;

    public MessageGroundShockEffect(){}
    MessageGroundShockEffect(IVec[] vecs)
    {
        int[] bytes=new int[vecs.length*3];
        nbt=new NBTTagCompound();
        for(int i=0;i<vecs.length;i++)
        {
            bytes[3*i]=vecs[i].x();
            bytes[3*i+1]=vecs[i].y();
            bytes[3*i+2]=vecs[i].z();
        }
        nbt.setIntArray("vecs",bytes);
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
