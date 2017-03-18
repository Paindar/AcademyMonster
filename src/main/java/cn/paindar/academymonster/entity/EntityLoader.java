package cn.paindar.academymonster.entity;

import cn.academy.vanilla.meltdowner.entity.EntityMdBall;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.entity.boss.EntityFakeRaingun;
import cn.paindar.academymonster.entity.boss.EntityInsaneMeltdowner;
import cn.paindar.academymonster.entity.boss.render.RenderFakeRailgun;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Created by Paindar on 2017/2/9.
 */
public class EntityLoader
{
    private static int nextID = 0;

    public EntityLoader()
    {

    }
    @SideOnly(Side.CLIENT)
    public static void registerRenders()
    {
        registerEntityRender(EntityMdBallNative.class, new EntityMdBallNative.R());
        registerEntityRender(EntityCoinThrowingNative.class,new EntityCoinThrowingNative.R());
        registerEntityRender(EntityRailgunFXNative.class,new EntityRailgunFXNative.R());
        registerEntityRender(EntityMDRayNative.class,new EntityMDRayNative.R());
        registerEntityRender(EntityArcNative.class,new EntityArcNative.Renderer());
        registerEntityRender(EntityMineRayNative.class,new EntityMineRayNative.R());
        registerEntityRender(EntityLightShield.class,new EntityLightShield.R());
        registerEntityRender(EntityFakeRaingun.class,new RenderFakeRailgun());
        registerEntityRender(EntityInsaneMeltdowner.class,new RenderFakeRailgun());
    }

    public static void registerEntity()
    {
        registerEntity(EntityMineRayNative.class,"am_mine_ray_eff",15,1,true);
        registerEntity(EntityLightShield.class,"am_light_shield_eff",15,1,true);
        registerEntity(EntityCoinThrowingNative.class,"am_coin_throwing_eff",15,1,true);
        registerEntity(EntityMdBallNative.class,"am_meltdown_ball_eff",15,1,true);
        registerEntity(EntityFakeRaingun.class,"am_fake_railgun",40,1,true);
        registerEntity(EntityInsaneMeltdowner.class,"am_insane_meltdowner",40,1,true);
        for(BiomeGenBase biome:BiomeGenBase.getBiomeGenArray())
        {
            if (biome != null && biome != BiomeGenBase.sky && biome != BiomeGenBase.hell)
            {
                registerEntitySpawn(EntityFakeRaingun.class, AMConfig.getInt("am.monster.fakerailgun.spawn", 1), 1, 1, EnumCreatureType.monster, biome);
                registerEntitySpawn(EntityInsaneMeltdowner.class, AMConfig.getInt("am.monster.insanemeltdowner.spawn", 1), 1, 1, EnumCreatureType.monster, biome);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static <T extends Entity> void registerEntityRender(Class<T> entityClass, Render render)
    {
        RenderingRegistry.registerEntityRenderingHandler(entityClass,render);
    }

    public static void registerEntitySpawn(Class<? extends Entity> entityClass, int spawnWeight, int min,
                                            int max, EnumCreatureType typeOfCreature, BiomeGenBase... biomes)
    {
        if (EntityLiving.class.isAssignableFrom(entityClass))
        {
            Class<? extends EntityLiving> entityLivingClass = entityClass.asSubclass(EntityLiving.class);
            EntityRegistry.addSpawn(entityLivingClass, spawnWeight, min, max, typeOfCreature, biomes);
        }
    }
    private static void registerEntity(Class<? extends Entity> entityClass, String name, int trackingRange,
                                       int updateFrequency, boolean sendsVelocityUpdates)
    {
        EntityRegistry.registerModEntity(entityClass, name, nextID++, AcademyMonster.instance, trackingRange, updateFrequency,
                sendsVelocityUpdates);
    }
}
