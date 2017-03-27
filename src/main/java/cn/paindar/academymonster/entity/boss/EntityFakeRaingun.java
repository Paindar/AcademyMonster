package cn.paindar.academymonster.entity.boss;

import cn.academy.ability.ModuleAbility;
import cn.academy.vanilla.ModuleVanilla;
import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/3/7.
 */
public class EntityFakeRaingun extends EntityMob implements IBossDisplayData
{
    public EntityFakeRaingun(World p_i1738_1_)
    {
        super(p_i1738_1_);
        this.setHealth(this.getMaxHealth());
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class,  0.6D, false));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 8, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        this.experienceValue = 50;
        List<String> banList= AMConfig.getStringArray("am.monster."+this.getClass().getSimpleName()+".ban",new ArrayList<>());
        SkillExtendedEntityProperties data=SkillExtendedEntityProperties.get(this);
        if(!banList.contains("ArcGen"))
            data.list.add(new AIArcGen(this,0.5f+ RandUtils.rangef(0,0.5f)));
        if(!banList.contains("BodyIntensify"))
            data.list.add(new AIBodyIntensify(this,0.25f+ RandUtils.rangef(0,0.75f)));
        if(!banList.contains("Railgun"))
            data.list.add(new AIRailgun(this,0.5f+ RandUtils.rangef(0,0.5f)));
        if(!banList.contains("ThunderBolt"))
            data.list.add(new AIThunderBolt(this,RandUtils.rangef(0,1f)));
        if(!banList.contains("ThunderClap"))
            data.list.add(new AIThunderClap(this, RandUtils.rangef(0,1f)));
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

    }

    protected boolean isAIEnabled()
    {
        return true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60);
    }

    public void mountEntity(Entity p_70078_1_)
    {
        this.ridingEntity = null;
    }
}
