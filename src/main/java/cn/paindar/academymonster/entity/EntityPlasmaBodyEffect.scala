package cn.paindar.academymonster.entity

import java.util.function.Consumer

import cn.academy.core.client.util.CameraPosition
import cn.academy.vanilla.vecmanip.client.effect.{TornadoEffect, TornadoRenderer}
import cn.lambdalib.s11n.network.TargetPoints
import cn.lambdalib.util.client.shader.{GLSLMesh, ShaderProgram}
import cn.lambdalib.util.deprecated.MeshUtils
import cn.lambdalib.util.generic.MathUtils.{lerpf, toDegrees}
import cn.lambdalib.util.generic.{MathUtils, VecUtils}
import cn.lambdalib.util.helper.GameTimer
import cn.lambdalib.util.mc._
import cn.paindar.academymonster.ability.{AIPlasmaCannon, BaseSkill}
import cn.paindar.academymonster.network.NetworkManager
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{MathHelper, MovingObjectPosition, ResourceLocation, Vec3}
import net.minecraft.world.{Explosion, World}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.util.vector.{Matrix4f, Vector3f, Vector4f}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Paindar on 2017/6/7.
  */
class LocalEntity(val world: World) extends Entity(world) {
  protected def entityInit() {
  }

  protected def readEntityFromNBT(tag: NBTTagCompound) {
    setDead()
  }

  protected def writeEntityToNBT(tag: NBTTagCompound) {
  }
}



@SideOnly(Side.CLIENT)
class TornadoEntityRenderer extends Render {
  import org.lwjgl.opengl.GL11._

  override def doRender(entity: Entity, x: Double, y: Double, z: Double, v3: Float, v4: Float): Unit = entity match {
    case eff: EntityTornadoEffect =>
      glPushMatrix()
      glTranslated(x, y, z)

      glDisable(GL_ALPHA_TEST)
      TornadoRenderer.doRender(eff.theTornado)

      glPopMatrix()
  }

  override def getEntityTexture(entity: Entity): ResourceLocation = null
}
class PlasmaBodyRenderer extends Render {
  val mesh = MeshUtils.createBillboard(new GLSLMesh, -.5, -.5, .5, .5)

  val shader = new ShaderProgram

  shader.linkShader(new ResourceLocation("academy:shaders/plasma_body.vert"), GL_VERTEX_SHADER)
  shader.linkShader(new ResourceLocation("academy:shaders/plasma_body.frag"), GL_FRAGMENT_SHADER)
  shader.compile()

  val pos_ballCount: Int = shader.getUniformLocation("ballCount")
  val pos_balls: Int = shader.getUniformLocation("balls")
  val pos_alpha: Int = shader.getUniformLocation("alpha")
  def doRender(entity: Entity, x: Double, y: Double, z: Double, partialTicks: Float, wtf: Float): Unit = entity match {
    case eff: EntityPlasmaBodyEffect =>
      val size = 22

      val playerPos = new Vector3f(
        RenderManager.renderPosX.toFloat,
        RenderManager.renderPosY.toFloat,
        RenderManager.renderPosZ.toFloat)

      val matrix = new Matrix4f()
      acquireMatrix(GL_MODELVIEW_MATRIX, matrix)

      glDepthMask(false)
      glEnable(GL_BLEND)
      glDisable(GL_ALPHA_TEST)
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
      glUseProgram(shader.getProgramID)

      // update ball location
      val deltaTime = eff.deltaTime

      eff.updateAlpha()

      val alpha = math.pow(eff.alpha, 2).toFloat

      def updateBalls() = {
        glUniform1i(pos_ballCount, eff.balls.size)
        eff.balls.zipWithIndex.foreach { case (ball, idx) => {
          val hrphase = ball.hmove.phase(deltaTime)
          val vtphase = ball.vmove.phase(deltaTime)

          val dx = ball.hmove.amp * MathHelper.sin(hrphase)
          val dy = ball.vmove.amp * MathHelper.sin(vtphase)
          val dz = ball.hmove.amp * MathHelper.cos(hrphase)

          val pos = new Vector4f(
            eff.posX.toFloat + ball.center.x + dx - playerPos.x,
            eff.posY.toFloat + ball.center.y + dy - playerPos.y,
            eff.posZ.toFloat + ball.center.z + dz - playerPos.z, 1)

          val camPos = Matrix4f.transform(matrix, pos, null)
          glUniform4f(pos_balls + idx, camPos.x, camPos.y, -camPos.z, ball.size)
        }}
      }
      updateBalls()

      glUniform1f(pos_alpha, alpha)
      //

      val campos = CameraPosition.getVec3
      val delta = Vec3.createVectorHelper(x-campos.xCoord,y-campos.yCoord,z-campos.zCoord)
      val yp = new EntityLook(
        -toDegrees(Math.atan2(delta.xCoord, delta.zCoord)).toFloat,
        -toDegrees(Math.atan2(delta.yCoord, Math.sqrt(delta.xCoord * delta.xCoord + delta.zCoord * delta.zCoord))).toFloat)

      glPushMatrix()

      glTranslated(x, y, z)
      glRotated(-yp.yaw + 180, 0, 1, 0)
      glRotated(-yp.pitch, 1, 0, 0)
      glScaled(size, size, 1)

      mesh.draw(shader.getProgramID)

      glPopMatrix()

      glUseProgram(0)
      glEnable(GL_ALPHA_TEST)
      glDepthMask(true)
  }

  protected def getEntityTexture(entity: Entity) = null

  private def acquireMatrix(matrixType: Int, dst: Matrix4f) = {
    val buffer = BufferUtils.createFloatBuffer(16)
    glGetFloat(matrixType, buffer)
    dst.load(buffer)
  }
}


class EntityTornadoEffect(world: World) extends LocalEntity(world) {


  val theTornado = new TornadoEffect(12, 8, 1, 0.3)

  var dead: Boolean = false
  var deadTick: Int = 0
  var player: EntityLivingBase=_
  var skill:BaseSkill=_
  var state=false
  def this(world:World,p: EntityLivingBase, s:BaseSkill) = {
    this(world)
    player=p
    skill=s
    var initPos: Vec3 = null
    val p0:Vec3 = player.getPosition(1)
    val p1 = VecUtils.add(p0,Vec3.createVectorHelper(0.0, -20.0, 0.0))
    val result: MovingObjectPosition = Raytrace.perform(player.worldObj, p0, p1, EntitySelectors.nothing)
    if (result.typeOfHit==MovingObjectPosition.MovingObjectType.BLOCK) {
      initPos = result.hitVec
    } else {
      initPos = p1
    }
    this.setPosition(initPos.xCoord,initPos.yCoord+15,initPos.zCoord)
  }


  ignoreFrustumCheck = true

  override def onUpdate(): Unit = {
    if (state) {
      dead = true
    }
    if(!worldObj.isRemote&&(player==null ||player.isDead)) {
      setDead()
      dead = true
    }

    if (dead) {
      deadTick += 1
      if (deadTick == 30) {
        setDead()
      }
    }
    theTornado.alpha = alpha * 0.5f

  }

  def alpha: Float = {
    if (!dead) {
      if (ticksExisted < 20.0f) ticksExisted / 20.0f else 1.0f
    } else {
      1 - deadTick / 20.0f
    }
  }
  def changeState(): Unit ={state = !state}

  override def shouldRenderInPass(pass: Int): Boolean = pass == 1
}

class EntityPlasmaBodyEffect(world: World) extends LocalEntity(world) {
  import collection.mutable
    import cn.lambdalib.util.generic.RandUtils._
  private var skill:AIPlasmaCannon=_
  var state=false
  var flying=false
  var speller:EntityLivingBase= _
  var delta:Vec3=_

  def this(speller:EntityLivingBase,s:AIPlasmaCannon)= {
    this(speller.worldObj)
    this.speller=speller
    skill=s
  }
  case class TrigPar(amp: Float, speed: Float, dphase: Float) {
    def phase(time: Float): Float = speed * time - dphase
  }
  case class BallInst(size: Float, center: Vector3f, hmove: TrigPar, vmove: TrigPar)

  val balls: ArrayBuffer[BallInst] = mutable.ArrayBuffer[BallInst]()

  def nextTrigPar(size: Float = 1.0f): TrigPar = {
    val amp = rangef(1.4f, 2f) * size
    val speed = rangef(0.5f, 0.7f)
    val dphase = rangef(0, MathUtils.PI_F * 2)
    TrigPar(amp, speed, dphase)
  }

  for (i <- 0 until 4) {
    def rvf = rangef(-1.5f, 1.5f)
    balls += BallInst(rangef(1, 1.5f),
      new Vector3f(rvf, rvf, rvf),
      nextTrigPar(),
      nextTrigPar())
  }
  for (i <- 0 until rangei(4, 6)) {
    def rvf = rangef(-3f, 3f)
    balls += BallInst(rangef(0.1f, 0.3f),
      new Vector3f(rvf, rvf, rvf),
      nextTrigPar(2.5f),
      nextTrigPar(2.5f))
  }

  setSize(10, 10)
  ignoreFrustumCheck = true

  var initTime: Long = GameTimer.getTime
  var alpha = 0.0f

  def deltaTime: Float = (GameTimer.getTime - initTime) / 1000.0f
  private def explode() = {
    WorldUtils.getEntities(world, posX, posY, posZ, 10, EntitySelectors.living).forEach(new Consumer[Entity](){
      override def accept(t: Entity): Unit = {
        t match{
          case entity:EntityLivingBase =>
            if(entity != speller){
              skill.attack(entity, rangef(0.8f, 1.2f) * lerpf(20,45,skill.getSkillExp))
              entity.hurtResistantTime = -1
            }
        }

      }
      })

    val explosion = new Explosion(world,this,
      posX, posY, posZ,
      lerpf(12.0f, 15.0f, skill.getSkillExp))
    explosion.isSmoking = true

    if (skill.canBreakBlock(worldObj)) {
      explosion.doExplosionA()
    }
    explosion.doExplosionB(true)
    flying=false
    setDead()

  }

  override def onUpdate(): Unit = {
    if(!worldObj.isRemote && speller.isDead){
      setDead()
      return
    }
    val terminated = state
    if(flying) {
      val start:Vec3=Vec3.createVectorHelper(posX,posY,posZ)
      posX+=delta.xCoord
      posY+=delta.yCoord
      posZ+=delta.zCoord
      val result=Raytrace.perform(worldObj,start,VecUtils.add(start, VecUtils.multiply(delta,3.0)),EntitySelectors.living,BlockSelectors.filNormal)
      if(result!=null && result.typeOfHit!=null && !worldObj.isRemote){
        explode()
        NetworkManager.sendPlasmaStateChange(TargetPoints.convert(this, 20),this)
      }
    }
    if (terminated && math.abs(alpha) <= 1e-3f) {
      setDead()
    }
  }

  def updateAlpha(): Unit = {
    val dt = deltaTime
    val terminated = state
    val desiredAlpha = if (terminated) 0 else 1

    alpha = move(alpha, desiredAlpha, dt * (if(terminated) 1f else 0.3f))

    initTime = GameTimer.getTime
  }

  def changeState(): Unit ={state = !state}

  private def move(from: Float, to: Float, max: Float) = {
    val delta = to - from
    from + math.min(math.abs(delta), max) * math.signum(delta)
  }
  def setTargetPoint(x:Double,y:Double,z:Double): Unit ={
    delta=Vec3.createVectorHelper(x-posX,y-posY,z-posZ).normalize
    flying=true
  }

  override def shouldRenderInPass(pass: Int): Boolean = pass == 1
}
