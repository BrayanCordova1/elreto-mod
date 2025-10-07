package org.gunix06.elreto.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.gunix06.elreto.block.ModBlocks;
import org.gunix06.elreto.block.entity.MachineBlockEntity;
import org.gunix06.elreto.item.ModItems;

public class MachineTop extends Block {
    public static final MapCodec<MachineTop> CODEC = createCodec(MachineTop::new);
    public static final EnumProperty<Direction> FACING = EnumProperty.of("facing", Direction.class, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    public MachineTop(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockPos belowPos = pos.down();
            BlockState belowState = world.getBlockState(belowPos);

            if (belowState.isOf(ModBlocks.MACHINE)) {
                var blockEntity = world.getBlockEntity(belowPos);

                if (blockEntity instanceof MachineBlockEntity machineEntity) {
                    boolean hasFicha = false;
                    int fichaSlot = -1;

                    for (int i = 0; i < player.getInventory().size(); i++) {
                        ItemStack stack = player.getInventory().getStack(i);
                        if (stack.getItem() == ModItems.FICHA && stack.getCount() >= 1) {
                            hasFicha = true;
                            fichaSlot = i;
                            break;
                        }
                    }

                    if (hasFicha) {
                        if (!machineEntity.isSpinning()) {
                            ItemStack fichaStack = player.getInventory().getStack(fichaSlot);
                            fichaStack.decrement(1);
                            Direction facing = belowState.get(org.gunix06.elreto.block.custom.Machine.FACING);
                            machineEntity.startSpin(facing, player);
                            player.sendMessage(Text.literal("§6¡Máquina activada! Girando..."), true);
                        } else {
                            player.sendMessage(Text.literal("§c¡La máquina ya está girando!"), true);
                        }
                    } else {
                        player.sendMessage(Text.literal("§c¡Necesitas 1 ficha para usar la máquina!"), true);
                    }
                    return ActionResult.SUCCESS;
                } else {
                    player.sendMessage(Text.literal("§c¡Error: No se encontró la entidad de la máquina!"), true);
                }
            } else {
                player.sendMessage(Text.literal("§c¡Necesitas colocar el bloque Machine debajo!"), true);
            }
        }
        return ActionResult.SUCCESS;
    }
}
