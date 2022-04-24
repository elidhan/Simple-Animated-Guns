package net.elidhan.anim_guns.key_binds;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static final KeyBinding aimDownSights =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.anim_guns.ads", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V,"category.anim_guns.ads"));

}
