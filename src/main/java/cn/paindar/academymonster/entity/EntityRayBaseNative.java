package cn.paindar.academymonster.entity;


import cn.academy.core.entity.IRay;
import cn.lambdalib.util.entityx.EntityAdvanced;
import cn.lambdalib.util.entityx.EntityCallback;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.helper.GameTimer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Copied from cn.academy.core.entity.EntityRayBase.java
 */
@SideOnly(Side.CLIENT)
public class EntityRayBaseNative extends EntityAdvanced implements IRay
{

    EntityLivingBase spawner;

    public int life = 30;

    public long blendInTime = 100;

    public long blendOutTime = 300;
    public long widthShrinkTime = 300;

    public double length = 15.0;

    public double widthWiggleRadius = 0.1;
    public double maxWiggleSpeed = 0.4;
    public double widthWiggle = 0.0;

    public double glowWiggleRadius = 0.1;
    public double maxGlowWiggleSpeed = 0.4;
    public double glowWiggle = 0.0;

    public boolean viewOptimize = true;

    long lastFrame = 0;
    long creationTime;

    /**
     * This just link the ray to a player. You still have to setup the view direction based on the ray type.
     */
    public EntityRayBaseNative(EntityLivingBase player) {
        this(player.worldObj);
        spawner = player;
    }

    public EntityRayBaseNative(World world) {
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
        return viewOptimize;
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

    @Override
    public EntityPlayer getPlayer() {
        return null;
    }

    public EntityLivingBase getSpawner() {
        return spawner;
    }

}
