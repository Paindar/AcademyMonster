package cn.paindar.academymonster.entity;

import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.network.MessageSkillInfoSync;
import cn.paindar.academymonster.network.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

/**
 * Created by Paindar on 2017/2/15.
 */
public class SkillExtendedEntityProperties implements IExtendedEntityProperties
{
    public static final String PROP_NAME= AcademyMonster.MODID;
    private String skillData=new String();

    public static SkillExtendedEntityProperties get(Entity e)
    {
        IExtendedEntityProperties info=e.getExtendedProperties(PROP_NAME);
        if (info == null)
        {
            info=new SkillExtendedEntityProperties();
            e.registerExtendedProperties(PROP_NAME, info);
        }
        return (SkillExtendedEntityProperties) info;
    }

    public void setSkillData(String data)
    {
        skillData=data;
    }
    public String getSkillData(){return skillData;}


    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        NBTTagCompound propertyData = new NBTTagCompound();
        propertyData.setString(AcademyMonster.MODID,skillData)  ;
        compound.setTag(PROP_NAME, propertyData);
    }

    /**
     * Called when the entity that this class is attached to is loaded.
     * In order to hook into this, you will need to subscribe to the EntityConstructing event.
     * Otherwise, you will need to initialize manually.
     *
     * @param compound The compound to load from.
     */
    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        if(compound.hasKey(PROP_NAME, Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound propertyData = compound.getCompoundTag(PROP_NAME);
            skillData=propertyData.getString(AcademyMonster.MODID);
            // Read data from propertyData
        }
    }

    /**
     * Used to initialize the extended properties with the entity that this is attached to, as well
     * as the world object.
     * Called automatically if you register with the EntityConstructing event.
     * May be called multiple times if the extended properties is moved over to a new entity.
     * Such as when a player switches dimension {Minecraft re-creates the player entity}
     *
     * @param entity The entity that this extended properties is attached to
     * @param world  The world in which the entity exists
     */
    @Override
    public void init(Entity entity, World world)
    {

    }
}
