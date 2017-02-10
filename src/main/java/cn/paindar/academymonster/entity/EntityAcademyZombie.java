package cn.paindar.academymonster.entity;

import cn.paindar.academymonster.ability.AIElectronBomb;
import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import cn.paindar.academymonster.ability.BaseAbility;
import cn.paindar.academymonster.entity.ai.EntityAIElectronBomb;
import cn.paindar.academymonster.entity.ai.EntityAIPenetrateTeleport;
import cn.lambdalib.util.generic.RandUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.lang.reflect.Constructor;


/**
 * Created by Paindar on 2017/2/9.
 */
public class EntityAcademyZombie extends EntityZombie implements IRangedAttackMob
{
    private final EntityAIBreakDoor aIBreakDoor = new EntityAIBreakDoor(this);
    private boolean canBreakDoor=false;
    private HashMap<BaseAbility,String> AbilityMap=new HashMap<BaseAbility,String>();
    public EntityAcademyZombie(World world) {
        super(world);
        AbilityMap.put(new AIPenetrateTeleport(this,1-RandUtils.rangef(0,1)*RandUtils.rangef(0,1)),"4～EntityAIPenetrateTeleport");
        AbilityMap.put(new AIElectronBomb(this,1),"5～EntityAIElectronBomb");//按照类似格式添加进map   4～指的是优先级4
        //abilityList.add(new AIPenetrateTeleport(this,1-RandUtils.rangef(0,1)*RandUtils.rangef(0,1)));
        //abilityList.add(new AIElectronBomb(this,1));
        BaseAbility skill=null;
        EntityAIBase BaseAI;
        String AbilityType=new String(AbilityMap.get(skill));
        if (AbilityType.equals(null))
        {

        }
        else
        {
            String mid[]=new String[2];
            mid=AbilityType.split("~");
            try
            {
                Class temp1=Class.forName("cn.painder.academymonster.ability."+mid[1]);
                Constructor constructor1=temp1.getConstructor(Object.class,Double.class);
                skill=(BaseAbility)constructor1.newInstance(this,1-RandUtils.rangef(0,1)*RandUtils.rangef(0,1));//动态生成技能对象

                Class temp2=Class.forName("cn.painder.academymonster.entity.ai.Entity"+mid[1]);
                Constructor constructor2=temp2.getConstructor(Object.class,Object.class);
                BaseAI=(EntityAIBase)constructor2.newInstance(this,skill);//动态生成怪物AI

                this.tasks.addTask(Integer.valueOf(mid[0]),BaseAI);//加入怪物AI至任务
            }catch (Exception e)
            {
                e.printStackTrace();
            }
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
