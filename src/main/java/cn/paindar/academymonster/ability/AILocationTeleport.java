package cn.paindar.academymonster.ability;

import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by voidcl on 2017/3/20.
 */
public class AILocationTeleport extends BaseSkill {

    private int dropHeight;
    private int InGround=2;

    public AILocationTeleport(EntityLivingBase speller,float exp)
    {
        super(speller,(int)lerpf(120,60,exp),exp,"teleporter.location_teleport");
        dropHeight=(int)lerpf(7,20,exp);
    }

    private boolean hasPlace(double x,double y,double z)
    {
        World world=speller.worldObj;
        Block b1=world.getBlock((int)x,(int)y,(int)z);
        Block b2=world.getBlock((int)x,(int)y+1,(int)z);
        return !b1.canCollideCheck(world.getBlockMetadata((int)x, (int)y, (int)z), false) && !b2.canCollideCheck(world.getBlockMetadata((int)x, (int)y+1, (int)z), false);

    }

    private boolean SkyOrGround(EntityLivingBase target)
    {
        return hasPlace(target.posX,target.posY+10,target.posZ);
    }

    public void spell(EntityLivingBase target)
    {
        System.out.println("dddddd");
        int rand=RandUtils.nextInt(1)-RandUtils.nextInt(1);
        if(target!=null&&!isSkillInCooldown())
        {
            if(SkyOrGround(target))
            {
                speller.setPositionAndUpdate(target.posX,target.posY+dropHeight,target.posZ);
                target.setPositionAndUpdate(target.posX+rand,target.posY+dropHeight,target.posZ+rand);
            }else
            {
                speller.setPositionAndUpdate(target.posX,target.posY-InGround,target.posZ);
                target.setPositionAndUpdate(target.posX+rand,target.posY-InGround,target.posZ+rand);
            }
            if(!speller.worldObj.isRemote)
            {
                List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
                for(Entity e:list)
                {

                    NetworkManager.sendSoundTo("tp.tp",target,.5f,(EntityPlayerMP)e);
                }
            }
        }else
        {
            return ;
        }
        super.spell();
    }

}
