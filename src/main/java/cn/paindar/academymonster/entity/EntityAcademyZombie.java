package cn.paindar.academymonster.entity;

import cn.paindar.academymonster.ability.AIElectronBomb;
import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import cn.paindar.academymonster.ability.BaseAbility;
import cn.paindar.academymonster.entity.ai.EntityAIElectronBomb;
import cn.paindar.academymonster.entity.ai.EntityAIPenetrateTeleport;
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
//        abilityList.add(new AIPenetrateTeleport(this,1-RandUtils.rangef(0,1)*RandUtils.rangef(0,1)));
        abilityList.add(new AIElectronBomb(this,1));
        for(BaseAbility skill:abilityList)
        {
            if(skill instanceof AIElectronBomb)
                this.tasks.addTask(4,new EntityAIElectronBomb(this,(AIElectronBomb)skill));
            else if (skill instanceof AIPenetrateTeleport)
                this.tasks.addTask(5,new EntityAIPenetrateTeleport(this,(AIPenetrateTeleport)skill));
        }

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
