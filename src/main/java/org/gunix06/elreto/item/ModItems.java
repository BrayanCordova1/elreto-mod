package org.gunix06.elreto.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.gunix06.elreto.Elreto;
import java.util.function.Function;

public class ModItems {
    public static final Item RAW_IRON_NUGGET = registerItem("raw_iron_nugget", Item::new);
    public static final Item RAW_COPPER_NUGGET = registerItem("raw_copper_nugget", Item::new);
    public static final Item RAW_GOLD_NUGGET = registerItem("raw_gold_nugget", Item::new);

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(Elreto.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Elreto.MOD_ID, name)))));
    }
    public static void registerModItems() {
        Elreto.LOGGER.info("Registering Mod Items for " + Elreto.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(RAW_IRON_NUGGET);
            entries.add(RAW_COPPER_NUGGET);
            entries.add(RAW_GOLD_NUGGET);
        });
    }
}