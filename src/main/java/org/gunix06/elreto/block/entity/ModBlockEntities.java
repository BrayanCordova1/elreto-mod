package org.gunix06.elreto.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.gunix06.elreto.Elreto;
import org.gunix06.elreto.block.ModBlocks;

public class ModBlockEntities {
    public static final BlockEntityType<MachineBlockEntity> MACHINE_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Elreto.MOD_ID, "machine_block_entity"),
                    FabricBlockEntityTypeBuilder.create(MachineBlockEntity::new, ModBlocks.MACHINE).build()
            );

    public static void registerBlockEntities() {
        Elreto.LOGGER.info("Registering Block Entities for " + Elreto.MOD_ID);
    }
}
