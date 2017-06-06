package cn.paindar.academymonster.network;

import cn.paindar.academymonster.ability.AIThunderBolt;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/3/7.
 */
public class MessageThunderBolt implements IMessage
{
    public static class Handler implements IMessageHandler<MessageThunderBolt, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageThunderBolt msg, MessageContext ctx)
        {
            EntityLivingBase ori=(EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(msg.nbt.getInteger("origin"));
            Vec3 target=Vec3.createVectorHelper(msg.nbt.getDouble("targetX"),msg.nbt.getDouble("targetY"),msg.nbt.getDouble("targetZ"));
                    List<Entity> aoes=new ArrayList<>();
            int list[]=msg.nbt.getIntArray("list");
            for(int i:list)
            {
                aoes.add( Minecraft.getMinecraft().theWorld.getEntityByID(i));
            }
            AIThunderBolt.spawnEffect(ori,target,aoes);
            return null;
        }
    }

    NBTTagCompound nbt=new NBTTagCompound();

    public MessageThunderBolt(){}

    public MessageThunderBolt(EntityLivingBase ori, Vec3 target, List<Entity> aoes)
    {
        int[] list=new int[aoes.size()];
        for(int i=0;i<aoes.size();i++)
        {
            list[i]=aoes.get(i).getEntityId();
        }
        nbt.setInteger("origin",ori.getEntityId());
        nbt.setDouble("targetX",target.xCoord);
        nbt.setDouble("targetY",target.yCoord);
        nbt.setDouble("targetZ",target.zCoord);
        nbt.setIntArray("list",list);
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
