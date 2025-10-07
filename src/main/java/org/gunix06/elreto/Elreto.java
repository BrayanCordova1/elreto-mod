package org.gunix06.elreto;

import net.fabricmc.api.ModInitializer;
import org.gunix06.elreto.block.ModBlocks;
import org.gunix06.elreto.block.entity.ModBlockEntities;
import org.gunix06.elreto.item.ModArmorMaterials;
import org.gunix06.elreto.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elreto implements ModInitializer {
    public static final String MOD_ID = "elreto";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
    }
}
