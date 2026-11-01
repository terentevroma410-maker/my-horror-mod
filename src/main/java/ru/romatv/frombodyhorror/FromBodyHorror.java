package ru.romatv.frombodyhorror;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public final class FromBodyHorror implements ModInitializer {
    public static final String MOD_ID = "from_body_horror";

    // First prototype: an unsettling "cicada whisper" event.
    // It can happen only at night and in a genuinely dark place.
    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
    }

    private void onWorldTick(ServerWorld world) {
        long dayTime = Math.floorMod(world.getTimeOfDay(), 24_000L);
        boolean night = dayTime >= 13_000L && dayTime < 23_000L;

        if (!night) {
            return;
        }

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.isSpectator() || player.isCreative()) {
                continue;
            }

            if (world.getLightLevel(player.getBlockPos()) > 4) {
                continue;
            }

            // About one check per 2 minutes per player on average.
            if (world.getRandom().nextInt(2_400) != 0) {
                continue;
            }

            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.DARKNESS,
                    50,
                    0,
                    false,
                    false,
                    true
            ));

            player.playSound(SoundEvents.AMBIENT_CAVE, 0.75F, 0.65F);
            player.sendMessage(
                    Text.translatable("message.from_body_horror.cicada_whisper"),
                    true
            );
        }
    }
}
