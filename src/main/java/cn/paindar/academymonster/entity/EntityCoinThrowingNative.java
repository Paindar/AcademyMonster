package cn.paindar.academymonster.entity;

import cn.academy.core.Resources;
import cn.lambdalib.util.client.RenderUtils;
import cn.lambdalib.util.entityx.EntityAdvanced;
import cn.lambdalib.util.entityx.MotionHandler;
import cn.lambdalib.util.entityx.handlers.Rigidbody;
import cn.lambdalib.util.helper.GameTimer;
import cn.paindar.academymonster.core.AcademyMonster;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by Paindar on 2017/2/12.
 */
public class EntityCoinThrowingNative extends EntityAdvanced implements IEntityAdditionalSpawnData
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
    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        NBTTagCompound nbt=new NBTTagCompound();
        if(speller!=null)
            nbt.setInteger("id",speller.getEntityId());
        if(axis!=null)
        {
            nbt.setDouble("xAxis", axis.xCoord);
            nbt.setDouble("yAxis", axis.yCoord);
            nbt.setDouble("zAxis", axis.zCoord);
        }
        ByteBufUtils.writeTag(buf, nbt);
    }


    @Override
    public void readSpawnData(ByteBuf buf)
    {
        NBTTagCompound nbt= ByteBufUtils.readTag(buf);
        speller=(EntityLivingBase) worldObj.getEntityByID(nbt.getInteger("id"));
        axis=Vec3.createVectorHelper(nbt.getDouble("xAxis"),nbt.getDouble("yAxis"),nbt.getDouble("zAxis"));
        if(speller==null)
            setDead();
    }

    static class R extends Render
    {

        public R() {}

        @Override
        public void doRender(Entity var1, double x, double y, double z,
                             float var8, float var9) {
            EntityCoinThrowingNative etc = (EntityCoinThrowingNative) var1;
            EntityLivingBase player = etc.speller;
            double dt = GameTimer.getTime() % 150;
            if(player == null)
                return;
            //If syncedSingle and in client computer, do not render
            if(etc.posY < player.posY)
                return;
            GL11.glPushMatrix(); {
                //x = player.posX - RenderManager.renderPosX;
                //y = etc.posY - RenderManager.renderPosY;
                //z = player.posZ - RenderManager.renderPosZ;


                GL11.glTranslated(x, y, z);
                GL11.glRotated(player.renderYawOffset, 0, -1, 0);
                GL11.glTranslated(-0.63, -0.60, 0.30);
                float scale = 0.3F;
                GL11.glScalef(scale, scale, scale);
                GL11.glTranslated(0.5, 0.5, 0);
                GL11.glRotated((dt * 360.0 / 300.0), etc.axis.xCoord, etc.axis.yCoord, etc.axis.zCoord);
                GL11.glTranslated(-0.5, -0.5, 0);
                RenderUtils.drawEquippedItem(0.0625, Resources.TEX_COIN_FRONT, Resources.TEX_COIN_BACK);
            } GL11.glPopMatrix();
        }

        @Override
        protected ResourceLocation getEntityTexture(Entity var1) {
            return null;
        }

    }



}
