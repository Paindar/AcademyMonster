package cn.paindar.academymonster.entity.boss;

import cn.academy.ability.ModuleAbility;
import cn.academy.ability.item.ItemInductionFactor;
import cn.academy.crafting.ModuleCrafting;
import cn.academy.vanilla.ModuleVanilla;
import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.entity.ai.EntityAIFakeRailgunAttack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Paindar on 2017/3/7.
 */
public class EntityFakeRaingun extends EntityMob implements IBossDisplayData
{
    public List<BaseSkill> skillList=new ArrayList<>();
    public EntityFakeRaingun(World p_i1738_1_)
    {
        super(p_i1738_1_);
        this.setHealth(this.getMaxHealth());
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIFakeRailgunAttack(this));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        this.experienceValue = 50;
        skillList.add(new AIThunderClap(this,0.5f+ RandUtils.rangef(0,0.5f)));
        skillList.add(new AIRailgun(this,0.5f+ RandUtils.rangef(0,0.5f)));
        skillList.add(new AIThunderBolt(this,0.5f+ RandUtils.rangef(0,0.5f)));
        skillList.add(new AIBodyIntensify(this,0.5f+ RandUtils.rangef(0,0.5f)));
        skillList.add(new AIArcGen(this,0.5f+ RandUtils.rangef(0,0.5f)));
    }

    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("ThunderClap",skillList.get(0).getSkillExp());
        nbt.setFloat("Railgun",skillList.get(1).getSkillExp());
        nbt.setFloat("ThunderBolt",skillList.get(2).getSkillExp());
        nbt.setFloat("BodyIntensify",skillList.get(3).getSkillExp());
        nbt.setFloat("ArcGen",skillList.get(4).getSkillExp());
    }


    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        skillList.clear();
        skillList.add(new AIThunderClap(this,nbt.getFloat("ThunderClap")));
        skillList.add(new AIRailgun(this,nbt.getFloat("Railgun")));
        skillList.add(new AIThunderBolt(this,nbt.getFloat("ThunderBolt")));
        skillList.add(new AIBodyIntensify(this,nbt.getFloat("BodyIntensify")));
        skillList.add(new AIArcGen(this,nbt.getFloat("ArcGen")));
    }

    public int getTotalArmorValue()
    {
        return 4;
    }

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        this.dropItem(Items.iron_ingot, RandUtils.nextInt(5));
        this.dropItem(Item.getItemFromBlock(Blocks.iron_block), RandUtils.nextInt(2));
        this.dropItem(ModuleVanilla.coin,RandUtils.nextInt(5)+3);
        if(RandUtils.nextFloat()<=1)
            this.dropItem(ModuleAbility.inductionFactor.create(ModuleVanilla.electromaster).getItem(),1);

    }

    protected boolean isAIEnabled()
    {
        return true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.50D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60);
    }

    public void mountEntity(Entity p_70078_1_)
    {
        this.ridingEntity = null;
    }
}
