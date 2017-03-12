package cn.paindar.academymonster.entity;

import cn.academy.core.Resources;
import cn.academy.core.client.ACRenderingHelper;
import cn.lambdalib.template.client.render.entity.RenderIcon;
import cn.lambdalib.util.client.RenderUtils;
import cn.lambdalib.util.client.shader.ShaderSimple;
import cn.lambdalib.util.entityx.EntityAdvanced;
import cn.lambdalib.util.entityx.EntityCallback;
import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.helper.GameTimer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Created by Paindar on 2017/2/10.
 */
public class EntityMdBallNative extends EntityAdvanced {

    static final int MAX_TETXURES = 5;

    static final float RANGE_FROM = 0.8f, RANGE_TO = 1.3f;

    //Synced states

    EntityLivingBase spawner;
    float subX = 0, subY = 0, subZ = 0;

    //Ctor init data
    int life = 50;



    //Client-side data
    int texID;

    long spawnTime;
    long lastTime;
    long burstTime = 400;
    double alphaWiggle = 0.8;
    double accel;

    double offsetX, offsetY, offsetZ;


    public EntityMdBallNative(EntityLivingBase player) {
        this(player, 2333333, null);
    }

    public EntityMdBallNative(EntityLivingBase player, int life) {
        this(player, life, null);
    }

    public EntityMdBallNative(EntityLivingBase player, int life, final EntityCallback<EntityMdBallNative> callback) {
        super(player.worldObj);
        this.spawner = player;

        // Calc the sub-offset
        float theta = -player.rotationYaw / 180 * MathUtils.PI_F +
                RandUtils.rangef(-MathUtils.PI_F * 0.45f, MathUtils.PI_F * 0.45f);

        float range = RandUtils.rangef(RANGE_FROM, RANGE_TO);
        subX = MathHelper.sin(theta) * range;
        subZ = MathHelper.cos(theta) * range;

        subY = RandUtils.rangef(-1.2f, 0.2f);

        // Pos init
        updatePosition();

        this.life = life;

        this.executeAfter(new EntityCallback<EntityMdBallNative>() {

            @Override
            public void execute(EntityMdBallNative target) {
                target.setDead();
            }

        }, life);
        if(callback != null)
            this.executeAfter(callback, life - 2);
    }

    public EntityMdBallNative(World world) {
        super(world);
        spawnTime = GameTimer.getTime();
        ignoreFrustumCheck = true; // Small variation in render tick posupdate will cause problem
    }


    @Override
    public void entityInit() {
        dataWatcher.addObject(3, Integer.valueOf(0));
        dataWatcher.addObject(4, Float.valueOf(0));
        dataWatcher.addObject(5, Float.valueOf(0));
        dataWatcher.addObject(6, Float.valueOf(0));
        dataWatcher.addObject(7, Integer.valueOf(0));
    }

    @Override
    public void onFirstUpdate() {
        if(!worldObj.isRemote) {
            dataWatcher.updateObject(3, Integer.valueOf(spawner.getEntityId()));
            dataWatcher.updateObject(4, Float.valueOf(subX));
            dataWatcher.updateObject(5, Float.valueOf(subY));
            dataWatcher.updateObject(6, Float.valueOf(subZ));
            dataWatcher.updateObject(7, Integer.valueOf(life));
        }
    }
    @Override
    public void onUpdate() {
        super.onUpdate();

        if(worldObj.isRemote) {

            if(getSpawner() == null) {
                int eid = dataWatcher.getWatchableObjectInt(3);
                Entity e = worldObj.getEntityByID(eid);
                if(e instanceof EntityLivingBase) {
                    spawner = (EntityLivingBase) e;
                }

            } else {
                if(subX == 0 && subY == 0 && subZ == 0) {
                    subX = dataWatcher.getWatchableObjectFloat(4);
                    subY = dataWatcher.getWatchableObjectFloat(5);
                    subZ = dataWatcher.getWatchableObjectFloat(6);
                    life = dataWatcher.getWatchableObjectInt(7);
                } else {
                    updatePosition();
                }
            }

        } else {

            updatePosition();

        }
    }

    protected EntityLivingBase getSpawner() {
        return spawner;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {}


    @SideOnly(Side.CLIENT)
    private boolean updateRenderTick() {

        if(spawner == null || (subX == 0 && subY == 0 && subZ == 0))
            return false;

        final double maxAccel = 4;
        long time = GameTimer.getTime();
        long life = time - spawnTime;

        //Alpha wiggling
        if(lastTime != 0) {
            long dt = time - lastTime;
            if(rand.nextInt(8) < 3) {
                accel = RandUtils.ranged(-maxAccel, maxAccel);
                //System.out.println("AccelChange=>" + accel);
            }

            //System.out.println("AV=>" + alphaVel);
            alphaWiggle += accel * dt / 1000.0;
            if(alphaWiggle > 1) alphaWiggle = 1;
            if(alphaWiggle < 0) alphaWiggle = 0;
        }
        lastTime = time;

        //Texture wiggling
        if(rand.nextInt(8) < 2) {
            texID = rand.nextInt(MAX_TETXURES);
        }

        //Surrounding
        float phase = life / 300.0f;
        offsetX = 0.03 * MathHelper.sin(phase);
        offsetZ = 0.03 * MathHelper.cos(phase);
        offsetY = 0.04 * MathHelper.cos((float) (phase * 1.4 + Math.PI / 3.5));

        updatePosition();

        return true;
    }

    private double getAlpha() {
        int lifeMS = life * 50;
        long time = GameTimer.getTime();
        long dt = time - spawnTime;

        final int blendTime = 150;
        if(dt > lifeMS - blendTime)
            return Math.max(0, MathUtils.lerpf(1, 0, (float) (dt - (lifeMS - blendTime)) / blendTime));
        if(dt > lifeMS - burstTime)
            return MathUtils.lerp(0.6, 1.0, (double) (dt - (lifeMS - burstTime)) / (burstTime - blendTime));
        if(dt < 300)
            return MathUtils.lerp(0, 0.6, (double) dt / 300);
        return 0.6;
    }

    private float getSize() {
        int lifeMS = life * 50;
        long time = GameTimer.getTime();
        long dt = time - spawnTime;

        if(dt > lifeMS - 100)
            return Math.max(0, MathUtils.lerpf(1.5f, 0, (float) (dt - (lifeMS - 100)) / 100));
        if(dt > lifeMS - 300)
            return MathUtils.lerpf(1, 1.5f, (float) (dt - (lifeMS - 300)) / 200);
        return 1;
    }

    private void updatePosition() {
        posX = spawner.posX + subX;
        posY = spawner.posY + subY + (worldObj.isRemote ? 0 : 1.6); //Fix for different sides
        posZ = spawner.posZ + subZ;
    }

    @SideOnly(Side.CLIENT)
    public static class R extends RenderIcon
    {

        ResourceLocation[] textures;
        ResourceLocation glowTexture;

        public R() {
            super(null);
            textures = Resources.getEffectSeq("mdball", MAX_TETXURES);
            glowTexture = Resources.getTexture("effects/mdball/glow");
            //this.minTolerateAlpha = 0.05f;
            this.shadowOpaque = 0;
        }

        @Override
        public void doRender(Entity par1Entity, double x, double y,
                             double z, float par8, float par9) {
            if(RenderUtils.isInShadowPass()) {
                return;
            }

            EntityMdBallNative ent = (EntityMdBallNative) par1Entity;
            if(!ent.updateRenderTick())
                return;

            EntityPlayer clientPlayer = Minecraft.getMinecraft().thePlayer;

            //HACK: Force set the render pos to prevent glitches
            {
                x = ent.posX - clientPlayer.posX;
                y = ent.posY - clientPlayer.posY;
                z = ent.posZ - clientPlayer.posZ;
                y += 1.6;
            }

            GL11.glPushMatrix();
            {
                ShaderSimple.instance().useProgram();
                GL11.glTranslated(ent.offsetX, ent.offsetY, ent.offsetZ);

                double alpha = ent.getAlpha();
                float size = ent.getSize();

                //Glow texture
                this.color.a = alpha * (0.3 + ent.alphaWiggle * 0.7);
                this.icon = glowTexture;
                this.setSize(0.7f * size);
                super.doRender(par1Entity, x, y, z, par8, par9);

                //Core
                this.color.a = alpha * (0.8 + 0.2 * ent.alphaWiggle);
                this.icon = textures[ent.texID];
                this.setSize(0.5f * size);
                super.doRender(par1Entity, x, y, z, par8, par9);
                GL20.glUseProgram(0);
            }
            GL11.glPopMatrix();
        }

    }

}
