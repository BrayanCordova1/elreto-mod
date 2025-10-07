package org.gunix06.elreto.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import org.gunix06.elreto.block.entity.ModBlockEntities;
import org.gunix06.elreto.client.renderer.MachineBlockEntityRenderer;

public class ElretoClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.MACHINE_BLOCK_ENTITY, MachineBlockEntityRenderer::new);
    }
}
