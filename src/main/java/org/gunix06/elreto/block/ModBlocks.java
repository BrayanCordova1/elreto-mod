package org.gunix06.elreto.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.gunix06.elreto.Elreto;
import org.gunix06.elreto.block.custom.Machine;
import org.gunix06.elreto.block.custom.MachineTop;

import java.util.function.Function;

public class ModBlocks {
    public static final Block MACHINE = registerBlock("machine",
            properties -> new Machine(properties.strength(-1.0f, 3600000.0f).dropsNothing().sounds(BlockSoundGroup.METAL).nonOpaque()));

    public static final Block MACHINE_TOP = registerBlock("machine_top",
            properties -> new MachineTop(properties.strength(-1.0f, 3600000.0f).dropsNothing().sounds(BlockSoundGroup.METAL).nonOpaque()));

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> function) {
        Block toRegister = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Elreto.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(Registries.BLOCK, Identifier.of(Elreto.MOD_ID, name), toRegister);
    }

    private static Block registerBlockWithoutBlockItem(String name, Function<AbstractBlock.Settings, Block> function) {
        return Registry.register(Registries.BLOCK, Identifier.of(Elreto.MOD_ID, name),
                function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Elreto.MOD_ID, name)))));
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Elreto.MOD_ID, name),
                new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Elreto.MOD_ID, name)))));
    }

    public static void registerModBlocks() {
        Elreto.LOGGER.info("Registering Mod Blocks for " + Elreto.MOD_ID);
    }
}
