package cn.paindar.academymonster.playerskill.electromaster;

import cn.academy.ability.api.AbilityContext;
import cn.academy.ability.api.Skill;
import cn.academy.ability.api.context.ClientRuntime;
import cn.academy.ability.api.context.KeyDelegate;
import cn.academy.ability.api.data.CPData;
import cn.academy.ability.api.data.PresetData;
import cn.academy.core.event.BlockDestroyEvent;
import cn.academy.vanilla.electromaster.client.effect.RailgunHandEffect;
import cn.academy.vanilla.electromaster.entity.EntityCoinThrowing;
import cn.academy.vanilla.electromaster.event.CoinThrowEvent;
import cn.lambdalib.s11n.network.TargetPoints;
import cn.lambdalib.s11n.network.NetworkMessage;
import cn.lambdalib.s11n.network.NetworkMessage.Listener;
import cn.lambdalib.util.client.renderhook.DummyRenderData;
import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.generic.RandUtils;
import cn.lambdalib.util.generic.VecUtils;
import cn.lambdalib.util.helper.Motion3D;
import cn.lambdalib.util.mc.EntitySelectors;
import cn.lambdalib.util.mc.SideHelper;
import cn.lambdalib.util.mc.WorldUtils;
import cn.paindar.academymonster.entity.EntityRailgunFXNative;
import cn.paindar.academymonster.events.RayShootingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.function.Predicate;

import static cn.lambdalib.util.generic.MathUtils.lerpf;
import static cn.lambdalib.util.generic.VecUtils.*;

public class Railgun extends Skill {

    public static final Railgun instance = new Railgun();

    private static final String
            MSG_CHARGE_EFFECT = "charge_eff",
            MSG_PERFORM       = "perform",
            MSG_REFLECT       = "reflect",
            MSG_COIN_PERFORM  = "coin_perform",
            MSG_ITEM_PERFORM  = "item_perform";

    private static final double
            REFLECT_DISTANCE = 15;
    private static final float range = 2;
    private static final float STEP=.5F;

    private Set<Item> acceptedItems = new HashSet<>();
    {
        acceptedItems.add(Items.iron_ingot);
        acceptedItems.add(Item.getItemFromBlock(Blocks.iron_block));
    }

    private Railgun() {
        super("railgun", 4);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private boolean isAccepted(ItemStack stack) {
        return stack != null && acceptedItems.contains(stack.getItem());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void activate(ClientRuntime rt, int keyID) {
        rt.addKey(keyID, new Delegate());
    }

    @SubscribeEvent
    public void onThrowCoin(CoinThrowEvent evt) {
        CPData cpData = CPData.get(evt.entityPlayer);
        PresetData pData = PresetData.get(evt.entityPlayer);

        boolean spawn = cpData.canUseAbility() &&
                pData.getCurrentPreset().hasControllable(this);

        if (spawn) {
            if (SideHelper.isClient()) {
                spawnClientEffect(evt.entityPlayer);

                informDelegate(evt.coin);
            } else {
                NetworkMessage.sendToAllAround(
                        TargetPoints.convert(evt.entityPlayer, 30),
                        instance,
                        MSG_CHARGE_EFFECT,
                        evt.entityPlayer
                );
            }
        }
    }

    private void informDelegate(EntityCoinThrowing coin) {
        ClientRuntime rt = ClientRuntime.instance();
        Collection<KeyDelegate> delegates = rt.getDelegates(ClientRuntime.DEFAULT_GROUP);
        if (!delegates.isEmpty()) {
            for (Iterator<KeyDelegate> i = delegates.iterator(); i.hasNext(); ) {
                KeyDelegate dele = i.next();
                if (dele instanceof Delegate) {
                    ((Delegate) dele).informThrowCoin(coin);
                    return;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Listener(channel=MSG_CHARGE_EFFECT, side= Side.CLIENT)
    private void hSpawnClientEffect(EntityPlayer target) {
        spawnClientEffect(target);
    }

    private void spawnClientEffect(EntityPlayer target) {
        DummyRenderData.get(target).addRenderHook(new RailgunHandEffect());
    }

    @SideOnly(Side.CLIENT)
    @Listener(channel=MSG_PERFORM, side=Side.CLIENT)
    public void performClient(EntityPlayer player, Vec3 str, Vec3 end) {
        player.worldObj.spawnEntityInWorld(new EntityRailgunFXNative(player, str, end));
    }


    private List<Entity> selectTargets(EntityLivingBase entity, EntityPlayer player, double incr_)
    {
        Motion3D motion = new Motion3D(entity, true).move(0.1).normalize();
        float yaw = -MathUtils.PI_F * 0.5f - motion.getRotationYawRadians(),
                pitch = motion.getRotationPitchRadians();
        Vec3 start = motion.getPosVec();
        Vec3 slope = motion.getMotionVec();

        Vec3 vp0 = VecUtils.vec(0, 0, 1);
        vp0.rotateAroundZ(pitch);
        vp0.rotateAroundY(yaw);

        Vec3 vp1 = VecUtils.vec(0, 1, 0);
        vp1.rotateAroundZ(pitch);
        vp1.rotateAroundY(yaw);
        Vec3 v0 = add(start, add(multiply(vp0, -range), multiply(vp1, -range))),
                v1 = add(start, add(multiply(vp0, range), multiply(vp1, -range))),
                v2 = add(start, add(multiply(vp0, range), multiply(vp1, range))),
                v3 = add(start, add(multiply(vp0, -range), multiply(vp1, range))),
                v4 = add(v0, multiply(slope, incr_)),
                v5 = add(v1, multiply(slope, incr_)),
                v6 = add(v2, multiply(slope, incr_)),
                v7 = add(v3, multiply(slope, incr_));
        AxisAlignedBB aabb = WorldUtils.minimumBounds(v0, v1, v2, v3, v4, v5, v6, v7);

        Predicate<Entity> areaSelector = target -> {
            Vec3 dv = subtract(vec(target.posX, target.posY, target.posZ), start);
            Vec3 proj = dv.crossProduct(slope);
            return !target.equals(entity) && proj.lengthVector() < range * 1.2;
        };
        return WorldUtils.getEntities(player.worldObj, aabb, EntitySelectors.everything().and(areaSelector));

    }

    private void performServer(EntityPlayer player) {
        AbilityContext ctx = AbilityContext.of(player, this);

        final float exp = ctx.getSkillExp();

        float cp     = lerpf(340, 455, exp);
        float overload = lerpf(160, 110, exp);
        if (ctx.consume(overload, cp)) {
            float dmg = lerpf(40, 100, exp);
            float energy = lerpf(900, 2000, exp);

            EntityLivingBase lastEntity = player;
            World world=lastEntity.worldObj;
            final double maxIncrement=45;
            double incr_=maxIncrement;

        /* Apply Entity Damage */
            {
                boolean reflectCheck = true;
                List<Vec3> paths = new ArrayList<>();
                Vec3 pos=Vec3.createVectorHelper(lastEntity.posX, lastEntity.posY +lastEntity.getEyeHeight(),
                        lastEntity.posZ);
                paths.add(pos);
                paths.add(add(pos, multiply(lastEntity.getLookVec(), incr_)));
                while (reflectCheck) {
                    reflectCheck=false;
                    if(incr_<=0)
                        break;
                    List<Entity> targets = selectTargets(lastEntity, player, incr_);
                    targets.sort(Comparator.comparingDouble(lastEntity::getDistanceSqToEntity));
                    for (Entity e : targets) {
                        if (e instanceof EntityLivingBase) {
                            RayShootingEvent event = new RayShootingEvent(player, (EntityLivingBase) e, incr_);
                            boolean result = MinecraftForge.EVENT_BUS.post(event);
                            incr_=event.range;
                            if (!result)
                                ctx.attack(e, dmg);
                            else {
                                incr_ -= (e.getDistanceToEntity(lastEntity));
                                paths.remove(paths.size()-1);
                                pos=Vec3.createVectorHelper(e.posX, e.posY +e.getEyeHeight(), e.posZ);
                                paths.add(pos);
                                paths.add(add(pos,multiply(e.getLookVec(), incr_)));
                                lastEntity = (EntityLivingBase) e;
                                reflectCheck=true;
                                break;
                            }
                        } else {
                            e.setDead();
                        }
                    }
                }

                {
                    int index=0;
                    Vec3 str=paths.get(index), end=paths.get(index+1), dir=subtract(end, str);
                    float yaw = (float)(-MathUtils.PI_F * 0.5f -Math.atan2(dir.xCoord, dir.zCoord)),
                            pitch = (float) -Math.atan2(dir.yCoord,
                                    Math.sqrt(dir.xCoord * dir.xCoord + dir.zCoord * dir.zCoord));
                    Vec3 vp0 = VecUtils.vec(0, 0, 1);
                    vp0.rotateAroundZ(pitch);
                    vp0.rotateAroundY(yaw);

                    Vec3 vp1 = VecUtils.vec(0, 1, 0);
                    vp1.rotateAroundZ(pitch);
                    vp1.rotateAroundY(yaw);
                    incr_=1;
                    for(int i=1;i<=maxIncrement;i++)
                    {
                        if(incr_>=dir.lengthVector()||i==maxIncrement || energy<=0)
                        {
                            index++;
                            if(energy<=0)
                            {
                                end=add(str, multiply(dir.normalize(),incr_));
                            }
                            incr_=1;
                            if(!player.worldObj.isRemote)
                            {
                                NetworkMessage.sendToAllAround(
                                        TargetPoints.convert(player, 30),
                                        instance,
                                        MSG_PERFORM,
                                        player,
                                        str,end
                                );
                            }
                            if(index==paths.size()-1)
                                break;
                            str=paths.get(index);
                            end=paths.get(index+1);
                            dir=subtract(end, str);
                            yaw = (float)(-MathUtils.PI_F * 0.5f -Math.atan2(dir.xCoord, dir.zCoord));
                            pitch = (float) -Math.atan2(dir.yCoord, Math.sqrt(dir.xCoord * dir.xCoord + dir.zCoord * dir.zCoord));
                            vp0 = VecUtils.vec(0, 0, 1);
                            vp0.rotateAroundZ(pitch);
                            vp0.rotateAroundY(yaw);

                            vp1 = VecUtils.vec(0, 1, 0);
                            vp1.rotateAroundZ(pitch);
                            vp1.rotateAroundY(yaw);
                        }
                        if(ctx.canBreakBlock(player.worldObj)) {
                            Vec3 cur=add(str,multiply(dir.normalize(),incr_));
                            for (double s = -range; s <= range; s += STEP) {
                                for (double t = -range; t <= range; t += STEP) {
                                    double rr = range * RandUtils.ranged(0.9, 1.1);
                                    if (s * s + t * t > rr * rr)
                                        continue;
                                    pos = VecUtils.add(cur,
                                            VecUtils.add(
                                                    VecUtils.multiply(vp0, s),
                                                    VecUtils.multiply(vp1, t)));
                                    energy = destroyBlock(world, (int) pos.xCoord, (int) pos.yCoord,
                                            (int) pos.zCoord, energy);
                                }
                            }
                        }

                        incr_++;
                    }
                }

            }
            instance.triggerAchievement(player);
            ctx.addSkillExp(0.005f);
            ctx.setCooldown((int) lerpf(300, 160, exp));
        }
    }

    private float destroyBlock(World world, int x, int y, int z, float energy) {
        Block block = world.getBlock(x, y, z);
        float hardness = block.getBlockHardness(world, x, y, z);
        if(hardness < 0|| energy < 0)
            hardness = 2333333;
        if(!MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(world, x, y, z))  && energy >= hardness)
        {
            if(block.getMaterial() != Material.air) {
                block.dropBlockAsItemWithChance(world, x, y, z,
                        world.getBlockMetadata(x, y, z), 0.05f, 0);
            }

            world.setBlockToAir(x, y, z);
            return energy-hardness;
        }
        return 0;
    }


    @Listener(channel=MSG_COIN_PERFORM, side=Side.SERVER)
    private void consumeCoinAtServer(EntityPlayer player, EntityCoinThrowing coin) {
        coin.setDead();
        performServer(player);
    }

    @Listener(channel=MSG_ITEM_PERFORM, side=Side.SERVER)
    private void consumeItemAtServer(EntityPlayer player) {
        ItemStack equipped = player.getCurrentEquippedItem();
        if (isAccepted(equipped)) {
            equipped.stackSize--;
            if (equipped.stackSize == 0) {
                player.setCurrentItemOrArmor(0, null);
            }

            performServer(player);
        }
    }

    private static class Delegate extends KeyDelegate {

        EntityCoinThrowing coin;

        int chargeTicks = -1;

        void informThrowCoin(EntityCoinThrowing coin) {
            if (this.coin == null || this.coin.isDead) {
                this.coin = coin;
                onKeyAbort();
            }
        }

        @Override
        public void onKeyDown() {
            if (coin == null) {
                if (instance.isAccepted(getPlayer().getCurrentEquippedItem())) {
                    instance.spawnClientEffect(getPlayer());
                    chargeTicks = 20;
                }
            } else {
                if (coin.getProgress() > 0.7) {
                    NetworkMessage.sendToServer(instance,
                            MSG_COIN_PERFORM, getPlayer(), coin);
                }

                coin = null; // Prevent second QTE judgement
            }
        }

        @Override
        public void onKeyTick() {
            if (chargeTicks != -1) {
                if (--chargeTicks == 0) {
                    NetworkMessage.sendToServer(instance,
                            MSG_ITEM_PERFORM, getPlayer());
                }
            }
        }

        @Override
        public void onKeyUp() {
            chargeTicks = -1;
        }

        @Override
        public void onKeyAbort() {
            chargeTicks = -1;
        }

        public DelegateState getState() {
            if (coin != null && !coin.isDead) {
                return coin.getProgress() < 0.6 ? DelegateState.CHARGE : DelegateState.ACTIVE;
            } else {
                return chargeTicks == -1 ? DelegateState.IDLE : DelegateState.CHARGE;
            }
        }

        @Override
        public ResourceLocation getIcon() {
            return instance.getHintIcon();
        }

        @Override
        public int createID() {
            return 0;
        }

        @Override
        public Skill getSkill() {
            return instance;
        }
    }
}
