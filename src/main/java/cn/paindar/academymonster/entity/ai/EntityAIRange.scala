package cn.paindar.academymonster.entity.ai

import cn.lambdalib.util.mc.Raytrace
import cn.paindar.academymonster.ability._
import cn.paindar.academymonster.core.AcademyMonster
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityLiving, EntityLivingBase}
import net.minecraft.util.{MovingObjectPosition, Vec3}

/**
  * Created by Paindar on 2017/6/15.
  */
class EntityAIRange(owner: EntityLiving, target: EntityLivingBase) extends EntityAIBaseX(owner) {
  override def execute: Boolean = {
    val imaDist = owner.getDistanceSqToEntity(target)
    if (target == null || target.isDead || imaDist > 400||(target.isInstanceOf[EntityPlayer] && target.asInstanceOf[EntityPlayer].capabilities.isCreativeMode)) {
      ieep.setAI(new EntityAIChasing(owner, target, 40))
      return false
    }

    if (imaDist <= 25) {
      ieep.setAI(new EntityAIMelee(owner, target))
      return false
    }
    import scala.collection.JavaConversions._
    ieep.list.foreach((skill:BaseSkill)=> {
      var validDist = .0
      skill match {
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
        case skill: AIThreateningTeleport if skill.canSpell =>
          AcademyMonster.log.info("23333")
          validDist = skill.getMaxDistance
          if (validDist * validDist >= imaDist) if (isTargetInHorizonIgnoreBlock(target)) {
            skill.spell()
            return true
          }
        case value: AIRailgun if skill.canSpell =>
          validDist = value.getMaxDistance
          if (validDist * validDist >= imaDist) if (isTargetInHorizonIgnoreBlock(target)) {
            value.spell()
            return true
          }
        case value: AILocManip if skill.canSpell =>
          value.spell()
          return true
        case value: AIPlasmaCannon if skill.canSpell =>
          value.spell()
          return true
        case value: AIThunderBolt if skill.canSpell =>
          validDist = value.getMaxDistance
          if (validDist * validDist >= imaDist) if (isTargetInHorizon(target)) {
            value.spell()
            return true
          }
        case value: AIThunderClap if skill.canSpell =>
          validDist = value.getMaxDistance
          if (validDist * validDist >= imaDist) if (isTargetInHorizon(target)) {
            value.spell(target.posX, target.posY, target.posZ)
            return true
          }
        case value: AIElectronBomb if skill.canSpell =>
          validDist = value.getMaxDistance
          if (validDist * validDist >= imaDist) if (isTargetInHorizon(target)) {
            value.spell()
            return true
          }
        case value: AIMeltdowner if skill.canSpell =>
          validDist = value.getMaxDistance
          if (validDist * validDist >= imaDist) if (isTargetInHorizonIgnoreBlock(target)) {
            value.spell()
            return true
          }
        case value: AIGroundShock if skill.canSpell =>
          validDist = value.getMaxDistance
          if (validDist * validDist >= imaDist) if (isTargetInHorizon(target)) {
            value.spell()
            return true
          }
        case value: AIFleshRipping if skill.canSpell =>
          validDist = value.getMaxDistance
          if (validDist * validDist >= imaDist) if (isTargetInHorizon(target)) {
            value.spell()
            return true
          }
        case skill1: AIPenetrateTeleport if skill.canSpell =>
          ieep.setAI(new EntityAIPenetrateTeleport(owner, target, skill1))
          return true
        case skill1: AIScatterBomb if skill.canSpell =>
          ieep.setAI(new EntityAIScatterBomb(owner, target, skill1))
          return false
        case value: AIElectronMissile if skill.canSpell =>
          validDist = value.getMaxDistance
          if (validDist * validDist >= imaDist) {
            value.spell()
            return true
          }
        case _ =>
      }
    })
    true
  }
}
