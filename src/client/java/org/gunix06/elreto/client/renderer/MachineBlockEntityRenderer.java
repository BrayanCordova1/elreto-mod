package org.gunix06.elreto.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import org.gunix06.elreto.block.entity.MachineBlockEntity;
import org.joml.Matrix4f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class MachineBlockEntityRenderer implements BlockEntityRenderer<MachineBlockEntity> {
    private final TextRenderer textRenderer;

    public MachineBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(MachineBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Renderizar números en la cara frontal del bloque machine_bottom
        renderSlotNumbers(entity, tickDelta, matrices, vertexConsumers, light);
    }

    private void renderSlotNumbers(MachineBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Obtener la dirección del bloque desde el BlockState
        Direction facing = Direction.NORTH;
        if (entity.getCachedState().contains(org.gunix06.elreto.block.custom.Machine.FACING)) {
            facing = entity.getCachedState().get(org.gunix06.elreto.block.custom.Machine.FACING);
        }

        // SUBIR MUCHO - Casi a la altura del machine_top (Y=1.0 es donde está machine_top)
        // Usar 1.5 para estar justo debajo de machine_top, en su parte frontal
        matrices.translate(0.5, 1.5, 0.5);

        // Rotar según la dirección del bloque - CORREGIR EAST
        float rotation = switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> -90f;  // CAMBIADO: era 270f, ahora 90f para corregir horizontal
            default -> 0f;
        };
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        // Mover hacia el centro del bloque - reducir Z para estar más adentro
        matrices.translate(0, 0, 0.15);  // CAMBIADO: de 0.505 a 0.35 para estar más centrado

        // Rotar para que el texto mire hacia afuera
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

        // Escala del texto - más grande para mejor visibilidad
        float baseScale = 0.018f;

        // Si está girando, añadir efecto de movimiento vertical (efecto slot machine)
        float yOffset = 0;
        if (entity.isSpinning()) {
            // Crear efecto de números cayendo
            yOffset = MathHelper.sin((entity.getSpinTimer() + tickDelta) * 0.4f) * 0.05f;
            // Hacer que los números pulsen mientras giran
            float pulse = 1.0f + MathHelper.sin((entity.getSpinTimer() + tickDelta) * 0.6f) * 0.1f;
            baseScale *= pulse;
        }

        matrices.scale(-baseScale, -baseScale, baseScale);

        // Obtener los números actuales
        int slot1 = entity.getSlot1();
        int slot2 = entity.getSlot2();
        int slot3 = entity.getSlot3();

        // Determinar el color según el estado
        int color1 = getSlotColor(entity, 1, tickDelta);
        int color2 = getSlotColor(entity, 2, tickDelta);
        int color3 = getSlotColor(entity, 3, tickDelta);

        // Espaciado entre números (ajustado para que se vea bien en el modelo)
        float spacing = 25f;

        // Renderizar cada slot con su animación individual
        matrices.push();
        matrices.translate(-spacing, yOffset * 50, 0);
        renderNumber(String.valueOf(slot1), matrices, vertexConsumers, color1, light);
        matrices.pop();

        matrices.push();
        matrices.translate(0, entity.getSpinTimer() >= 50 ? 0 : yOffset * 60, 0);
        renderNumber(String.valueOf(slot2), matrices, vertexConsumers, color2, light);
        matrices.pop();

        matrices.push();
        matrices.translate(spacing, entity.getSpinTimer() >= 60 ? 0 : yOffset * 70, 0);
        renderNumber(String.valueOf(slot3), matrices, vertexConsumers, color3, light);
        matrices.pop();

        matrices.pop();
    }

    private int getSlotColor(MachineBlockEntity entity, int slotNumber, float tickDelta) {
        if (entity.isSpinning()) {
            // Durante el giro, alternar entre amarillo brillante y naranja con efecto suave
            int phase = ((int)((entity.getSpinTimer() + tickDelta) * 2) + slotNumber) % 2;
            return phase == 0 ? 0xFFFF00 : 0xFFAA00;
        } else {
            // Cuando termina, verificar si es ganador
            int s1 = entity.getFinalSlot1();
            int s2 = entity.getFinalSlot2();
            int s3 = entity.getFinalSlot3();

            if (s1 == s2 && s2 == s3) {
                // JACKPOT - todos los números en rojo brillante pulsante
                float brightness = 0.8f + MathHelper.sin(System.currentTimeMillis() * 0.01f) * 0.2f;
                return (int)(255 * brightness) << 16; // Rojo pulsante
            } else if ((slotNumber == 1 && (s1 == s2 || s1 == s3)) ||
                       (slotNumber == 2 && (s2 == s1 || s2 == s3)) ||
                       (slotNumber == 3 && (s3 == s1 || s3 == s2))) {
                // Números que coinciden en cyan brillante
                return 0x00FFFF;
            } else {
                // Números que no coinciden en blanco
                return 0xFFFFFF;
            }
        }
    }

    private void renderNumber(String text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int color, int light) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float x = -textRenderer.getWidth(text) / 2f;

        // Renderizar con sombra para mejor visibilidad dentro del modelo
        textRenderer.draw(text, x, 0, color, true, matrix, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, 0x40000000, light);
    }
}
