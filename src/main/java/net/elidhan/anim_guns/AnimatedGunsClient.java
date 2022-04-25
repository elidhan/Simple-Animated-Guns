package net.elidhan.anim_guns;

import net.elidhan.anim_guns.util.ModelPredicateProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimatedGunsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelPredicateProvider.registerModels();
    }
}
