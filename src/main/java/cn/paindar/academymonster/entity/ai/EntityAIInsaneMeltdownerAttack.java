package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.entity.boss.EntityInsaneMeltdowner;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * Created by Paindar on 2017/3/16.
 */
public class EntityAIInsaneMeltdownerAttack extends EntityAIBase
{
    private final EntityInsaneMeltdowner speller;
    private EntityLivingBase target;
    private int tick=0;

    public EntityAIInsaneMeltdownerAttack(EntityInsaneMeltdowner speller)
    {
        this.speller=speller;
    }
    @Override
    public boolean shouldExecute()
    {
        if(speller.getAttackTarget()!=null)
        {
            target=speller.getAttackTarget();
            return true;
        }
        return false;
    }

    @Override
    public void updateTask()
    {
        List<BaseSkill> list=speller.skillList;
        tick=(tick++)%10;
        if(tick>=3)
            return;
        boolean success=false;
        for(BaseSkill skill:list)
        {
            if(!skill.isSkillInCooldown())
            {
                MovingObjectPosition result= Raytrace.rayTraceBlocks(speller.worldObj,
                        Vec3.createVectorHelper(speller.posX, speller.posY, speller.posZ),
                        Vec3.createVectorHelper(target.posX, target.posY, target.posZ), BlockSelectors.filNormal);
                if(skill instanceof AIScatterBomb)
                {
                    if(target!=null && speller.getDistanceSqToEntity(target)<= ((AIScatterBomb)skill).getMaxDistance() * ((AIScatterBomb)skill).getMaxDistance())
                    {
                        if(!skill.isChanting())
                        {
                            skill.spell();
                            success=true;
                        }
                        else
                        {
                            double range = speller.getDistanceSqToEntity(target);
                            if (range <= ((AIScatterBomb)skill).getMaxDistance() * ((AIScatterBomb)skill).getMaxDistance())
                            {
                                if(((AIScatterBomb)skill).getBallSize()>=7)
                                {
                                    skill.spell();
                                    success=true;
                                }
                            }
                        }
                    }
                }
                else if(skill instanceof AIMeltdowner)
                {
                    MovingObjectPosition trace = Raytrace.traceLiving(speller,((AIMeltdowner)skill).getMaxDistance(), EntitySelectors.living(), BlockSelectors.filNothing);
                    if (trace != null && trace.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY)
                    {
                        if(trace.entityHit==target)
                        {
                            skill.spell();
                            success=true;
                        }
                    }
                }
                else if(skill instanceof AIElectronMissile)
                {
                    AIElectronMissile missile=(AIElectronMissile)skill;
                    if(speller.getDistanceSqToEntity(target)<=missile.getMaxDistance()*missile.getMaxDistance())
                    {
                        skill.spell();
                        success=true;
                    }
                }
                else if (skill instanceof AIElectronBomb)
                {
                    if (result != null && result.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY)
                    {
                        if(result.entityHit==target)
                        {
                            skill.spell();
                            success=true;
                        }
                    }
                }
                if(success)
                    break;
            }
        }
    }
}
