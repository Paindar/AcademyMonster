package cn.paindar.academymonster.network;

import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;


/**
 * Created by Paindar on 2017/2/15.
 */
public class MessageSkillInfoSync implements IMessage
{
    public static class Handler implements IMessageHandler<MessageSkillInfoSync, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageSkillInfoSync msg, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                EntityLiving entity= (EntityLiving) Minecraft.getMinecraft().theWorld.getEntityByID(msg.nbt.getInteger("id"));
                SkillExtendedEntityProperties info= SkillExtendedEntityProperties.get(entity);
                info.setSkillData(msg.nbt.getString("list"));
            }
            return null;
        }
    }

    NBTTagCompound nbt;

    public MessageSkillInfoSync(){}

    public MessageSkillInfoSync(EntityLiving entity)
    {
        nbt=new NBTTagCompound();
        nbt.setString("list", SkillExtendedEntityProperties.get(entity).getSkillData());
        nbt.setInteger("id",entity.getEntityId());
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
