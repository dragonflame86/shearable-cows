package dev.dragoncommands.shearable_cows.mixin.client;

import dev.dragoncommands.shearable_cows.ShearedEntity;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntityRenderer.class)
public class CowRendererMixin {

	@Inject(at = @At("HEAD"), method = "getTexture(Lnet/minecraft/entity/passive/CowEntity;)Lnet/minecraft/util/Identifier;", cancellable = true)
	private void run(CowEntity cowEntity, CallbackInfoReturnable<Identifier> cir) {

		if(cowEntity instanceof ShearedEntity sheared && sheared.isShorn()) {
			cir.setReturnValue(new Identifier("shearable-cows","textures/entity/cow.png"));
		}
	}

}