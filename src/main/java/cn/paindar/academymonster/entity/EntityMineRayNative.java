package cn.paindar.academymonster.entity;

import cn.academy.core.client.render.ray.RendererRayComposite;
import cn.academy.core.entity.IRay;
import cn.academy.vanilla.meltdowner.client.render.MdParticleFactory;
import cn.lambdalib.particle.Particle;
import cn.lambdalib.util.entityx.EntityAdvanced;
import cn.lambdalib.util.entityx.EntityCallback;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.GameTimer;
import cn.lambdalib.util.helper.Motion3D;
import cn.lambdalib.util.mc.BlockSelectors;
import cn.lambdalib.util.mc.Raytrace;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/2/17.
 */
public class EntityMineRayNative extends EntityAdvanced implements IRay,IEntityAdditionalSpawnData
{

    private EntityLivingBase spawner;

    private long blendInTime = 100;

    private long blendOutTime = 300;
    private long widthShrinkTime = 300;
    private double maxDist;
    private double length;

    private double widthWiggleRadius = 0.1;
    private double maxWiggleSpeed = 0.4;
    private double widthWiggle = 0.0;

    private double glowWiggleRadius = 0.1;
    private double maxGlowWiggleSpeed = 0.4;
    private double glowWiggle = 0.0;

    private long lastFrame = 0;
    private long creationTime;
    private int life=23333333;


    public EntityMineRayNative(EntityLivingBase speller,double len)
    {
        this(speller.worldObj);
        maxDist=len;
        spawner = speller;
        this.setPositionAndRotation(speller.posX,speller.posY,speller.posZ,0f,0f);
        this.blendInTime = 200;
        this.blendOutTime = 400;
    }

    public EntityMineRayNative(World world)
    {
        super(world);
        creationTime = GameTimer.getTime();
        ignoreFrustumCheck = true;
    }
    public void setFromTo(Vec3 from, Vec3 to) {
        setFromTo(from.xCoord, from.yCoord, from.zCoord, to.xCoord, to.yCoord, to.zCoord);
    }

    public void setFromTo(double x0, double y0, double z0, double x1, double y1, double z1) {
        setPosition(x0, y0, z0);

        double dx = x1 - x0, dy = y1 - y0, dz = z1 - z0;
        double dxzsq = dx * dx + dz * dz;
        rotationYaw = (float) (-Math.atan2(dx, dz) * 180 / Math.PI);
        rotationPitch = (float) (-Math.atan2(dy, Math.sqrt(dxzsq)) * 180 / Math.PI);

        length = Math.sqrt(dxzsq + dy * dy);
    }

    @Override
    protected void onFirstUpdate() {
        executeAfter(new EntityCallback() {
            @Override
            public void execute(Entity target) {
                setDead();
            }
        }, life);
    }

    protected long getDeltaTime() {
        return GameTimer.getTime() - creationTime;
    }

    @Override
    public Vec3 getPosition() {
        return Vec3.createVectorHelper(posX, posY, posZ);
    }

    @Override
    public double getLength() {
        long dt = GameTimer.getTime() - creationTime;
        return (dt < blendInTime ? (double)dt / blendInTime : 1) * length;
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

    //TODO Add glow texture alpha wiggle
    @Override
    public double getAlpha() {
        long dt = getDeltaTime();
        long lifeMS = getLifeMS();
        return dt > lifeMS - blendOutTime ? 1 - (double) (dt + blendOutTime - lifeMS) / blendOutTime : 1.0;
    }

    @Override
    public double getWidth() {
        long dt = getDeltaTime();
        long lifeMS = getLifeMS();
        return widthWiggle +
                (dt > lifeMS - widthShrinkTime ? 1 - (double) (dt + widthShrinkTime - lifeMS) / widthShrinkTime : 1.0);
    }

    @Override
    public boolean needsViewOptimize() {
        return false;
    }

    @Override
    public double getStartFix() {
        return 0.0;
    }

    @Override
    public void onRenderTick() {
        long time = GameTimer.getTime();
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

    @Override
    public double getGlowAlpha() {
        long dt = GameTimer.getTime() - creationTime;
        long lifeMS = getLifeMS();
        return (1 - glowWiggleRadius + glowWiggle) * getAlpha();
    }


    @SideOnly(Side.CLIENT)
    public void onUpdate()
    {
        EntityLivingBase speller = this.spawner;
        if(speller==null)
        {
            setDead();
            return;
        }
        MovingObjectPosition result = Raytrace.traceLiving(speller, maxDist,null, BlockSelectors.filNormal);
        Vec3 str=Vec3.createVectorHelper(speller.posX, speller.posY +  1.6, speller.posZ);
        Vec3 end=VecUtils.add(str,VecUtils.multiply(speller.getLookVec(),maxDist));
        if(result!=null)
        {
            end=Vec3.createVectorHelper(result.blockX,result.blockY,result.blockZ);
        }
        this.setFromTo(str,end);
        if(RandUtils.nextDouble() < 0.5)
        {
            Particle p = MdParticleFactory.INSTANCE.next(worldObj,
                    new Motion3D(this, true).move(RandUtils.ranged(0, 10)).getPosVec(),
                    VecUtils.vec(RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03), RandUtils.ranged(-.03, .03)));
            worldObj.spawnEntityInWorld(p);
        }
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return null;
    }


    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        NBTTagCompound nbt=new NBTTagCompound();
        if(spawner!=null)
            nbt.setInteger("id",spawner.getEntityId());
        nbt.setDouble("len",length);
        nbt.setDouble("maxDist",maxDist);
        ByteBufUtils.writeTag(buf, nbt);
    }


    @Override
    public void readSpawnData(ByteBuf buf)
    {
        NBTTagCompound nbt= ByteBufUtils.readTag(buf);
        spawner=(EntityLivingBase) worldObj.getEntityByID(nbt.getInteger("id"));
        length=nbt.getDouble("len");
        maxDist=nbt.getDouble("maxDist");
        if(spawner==null)
            setDead();
    }

    @SideOnly(Side.CLIENT)
    static class R extends RendererRayComposite
    {

        public R()
        {
            super("mdray_expert");
            this.cylinderIn.width = 0.045;
            this.cylinderIn.color.setColor4i(216, 248, 216, 230);

            this.cylinderOut.width = 0.056;
            this.cylinderOut.color.setColor4i(106, 242, 106, 50);

            this.glow.width = 0.5;
            this.glow.color.a = 0.7;
        }

        @Override
        public void doRender(Entity ent, double x,
                             double y, double z, float a, float b) {
            this.cylinderIn.width = 0.045;
            this.cylinderIn.color.setColor4i(216, 248, 216, 180);

            this.cylinderOut.width = 0.056;
            this.cylinderOut.color.setColor4i(106, 242, 106, 50);

            this.glow.color.a = 0.5;
            super.doRender(ent, x, y, z, a ,b);
        }
    }
}

