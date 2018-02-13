package cn.paindar.academymonster.entity;

import cn.paindar.academymonster.ability.BaseSkill;
import cn.paindar.academymonster.core.AcademyMonster;
import cn.paindar.academymonster.core.SkillManager;
import cn.paindar.academymonster.entity.ai.EntityAIBaseX;
import cn.paindar.academymonster.entity.ai.EntityAIWander;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

import java.lang.ref.WeakReference;
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
    private WeakReference<EntityLivingBase> speller;
    public List<BaseSkill> list=new ArrayList<>();
    private int time=0;
    public SkillManager.Catalog catalog;
    private EntityAIBaseX ai=null;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase== TickEvent.Phase.START)
            return;
        time++;
        if(time>=10)
        {
            if(ai!=null)
                ai.execute(speller.get());
            time=0;
        }
        if(speller.get()==null || speller.get().isDead)
        {
            FMLCommonHandler.instance().bus().unregister(this);//free this class if possible.
            ai=null;
        }
    }

    public static SkillExtendedEntityProperties get(Entity e)
    {
        IExtendedEntityProperties info=e.getExtendedProperties(PROP_NAME);
        if (info == null)
        {
            info=new SkillExtendedEntityProperties((EntityLivingBase) e);
            e.registerExtendedProperties(PROP_NAME, info);
            FMLCommonHandler.instance().bus().register(info);//to line 50, maybe need free
        }
        return (SkillExtendedEntityProperties) info;
    }

    public SkillExtendedEntityProperties(){}

    SkillExtendedEntityProperties(EntityLivingBase e)
    {
        speller= new WeakReference<>(e);
    }
    public void setSkillData(String data)
    {
        skillData=data;
    }// used for AIM Scanner's info sync.
    public String getSkillData(){return skillData;}// used for AIM Scanner's info sync.
    public EntityLivingBase getSpeller(){return speller.get();}
    public void setAI(EntityAIBaseX ai)//update a AI, fired in initialization and update AI action.
    {
        this.ai=ai;
    }

    public void flushSkillData()//Not sure if it's useful in the future, keep it temporary.
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
        if(speller.get()==null || speller.get().worldObj.isRemote)
            return;
        String[] strList=skillData.split("-");
        for(String name:strList)
        {
            String[] skillInfo=name.split("~");
            float exp;
            if(skillInfo.length!=2)
            {
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
            BaseSkill skill = SkillManager.instance.createSkillInstance(skillInfo[0],speller.get(),exp);
            //SkillManager.instance.addSkillAI(skill,(EntityLiving) speller);
            if(skill!=null)list.add(skill);
        }
        setAI(new EntityAIWander());
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
        this.speller=new WeakReference<EntityLivingBase>((EntityLivingBase) entity);
    }

    /**
     * release all data about SEEP
     */
    public void release()
    {
        speller.clear();
        list.clear();
        ai=null;
    }
}
