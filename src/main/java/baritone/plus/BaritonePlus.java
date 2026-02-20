package baritone.plus;

import baritone.plus.combat.FightBot;
import baritone.plus.crystal.CrystalBot;
import baritone.plus.mine.MineBot;
import baritone.plus.gui.FeatureGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Main entry point for Baritone Plus — adds Fight Bot, Crystal Bot,
 * improved Mine Bot, and a unified GUI toggled with Right Shift.
 */
public class BaritonePlus implements ClientModInitializer {

    public static final String MOD_ID = "baritone_plus";

    private static BaritonePlus instance;

    private final FightBot fightBot = new FightBot();
    private final CrystalBot crystalBot = new CrystalBot();
    private final MineBot mineBot = new MineBot();

    private KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        instance = this;

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Baritone Plus GUI",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "Baritone Plus"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openGuiKey.wasPressed()) {
                client.openScreen(new FeatureGui(client.currentScreen));
            }

            if (client.player != null && client.world != null) {
                fightBot.tick(client);
                crystalBot.tick(client);
                mineBot.tick(client);
            }
        });
    }

    public static BaritonePlus getInstance() {
        return instance;
    }

    public FightBot getFightBot() {
        return fightBot;
    }

    public CrystalBot getCrystalBot() {
        return crystalBot;
    }

    public MineBot getMineBot() {
        return mineBot;
    }
}
