package cn.paindar.academyzombie.entity;

import cn.paindar.academyzombie.ability.AIPenetrateTeleport;
import cn.paindar.academyzombie.ability.BaseAbility;
import cn.paindar.academyzombie.core.AcademyZombie;
import cn.paindar.academyzombie.entity.ai.EntityAIPenetrateTeleport;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Paindar on 2017/2/9.
 */
public class EntityAcademyZombie extends EntityZombie implements IRangedAttackMob
{
    private final EntityAIBreakDoor aIBreakDoor = new EntityAIBreakDoor(this);
    private boolean canBreakDoor=false;
    public List<BaseAbility> abilityList=new ArrayList<>();

    public EntityAcademyZombie(World world) {
        super(world);
        abilityList.add(new AIPenetrateTeleport(1));
        this.tasks.addTask(5,new EntityAIPenetrateTeleport(this));
    }

    //设置是否能够破门而入
    public void func_146070_a(boolean p_146070_1_)
    {
        if (this.canBreakDoor != p_146070_1_)
        {
            this.canBreakDoor = p_146070_1_;

            if (p_146070_1_)
            {
                this.tasks.addTask(1, this.aIBreakDoor);
            }
            else
            {
                this.tasks.removeTask(this.aIBreakDoor);
            }
        }
    }



    @Override
    protected void dropFewItems(boolean arg1, int arg2)
    {
        super.dropFewItems(arg1,arg2);
    }

    /**
     * Attack the specified entity using a ranged attack.
     *
     * @param p_82196_1_
     * @param p_82196_2_
     */
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_)
    {

    }
}
