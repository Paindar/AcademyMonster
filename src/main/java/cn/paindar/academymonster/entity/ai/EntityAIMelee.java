package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.Raytrace;
import cn.paindar.academymonster.ability.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/5/26.
 */
public class EntityAIMelee extends EntityAIBaseX
{
    EntityLivingBase target;
    EntityAIMelee(EntityLiving owner, EntityLivingBase tgt)
    {
        super(owner);
        target=tgt;
    }

    @Override
    public boolean execute()
    {
        if(target==null || target.isDead )
        {
            ieep.setAI(new EntityAIWander(owner));
            return false;
        }
        double imaDist=owner.getDistanceSqToEntity(target);
        if ( imaDist<=9)
        {
            for(BaseSkill skill :ieep.list)
            {
                double validDist;
                if (skill instanceof AIDirectedShock && skill.canSpell())
                {
                    validDist = ((AIDirectedShock) skill).getMaxDistance();
                    if (validDist * validDist >= imaDist)
                    {
                        if (isTargetInHorizon(target))
                        {
                            ((AIDirectedShock) skill).spell();
                            break;
                        }
                    }
                }
                else if(skill instanceof AIArcGen && skill.canSpell())
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
                else if(skill instanceof AIBloodRetrograde && skill.canSpell())
                {
                    validDist=((AIBloodRetrograde) skill).getMaxDistance();
                    if(validDist*validDist>=imaDist)
                    {
                        if(isTargetInHorizon(target))
                        {
                            ((AIBloodRetrograde) skill).spell();
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
                else if (skill instanceof AIScatterBomb && skill.canSpell())
                {
                    ieep.setAI(new EntityAIScatterBomb(owner,target, (AIScatterBomb) skill));
                    return false;
                }
                else if (skill instanceof AILocationTeleport && skill.canSpell())
                {
                    if(6.25>=imaDist)
                    {
                        if(isTargetInHorizon(target))
                        {
                            ((AILocationTeleport)skill).spell(target);
                            break;
                        }
                    }
                    break;
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
        else
        {
            ieep.setAI(new EntityAIRange(owner,target));
        }


            return false;
    }
}
