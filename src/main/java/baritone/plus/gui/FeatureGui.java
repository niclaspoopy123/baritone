package baritone.plus.gui;

import baritone.plus.BaritonePlus;
import baritone.plus.combat.FightBot;
import baritone.plus.crystal.CrystalBot;
import baritone.plus.mine.MineBot;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

/**
 * FeatureGui — unified settings screen for all Baritone Plus modules.
 * Opened by pressing Right Shift.
 */
public class FeatureGui extends Screen {

    private final Screen parent;

    public FeatureGui(Screen parent) {
        super(new LiteralText("Baritone Plus"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        BaritonePlus bp = BaritonePlus.getInstance();
        FightBot fight = bp.getFightBot();
        CrystalBot crystal = bp.getCrystalBot();
        MineBot mine = bp.getMineBot();

        int centerX = this.width / 2;
        int y = 40;
        int btnWidth = 200;
        int btnHeight = 20;
        int gap = 24;

        // --- Fight Bot section ---
        addButton(new ButtonWidget(centerX - btnWidth / 2, y, btnWidth, btnHeight,
                fightLabel(fight),
                btn -> {
                    fight.setEnabled(!fight.isEnabled());
                    btn.setMessage(fightLabel(fight));
                }));
        y += gap;

        addButton(new ButtonWidget(centerX - btnWidth / 2, y, btnWidth, btnHeight,
                targetPlayersLabel(fight),
                btn -> {
                    fight.setTargetPlayers(!fight.isTargetPlayers());
                    btn.setMessage(targetPlayersLabel(fight));
                }));
        y += gap;

        // --- Crystal Bot section ---
        y += gap / 2; // spacer
        addButton(new ButtonWidget(centerX - btnWidth / 2, y, btnWidth, btnHeight,
                crystalLabel(crystal),
                btn -> {
                    crystal.setEnabled(!crystal.isEnabled());
                    btn.setMessage(crystalLabel(crystal));
                }));
        y += gap;

        // --- Mine Bot section ---
        y += gap / 2; // spacer
        addButton(new ButtonWidget(centerX - btnWidth / 2, y, btnWidth, btnHeight,
                mineLabel(mine),
                btn -> {
                    mine.setEnabled(!mine.isEnabled());
                    btn.setMessage(mineLabel(mine));
                }));
        y += gap;

        addButton(new ButtonWidget(centerX - btnWidth / 2, y, btnWidth, btnHeight,
                veinMineLabel(mine),
                btn -> {
                    mine.setVeinMine(!mine.isVeinMine());
                    btn.setMessage(veinMineLabel(mine));
                }));
        y += gap;

        addButton(new ButtonWidget(centerX - btnWidth / 2, y, btnWidth, btnHeight,
                avoidLavaLabel(mine),
                btn -> {
                    mine.setAvoidLava(!mine.isAvoidLava());
                    btn.setMessage(avoidLavaLabel(mine));
                }));
        y += gap;

        // --- Close button ---
        y += gap / 2;
        addButton(new ButtonWidget(centerX - btnWidth / 2, y, btnWidth, btnHeight,
                "Close",
                btn -> onClose()));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        drawCenteredString(this.font, "Baritone Plus", this.width / 2, 15, 0x55FFFF);
        drawCenteredString(this.font, "Press Right Shift to toggle this menu",
                this.width / 2, this.height - 15, 0xAAAAAA);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.minecraft.openScreen(parent);
    }

    // --- Label helpers ---

    private static String fightLabel(FightBot f) {
        return "Fight Bot: " + (f.isEnabled() ? "\u00a7aON" : "\u00a7cOFF");
    }

    private static String targetPlayersLabel(FightBot f) {
        return "Target Players: " + (f.isTargetPlayers() ? "\u00a7aYES" : "\u00a7cNO");
    }

    private static String crystalLabel(CrystalBot c) {
        return "Crystal Bot: " + (c.isEnabled() ? "\u00a7aON" : "\u00a7cOFF");
    }

    private static String mineLabel(MineBot m) {
        return "Mine Bot: " + (m.isEnabled() ? "\u00a7aON" : "\u00a7cOFF");
    }

    private static String veinMineLabel(MineBot m) {
        return "Vein Mine: " + (m.isVeinMine() ? "\u00a7aON" : "\u00a7cOFF");
    }

    private static String avoidLavaLabel(MineBot m) {
        return "Avoid Lava: " + (m.isAvoidLava() ? "\u00a7aON" : "\u00a7cOFF");
    }
}
