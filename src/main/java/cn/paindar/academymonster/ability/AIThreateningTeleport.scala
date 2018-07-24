package cn.paindar.academymonster.ability

import cn.academy.vanilla.ModuleVanilla
import net.minecraft.entity.{Entity, EntityLivingBase}
import cn.lambdalib.util.generic.MathUtils.lerpf
import cn.lambdalib.util.generic.RandUtils
import cn.lambdalib.util.mc.{BlockSelectors, EntitySelectors, Raytrace, WorldUtils}
import cn.paindar.academymonster.core.AcademyMonster
import cn.paindar.academymonster.network.NetworkManager
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.{MovingObjectPosition, Vec3}

/**
  * Created by Paindar on 2017/6/15.
  */
class AIThreateningTeleport(speller:EntityLivingBase,exp:Float) extends BaseSkill(speller,lerpf(60,40,exp).toInt,exp,"teleporter.threatening_teleport"){
  val damage:Float = lerpf(6,12,exp)
  val maxDistance: Int = lerpf(8,15,exp).toInt
  var x, y,z:Double =_
  var time:Int=_

  def getMaxDistance: Int = maxDistance
  override def spell(): Unit = {
    if(canSpell){
      var pos: Vec3 = null
      var result: MovingObjectPosition = null
      var pair=Raytrace.getLookingPos(speller,maxDistance,EntitySelectors.living().and(EntitySelectors.exclude(speller)))
      pair=if(result != null)Raytrace.getLookingPos(speller,maxDistance,EntitySelectors.nothing(),BlockSelectors.filNormal)else pair
      pos=pair.getLeft
      result=pair.getRight
      if (!speller.worldObj.isRemote) {
        val list = WorldUtils.getEntities(speller, 25, EntitySelectors.player)
        import scala.collection.JavaConversions._
        list.foreach((e:Entity)=>NetworkManager.sendSoundTo("tp.tp", speller, .5f, e.asInstanceOf[EntityPlayerMP]))
      }
      if(result!=null){
        x=pos.xCoord
        y=pos.yCoord+1
        z=pos.zCoord
        time=0
        isChanting=true
      }
      else{
        val stick:ItemStack = new ItemStack(ModuleVanilla.needle,1)
        speller.worldObj.spawnEntityInWorld(new EntityItem(speller.worldObj,pos.xCoord,pos.yCoord,pos.zCoord,stick))
        super.spell()
      }
    }
  }

  override def onTick():Unit = {
    if(isChanting){
      if(speller==null || speller.isDead){
        isChanting=false
        return
      }
      else{
        time+=1
        if(time>=10){
          isChanting=false
          val result=WorldUtils.getEntities(speller.worldObj,x,y,z,2,EntitySelectors.living())
          if(result.isEmpty){
            val stick:ItemStack = new ItemStack(ModuleVanilla.needle,1)
            speller.worldObj.spawnEntityInWorld(new EntityItem(speller.worldObj,x,y,z,stick))
            super.spell()
          }
          else{
            val target=result.get(0)
            attackIgnoreArmor(target.asInstanceOf[EntityLivingBase],damage)
            if(RandUtils.nextFloat()>0.6){
              val stick:ItemStack = new ItemStack(ModuleVanilla.needle,1)
              speller.worldObj.spawnEntityInWorld(new EntityItem(speller.worldObj,x,y,z,stick))
            }
            super.spell()
          }
        }
      }
    }
  }
}
