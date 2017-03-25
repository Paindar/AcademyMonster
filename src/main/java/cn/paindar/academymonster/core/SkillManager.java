package cn.paindar.academymonster.core;

import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.entity.ai.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created by Paindar on 2017/3/25.
 */
public class SkillManager
{
    class SkillInfo
    {
        Class<? extends BaseSkill> klass;
        float prob;
        Class<? extends EntityAIBase> aiType;
        int prop;
        int lvl;
        String name;
        Catalog type;

        @Override
        public String toString()
        {
            StringBuilder builder=new StringBuilder();
            builder.append(klass).append(" prob=").append(prob).append(" AIType= ").append(aiType)
                    .append(" prop= ").append(prop).append(" level = ").append(lvl).append(" skill name = ").append(name)
                    .append(" catalog type = ").append(type).append(" at ").append(super.toString());
            return builder.toString();
        }
    }
    enum Catalog{vector,meltdown,electro,teleport}
    public static SkillManager instance=new SkillManager();
    private static List<SkillInfo> list=new ArrayList<>();

    private SkillManager(){}

    public void addSkillAI(BaseSkill skill,EntityLiving entity)
    {
        if(entity.worldObj.isRemote)
            return;
        Constructor constructor=null;
        Class<? extends EntityAIBase> aClass;
        Constructor[] tempConstructor;
        Class[] parameterTypes=new Class[2];
        SkillInfo info=null;
        for(int i=0;i<list.size();i++)
        {
            info=list.get(i);
            if(info.name.equals(skill.getUnlocalizedSkillName()))
                break;
        }
        try
        {
            aClass = info.aiType;
            if(aClass!=null)
            {
                tempConstructor = aClass.getDeclaredConstructors();
                parameterTypes = tempConstructor[0].getParameterTypes();
                constructor = aClass.getConstructor(parameterTypes[0], parameterTypes[1]);
            }
            else
                return;
        }
        catch(Exception e)
        {
            AcademyMonster.log.error("No such constructor: (EntityLivingBase.class, float.class)");
            e.printStackTrace();
        }
        if(constructor==null)
            return;
        EntityAIBase baseAI=null;
        try
        {
            baseAI = (EntityAIBase) constructor.newInstance(entity, skill);
        }
        catch (Exception e)
        {
            AcademyMonster.log.error("param1: " + parameterTypes[0] + " , param2:" + parameterTypes[1]);
            AcademyMonster.log.error("Argument: " + entity + " , " + skill);
            e.printStackTrace();
        }
        entity.tasks.addTask(info.prop, baseAI);
    }
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

    private void registerSkill(Class<? extends BaseSkill> skill,float defaultProb,Class<? extends EntityAIBase> aiClass,int aiLevel,int skillLevel,Catalog type)
    {
        float prob=(float) AMConfig.getDouble("am.skill."+skill.getSimpleName().substring(2)+".prob",defaultProb);
        if (prob<=1e-6)
            return ;
        SkillInfo info=new SkillInfo();
        info.klass=skill;
        info.prob=defaultProb;
        info.aiType=aiClass;
        info.prop=aiLevel;
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
        registerSkill(AIArcGen.class,1,EntityAIArcGen.class,5,1,Catalog.electro);
        registerSkill(AIBodyIntensify.class, 1,EntityAIBodyIntensify.class,5,2,Catalog.electro);
        registerSkill(AIBloodRetrograde.class,0.7f,EntityAIBloodRetrograde.class,5,3,Catalog.vector);
        registerSkill(AIDirectedShock.class, 2,EntityAIDirectedShock.class,5,1,Catalog.vector);
        registerSkill(AIElectronBomb.class, 1,EntityAIElectronBomb.class,5,1,Catalog.meltdown);
        registerSkill(AIElectronMissile.class,0.1f,EntityAIElectronMissile.class,5,5,Catalog.meltdown);
        registerSkill(AIFlashing.class,0.2f,EntityAIFlashing.class,5,5,Catalog.teleport);
        registerSkill(AIFleshRipping.class, 1,EntityAIFleshRipping.class,5,3,Catalog.teleport);
        registerSkill(AIGroundShock.class, 1f,EntityAIGroundShock.class,5,2,Catalog.vector);
        registerSkill(AILightShield.class,1f,EntityAILightShield.class,5,2,Catalog.meltdown);
        registerSkill(AILocationTeleport.class,0.7f,EntityAILocationTeleport.class,5,3,Catalog.teleport);
        registerSkill(AIMeltdowner.class,0.1f,EntityAIMeltdowner.class,5,4,Catalog.meltdown);
        registerSkill(AIMineRay.class,0.7f,EntityAIMineRay.class,5,3,Catalog.meltdown);
        registerSkill(AIPenetrateTeleport.class, 1,EntityAIPenetrateTeleport.class,4,2,Catalog.teleport);
        registerSkill(AIRailgun.class, 0.3f,EntityAIRailgun.class,5,4,Catalog.electro);
        registerSkill(AIThunderBolt.class,0.7f,EntityAIThunderBlot.class,5,4,Catalog.electro);
        registerSkill(AIThunderClap.class,0.4f,EntityAIThunderClap.class,5,5,Catalog.electro);
        registerSkill(AIVecReflect.class, 0.3f,null,5,3,Catalog.vector);
        registerSkill(AIScatterBomb.class,0.5f,EntityAIScatterBomb.class,5,2,Catalog.meltdown);
        registerSkill(AIShiftTeleport.class,0.7f,EntityAIShiftTeleport.class,5,3,Catalog.teleport);
        list.sort((a,b)->(a.type!=b.type)?(a.type.ordinal()<b.type.ordinal()?-1:1):(a.lvl!=b.lvl?(a.lvl<b.lvl?-1:1):0));
    }


    public void addSkill(EntityLiving entity)
    {
        if(entity.worldObj.isRemote)
            return;
        double prob=AMConfig.getDouble("am.skill.prob",0.3f);
        double factor=AMConfig.getDouble("am.skill.factor",0.5f);
        double sumWeight=0;

        Catalog[] logs=Catalog.values();
        Catalog type=logs[RandUtils.nextInt(logs.length)];

        int level=1,last=0,mark=0;
        List<SkillInfo> filtList=new ArrayList<>();
        BaseSkill skill;
        SkillExtendedEntityProperties data=SkillExtendedEntityProperties.get(entity);
        while(prob>=RandUtils.nextFloat())
        {
            prob*=factor;
            SkillInfo info;
            if(level!=last)
            {
                for(;mark<list.size();mark++)
                {
                    info=list.get(mark);
                    if(info.type==type)
                    {
                        if(info.lvl<=level)
                        {
                            filtList.add(info);
                            sumWeight+=info.prob;
                        }
                        else
                            break;
                    }
                }
            }
            if(filtList.isEmpty())
            {
                if(mark>=list.size())
                {
                    break;
                }
                else
                {
                    level++;
                    prob/=factor;
                    continue;
                }
            }

            int index=0;
            info=filtList.get(0);
            double p=RandUtils.ranged(0,sumWeight);
            while(filtList.size()>index)
            {
                info=filtList.get(index);
                if (p < info.prop)
                    break;
                p-=info.prob;
                index++;
            }
            filtList.remove(index);
            last=info.lvl;
            if(info.lvl==level)
                level++;
            Class<? extends BaseSkill> skillClass=info.klass;
            if(skillClass==null)
                continue;
            Constructor constructor;
            float randExp=RandUtils.nextFloat();
            try
            {
                constructor = skillClass.getConstructor(EntityLivingBase.class, float.class);
                randExp=0.01f+randExp*randExp;
                skill = (BaseSkill) constructor.newInstance(entity,randExp);
                data.list.add(skill);
            }
            catch(Exception e)
            {
                AcademyMonster.log.error("No such constructor: (EntityLivingBase.class, float.class)");
                e.printStackTrace();
            }
        }
        data.level=level-1;
    }

}
