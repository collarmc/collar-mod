<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/mixin/MinecraftClientFieldMixin.java
package com.collarmc.mod.fabric.mixin;
=======
package com.collarmc.collar.mod.glue.mixin;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/mixin/MinecraftClientFieldMixin.java

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientFieldMixin {
    @Accessor(value = "bufferBuilders")
    BufferBuilderStorage bufferBuilders();
}
