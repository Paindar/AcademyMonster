package cn.paindar.academymonster.network;

import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.entity.EntityMagManipBlock;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Paindar on 2017/6/5.
 */
public class MessageMagManipBlockSync implements IMessage
{
    public static class Handler implements IMessageHandler<MessageMagManipBlockSync, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageMagManipBlockSync msg, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                EntityMagManipBlock entity= (EntityMagManipBlock) Minecraft.getMinecraft().theWorld.getEntityByID(msg.nbt.getInteger("id"));
                entity.setPlaceWhenCollide(msg.nbt.getBoolean("value"));
            }
            return null;
        }
    }
    NBTTagCompound nbt;
    public MessageMagManipBlockSync(){}
    public MessageMagManipBlockSync(EntityMagManipBlock entity,boolean value)
    {
        nbt=new NBTTagCompound();
        nbt.setBoolean("value",value);
        nbt.setInteger("id",entity.getEntityId());
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
