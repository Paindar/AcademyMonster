package cn.paindar.academymonster.core;

import cn.academy.ability.ModuleAbility;
import cn.academy.vanilla.ModuleVanilla;
import cn.lambdalib.util.generic.RandUtils;
import cn.paindar.academymonster.entity.SkillExtendedEntityProperties;
import cn.paindar.academymonster.network.NetworkManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Created by Paindar on 2017/2/12.
 * A test class used to spawn AcademyEntity.
 */
public class GlobalEventHandle
{
    public GlobalEventHandle()
    {
    }

    static boolean isClassAllowed(EntityLiving entity)
    {
        return (entity instanceof EntityMob || (entity instanceof IMob) || entity instanceof EntityGolem);
    }

    @SubscribeEvent
    public void onEntityJoinedWorld(EntityJoinWorldEvent  event)
    {
        if(!event.world.isRemote && event.entity instanceof EntityLiving && isClassAllowed((EntityLiving)event.entity))
        {
            SkillExtendedEntityProperties data = SkillExtendedEntityProperties.get(event.entity);
            String savedSkills=data.getSkillData();
            if(savedSkills.isEmpty())
            {
                SkillManager.instance.addSkill((EntityLiving)event.entity);
            }
            data.init();

            EntityTracker tracker = ((WorldServer)event.world).getEntityTracker();
            for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(event.entity)) {
                NetworkManager.sendEntitySkillInfoTo((EntityLiving)event.entity, (EntityPlayerMP)entityPlayer);
            }
        }
    }

    @SubscribeEvent
    public void playerStartedTracking(PlayerEvent.StartTracking e) {
        if(e.target instanceof EntityLiving)
        {
            SkillExtendedEntityProperties data = SkillExtendedEntityProperties.get(e.target);
            if (data != null)
            {
                NetworkManager.sendEntitySkillInfoTo((EntityLiving) e.target,(EntityPlayerMP) e.entityPlayer);
            }

        }
    }

    @SubscribeEvent
    public void onMonsterDied(LivingDeathEvent event)
    {
        if(! (event.entity instanceof EntityLiving) || event.entity instanceof IBossDisplayData)
            return;
        EntityLiving theDead=(EntityLiving)event.entity;
        SkillExtendedEntityProperties data=SkillExtendedEntityProperties.get(theDead);
        if(data.level>=3)
        {
            switch(data.catalog)
            {
                case electro:
                    if(RandUtils.nextFloat()<=-0.35+data.level*0.15)
                        theDead.entityDropItem(ModuleAbility.inductionFactor.create(ModuleVanilla.electromaster),1);
                    break;
                case meltdown:
                    if(RandUtils.nextFloat()<=-1+data.level*0.4)
                        theDead.entityDropItem(ModuleAbility.inductionFactor.create(ModuleVanilla.meltdowner),1);
                    break;
                case teleport:
                    if(RandUtils.nextFloat()<=-1+data.level*0.4)
                        theDead.entityDropItem(ModuleAbility.inductionFactor.create(ModuleVanilla.teleporter),1);
                    break;
                case vector:
                    if(RandUtils.nextFloat()<=-1+data.level*0.4)
                        theDead.entityDropItem(ModuleAbility.inductionFactor.create(ModuleVanilla.vecManip),1);
                    break;
            }
        }
    }

//    @SubscribeEvent
//    @SideOnly(Side.CLIENT)
//    public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event)
//    {
//        BossHealthBar.flushHealthBar(event);
//    }

}
