<<<<<<< HEAD:1.16/fabric/src/main/java/com/collarmc/mod/fabric/mixin/PlayerListEntryMixin.java
package com.collarmc.mod.fabric.mixin;
=======
package com.collarmc.collar.mod.glue.mixin;
>>>>>>> topic/merge:1.16/glue/src/main/java/com/collarmc/collar/mod/glue/mixin/PlayerListEntryMixin.java

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public interface PlayerListEntryMixin {
    @Accessor(value = "textures")
    Map<Type, Identifier> textures();
}
