package dev.wp.industrialization_overdrive;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;

public class IOUtil {
    public final static boolean isEILoaded = false; // Disabled until EI is ported to 26.1.x
    // was ModList.get().isLoaded("extended_industrialization");
    public final static boolean isAE2Loaded = isModLoaded("ae2");

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isRealPlayer(Player player) {
        if (player == null) return false;
        return !(player instanceof FakePlayer);
    }
}
