package org.gunix06.elreto.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.gunix06.elreto.sound.ModSounds;
import org.jetbrains.annotations.Nullable;
import java.util.Random;

public class MachineBlockEntity extends BlockEntity {
    private boolean isSpinning = false;
    private int spinTimer = 0;
    private static final int SPIN_DURATION = 60;

    private int slot1 = 0;
    private int slot2 = 0;
    private int slot3 = 0;

    private int finalSlot1 = 0;
    private int finalSlot2 = 0;
    private int finalSlot3 = 0;

    private boolean hasNotifiedPlayer = false;
    private Direction spawnDirection = Direction.NORTH;
    private java.util.UUID playerUUID = null;

    private static final Random RANDOM = new Random();
    private static final int MAX_NUMBER = 9;

    public MachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MACHINE_BLOCK_ENTITY, pos, state);
    }

    public void startSpin(Direction facing, PlayerEntity player) {
        if (!isSpinning) {
            isSpinning = true;
            spinTimer = 0;
            spawnDirection = facing;
            playerUUID = player.getUuid();

            finalSlot1 = RANDOM.nextInt(MAX_NUMBER + 1);
            finalSlot2 = RANDOM.nextInt(MAX_NUMBER + 1);
            finalSlot3 = RANDOM.nextInt(MAX_NUMBER + 1);

            markDirty();
            if (world != null) {
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
        }
    }

    public boolean isSpinning() {
        return isSpinning;
    }

    public int getSlot1() {
        return slot1;
    }

    public int getSlot2() {
        return slot2;
    }

    public int getSlot3() {
        return slot3;
    }

    public int getFinalSlot1() {
        return finalSlot1;
    }

    public int getFinalSlot2() {
        return finalSlot2;
    }

    public int getFinalSlot3() {
        return finalSlot3;
    }

    public int getSpinTimer() {
        return spinTimer;
    }

    public static void tick(World world, BlockPos pos, BlockState state, MachineBlockEntity blockEntity) {
        if (blockEntity.isSpinning) {
            blockEntity.spinTimer++;

            if (blockEntity.spinTimer % 2 == 0) {
                blockEntity.slot1 = RANDOM.nextInt(MAX_NUMBER + 1);
                blockEntity.slot2 = RANDOM.nextInt(MAX_NUMBER + 1);
                blockEntity.slot3 = RANDOM.nextInt(MAX_NUMBER + 1);
            }

            if (blockEntity.spinTimer >= SPIN_DURATION - 20) {
                blockEntity.slot1 = blockEntity.finalSlot1;
            }
            if (blockEntity.spinTimer >= SPIN_DURATION - 10) {
                blockEntity.slot2 = blockEntity.finalSlot2;
            }
            if (blockEntity.spinTimer >= SPIN_DURATION) {
                blockEntity.slot3 = blockEntity.finalSlot3;
                blockEntity.isSpinning = false;
                if (!world.isClient) {
                    blockEntity.checkWinCondition(world, pos);
                    blockEntity.hasNotifiedPlayer = false;
                }
            }

            blockEntity.markDirty();
            if (!world.isClient) {
                world.updateListeners(pos, state, state, 3);
            }
        }
    }

    private void checkWinCondition(World world, BlockPos pos) {
        int matchingNumbers = 0;

        if (slot1 == slot2 && slot2 == slot3) {
            matchingNumbers = 3;
        } else if (slot1 == slot2 || slot2 == slot3 || slot1 == slot3) {
            matchingNumbers = 2;
        }

        if (world instanceof ServerWorld serverWorld && !hasNotifiedPlayer && playerUUID != null) {
            PlayerEntity player = serverWorld.getPlayerByUuid(playerUUID);

            if (player != null) {
                player.sendMessage(Text.literal("§e━━━━━━━━━━━━━━━━━━━━━━"), false);
                player.sendMessage(Text.literal("§6§l   RESULTADO DE LA MÁQUINA"), false);
                player.sendMessage(Text.literal("§f   [ §b" + slot1 + " §f] [ §b" + slot2 + " §f] [ §b" + slot3 + " §f]"), false);

                if (matchingNumbers >= 2) {
                    ItemStack reward = getReward(matchingNumbers);

                    world.playSound(null, pos, ModSounds.MACHINE_WIN, SoundCategory.BLOCKS, 1.0f, 1.0f);

                    if (matchingNumbers == 3) {
                        player.sendMessage(Text.literal("§a§l   ¡JACKPOT! ¡3 NÚMEROS IGUALES!"), false);
                        player.sendMessage(Text.literal("§6   Premio: §e" + reward.getCount() + "x " + reward.getName().getString()), false);
                    } else {
                        player.sendMessage(Text.literal("§a   ¡2 números iguales!"), false);
                        player.sendMessage(Text.literal("§6   Premio: §e" + reward.getCount() + "x " + reward.getName().getString()), false);
                    }
                    spawnRewardItem(world, pos, reward);
                } else {
                    player.sendMessage(Text.literal("§c   No hay coincidencias. ¡Inténtalo de nuevo!"), false);
                }
                player.sendMessage(Text.literal("§e━━━━━━━━━━━━━━━━━━━━━━"), false);
            }
            hasNotifiedPlayer = true;
        }
    }

    private void spawnRewardItem(World world, BlockPos pos, ItemStack reward) {
        BlockPos spawnPos = pos.offset(spawnDirection.getOpposite());

        net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
            world,
            spawnPos.getX() + 0.5,
            spawnPos.getY() + 0.1,
            spawnPos.getZ() + 0.5,
            reward
        );

        itemEntity.setVelocity(0, 0, 0);
        itemEntity.setToDefaultPickupDelay();

        world.spawnEntity(itemEntity);
    }

    private ItemStack getReward(int matchingNumbers) {
        if (matchingNumbers == 3) {
            int rewardType = RANDOM.nextInt(4);
            return switch (rewardType) {
                case 0 -> new ItemStack(Items.DIAMOND, 3 + RANDOM.nextInt(3));
                case 1 -> new ItemStack(Items.EMERALD, 5 + RANDOM.nextInt(5));
                case 2 -> new ItemStack(Items.GOLD_INGOT, 8 + RANDOM.nextInt(8));
                default -> new ItemStack(Items.IRON_INGOT, 10 + RANDOM.nextInt(6));
            };
        } else {
            int rewardType = RANDOM.nextInt(5);
            return switch (rewardType) {
                case 0 -> new ItemStack(Items.IRON_INGOT, 2 + RANDOM.nextInt(3));
                case 1 -> new ItemStack(Items.GOLD_NUGGET, 3 + RANDOM.nextInt(4));
                case 2 -> new ItemStack(Items.COAL, 5 + RANDOM.nextInt(5));
                case 3 -> new ItemStack(Items.COPPER_INGOT, 4 + RANDOM.nextInt(4));
                default -> new ItemStack(Items.LAPIS_LAZULI, 3 + RANDOM.nextInt(5));
            };
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean("IsSpinning", isSpinning);
        nbt.putInt("SpinTimer", spinTimer);
        nbt.putInt("Slot1", slot1);
        nbt.putInt("Slot2", slot2);
        nbt.putInt("Slot3", slot3);
        nbt.putInt("FinalSlot1", finalSlot1);
        nbt.putInt("FinalSlot2", finalSlot2);
        nbt.putInt("FinalSlot3", finalSlot3);
        nbt.putBoolean("HasNotifiedPlayer", hasNotifiedPlayer);
        nbt.putString("SpawnDirection", spawnDirection.getId());
        if (playerUUID != null) {
            nbt.putString("PlayerUUID", playerUUID.toString());
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        isSpinning = nbt.getBoolean("IsSpinning").orElse(false);
        spinTimer = nbt.getInt("SpinTimer").orElse(0);
        slot1 = nbt.getInt("Slot1").orElse(0);
        slot2 = nbt.getInt("Slot2").orElse(0);
        slot3 = nbt.getInt("Slot3").orElse(0);
        finalSlot1 = nbt.getInt("FinalSlot1").orElse(0);
        finalSlot2 = nbt.getInt("FinalSlot2").orElse(0);
        finalSlot3 = nbt.getInt("FinalSlot3").orElse(0);
        hasNotifiedPlayer = nbt.getBoolean("HasNotifiedPlayer").orElse(false);
        String dirId = nbt.getString("SpawnDirection").orElse(Direction.NORTH.getId());
        spawnDirection = Direction.byId(dirId);
        if (nbt.contains("PlayerUUID")) {
            try {
                String uuidString = nbt.getString("PlayerUUID").orElse(null);
                if (uuidString != null) {
                    playerUUID = java.util.UUID.fromString(uuidString);
                }
            } catch (IllegalArgumentException e) {
                playerUUID = null;
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
