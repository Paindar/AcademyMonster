package cn.paindar.academymonster.entity;

import cn.academy.core.client.render.ray.RendererRayComposite;
import cn.academy.vanilla.meltdowner.client.render.MdParticleFactory;
import cn.lambdalib.particle.Particle;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.Motion3D;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

/**
 * Created by Paindar on 2017/3/13.
 */
public class EntityMDRayNative extends EntityRayBaseNative
{
    public EntityMDRayNative(EntityLivingBase _player, double length) {
        this(_player, new Motion3D(_player, true), length);
    }

    public EntityMDRayNative(EntityLivingBase spawner, Motion3D mo, double length) {
        super(spawner);

        Vec3 start = mo.getPosVec(), end = mo.move(length).getPosVec();
        this.setFromTo(start, end);
        this.blendInTime = 200;
        this.blendOutTime = 700;
        this.life = 50;
        this.length = length;
    }

    @Override
    protected void onFirstUpdate()
    {
        super.onFirstUpdate();
        worldObj.playSound(posX, posY, posZ, "academy:md.meltdowner", 0.5f, 1.0f, false);
    }
    @Override
    public void onUpdate() {
        super.onUpdate();
        if(RandUtils.nextDouble() < 0.8) {
            Particle p = MdParticleFactory.INSTANCE.next(worldObj,
                    new Motion3D(this, true).move(RandUtils.ranged(0, 10)).getPosVec(),
                    VecUtils.vec(RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03)));
            worldObj.spawnEntityInWorld(p);
        }
    }

    public static class R extends RendererRayComposite
    {

        public R() {
            super("mdray");
            this.cylinderIn.width = 0.17;
            this.cylinderIn.color.setColor4i(216, 248, 216, 230);

            this.cylinderOut.width = 0.22;
            this.cylinderOut.color.setColor4i(106, 242, 106, 50);

            this.glow.width = 1.5;
            this.glow.color.a = 0.8;
        }

    }
}
