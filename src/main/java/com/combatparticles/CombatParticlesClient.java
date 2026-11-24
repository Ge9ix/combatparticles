package com.combatparticles;

import com.combatparticles.screen.CombatSettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class CombatParticlesClient implements ClientModInitializer {
	private static KeyMapping openKey;
	private static KeyMapping cycleKey;

	@Override
	public void onInitializeClient() {
		CombatConfig.load();
		ParticleFilter.rebuild();

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("combatparticles", "main"));
		openKey = KeyBindingHelper.registerKeyBinding(
			new KeyMapping("key.combatparticles.open", GLFW.GLFW_KEY_SEMICOLON, category));
		cycleKey = KeyBindingHelper.registerKeyBinding(
			new KeyMapping("key.combatparticles.cycle", GLFW.GLFW_KEY_APOSTROPHE, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openKey.consumeClick()) {
				if (client.screen == null) {
					client.setScreen(new CombatSettingsScreen(null));
				}
			}
			while (cycleKey.consumeClick()) {
				CombatConfig config = CombatConfig.get();
				Profile[] all = Profile.values();
				config.activeProfile = all[(config.activeProfile.ordinal() + 1) % all.length];
				CombatConfig.saveAndApply();
				if (client.player != null) {
					client.player.displayClientMessage(
						Component.literal("Combat Particles: " + config.activeProfile.displayName), true);
				}
			}
		});
	}
}
