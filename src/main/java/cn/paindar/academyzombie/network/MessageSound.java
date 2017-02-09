package cn.paindar.academyzombie.network;

import cn.academy.core.client.sound.ACSounds;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.io.UnsupportedEncodingException;

/**
 * Created by Paindar on 2017/2/9.
 */
public class MessageSound  implements IMessage
{
    public static class Handler implements IMessageHandler<MessageSound, IMessage>
    {
        @Override
        public IMessage onMessage(MessageSound message, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                ACSounds.playClient(Minecraft.getMinecraft().thePlayer, "tp.tp", .5f);
            }
            return null;
        }
    }
    String msg;

    public MessageSound()
    {

    }

   public MessageSound(String sound)
    {
        msg=sound;
    }
    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        msg=byteBuf.toString();
    }


    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        try {
            byteBuf.writeBytes(msg.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

