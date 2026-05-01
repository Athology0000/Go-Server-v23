package org.cobalt.mixin.render;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.cobalt.internal.visual.DeadEntityCleanerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

  @Inject(
    method = "shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z",
    at = @At("HEAD"),
    cancellable = true
  )
  private <E extends Entity> void cobalt$hideDeadEntities(
    E entity,
    Frustum frustum,
    double cameraX,
    double cameraY,
    double cameraZ,
    CallbackInfoReturnable<Boolean> cir
  ) {
    if (DeadEntityCleanerModule.shouldHideEntity(entity)) {
      cir.setReturnValue(false);
    }
  }
}
