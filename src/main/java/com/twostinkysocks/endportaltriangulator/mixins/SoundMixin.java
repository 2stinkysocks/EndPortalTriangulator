package com.twostinkysocks.endportaltriangulator.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.BaseText;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundMixin {
    @Inject(at = @At("HEAD"), method = "play(Lnet/minecraft/client/sound/SoundInstance;)V")
    private void inject(SoundInstance sound, CallbackInfo ci) {
        if(sound.getId().getPath() != null && MinecraftClient.getInstance().player != null) {
            double playerX = MinecraftClient.getInstance().player.getX();
            double playerZ = MinecraftClient.getInstance().player.getZ();

            double soundX = sound.getX();
            double soundZ = sound.getZ();

            double slope = (soundZ - playerZ)/(soundX - playerX);
            double intercept = playerZ - (slope * playerX);

            BaseText clickable = new BaseText() {
                @Override
                public BaseText copy() {
                    return null;
                }
            };
            clickable.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, slope + "x + " + intercept)));
            if(sound.getId().getPath().equals("block.end_portal.spawn")) {
                clickable.append("Click to copy end portal vector (paste into desmos)");
                MinecraftClient.getInstance().player.sendMessage(new LiteralText("(" + playerX + ", " + playerZ + ") (" + soundX + ", " + soundZ + ")"), false);
                MinecraftClient.getInstance().player.sendMessage(new LiteralText(slope + "x + " + intercept), false);
                MinecraftClient.getInstance().player.sendMessage(clickable, false);
            } else if(sound.getId().getPath().equals("entity.wither.spawn")) {
                clickable.append("Click to copy wither spawn vector (paste into desmos)");
                MinecraftClient.getInstance().player.sendMessage(new LiteralText("(" + playerX + ", " + playerZ + ") (" + soundX + ", " + soundZ + ")"), false);
                MinecraftClient.getInstance().player.sendMessage(new LiteralText(slope + "x + " + intercept), false);
                MinecraftClient.getInstance().player.sendMessage(clickable, false);
            }
        }
    }
}
