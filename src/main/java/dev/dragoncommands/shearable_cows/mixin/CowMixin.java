package dev.dragoncommands.shearable_cows.mixin;

import dev.dragoncommands.shearable_cows.ShearedEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntity.class)
public abstract class CowMixin extends AnimalEntity implements Shearable, ShearedEntity {

	protected CowMixin(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	private static final TrackedData<Boolean> SHORN;

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SHORN, false);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);

		nbt.putBoolean("shorn", isShorn());
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		this.dataTracker.set(SHORN, nbt.getBoolean("shorn"));
	}

	@Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
	private void init(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			this.sheared(SoundCategory.PLAYERS);
			this.emitGameEvent(GameEvent.SHEAR, player);

			if (!this.world.isClient) {
				itemStack.damage(1, player, (playerx) -> {
					playerx.sendToolBreakStatus(hand);
				});
			}

			cir.setReturnValue(ActionResult.success(this.world.isClient));
		} else if (itemStack.isOf(Items.BUCKET) && this.isShorn()) {
			cir.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(at = @At("HEAD"), method = "getAmbientSound", cancellable = true)
	public void replaceSound(CallbackInfoReturnable<SoundEvent> cir) {
		if(this.isShorn()) {
			cir.setReturnValue(SoundEvents.ENTITY_COW_HURT);
		}
	}

	@Override
	public boolean isShearable() {
		return this.isAlive() && !this.isBaby() && !this.isShorn();
	}

	@Override
	public void sheared(SoundCategory shearedSoundCategory) {
		this.world.playSoundFromEntity((PlayerEntity)null, this, SoundEvents.ENTITY_MOOSHROOM_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
		this.world.playSoundFromEntity((PlayerEntity)null, this, SoundEvents.ENTITY_COW_HURT, shearedSoundCategory, 1.0F, 1.0F);

		this.goalSelector.add(0, new ActiveTargetGoal<>(this, CowEntity.class, false));

		if(!this.world.isClient()) {

			for(int i = 0; i < random.nextBetween(1,3); i++)
				this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.LEATHER)));
		}

		this.dataTracker.set(SHORN, true);

	}

	@Override
	public boolean isShorn() {
		return this.dataTracker.get(SHORN);
	}

	static {
		SHORN = DataTracker.registerData(CowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	}
}