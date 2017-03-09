package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.entity.boss.EntityFakeRaingun;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * Created by Paindar on 2017/3/8.
 */
public class EntityAIFakeRailgunAttack extends EntityAIBase
{
    private final EntityFakeRaingun speller;
    private EntityLivingBase target;
    private int tick=0;

    public EntityAIFakeRailgunAttack(EntityFakeRaingun speller)
    {
        this.speller=speller;
    }
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
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
        for(BaseSkill skill:list)
        {
            if(skill.available())
            {
                MovingObjectPosition result= Raytrace.rayTraceBlocks(speller.worldObj,
                        Vec3.createVectorHelper(speller.posX, speller.posY, speller.posZ),
                        Vec3.createVectorHelper(target.posX, target.posY, target.posZ), BlockSelectors.filNormal);
                if(skill instanceof AIThunderClap)
                {
                    if(result==null) ((AIThunderClap)skill).spell(target.posX,target.posY,target.posZ);
                }
                else if(skill instanceof AIRailgun)
                {
                    skill.spell();
                }
                else if(skill instanceof AIThunderBolt)
                {
                    MovingObjectPosition trace = Raytrace.traceLiving(speller,((AIThunderBolt)skill).getMaxDistance(), EntitySelectors.living(), BlockSelectors.filNothing);
                    if (trace != null && trace.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY)
                    {
                        if(trace.entityHit==target)
                        {
                            skill.spell();
                        }
                    }
                }
                else if(skill instanceof AIBodyIntensify)
                {
                    skill.spell();
                }
                else if (skill instanceof AIArcGen)
                {
                    if (result != null && result.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY)
                    {
                        if(result.entityHit==target)
                        {
                            skill.spell();
                        }
                    }
                    else if (result != null && result.typeOfHit== MovingObjectPosition.MovingObjectType.BLOCK)
                    {
                        Block block=speller.worldObj.getBlock(result.blockX,result.blockY,result.blockZ);
                        if(block.getMaterial()== Material.wood)
                        {
                            skill.spell();
                        }
                    }
                }
                break;
            }
        }
    }
}
