package cn.paindar.academymonster.ability;

import cn.academy.vanilla.vecmanip.skill.DirectedShock;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

import static cn.lambdalib.util.generic.MathUtils.lerpf;

/**
 * Created by Paindar on 2017/2/12.
 */
public class AIDirectedShock extends BaseSkill
{
    private final float maxDistance=3;
    private float damage;
    public AIDirectedShock(EntityLivingBase speller, float exp)
    {
        super(speller, (int)lerpf(60, 20, exp), exp,DirectedShock.getFullName());
        damage=lerpf(7, 15,exp);
    }

    private void knockback(Entity targ)
    {
        if (getSkillExp() >= 0.25f)
        {
            Vec3 slope=speller.getLookVec();
            targ.setPosition(targ.posX, targ.posY + 0.1, targ.posZ);
            if(targ.worldObj.isRemote)targ.setVelocity(slope.xCoord*3f,slope.yCoord*3f,slope.zCoord*3f);
        }
    }


    public void spell()
    {
        MovingObjectPosition result = Raytrace.traceLiving(speller, 3, EntitySelectors.living());
        if(result!=null && result.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY)
        {

            if(result.entityHit instanceof EntityLivingBase)
            {
                EntityLivingBase entity=(EntityLivingBase)result.entityHit;
                attack((EntityLivingBase) result.entityHit, damage);
                knockback(entity);
                List<Entity> list= WorldUtils.getEntities(speller, 25, EntitySelectors.player());
                for(Entity e:list)
                {
                    NetworkManager.sendSoundTo("vecmanip.directed_shock",speller,0.5f,(EntityPlayerMP)e);
                }
            }
        }
        super.spell();
    }

    public float getMaxDistance()
    {
        return maxDistance;
    }

}
