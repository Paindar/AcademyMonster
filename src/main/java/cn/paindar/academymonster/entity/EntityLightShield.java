package cn.paindar.academymonster.entity;

import cn.academy.core.Resources;
import cn.lambdalib.util.client.RenderUtils;
import cn.lambdalib.util.client.shader.GLSLMesh;
import cn.lambdalib.util.client.shader.ShaderSimple;
import cn.lambdalib.util.deprecated.MeshUtils;
import cn.lambdalib.util.entityx.EntityAdvanced;
import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.helper.GameTimer;
import cn.lambdalib.util.helper.Motion3D;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Created by Paindar on 2017/2/26.
 */
public class EntityLightShield extends EntityAdvanced implements IEntityAdditionalSpawnData
{

    static final float SIZE = 1.8f;

    // Intrusive render states
    public float rotation;
    public long lastRender;

    private EntityLivingBase spawner;

    public EntityLightShield(World world)
    {
        super(world);
    }
    public EntityLightShield(EntityLivingBase spawner) {
        super(spawner.worldObj);
        this.spawner = spawner;
        this.setSize(SIZE, SIZE);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(spawner==null)
        {
            setDead();
            return;
        }
        Motion3D mo = new Motion3D(spawner, true).move(1);
        mo.py -= 0.5;
        setPosition(mo.px, mo.py, mo.pz);

        this.rotationYaw = spawner.rotationYawHead;
        this.rotationPitch = spawner.rotationPitch;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {}



    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        NBTTagCompound nbt=new NBTTagCompound();
        if(spawner!=null)
            nbt.setInteger("id",spawner.getEntityId());
        ByteBufUtils.writeTag(buf, nbt);
    }


    @Override
    public void readSpawnData(ByteBuf buf)
    {
        NBTTagCompound nbt= ByteBufUtils.readTag(buf);
        spawner=(EntityLivingBase) worldObj.getEntityByID(nbt.getInteger("id"));
        if(spawner==null)
            setDead();
    }

    static class R extends Render
    {

        GLSLMesh mesh;
        ResourceLocation texture;

        R() {
            texture = Resources.getTexture("effects/mdshield");
            mesh = MeshUtils.createBillboard(new GLSLMesh(), -0.5, -0.5, 0.5, 0.5);
            this.shadowOpaque = 0;
        }

        @Override
        public void doRender(Entity _entity, double x,
                             double y, double z, float a, float b) {
            if(RenderUtils.isInShadowPass()) {
                return;
            }

            long time = GameTimer.getTime();
            EntityLightShield entity = (EntityLightShield) _entity;

            // Calculate rotation
            long dt;
            if(entity.lastRender == 0) dt = 0;
            else dt = time - entity.lastRender;

            float rotationSpeed = MathUtils.lerpf(0.8f, 2f, Math.min(entity.ticksExisted / 30.0f, 1f));
            entity.rotation += rotationSpeed * dt;
            if(entity.rotation >= 360f) entity.rotation -= 360f;

            ShaderSimple.instance().useProgram();
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.05f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glPushMatrix();

            GL11.glTranslated(x, y, z);
            GL11.glRotatef(-entity.rotationYaw, 0, 1, 0);
            GL11.glRotatef(entity.rotationPitch, 1, 0, 0);
            GL11.glRotatef(entity.rotation, 0, 0, 1);

            float size = EntityLightShield.SIZE * MathUtils.lerpf(0.2f, 1f, Math.min(entity.ticksExisted / 15.0f, 1f));
            float alpha = Math.min(entity.ticksExisted / 6.0f, 1.0f);

            GL11.glScalef(size, size, 1);

            RenderUtils.loadTexture(texture);
            mesh.draw(ShaderSimple.instance());

            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1f);
            GL20.glUseProgram(0);

            entity.lastRender = time;
        }

        @Override
        protected ResourceLocation getEntityTexture(Entity entity) {
            return null;
        }

    }
}
