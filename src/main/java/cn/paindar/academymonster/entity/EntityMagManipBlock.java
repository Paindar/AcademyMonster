package cn.paindar.academymonster.entity;

import cn.academy.core.event.BlockDestroyEvent;
import cn.lambdalib.s11n.network.TargetPoints;
import cn.lambdalib.util.entityx.event.CollideEvent;
import cn.lambdalib.util.entityx.handlers.Rigidbody;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.EntitySyncer;
import cn.lambdalib.util.mc.RichEntity;
import cn.paindar.academymonster.ability.BaseSkill;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Paindar on 2017/6/4.
 */
public class EntityMagManipBlock extends EntityBlockNative
{
    private final static int ActNothing = 0, ActMoveTo = 1;
    private float damage;
    private EntitySyncer syncer;

    private EntityLivingBase player2;

    private float yawSpeed = RandUtils.rangef(1, 3);
    private float pitchSpeed = RandUtils.rangef(1, 3);

    private int actionType=1;
    private float tx,ty,tz;
    public double radium=0;

    private BaseSkill skill;
    public EntityMagManipBlock(World world)
    {
        super(world);
        placeWhenCollide=false;
    }

    public EntityMagManipBlock(EntityLivingBase spawner, float damage,BaseSkill skill)
    {
        this(spawner.worldObj);
        this.damage = damage;
        this.player2 = spawner;
        this.skill=skill;
    }

    public void setMoveTo(double x, double y, double z)
    {
        //这里可能发生坐标错位，原因同踏击导向等技能
        actionType = ActMoveTo;
        tx = (float)x;
        ty = (float)y;
        tz = (float)z;
    }

    @Override
    public void setPlaceFromServer(boolean value) {
        placeWhenCollide = value;

        NetworkManager.sendMagToAllAround(
                TargetPoints.convert(this, 20),
                this, value
        );
    }

    public void stopMoveTo()
    {
            actionType = ActNothing;
    }

    @Override
    public void entityInit()
    {
            super.entityInit();
            syncer =new EntitySyncer(this);
            syncer.init();
    }

    @Override
    public void onFirstUpdate()
    {
        super.onFirstUpdate();

        Rigidbody rb = getMotionHandler(Rigidbody.class);
        rb.entitySel = t -> t != player2;

        regEventHandler(new CollideEvent.CollideHandler(){
            @Override
            public void onEvent(CollideEvent event)
            {
                if (!worldObj.isRemote && event.result != null )
                {
                    if(event.result.typeOfHit== MovingObjectPosition.MovingObjectType.ENTITY)
                    {
                        if(event.result.entityHit != null && event.result.entityHit instanceof EntityLivingBase && event.result.entityHit!=player2)
                            skill.attack((EntityLivingBase) event.result.entityHit, damage);
                    }
                    else if (event.result.typeOfHit== MovingObjectPosition.MovingObjectType.BLOCK)
                    {
                        Explosion explosion=new Explosion(worldObj,player2,posX,posY,posZ,3);
                        explosion.doExplosionB(true);
                        setDead();
                    }
                }
            }
        });

    }

    public void setPlaceWhenCollide(boolean value)
    {
        placeWhenCollide = value;
    }

    @Override
    public void onUpdate()
    {
        syncer.update();
        super.onUpdate();
        yaw += yawSpeed;
        pitch += pitchSpeed;

        switch (actionType)
        {
            case ActMoveTo:
            {
                double dist = this.getDistanceSq(tx, ty, tz);
                Vec3 delta = Vec3.createVectorHelper(tx - posX, ty - posY, tz - posZ).normalize();
                Vec3 mo = VecUtils.multiply(delta,.2 * ((dist<4)?dist/4:1.0));

                new RichEntity(this).setVel(mo);
            }
            case ActNothing:
                motionY -= 0.04;
        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
    }

}
