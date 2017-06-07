package cn.paindar.academymonster.ability

import cn.lambdalib.s11n.network.TargetPoints
import net.minecraft.entity.EntityLivingBase
import cn.lambdalib.util.generic.MathUtils.lerpf
import cn.lambdalib.util.mc.{BlockSelectors, EntitySelectors, Raytrace}
import cn.paindar.academymonster.config.AMConfig
import cn.paindar.academymonster.entity.{EntityPlasmaBodyEffect, EntityTornadoEffect}
import cn.paindar.academymonster.network.NetworkManager
import net.minecraft.util.MovingObjectPosition
import net.minecraft.world.World

/**
  * Created by Paindar on 2017/6/7.
  */

object AIPlasmaCannon{
  private val canDestroyBlock = AMConfig.getBoolean("am.skill.PlasmaCannon.destroyBlock", true)
}
class AIPlasmaCannon(speller:EntityLivingBase, exp:Float) extends BaseSkill(speller,lerpf(400,650,exp).toInt,exp,"vecmanip.plasma_cannon")
{
  var time: Int = _
  var effect:EntityPlasmaBodyEffect=_
  var body:EntityTornadoEffect=_

  override def spell(): Unit = {
    if(canSpell){
      effect = new EntityPlasmaBodyEffect(speller, this)
      time=0
      isChanting=true
      effect.setPosition(speller.posX, speller.posY + 15, speller.posZ)
      body=new EntityTornadoEffect(speller.worldObj, speller, this)
      speller.worldObj.spawnEntityInWorld(body)
      speller.worldObj.spawnEntityInWorld(effect)
    }
  }

  override def onTick():Unit = {
    if(!isChanting)
      return
    if({time+=1;time>60||effect==null||effect.isDead}) {
      stop()
    }
  }
  private def flyTo(x:Double,y:Double,z:Double):Unit={
    effect.setTargetPoint(x,y,z)
    NetworkManager.sendPlasmaStateChange(TargetPoints.convert(effect, 20),body)
  }
  def stop():Unit={
    isChanting=false
    time=0
    if(speller.worldObj.isRemote)
      return
    super.spell()
    if(speller==null || speller.isDead){
      NetworkManager.sendPlasmaStateChange(TargetPoints.convert(effect, 20),effect)
      return
    }
    val result = Raytrace.getLookingPos(speller, 10, EntitySelectors.nothing, BlockSelectors.filEverything)
    if(result.getValue!=null && result.getValue.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
      flyTo(result.getValue.blockX.toDouble,result.getValue.blockY.toDouble,result.getValue.blockZ.toDouble)
    }
    else
      flyTo(result.getKey.xCoord,result.getKey.yCoord,result.getKey.zCoord)


  }

  def canBreakBlock(world:World):Boolean= AIPlasmaCannon.canDestroyBlock
}
