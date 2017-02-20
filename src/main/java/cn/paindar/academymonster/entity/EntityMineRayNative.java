package cn.paindar.academymonster.entity;

import cn.academy.core.client.render.RendererList;
import cn.academy.core.client.render.ray.RendererRayComposite;
import cn.academy.core.client.render.ray.RendererRayCylinder;
import cn.academy.core.client.render.ray.RendererRayGlow;
import cn.academy.core.entity.IRay;
import cn.academy.vanilla.meltdowner.client.render.MdParticleFactory;
import cn.lambdalib.particle.Particle;
import cn.lambdalib.util.entityx.EntityAdvanced;
import cn.lambdalib.util.entityx.EntityCallback;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.GameTimer;
import cn.lambdalib.util.helper.Motion3D;
import cn.paindar.academymonster.core.AcademyMonster;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/2/17.
 */
public class EntityMineRayNative extends Entity
{

    private EntityLivingBase spawner;

    private int life = 30;

    private long blendInTime = 100;

    private long blendOutTime = 300;
    private long widthShrinkTime = 300;

    private double length;

    private double widthWiggleRadius = 0.1;
    private double maxWiggleSpeed = 0.4;
    private double widthWiggle = 0.0;

    private double glowWiggleRadius = 0.1;
    private double maxGlowWiggleSpeed = 0.4;
    private double glowWiggle = 0.0;

    private long lastFrame = 0;
    private long creationTime;


    public EntityMineRayNative(EntityLivingBase speller,double len)
    {
        this(speller.worldObj);
        length=len;
        spawner = speller;
        this.setPositionAndRotation(speller.posX,speller.posY,speller.posZ,0f,0f);
        this.blendInTime = 200;
        this.blendOutTime = 400;
        this.life = 233333;
    }

    public EntityMineRayNative(World world) {
        super(world);
        creationTime = GameTimer.getTime();
        ignoreFrustumCheck = false;
    }

    public void setFromTo(double x0, double y0, double z0, double x1, double y1, double z1) {
        setPosition(x0, y0, z0);

        double dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        double dxzsq = dx * dx + dz * dz;
        rotationYaw = (float) (-Math.atan2(dx, dz) * 180 / Math.PI);
        rotationPitch = (float) (-Math.atan2(dy, Math.sqrt(dxzsq)) * 180 / Math.PI);

        length = Math.sqrt(dxzsq + dy * dy);
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(7, Integer.valueOf(0));
        this.dataWatcher.addObject(8, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(9, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(6, Float.valueOf(1.0F));
    }

    public void onUpdate() {
        super.onUpdate();
        EntityLivingBase speller = this.spawner;
        Vec3 end = new Motion3D(speller, true).move(15).getPosVec();
        this.setFromTo(speller.posX, speller.posY +  1.6, speller.posZ, end.xCoord, end.yCoord, end.zCoord);
        this.dataWatcher.updateObject(6, Float.valueOf(1.0F));
        if(RandUtils.nextDouble() < 0.5) {
            Particle p = MdParticleFactory.INSTANCE.next(worldObj,
                    new Motion3D(this, true).move(RandUtils.ranged(0, 10)).getPosVec(),
                    VecUtils.vec(RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03)));
            worldObj.spawnEntityInWorld(p);
        }
    }

    protected long getDeltaTime() {
        return GameTimer.getTime() - creationTime;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        posX = tag.getDouble("x");
        posY = tag.getDouble("y");
        posZ = tag.getDouble("z");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setDouble("x", posX);
        tag.setDouble("y", posY);
        tag.setDouble("z", posZ);
    }

    public long getLifeMS() {
        return life * 50;
    }

    public double getAlpha() {
        long dt = getDeltaTime();
        long lifeMS = getLifeMS();
        return dt > lifeMS - blendOutTime ? 1 - (double) (dt + blendOutTime - lifeMS) / blendOutTime : 1.0;
    }

    void onRenderTick() {
        long time = GameTimer.getTime();
        AcademyMonster.log.info("render.");
        if(lastFrame != 0) {
            long dt = time - lastFrame;
            widthWiggle += dt * RandUtils.ranged(-maxWiggleSpeed, maxWiggleSpeed) / 1000.0;
            if(widthWiggle > widthWiggleRadius)
                widthWiggle = widthWiggleRadius;
            if(widthWiggle < 0)
                widthWiggle = 0;

            glowWiggle += dt * RandUtils.ranged(-maxGlowWiggleSpeed, maxGlowWiggleSpeed) / 1000.0;
            if(glowWiggle > glowWiggleRadius)
                glowWiggle = glowWiggleRadius;
            if(glowWiggle < 0)
                glowWiggle = 0;
        }

        lastFrame = GameTimer.getTime();
    }

    public static class R extends RendererList
    {
        RendererRayGlow glow;
        RendererRayCylinder cylinderIn, cylinderOut;

        R() {
            append(glow = RendererRayGlow.createFromName("mdray_small"));
            append(cylinderIn = new RendererRayCylinder(0.05f));
            append(cylinderOut = new RendererRayCylinder(0.08f));
            cylinderIn.headFix = 0.98;
            this.cylinderIn.width = 0.03;
            this.cylinderIn.color.setColor4i(216, 248, 216, 230);

            this.cylinderOut.width = 0.045;
            this.cylinderOut.color.setColor4i(106, 242, 106, 50);

            this.glow.width = 0.3;
            this.glow.color.a = 0.5;
        }
        public void doRender(Entity ent, double x,
                             double y, double z, float a, float b) {
            ((EntityMineRayNative)ent).onRenderTick();
            super.doRender(ent, x, y, z, a, b);
        }

        public void plainDoRender(Entity ent, double x,
                                  double y, double z, float a, float b) {
            super.doRender(ent, x, y, z, a, b);
        }

        @Override
        protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
            return null;
        }

    }
}

