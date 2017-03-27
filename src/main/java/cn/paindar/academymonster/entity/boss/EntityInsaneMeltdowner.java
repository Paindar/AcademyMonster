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
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paindar on 2017/3/16.
 */
public class EntityInsaneMeltdowner extends EntityMob implements IBossDisplayData
{
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
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 8, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        this.experienceValue = 50;

        List<String> banList= AMConfig.getStringArray("am.monster."+this.getClass().getSimpleName()+".ban",new ArrayList<>());
        SkillExtendedEntityProperties data= SkillExtendedEntityProperties.get(this);
        //never checked
        if(!banList.contains("ElectronBomb"))
            data.list.add(new AIElectronBomb(this,0.5f+ RandUtils.rangef(0,0.5f)));
        if(!banList.contains("ScatterBomb"))
            data.list.add(new AIScatterBomb(this,0.25f+ RandUtils.rangef(0,0.75f)));
        if(!banList.contains("Meltdowner"))
            data.list.add(new AIMeltdowner(this,0.5f+ RandUtils.rangef(0,0.5f)));
        if(!banList.contains("ElectronMissile"))
            data.list.add(new AIElectronMissile(this,RandUtils.rangef(0,1f)));
    }

    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
        this.dropItem(ModuleVanilla.silbarn, RandUtils.nextInt(12));

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
