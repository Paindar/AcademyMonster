package cn.paindar.academymonster.entity;

import cn.academy.vanilla.meltdowner.entity.EntityMdBall;
import cn.paindar.academymonster.core.AcademyMonster;
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
        registerEntity(EntityAcademyZombie.class, "AcademyZombie", 80, 3, true);
        EntityLoader.registerRenders();
        BiomeGenBase[] allBiome=BiomeGenBase.getBiomeGenArray();
        for(BiomeGenBase i:allBiome)
        {
            //AcademyMonster.log.info(i); i!=BiomeGenBase.hell &&
            if (i!=null &&i!=BiomeGenBase.sky)
            {
                registerEntitySpawn(EntityAcademyZombie.class,100,4, 4, EnumCreatureType.monster,i);
                registerEntitySpawn(EntityAcademySkeleton.class,100,4, 4, EnumCreatureType.monster,i);
            }
        }
    }
    @SideOnly(Side.CLIENT)
    public static void registerRenders()
    {
        registerEntityRender(EntityMdBallNative.class, new EntityMdBall.R());
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
