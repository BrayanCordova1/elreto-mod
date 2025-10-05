package org.gunix06.elreto.item;

import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
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

    public static final Item COPPER_NUGGET = registerItem("copper_nugget", Item::new);

    public static final Item COPPER_SWORD = registerItem("copper_sword",
            setting -> new Item(setting.sword(ModToolMaterials.COPPER, 5, -2.4f)));
    public static final Item COPPER_PICKAXE = registerItem("copper_pickaxe",
            setting -> new Item(setting.pickaxe(ModToolMaterials.COPPER, 2, -2.8f)));
    public static final Item COPPER_SHOVEL = registerItem("copper_shovel",
            setting -> new ShovelItem(ModToolMaterials.COPPER, 2.5f, -3.0f, setting));
    public static final Item COPPER_AXE = registerItem("copper_axe",
            setting -> new AxeItem(ModToolMaterials.COPPER, 7, -3.2f, setting));
    public static final Item COPPER_HOE = registerItem("copper_hoe",
            setting -> new HoeItem(ModToolMaterials.COPPER, 0, -1f, setting));

    public static  final Item COPPER_HELMET = registerItem("copper_helmet",
            setting -> new Item(setting.armor(ModArmorMaterials.COPPER_ARMOR_MATERIAL, EquipmentType.HELMET)));
    public static final Item COPPER_CHESTPLATE = registerItem("copper_chestplate",
            setting -> new Item(setting.armor(ModArmorMaterials.COPPER_ARMOR_MATERIAL, EquipmentType.CHESTPLATE)));
    public static final Item COPPER_LEGGINGS = registerItem("copper_leggings",
            setting -> new Item(setting.armor(ModArmorMaterials.COPPER_ARMOR_MATERIAL, EquipmentType.LEGGINGS)));
    public static final Item COPPER_BOOTS = registerItem("copper_boots",
            setting -> new Item(setting.armor(ModArmorMaterials.COPPER_ARMOR_MATERIAL, EquipmentType.BOOTS)));

    public static final Item IRON_SMITHING_TEMPLATE = registerItem("iron_smithing_template", SmithingTemplateItem::of);
    public static final Item DIAMOND_SMITHING_TEMPLATE = registerItem("diamond_smithing_template", SmithingTemplateItem::of);

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(Elreto.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Elreto.MOD_ID, name)))));
    }
    public static void registerModItems() {
        Elreto.LOGGER.info("Registering Mod Items for " + Elreto.MOD_ID);
    }
}