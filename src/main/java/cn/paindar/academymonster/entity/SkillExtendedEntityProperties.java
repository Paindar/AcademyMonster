package cn.paindar.academymonster.entity;

import cn.paindar.academymonster.ability.BaseSkill;
import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.core.SkillManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/2/15.
 */
public class SkillExtendedEntityProperties implements IExtendedEntityProperties
{
    private static final String PROP_NAME= AcademyMonster.MODID;
    private String skillData="";
    public int level=0;
    private EntityLivingBase speller;
    public List<BaseSkill> list=new ArrayList<>();

    public static SkillExtendedEntityProperties get(Entity e)
    {
        IExtendedEntityProperties info=e.getExtendedProperties(PROP_NAME);
        if (info == null)
        {
            info=new SkillExtendedEntityProperties((EntityLivingBase) e);
            e.registerExtendedProperties(PROP_NAME, info);
        }
        return (SkillExtendedEntityProperties) info;
    }
    public SkillExtendedEntityProperties(){}

    SkillExtendedEntityProperties(EntityLivingBase e)
    {
        speller=e;
    }
    public void setSkillData(String data)
    {
        skillData=data;
        init();
    }
    public String getSkillData(){return skillData;}

    public void flushSkillData()
    {
        StringBuilder string=new StringBuilder();
        for(BaseSkill skill:list)
        {
            string.append(skill.getUnlocalizedSkillName()).append('~').append(skill.getSkillExp()).append('-');
        }
        skillData=string.toString();
    }


    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        NBTTagCompound propertyData;
        if(compound.hasKey(PROP_NAME, Constants.NBT.TAG_COMPOUND))
        {
            propertyData = compound.getCompoundTag(PROP_NAME);
        }
        else
            propertyData = new NBTTagCompound();
        propertyData.setString(AcademyMonster.MODID,skillData);
        compound.setTag(PROP_NAME, propertyData);
    }
    public void init()
    {
        if(speller.worldObj.isRemote)
            return;
        flushSkillData();
        String[] strList=skillData.split("-");
        for(String name:strList)
        {
            String[] skillInfo=name.split("~");
            float exp;
            if(skillInfo.length!=2)
            {
                AcademyMonster.log.warn("Entity "+speller+" has invalid skill info:"+name);
                continue;
            }
            try
            {
                exp=Float.parseFloat(skillInfo[1]);
            }
            catch(Exception e)
            {
                AcademyMonster.log.warn("Failed to translate "+speller + " in "+skillInfo[0]+"  "+skillInfo[1]);
                exp=0;
            }
            BaseSkill skill = SkillManager.instance.createSkillInstance(skillInfo[0],speller,exp);
            list.add(skill);
            SkillManager.instance.addSkillAI(skill,(EntityLiving) speller);
        }
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
        this.speller=(EntityLivingBase) entity;
    }
}
