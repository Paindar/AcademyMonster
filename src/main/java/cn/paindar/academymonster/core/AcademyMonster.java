package cn.paindar.academymonster.core;

import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.ability.*;
import cn.paindar.academymonster.ability.event.GlobalEventHandle;
import cn.paindar.academymonster.entity.EntityLoader;
import cn.paindar.academymonster.entity.ai.*;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import javafx.util.Pair;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    public static Configuration config;
    @Instance
    public static AcademyMonster instance;
    private static List<Pair<Class<? extends BaseAbility>,Float>> skillList=new ArrayList<>();
    private static List<Class<? extends EntityAIBase>> aiList=new ArrayList<>();
    private static List<Integer> aiLevelList=new ArrayList<>();
    private static float sumWeight=0f;

    private static void registerSkill(Class<? extends BaseAbility> skill,float prob,Class<? extends EntityAIBase> aiClass,int aiLevel)
    {
        skillList.add(new Pair(skill,prob));
        aiList.add(aiClass);
        aiLevelList.add(aiLevel);
        sumWeight+=prob;
    }
    static
    {
        registerSkill(AIBodyIntensify.class,1,EntityAIBodyIntensify.class,5);
        registerSkill(AIDirectedShock.class,2,EntityAIDirectedShock.class,5);
        registerSkill(AIElectronBomb.class,1,EntityAIElectronBomb.class,5);
        registerSkill(AIFleshRipping.class,1,EntityAIFleshRipping.class,5);
        registerSkill(AIPenetrateTeleport.class,1,EntityAIPenetrateTeleport.class,4);
        registerSkill(AIRailgun.class,0.2f,EntityAIRailgun.class,5);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        new EntityLoader();
        NetworkManager.init(event);
        config=new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        config.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new GlobalEventHandle());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
    }

    public void addSkill(EntityLiving entity)
    {
        if(entity.worldObj.isRemote)
           return;
        @SuppressWarnings("unchecked")
        List<Pair<Class<? extends BaseAbility>,Float>> tempList=new ArrayList(skillList);
        float prob=0.3f;
        float factor=0.5f;
        float tempSum=sumWeight;

        String string = entity.getEntityData().getString(MODID);
        while(RandUtils.nextFloat()<=prob && tempList.size()>0)
        {

            float rand=RandUtils.nextFloat()*tempSum;
            int id=-1;
            for(int i=0;i<tempList.size();i++)
            {
                if(rand<tempList.get(i).getValue())
                {
                    id=skillList.indexOf(tempList.get(i));
                    tempSum-=tempList.get(i).getValue();
                    tempList.remove(i);//抽取一个技能并且不放回

                    break;
                }
                rand-=tempList.get(i).getValue();
            }
            Class<? extends BaseAbility> elem=skillList.get(id).getKey();
            Constructor constructor;
            BaseAbility skill;
            Class aclass;
            Constructor[] tempConstructor;
            Class[] parameterTypes;
            float randExp=RandUtils.rangef(0, 1);
            try
            {
                constructor = elem.getConstructor(EntityLivingBase.class, float.class);
                skill = (BaseAbility) constructor.newInstance(entity, randExp * randExp);

                aclass = aiList.get(id);

                tempConstructor = aclass.getDeclaredConstructors();
                parameterTypes = tempConstructor[0].getParameterTypes();
                constructor = aclass.getConstructor(parameterTypes[0], parameterTypes[1]);

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
            string+=skill.getSkillName()+"\\s";
            prob*=factor;
        }
        entity.getEntityData().setString(MODID,string);
        //AcademyMonster.log.info("entity "+event.entity+" have ability:" +event.entity.getEntityData().getString(AcademyMonster.MODID));
    }


    private boolean isClassAllowed(EntityLiving entity)
    {
        if (entity instanceof EntityMob || (entity instanceof IMob))
        {
            if (entity instanceof IEntityOwnable)
            {
                return false;
            }
            if (instance.checkEntityClassAllowed(entity))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkEntityClassAllowed(EntityLivingBase entity)
    {
        return true;
    }


}
