package team.catgirl.collar.mod.fabric.client;

import net.fabricmc.api.ClientModInitializer;

public class CollarFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("Hello from 1.17 Fabric :D");
    }
}
