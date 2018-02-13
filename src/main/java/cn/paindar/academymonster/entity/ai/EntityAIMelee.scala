package cn.paindar.academymonster.entity.ai

import cn.lambdalib.util.mc.Raytrace
import net.minecraft.entity.EntityLivingBase
import cn.paindar.academymonster.ability._
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{MovingObjectPosition, Vec3}

/**
  * Created by Paindar on 2017/6/15.
  */
class EntityAIMelee(target: EntityLivingBase) extends EntityAIBaseX {

  override def execute(owner:EntityLivingBase): Boolean = {
    val ieep:SkillExtendedEntityProperties=SkillExtendedEntityProperties.get(owner)
    if (target == null || target.isDead||(target.isInstanceOf[EntityPlayer] &&
        target.asInstanceOf[EntityPlayer].capabilities.isCreativeMode)) {
      ieep.setAI(new EntityAIWander())
      return false
    }
    val imaDist = owner.getDistanceSqToEntity(target)
    if (imaDist <= 25) {
      import scala.collection.JavaConversions._
      ieep.list.foreach((skill:BaseSkill)=>{
        var validDist = .0
        skill match {
          case shock: AIDirectedShock if skill.canSpell =>
            validDist = shock.getMaxDistance
            if (validDist * validDist >= imaDist) if (isTargetInHorizon(owner, target)) {
              shock.spell()
              return true
            }
          case skill: AIThreateningTeleport if skill.canSpell =>
            validDist = skill.getMaxDistance
            if (validDist * validDist >= imaDist) if (isTargetInHorizonIgnoreBlock(owner, target)) {
              skill.spell()
              return true
            }
          case gen: AIArcGen if skill.canSpell =>
            validDist = gen.getMaxDistance
            if (validDist * validDist >= imaDist) {
              var lookingPos = owner.getLookVec
              val direct = Vec3.createVectorHelper(target.posX - owner.posX, 0, target.posZ - owner.posZ).normalize
              lookingPos.yCoord = 0
              lookingPos = lookingPos.normalize
              val trace = Raytrace.perform(owner.worldObj, Vec3.createVectorHelper(owner.posX, owner.posY + owner.getEyeHeight, owner.posZ), Vec3.createVectorHelper(target.posX, target.posY + target.getEyeHeight, target.posZ))
              if (lookingPos.xCoord * direct.xCoord + lookingPos.zCoord * direct.zCoord >= 0.5) if (trace != null) if (trace.typeOfHit eq MovingObjectPosition.MovingObjectType.ENTITY) {
                gen.spell()
                return true
              }
              else if (trace.typeOfHit eq MovingObjectPosition.MovingObjectType.BLOCK) {
                val block = owner.worldObj.getBlock(trace.blockX, trace.blockY, trace.blockZ)
                if (block.getMaterial eq Material.wood) {
                  gen.spell()
                  return true
                }
              }
            }
          case value: AIElectronCurtains if skill.canSpell =>
            value.spell()
            return true
          case value: AIElectronBomb if skill.canSpell =>
            validDist = value.getMaxDistance
            if (validDist * validDist >= imaDist) if (isTargetInHorizon(owner, target)) {
              value.spell()
              return true
            }
          case value: AIGroundShock if skill.canSpell =>
            validDist = value.getMaxDistance
            if (validDist * validDist >= imaDist) if (isTargetInHorizon(owner, target)) {
              value.spell()
              return true
            }
          case value: AIBloodRetrograde if skill.canSpell =>
            validDist = value.getMaxDistance
            if (validDist * validDist >= imaDist) if (isTargetInHorizon(owner, target)) {
              value.spell()
              return true
            }
          case value: AIFleshRipping if skill.canSpell =>
            validDist = value.getMaxDistance
            if (validDist * validDist >= imaDist) if (isTargetInHorizon(owner, target)) {
              value.spell()
              return true
            }
          case value: AILocManip if skill.canSpell =>
            value.spell()
            return true
          case value: AIThunderBolt if skill.canSpell =>
            validDist = value.getMaxDistance
            if (validDist * validDist >= imaDist) if (isTargetInHorizon(owner, target)) {
              value.spell()
              return true
            }
          case skill1: AIScatterBomb if skill.canSpell =>
            ieep.setAI(new EntityAIScatterBomb(target, skill1))
            return false
          case value: AILocationTeleport if skill.canSpell =>
            if (6.25 >= imaDist) if (isTargetInHorizon(owner, target)) {
              value.spell(target)
              return true
            }
          case value: AIElectronMissile if skill.canSpell =>
            validDist = value.getMaxDistance
            if (validDist * validDist >= imaDist) {
              value.spell()
              return true
            }
          case _ =>
        }
      })
    }
    else{
      ieep.setAI(new EntityAIRange(target))
      return false
    }
    true
  }
}
