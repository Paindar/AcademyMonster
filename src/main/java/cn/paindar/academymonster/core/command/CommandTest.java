package cn.paindar.academymonster.core.command;

import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

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
//        EntityPlayer player = (EntityPlayer) sender;
//        List<Entity> list = WorldUtils.getEntities(player,40, EntitySelectors.everything());
//        for(Entity e:list){
//            if(e instanceof EntityLivingBase)
//            {
//                AcademyMonster.log.info(String.format("EntityName=%s, health=%f/%f, have skill: %s",
//                        e.getCommandSenderName(),((EntityLivingBase)e).getHealth(),((EntityLivingBase) e).getMaxHealth(),
//                        SkillExtendedEntityProperties.get(e).getSkillData()));
//            }
//        }
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
