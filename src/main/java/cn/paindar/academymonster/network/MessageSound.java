package cn.paindar.academymonster.network;

import cn.academy.core.client.sound.ACSounds;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

import java.io.UnsupportedEncodingException;

/**
 * Created by Paindar on 2017/2/9.
 */
public class MessageSound  implements IMessage
{
    public static class Handler implements IMessageHandler<MessageSound, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageSound msg, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                if(msg.nbt.hasKey("target"))
                    ACSounds.playClient(Minecraft.getMinecraft().theWorld.getEntityByID(msg.nbt.getInteger("target"))
                            ,msg.nbt.getString("sound")
                            ,msg.nbt.getFloat("vol"));
                else
                    ACSounds.playClient(Minecraft.getMinecraft().theWorld
                            ,msg.nbt.getDouble("x")
                            ,msg.nbt.getDouble("y")
                            ,msg.nbt.getDouble("z")
                            ,msg.nbt.getString("sound")
                            ,msg.nbt.getFloat("vol")
                            ,msg.nbt.getFloat("pitch"));
            }
            return null;
        }
    }
    NBTTagCompound nbt;

    public MessageSound()
    {

    }

    public MessageSound(String sound,double posX,double posY,double posZ,float vol,float pitch)
    {
        nbt=new NBTTagCompound();
        nbt.setString("sound",sound);
        nbt.setDouble("x",posX);
        nbt.setDouble("y",posY);
        nbt.setDouble("z",posZ);
        nbt.setFloat("vol",vol);
        nbt.setFloat("pitch",pitch);
    }

   public MessageSound(String sound, EntityLivingBase target,float vol)
    {
        nbt=new NBTTagCompound();
        nbt.setString("sound",sound);
        nbt.setInteger("target",target.getEntityId());
        nbt.setFloat("vol",vol);
    }
    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        nbt= ByteBufUtils.readTag(byteBuf);
    }


    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        ByteBufUtils.writeTag(byteBuf, nbt);
    }
}

