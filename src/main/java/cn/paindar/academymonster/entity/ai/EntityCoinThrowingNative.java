package cn.paindar.academymonster.entity.ai;

import cn.lambdalib.util.entityx.EntityAdvanced;
import cn.lambdalib.util.entityx.MotionHandler;
import cn.lambdalib.util.entityx.handlers.Rigidbody;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by Paindar on 2017/2/12.
 */
public class EntityCoinThrowingNative extends EntityAdvanced
{

    private static final int MAXLIFE = 120;
    private static final double INITVEL = 0.92;
    private float initHt;
    private double maxHt;

    public EntityLivingBase speller;

    public Vec3 axis;
    public boolean isSync = false;
    public EntityCoinThrowingNative(World world)
    {
        super(world);
    }

    public EntityCoinThrowingNative(EntityLivingBase speller)
    {
        super(speller.worldObj);
        this.speller = speller;
        this.initHt = (float) speller.posY;
        setPosition(speller.posX, speller.posY, speller.posZ);
        this.motionY = speller.motionY;
        setup();
        this.ignoreFrustumCheck = true;
    }

    private void setup() {
        Rigidbody rb = new Rigidbody();
        rb.gravity = 0.06;
        this.addMotionHandler(rb);
        this.addMotionHandler(new EntityCoinThrowingNative.KeepPosition());
        this.motionY += INITVEL;
        axis = Vec3.createVectorHelper(.1 + rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        this.setSize(0.2F, 0.2F);
    }

    private class KeepPosition extends MotionHandler<EntityCoinThrowingNative>
    {

        public KeepPosition() {}

        @Override
        public void onUpdate() {
            if(EntityCoinThrowingNative.this.speller != null) {
                posX = speller.posX;
                posZ = speller.posZ;
                if((posY < speller.posY && motionY < 0) || ticksExisted > MAXLIFE) {
                    finishThrowing();
                }
            }

            maxHt = Math.max(maxHt, posY);
        }

        @Override
        public String getID() {
            return "kip";
        }

        @Override
        public void onStart() {}

    }

    void finishThrowing() {
        setDead();
    }

    public double getProgress() {
        if(motionY > 0) { //Throwing up
            return (INITVEL - motionY) / INITVEL * 0.5;
        } else {
            return Math.min(1.0, 0.5 + ((maxHt - posY) / (maxHt - initHt)) * 0.5);
        }
    }

    @Override
    public void entityInit() {
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

    }


}
