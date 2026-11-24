package com.combatparticles.screen;

import com.combatparticles.CombatConfig;
import com.combatparticles.ParticleFilter;
import com.combatparticles.Profile;
import com.combatparticles.ProfileSettings;
import com.combatparticles.TotemAnimation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/** The mod's own settings screen. No other mod needed. */
public class CombatSettingsScreen extends Screen {
	private static final int[] SIZES = {50, 75, 100, 125, 150, 200, 250, 300};

	private record Opt(String name, Function<ProfileSettings, Boolean> getter, BiConsumer<ProfileSettings, Boolean> setter) {}

	private static final List<Opt> OPTIONS = List.of(
		new Opt("Totem pop particles", s -> s.totemPop, (s, v) -> s.totemPop = v),
		new Opt("Crit particles", s -> s.crit, (s, v) -> s.crit = v),
		new Opt("Crit: only my hits", s -> s.critOnlyMine, (s, v) -> s.critOnlyMine = v),
		new Opt("Enchanted hit", s -> s.enchantedHit, (s, v) -> s.enchantedHit = v),
		new Opt("Damage hearts", s -> s.damageIndicator, (s, v) -> s.damageIndicator = v),
		new Opt("Sweep arc", s -> s.sweep, (s, v) -> s.sweep = v),
		new Opt("Explosions", s -> s.explosions, (s, v) -> s.explosions = v),
		new Opt("Mace smash", s -> s.maceSmash, (s, v) -> s.maceSmash = v),
		new Opt("Block break", s -> s.blockParticles, (s, v) -> s.blockParticles = v),
		new Opt("All other particles", s -> s.everythingElse, (s, v) -> s.everythingElse = v)
	);

	private final Screen parent;
	private int titleY;
	private int hintY;

	public CombatSettingsScreen(Screen parent) {
		super(Component.literal("Combat Particles"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		CombatConfig config = CombatConfig.get();
		ProfileSettings settings = config.activeSettings();

		int left = this.width / 2 - 155;
		int right = this.width / 2 + 5;
		int top = Math.max(32, (this.height - 200) / 2 + 12);
		this.titleY = top - 18;
		this.hintY = top + 50;

		// Row 1: master switch + profile
		addButton(left, top, 150, onOff("Combat Particles", config.enabled), button -> {
			config.enabled = !config.enabled;
			button.setMessage(onOff("Combat Particles", config.enabled));
			ParticleFilter.rebuild();
		});
		addButton(right, top, 150, Component.literal("Profile: " + config.activeProfile.displayName), button -> {
			Profile[] all = Profile.values();
			config.activeProfile = all[(config.activeProfile.ordinal() + 1) % all.length];
			ParticleFilter.rebuild();
			// redraw the toggles for the newly chosen profile
			this.clearWidgets();
			this.init();
		});

		// Row 2: totem animation + small totem size
		addButton(left, top + 26, 150, Component.literal("My totem: " + settings.totemAnimation.displayName), button -> {
			TotemAnimation[] all = TotemAnimation.values();
			settings.totemAnimation = all[(settings.totemAnimation.ordinal() + 1) % all.length];
			button.setMessage(Component.literal("My totem: " + settings.totemAnimation.displayName));
			ParticleFilter.rebuild();
		});
		addButton(right, top + 26, 150, Component.literal("Small totem size: " + config.smallTotemSizePercent + "%"), button -> {
			config.smallTotemSizePercent = nextSize(config.smallTotemSizePercent);
			button.setMessage(Component.literal("Small totem size: " + config.smallTotemSizePercent + "%"));
		});

		// Particle toggles, two columns
		int toggleTop = top + 62;
		for (int i = 0; i < OPTIONS.size(); i++) {
			Opt opt = OPTIONS.get(i);
			int x = i % 2 == 0 ? left : right;
			int y = toggleTop + (i / 2) * 22;
			addButton(x, y, 150, onOff(opt.name(), opt.getter().apply(settings)), button -> {
				boolean now = !opt.getter().apply(settings);
				opt.setter().accept(settings, now);
				button.setMessage(onOff(opt.name(), now));
				ParticleFilter.rebuild();
			});
		}

		addButton(this.width / 2 - 100, toggleTop + 5 * 22 + 8, 200, Component.literal("Done"), button -> this.onClose());
	}

	private void addButton(int x, int y, int width, Component label, Button.OnPress onPress) {
		this.addRenderableWidget(Button.builder(label, onPress).bounds(x, y, width, 20).build());
	}

	private static Component onOff(String name, boolean on) {
		return Component.literal(name + ": ")
			.append(Component.literal(on ? "ON" : "OFF").withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED));
	}

	private static int nextSize(int current) {
		for (int size : SIZES) {
			if (size > current) {
				return size;
			}
		}
		return SIZES[0];
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, this.titleY, 0xFFFFFFFF);
		graphics.drawCenteredString(this.font,
			Component.literal("Particles to keep for this profile (OFF = removed)"), this.width / 2, this.hintY, 0xFFAAAAAA);
	}

	@Override
	public void onClose() {
		CombatConfig.saveAndApply();
		this.minecraft.setScreen(this.parent);
	}
}
