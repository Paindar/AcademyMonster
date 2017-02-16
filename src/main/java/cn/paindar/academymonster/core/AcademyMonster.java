package cn.paindar.academymonster.core;

import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.config.AMConfig;
import cn.paindar.academymonster.core.command.CommandTest;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.entity.ai.*;
import com.typesafe.config.Config;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Paindar on 2017/2/9.
 */

@Mod(modid =AcademyMonster.MODID , name = AcademyMonster.NAME, version = AcademyMonster.VERSION,
        dependencies = "required-after:LambdaLib@@LL_VERSION@") // LambdaLib is currently unstable. Supports only one version.
public class AcademyMonster
{
    public static final String MODID = "academy-monster";
    public static final String NAME = "Academy Monster";
    public static final String VERSION = "@VERSION@";
    public static final Logger log = LogManager.getLogger("AcademyMonster");
    @SidedProxy(clientSide = "cn.paindar.academymonster.core.ClientProxy",
            serverSide = "cn.paindar.academymonster.core.CommonProxy")
    public static CommonProxy proxy;
    @Instance
    public static AcademyMonster instance;
    private static List<Class<? extends BaseSkill>> skillList=new ArrayList<>();
    private static List<Float> probList=new ArrayList<>();
    private static List<Class<? extends EntityAIBase>> aiList=new ArrayList<>();
    private static List<Integer> aiLevelList=new ArrayList<>();
    private static float sumWeight=0f;



    private static void registerSkill(Class<? extends BaseSkill> skill,float defaultProb,Class<? extends EntityAIBase> aiClass,int aiLevel)
    {
        skillList.add(skill);
        float prob=(float)AMConfig.getDouble("am.skill."+skill.getSimpleName().substring(2)+".prob",defaultProb);
        probList.add(prob);//getSimpleName().substring(2)
        aiList.add(aiClass);
        aiLevelList.add(aiLevel);
        sumWeight+=prob;
    }
    void initSkill()
    {
        registerSkill(AIBodyIntensify.class, 1,EntityAIBodyIntensify.class,5);
        registerSkill(AIDirectedShock.class, 2,EntityAIDirectedShock.class,5);
        registerSkill(AIElectronBomb.class, 1,EntityAIElectronBomb.class,5);
        registerSkill(AIFleshRipping.class, 1,EntityAIFleshRipping.class,5);
        registerSkill(AIPenetrateTeleport.class, 2,EntityAIPenetrateTeleport.class,4);
        registerSkill(AIRailgun.class, 0.5f,EntityAIRailgun.class,5);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);


    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandTest());
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
    }

    public void addSkill(EntityLiving entity)
    {
        if(entity.worldObj.isRemote ||!isClassAllowed(entity))
           return;
        List<Class<? extends BaseSkill>> tempList= new ArrayList<>(skillList);
        List<Float> tempProbList=new ArrayList<>(probList);
        double prob=AMConfig.getDouble("am.skill.prob",0.3f);
        double factor=AMConfig.getDouble("am.skill.factor",0.5f);
        float tempSum=sumWeight;
        List<String> banList=AMConfig.getStringArray("am.monster."+entity.getClass().getSimpleName()+".ban",new ArrayList<>());

        String string = entity.getEntityData().getString(MODID);
        while(RandUtils.nextFloat()<=prob && tempList.size()>0)
        {
            float rand=RandUtils.nextFloat()*tempSum;
            int id=-1;
            for(int i=0;i<tempProbList.size();i++)
            {
                if(rand<tempProbList.get(i))
                {
                    id=skillList.indexOf(tempList.get(i));
                    if(banList.indexOf(skillList.get(id).getSimpleName().substring(2))==-1)
                    {
                        tempSum -= tempProbList.get(i);
                    }
                    else
                    {
                        id=-1;
                    }
                    tempList.remove(i);
                    tempProbList.remove(i);//抽取一个技能并且不放回
                    break;
                }
                rand-=tempProbList.get(i);
            }
            if(id==-1)
                continue;
            Class<? extends BaseSkill> elem=skillList.get(id);
            Constructor constructor;
            BaseSkill skill;
            Class<? extends EntityAIBase> aClass;
            Constructor[] tempConstructor;
            Class[] parameterTypes;
            float randExp=RandUtils.rangef(0, 1);
            try
            {
                constructor = elem.getConstructor(EntityLivingBase.class, float.class);
                skill = (BaseSkill) constructor.newInstance(entity, randExp * randExp);

                aClass = aiList.get(id);

                tempConstructor = aClass.getDeclaredConstructors();
                parameterTypes = tempConstructor[0].getParameterTypes();
                constructor = aClass.getConstructor(parameterTypes[0], parameterTypes[1]);

            }
            catch(Exception e)
            {
                AcademyMonster.log.error("No such constructor: (EntityLivingBase.class, float.class)");
                e.printStackTrace();
                throw new RuntimeException();
            }
            EntityAIBase baseAI;
            try{baseAI=(EntityAIBase)constructor.newInstance(entity,skill);}
            catch (Exception e)
            {
                AcademyMonster.log.error("param1: " + parameterTypes[0] + " , param2:" + parameterTypes[1]);
                AcademyMonster.log.error("Argument: " + entity + " , " + skill);
                e.printStackTrace();
                throw new RuntimeException();
            }
            entity.tasks.addTask(aiLevelList.get(id),baseAI);//加入怪物AI至任务
            string+= BaseSkill.getUnlocalizedSkillName()+"~"+randExp+"-";
            prob*=factor;
        }
        SkillExtendedEntityProperties info= SkillExtendedEntityProperties.get(entity);
        info.setSkillData(string);
        //AcademyMonster.log.info("entity "+entity+" have ability:" +entity.getEntityData().getString(AcademyMonster.MODID));
    }

    public void refreshSkills(EntityLiving entity,String skillStr)
    {
        String[] strList=skillStr.split("-");

        for(Class<? extends BaseSkill> skillClass:skillList)
        {
            try
            {

                Method method=skillClass.getMethod("getUnlocalizedSkillName");
                String name=(String) method.invoke(skillClass);
                for(String item:strList)
                {
                    String[] skillInfo=item.split("~");
                    if(skillInfo[0].equals(name))
                    {
                        Constructor constructor;
                        BaseSkill skill;
                        Class<? extends EntityAIBase> aClass;
                        Constructor[] tempConstructor;
                        Class[] parameterTypes;
                        float randExp=Float.parseFloat(skillInfo[1]);
                        int id=skillList.indexOf(skillClass);
                        try
                        {
                            constructor = skillClass.getConstructor(EntityLivingBase.class, float.class);
                            skill = (BaseSkill) constructor.newInstance(entity, randExp);

                            aClass = aiList.get(id);

                            tempConstructor = aClass.getDeclaredConstructors();
                            parameterTypes = tempConstructor[0].getParameterTypes();
                            constructor = aClass.getConstructor(parameterTypes[0], parameterTypes[1]);

                        }
                        catch(Exception e)
                        {
                            AcademyMonster.log.error("No such constructor: (EntityLivingBase.class, float.class)");
                            e.printStackTrace();
                            throw new RuntimeException();
                        }
                        EntityAIBase baseAI;
                        try{baseAI=(EntityAIBase)constructor.newInstance(entity,skill);}
                        catch (Exception e)
                        {
                            AcademyMonster.log.error("param1: " + parameterTypes[0] + " , param2:" + parameterTypes[1]);
                            AcademyMonster.log.error("Argument: " + entity + " , " + skill);
                            e.printStackTrace();
                            throw new RuntimeException();
                        }
                        entity.tasks.addTask(aiLevelList.get(id),baseAI);//加入怪物AI至任务
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException();
            }

        }
    }


    private boolean isClassAllowed(EntityLiving entity)
    {
        if (entity instanceof EntityMob || (entity instanceof IMob))
        {
            if (entity instanceof IEntityOwnable)
            {
                return false;
            }
            if (checkEntityClassAllowed(entity))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkEntityClassAllowed(EntityLiving entity)
    {
        return entity instanceof EntityMob;
    }


}
