package com.collarmc.mod.common.features;

import com.collarmc.client.Collar;
import com.collarmc.client.api.textures.Texture;
import com.collarmc.client.api.textures.TexturesApi;
import com.collarmc.client.api.textures.TexturesListener;
import com.collarmc.plastic.Plastic;

public class Textures implements TexturesListener {

    private final Plastic plastic;

    public Textures(Plastic plastic) {
        this.plastic = plastic;
    }

    @Override
    public void onTextureReceived(Collar collar, TexturesApi texturesApi, Texture texture) {

    }
}
