package cn.paindar.academymonster.core;

import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.entity.ai.*;
import cn.paindar.academymonster.entity.boss.EntityFakeRaingun;
import cn.paindar.academymonster.entity.boss.EntityInsaneMeltdowner;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import java.lang.reflect.Constructor;
import java.util.*;

import static cn.lambdalib.util.generic.RandUtils.rangef;

/**
 * Created by Paindar on 2017/3/25.
 */
public class SkillManager
{
    class SkillInfo
    {
        Class<? extends BaseSkill> klass;
        float prob;
        int lvl;
        String name;
        Catalog type;

        @Override
        public String toString()
        {
            return String.valueOf(klass) + " prob=" + prob +
                     " level = " + lvl + " skill name = " + name +
                    " catalog type = " + type + " at " + super.toString();
        }
    }
    public enum Catalog{vector,meltdown,electro,teleport}
    public static SkillManager instance=new SkillManager();
    private static List<SkillInfo> list=new ArrayList<>();
    public Map<Integer,BaseSkill> effectMgr=new HashMap<>();

    private SkillManager(){}

    public BaseSkill createSkillInstance(String skillName, EntityLivingBase speller, float exp)
    {
        BaseSkill skill;
        Class<? extends BaseSkill> skillClass=null;
        for(SkillInfo info:list)
        {
            if(info.name.equals(skillName))
            {
                skillClass=info.klass;
                break;
            }
        }
        if(skillClass==null)
            return null;
        Constructor constructor;
        try
        {
            constructor = skillClass.getConstructor(EntityLivingBase.class, float.class);
            skill = (BaseSkill) constructor.newInstance(speller,exp);
            return skill;
        }
        catch(Exception e)
        {
            AcademyMonster.log.error("No such constructor: (EntityLivingBase.class, float.class)");
            e.printStackTrace();
        }
        return null;
    }

    private void registerSkill(Class<? extends BaseSkill> skill,float defaultProb,int skillLevel,Catalog type)
    {
        float prob=(float) AMConfig.getDouble("am.skill."+skill.getSimpleName().substring(2)+".prob",defaultProb);
        if (prob<=1e-6)
            return ;
        SkillInfo info=new SkillInfo();
        info.klass=skill;
        info.prob=defaultProb;
        info.lvl=skillLevel;
        info.type=type;

        Constructor constructor;
        BaseSkill scill;
        try
        {
            constructor = skill.getConstructor(EntityLivingBase.class, float.class);
            scill = (BaseSkill) constructor.newInstance(null,0);
            info.name=scill.getUnlocalizedSkillName();
        }
        catch(Exception e)
        {
            AcademyMonster.log.error("No such constructor: (EntityLivingBase.class, float.class)");
            e.printStackTrace();
        }
        list.add(info);
    }
    void initSkill()
    {
        registerSkill(AIArcGen.class,1,1,Catalog.electro);
        registerSkill(AIBodyIntensify.class, 1,2,Catalog.electro);
        registerSkill(AIBloodRetrograde.class,0.7f,3,Catalog.vector);
        registerSkill(AIDirectedShock.class, 2,1,Catalog.vector);
        registerSkill(AIElectronBomb.class, 1,1,Catalog.meltdown);
        registerSkill(AIElectronMissile.class,0.1f,5,Catalog.meltdown);
        //registerSkill(AIFlashing.class,0.2f,5,5,Catalog.teleport);
        registerSkill(AIFleshRipping.class, 1,3,Catalog.teleport);
        registerSkill(AIGroundShock.class, 1f,2,Catalog.vector);
        //registerSkill(AILightShield.class,1f,5,2,Catalog.meltdown);
        registerSkill(AILocationTeleport.class,0.7f,3,Catalog.teleport);
        registerSkill(AIMeltdowner.class,0.1f,4,Catalog.meltdown);
        //registerSkill(AIMineRay.class,0.7f,5,3,Catalog.meltdown);
        registerSkill(AIPenetrateTeleport.class, 1,2,Catalog.teleport);
        registerSkill(AIRailgun.class, 0.3f,4,Catalog.electro);
        registerSkill(AIThunderBolt.class,0.7f,4,Catalog.electro);
        registerSkill(AIThunderClap.class,0.4f,5,Catalog.electro);
        registerSkill(AIVecReflect.class, 0.3f,3,Catalog.vector);
        registerSkill(AIScatterBomb.class,0.5f,2,Catalog.meltdown);
        //registerSkill(AIShiftTeleport.class,0.7f,5,3,Catalog.teleport);
        list.sort((a,b)->(a.type!=b.type)?(a.type.ordinal()<b.type.ordinal()?-1:1):(a.lvl!=b.lvl?(a.lvl<b.lvl?-1:1):0));
    }


    public void addSkill(EntityLiving entity)
    {
        if(entity.worldObj.isRemote)
            return;
        List<String> banList=AMConfig.getStringArray("am.monster."+entity.getClass().getSimpleName()+".ban",new ArrayList<>());
        StringBuilder builder=new StringBuilder();
        if(entity instanceof EntityFakeRaingun)
        {
            SkillExtendedEntityProperties data=SkillExtendedEntityProperties.get(entity);
            if(!banList.contains("ArcGen"))
               builder.append("ac.ability.electromaster.arc_gen.name").append('~').append(rangef(0,0.5f)).append('-');
            if(!banList.contains("BodyIntensify"))
                builder.append("ac.ability.electromaster.body_intensify.name").append('~').append(0.25f+ rangef(0,0.75f)).append('-');
            if(!banList.contains("Railgun"))
                builder.append("ac.ability.electromaster.railgun.name").append('~').append(0.5f+ rangef(0,0.5f)).append('-');
            if(!banList.contains("ThunderBolt"))
                builder.append("ac.ability.electromaster.thunder_bolt.name").append('~').append(rangef(0,1f)).append('-');
            if(!banList.contains("ThunderClap"))
                builder.append("ac.ability.electromaster.thunder_clap.name").append('~').append(0.5f+ rangef(0,1f)).append('-');
            data.setSkillData(builder.toString());
        }
        else if(entity instanceof EntityInsaneMeltdowner)
        {
            SkillExtendedEntityProperties data= SkillExtendedEntityProperties.get(entity);
            //unchecked//
            if(!banList.contains("ElectronBomb"))
                builder.append("ac.ability.meltdowner.electron_bomb.name").append('~').append(0.5f+ rangef(0,0.5f)).append('-');
            if(!banList.contains("BodyIntensify"))
                builder.append("ac.ability.meltdowner.scatter_bomb.name").append('~').append(0.25f+ rangef(0,0.75f)).append('-');
            if(!banList.contains("Railgun"))
                builder.append("ac.ability.meltdowner.meltdowner.name").append('~').append(0.5f+ rangef(0,0.5f)).append('-');
            if(!banList.contains("ThunderBolt"))
                builder.append("ac.ability.meltdowner.electron_missile.name").append('~').append(rangef(0,1f)).append('-');
            data.setSkillData(builder.toString());
        }
        else
        {
            double prob=AMConfig.getDouble("am.skill.prob",0.3f);
            double factor=AMConfig.getDouble("am.skill.factor",0.5f);
            double sumWeight=0;
            Catalog[] logs = Catalog.values();
            Catalog type = logs[RandUtils.nextInt(logs.length)];

            int level = 1, last = 0, mark = 0;
            List<SkillInfo> filtList = new ArrayList<>();
            SkillExtendedEntityProperties data = SkillExtendedEntityProperties.get(entity);
            data.catalog = type;
            while (prob >= RandUtils.nextFloat())
            {
                prob *= factor;
                SkillInfo info;
                if (level != last)
                {
                    for (; mark < list.size(); mark++)
                    {
                        info = list.get(mark);
                        if (info.type == type && !banList.contains(info.klass.getSimpleName().substring(2)))
                        {
                            if (info.lvl <= level)
                            {
                                filtList.add(info);
                                sumWeight += info.prob;
                            }
                            else
                                break;
                        }
                    }
                }//flush available skill list
                if (filtList.isEmpty())
                {
                    if (mark >= list.size())
                    {
                        break;
                    } else
                    {
                        level++;
                        prob /= factor;
                        continue;
                    }
                }

                int index = 0;
                info = filtList.get(0);
                double p = RandUtils.ranged(0, sumWeight);
                while (filtList.size() > index)
                {
                    info = filtList.get(index);
                    if (p < info.prob)
                        break;
                    p -= info.prob;
                    index++;
                }
                filtList.remove(index);
                sumWeight-=info.prob;
                last = info.lvl;
                if (info.lvl == level)
                    level++;

                float randExp = RandUtils.nextFloat();
                randExp = 0.01f + randExp * randExp;
                builder.append(info.name).append('~').append(randExp).append('-');
            }
            data.setSkillData(builder.toString());
            data.level = level - 1;
        }
    }

}
