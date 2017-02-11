package cn.paindar.academymonster.entity;

import cn.paindar.academymonster.ability.AIBodyIntensify;
import cn.paindar.academymonster.ability.AIElectronBomb;
import cn.paindar.academymonster.ability.AIPenetrateTeleport;
import cn.paindar.academymonster.ability.BaseAbility;
import cn.paindar.academymonster.entity.ai.EntityAIBodyIntensify;
import cn.paindar.academymonster.entity.ai.EntityAIElectronBomb;
import cn.paindar.academymonster.entity.ai.EntityAIPenetrateTeleport;
import cn.lambdalib.util.generic.RandUtils;
import javafx.util.Pair;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.lang.reflect.Constructor;


/**
 * Created by Paindar on 2017/2/9.
 */
public class EntityAcademyZombie extends EntityZombie implements IRangedAttackMob
{
    private static HashMap<Class<? extends EntityAIBase>,Integer> aiLevel=new HashMap<>();
    private static List<Pair<Class<? extends BaseAbility>,Class<? extends EntityAIBase>>> validSkillList=new ArrayList<>();
    static
    {
        validSkillList.add(new Pair<>(AIBodyIntensify.class, EntityAIBodyIntensify.class));
        aiLevel.put(EntityAIBodyIntensify.class,5);
        validSkillList.add(new Pair<>(AIElectronBomb.class, EntityAIElectronBomb.class));
        aiLevel.put(EntityAIElectronBomb.class,5);
        validSkillList.add(new Pair<>(AIPenetrateTeleport.class, EntityAIPenetrateTeleport.class));
        aiLevel.put(EntityAIPenetrateTeleport.class,4);
    }

    private final EntityAIBreakDoor aIBreakDoor = new EntityAIBreakDoor(this);
    private boolean canBreakDoor=false;
    private static float factor=0.5f;


    public EntityAcademyZombie(World world) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        super(world);
        Collections.shuffle(validSkillList);
        float prob=1f;
        int level=0;
        BaseAbility skill=null;
        EntityAIBase baseAI=null;
        Constructor constructor=null;
        while(RandUtils.nextFloat()<=prob)
        {
            if(level>=validSkillList.size())
                break;
            else
            {
                constructor=validSkillList.get(level).getKey().getConstructor(EntityLivingBase.class,float.class);
                skill=(BaseAbility)constructor.newInstance(this,1-RandUtils.rangef(0,1)*RandUtils.rangef(0,1));//动态生成技能对象
                Class aclass=validSkillList.get(level).getValue();
                Constructor[] tempconstructor=aclass.getDeclaredConstructors();
                Class[] parameterTypes=tempconstructor[0].getParameterTypes();
                constructor=validSkillList.get(level).getValue().getConstructor(parameterTypes[0],parameterTypes[1]);
                baseAI=(EntityAIBase)constructor.newInstance(this,skill);//动态生成怪物AI
                this.tasks.addTask(aiLevel.get(baseAI.getClass()),baseAI);//加入怪物AI至任务
                prob*=factor;

            }
            level++;
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
