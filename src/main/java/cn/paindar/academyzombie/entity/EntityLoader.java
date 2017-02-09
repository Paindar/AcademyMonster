package cn.paindar.academyzombie.entity;

import cn.paindar.academyzombie.core.AcademyZombie;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/2/9.
 */
public class EntityLoader
{
    private static int nextID = 0;

    public EntityLoader()
    {
        registerEntity(EntityAcademyZombie.class, "AcademyZombie", 80, 3, true);
        BiomeGenBase[] allBiome=BiomeGenBase.getBiomeGenArray();
        for(BiomeGenBase i:allBiome)
        {
            //AcademyZombie.log.info(i); i!=BiomeGenBase.hell &&
            if (i!=null &&i!=BiomeGenBase.sky)
                registerEntitySpawn(EntityAcademyZombie.class,100,4, 4, EnumCreatureType.monster,i);
        }
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
        EntityRegistry.registerModEntity(entityClass, name, nextID++, AcademyZombie.instance, trackingRange, updateFrequency,
                sendsVelocityUpdates);
    }
}
