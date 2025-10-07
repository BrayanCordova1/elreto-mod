package org.gunix06.elreto.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.gunix06.elreto.Elreto;

public class ModSounds {
    public static final SoundEvent MACHINE_START = registerSoundEvent("machine_start");
    public static final SoundEvent MACHINE_WIN = registerSoundEvent("machine_win");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(Elreto.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        Elreto.LOGGER.info("Registering Sounds for " + Elreto.MOD_ID);
    }
}

