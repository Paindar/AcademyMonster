package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.core.AcademyMonster;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/5/26.
 */
public class EntityAIRange extends EntityAIBaseX
{
    EntityLivingBase target;
    EntityAIRange(EntityLiving owner,EntityLivingBase tgt)
    {
        super(owner);
        target=tgt;
    }

    @Override
    public boolean execute()
    {
        double imaDist=owner.getDistanceSqToEntity(target);
        if(target==null || target.isDead ||imaDist>400)
        {
            ieep.setAI(new EntityAIChasing(owner,target,40));
            return false;
        }
        if (imaDist<=9)
        {
            ieep.setAI(new EntityAIMelee(owner,target));
            return false;
        }
        for(BaseSkill skill :ieep.list)
        {
            double validDist;
            if(skill instanceof AIArcGen && skill.canSpell())
            {
                validDist=((AIArcGen) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    Vec3 lookingPos=owner.getLookVec(),direct=Vec3.createVectorHelper(target.posX-owner.posX,0,target.posZ-owner.posZ).normalize();
                    lookingPos.yCoord=0;
                    lookingPos=lookingPos.normalize();
                    MovingObjectPosition trace = Raytrace.perform(owner.worldObj,
                            Vec3.createVectorHelper(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ),
                            Vec3.createVectorHelper(target.posX,target.posY+target.getEyeHeight(),target.posZ)
                    );
                    if (lookingPos.xCoord*direct.xCoord+lookingPos.zCoord*direct.zCoord>=0.5)
                    {
                        if (trace != null)
                        {
                            if (trace.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY)
                            {
                                ((AIArcGen) skill).spell();
                                break;
                            }
                            else if (trace.typeOfHit== MovingObjectPosition.MovingObjectType.BLOCK)
                            {
                                Block block=owner.worldObj.getBlock(trace.blockX,trace.blockY,trace.blockZ);
                                if(block.getMaterial()== Material.wood)
                                {
                                    ((AIArcGen) skill).spell();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else if(skill instanceof AIRailgun && skill.canSpell())
            {
                validDist=((AIRailgun) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    if(isTargetInHorizonIgnoreBlock(target))
                    {
                        ((AIRailgun) skill).spell();
                        break;
                    }
                }
            }
            else if(skill instanceof AIThunderBolt && skill.canSpell())
            {
                validDist=((AIThunderBolt) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    if(isTargetInHorizon(target))
                    {
                        ((AIThunderBolt) skill).spell();
                        break;
                    }
                }
            }
            else if(skill instanceof AIThunderClap && skill.canSpell())
            {
                validDist=((AIThunderClap) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    if(isTargetInHorizon(target))
                    {
                        ((AIThunderClap) skill).spell(target.posX,target.posY,target.posZ);
                        break;
                    }
                }
            }
            else if(skill instanceof AIElectronBomb && skill.canSpell())
            {
                validDist=((AIElectronBomb) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    if(isTargetInHorizon(target))
                    {
                        ((AIElectronBomb) skill).spell();
                        break;
                    }
                }
            }
            else if(skill instanceof AIMeltdowner && skill.canSpell())
            {
                validDist=((AIMeltdowner) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    if(isTargetInHorizonIgnoreBlock(target))
                    {
                        ((AIMeltdowner) skill).spell();
                        break;
                    }
                }
            }
            else if(skill instanceof AIGroundShock && skill.canSpell())
            {
                validDist=((AIGroundShock) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    if(isTargetInHorizon(target))
                    {
                        ((AIGroundShock) skill).spell();
                        break;
                    }
                }
            }
            else if(skill instanceof AIFleshRipping && skill.canSpell())
            {
                validDist=((AIFleshRipping) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    if(isTargetInHorizon(target))
                    {
                        ((AIFleshRipping) skill).spell();
                        break;
                    }
                }
            }
            else if(skill instanceof AIPenetrateTeleport && skill.canSpell())
            {
                ieep.setAI(new EntityAIPenetrateTeleport(owner,target, (AIPenetrateTeleport) skill));
                break;
            }
            else if (skill instanceof AIScatterBomb && skill.canSpell())
            {
                ieep.setAI(new EntityAIScatterBomb(owner,target, (AIScatterBomb) skill));
                return false;
            }
            else if(skill instanceof AIElectronMissile && skill.canSpell())
            {
                validDist=((AIElectronMissile) skill).getMaxDistance();
                if(validDist*validDist>=imaDist)
                {
                    ((AIElectronMissile) skill).spell();
                    break;
                }
            }
        }
        return true;
    }
}
