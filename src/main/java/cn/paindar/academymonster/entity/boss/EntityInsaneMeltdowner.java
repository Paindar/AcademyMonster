package cn.paindar.academymonster.entity.boss;

import cn.academy.ability.ModuleAbility;
import cn.academy.vanilla.ModuleVanilla;
import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.entity.ai.EntityAIInsaneMeltdownerAttack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/3/16.
 */
public class EntityInsaneMeltdowner extends EntityMob implements IBossDisplayData
{
    public List<BaseSkill> skillList=new ArrayList<>();
    public EntityInsaneMeltdowner(World world)
    {
        super(world);
        this.setHealth(this.getMaxHealth());
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class,  0.6D, false));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.tasks.addTask(4, new EntityAIInsaneMeltdownerAttack(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 8, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        this.experienceValue = 50;

        skillList.add(new AIElectronBomb(this,0.5f+ RandUtils.rangef(0,0.5f)));
        skillList.add(new AIScatterBomb(this,0.25f+ RandUtils.rangef(0,0.75f)));
        skillList.add(new AIMeltdowner(this,0.5f+ RandUtils.rangef(0,0.5f)));
        skillList.add(new AIElectronMissile(this,RandUtils.rangef(0,1f)));
        StringBuilder info=new StringBuilder();
        for(BaseSkill skill:skillList)
        {
            float exp=skill.getSkillExp();
            info.append(skill.getUnlocalizedSkillName()+"~"+exp+"-");
        }
        SkillExtendedEntityProperties properties= SkillExtendedEntityProperties.get(this);
        properties.setSkillData(info.toString());
    }

    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        StringBuffer sbuffer=new StringBuffer();
        for(BaseSkill skill:skillList)
        {
            String name=skill.getClass().getName();
            sbuffer.append(name+"-");
            nbt.setFloat(name,skill.getSkillExp());
        }
        nbt.setString("allSkill",sbuffer.toString());
    }
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        skillList.clear();
        String[] list=nbt.getString("allSkill").split("-");
        StringBuilder info=new StringBuilder();
        for(String item:list)
        {
            try
            {
                Class klass=Class.forName(item);
                Constructor constructor;
                BaseSkill skill;
                float exp=nbt.getFloat(item);
                try
                {
                    constructor = klass.getConstructor(EntityLivingBase.class, float.class);
                    skill = (BaseSkill) constructor.newInstance(this, exp);
                    skillList.add(skill);
                    info.append(skill.getUnlocalizedSkillName()+"~"+exp+"-");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }

        }
        SkillExtendedEntityProperties properties= SkillExtendedEntityProperties.get(this);
        properties.setSkillData(info.toString());
    }

    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        this.dropItem(ModuleVanilla.silbarn, RandUtils.nextInt(12));
        if(RandUtils.nextFloat()<=1)
            this.entityDropItem(ModuleAbility.inductionFactor.create(ModuleVanilla.meltdowner),1);

    }

    protected boolean isAIEnabled()
    {
        return true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D);

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60);
    }

    public void mountEntity(Entity p_70078_1_)
    {
        this.ridingEntity = null;
    }
}
