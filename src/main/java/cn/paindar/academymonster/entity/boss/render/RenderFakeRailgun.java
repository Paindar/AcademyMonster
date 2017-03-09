package cn.paindar.academymonster.entity.boss.render;

import cn.paindar.academymonster.entity.boss.EntityFakeRaingun;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Paindar on 2017/3/7.
 */
@SideOnly(Side.CLIENT)
public class RenderFakeRailgun extends RendererLivingEntity
{
    private static final ResourceLocation textures = new ResourceLocation("textures/entity/steve.png");
    public ModelBiped modelBipedMain;
    public ModelBiped modelArmorChestplate;
    public ModelBiped modelArmor;

    public RenderFakeRailgun()
    {
        super(new ModelBiped(0.0F), 0.5F);
        this.modelBipedMain = (ModelBiped)this.mainModel;
        this.modelArmorChestplate = new ModelBiped(1.0F);
        this.modelArmor = new ModelBiped(0.5F);
    }


    public void doRender(EntityFakeRaingun entity, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);

        double d3 = p_76986_4_ - (double)entity.yOffset;

        super.doRender((EntityLivingBase)entity, p_76986_2_, d3, p_76986_6_, p_76986_8_, p_76986_9_);
        this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
    }

    protected void preRenderCallback(EntityFakeRaingun entity, float partialTickTime)
    {
        float f1 = 0.9375F;
        GL11.glScalef(f1, f1, f1);
    }


    protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
    {
        this.preRenderCallback((EntityFakeRaingun)p_77041_1_, p_77041_2_);
    }

    public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        this.doRender((EntityFakeRaingun)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        this.doRender((EntityFakeRaingun)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     *
     * @param p_110775_1_
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return textures;
    }
}
