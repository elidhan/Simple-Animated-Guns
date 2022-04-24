package net.elidhan.anim_guns;

import net.elidhan.anim_guns.util.ModelPredicateProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AnimatedGunsClient implements ClientModInitializer
{
    //private static KeyBinding aimDownSights;
    //private static boolean adsEnabled;
    @Override
    public void onInitializeClient()
    {
        ModelPredicateProvider.registerModels();
        //adsEnabled = false;
        //aimDownSights = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.anim_guns.ads", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V,"category.anim_guns.ads"));
    }
}
