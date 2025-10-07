package org.gunix06.elreto.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
import org.gunix06.elreto.block.entity.MachineBlockEntity;
import org.gunix06.elreto.block.entity.ModBlockEntities;
import org.gunix06.elreto.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class Machine extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<Machine> CODEC = createCodec(Machine::new);
    public static final EnumProperty<Direction> FACING = EnumProperty.of("facing", Direction.class, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    public Machine(Settings settings) {
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
    protected MapCodec<? extends BlockWithEntity> getCodec() {
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MachineBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

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
                        Direction facing = state.get(FACING);
                        machineEntity.startSpin(facing, player);
                        player.sendMessage(Text.literal("§6¡Máquina activada! Girando..."), true);
                    } else {
                        player.sendMessage(Text.literal("§c¡La máquina ya está girando!"), true);
                    }
                } else {
                    player.sendMessage(Text.literal("§c¡Necesitas 1 ficha para usar la máquina!"), true);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.MACHINE_BLOCK_ENTITY, MachineBlockEntity::tick);
    }
}
