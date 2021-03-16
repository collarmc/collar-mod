package team.catgirl.collar.mod.common.features;

import team.catgirl.collar.client.Collar;
import team.catgirl.collar.client.api.textures.Texture;
import team.catgirl.collar.client.api.textures.TexturesApi;
import team.catgirl.collar.client.api.textures.TexturesListener;
import team.catgirl.plastic.Plastic;

public class Textures implements TexturesListener {

    private final Plastic plastic;

    public Textures(Plastic plastic) {
        this.plastic = plastic;
    }

    @Override
    public void onTextureReceived(Collar collar, TexturesApi texturesApi, Texture texture) {

    }
}
