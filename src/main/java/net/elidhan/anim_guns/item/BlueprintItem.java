package net.elidhan.anim_guns.item;

import net.minecraft.item.Item;

import java.util.*;

public class BlueprintItem extends Item
{
    public static final List<BlueprintItem> BLUEPRINT_ITEM_LIST = new ArrayList<>();
    public BlueprintItem(Settings settings)
    {
        super(settings);
        BLUEPRINT_ITEM_LIST.add(this);
    }
}
