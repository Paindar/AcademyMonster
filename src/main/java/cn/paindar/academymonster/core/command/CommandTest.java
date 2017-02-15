package cn.paindar.academymonster.core.command;

import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by Paindar on 2017/2/13.
 */
public class CommandTest extends CommandBase
{
    @Override
    public String getCommandName()
    {

        return "amtest";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "commands.position.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
//        if (args.length > 1)
//        {
//            throw new WrongUsageException("commands.position.usage");
//        }
//        else
//        {
//
//        }
    }
}
