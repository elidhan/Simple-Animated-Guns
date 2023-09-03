package net.elidhan.anim_guns.attribute;

import net.elidhan.anim_guns.AnimatedGuns;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GunAttributes
{
    public static final EntityAttribute GUN_DAMAGE = GunAttributes.register(new Identifier(AnimatedGuns.MOD_ID, "gun_damage"), new ClampedEntityAttribute("attribute.name.anim_guns.gun_damage", 1.0, 0.0, 1024.0));

    private static EntityAttribute register(Identifier id, EntityAttribute attribute) {
        return Registry.register(Registry.ATTRIBUTE, id, attribute);
    }

    public static void registerAttributes()
    {
        AnimatedGuns.LOGGER.info("Registering GunAttributes for " + AnimatedGuns.MOD_ID);
    }
}
