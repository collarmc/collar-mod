package com.collarmc.mod.glue.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.collarmc.plastic.Plastic;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    //@Shadow @Final private static Logger LOGGER;

    //@Shadow @Nullable private ServerInfo currentServerEntry;

    @Inject(at = @At("HEAD"), method = "tick")
    private void onStartTick(CallbackInfo info) {
        Plastic.getPlastic().onTick();
    }

    /*
    @Inject(at = @At("TAIL"), method="setCurrentServerEntry(Lnet/minecraft/client/network/ServerInfo;)V")
    private void onSetCurrentServerEntry1(CallbackInfo info) {
        LOGGER.info("SetCurrentServerEntry1 " + this.currentServerEntry == null ? "NO_ADDRESS" : this.currentServerEntry.address);
    }

    @Inject(at= @At("TAIL"), method = "setCurrentServerEntry(Lnet/minecraft/client/realms/dto/RealmsServer;Ljava/lang/String;)V")
    private void onSetCurrentServerEntry2(CallbackInfo info) {
        LOGGER.info("SetCurrentServerEntry2 " + this.currentServerEntry == null ? "NO_ADDRESS" : this.currentServerEntry.address);
    }

    @Inject(at= @At("HEAD"), method = "getCurrentServerEntry")
    private void onGetCurrentServerEntry(CallbackInfoReturnable<ServerInfo> cir) {
        LOGGER.info("GetCurrentServerEntry " + this.currentServerEntry == null ? "NO_ADDRESS" : this.currentServerEntry.address);
    }
    */
}
