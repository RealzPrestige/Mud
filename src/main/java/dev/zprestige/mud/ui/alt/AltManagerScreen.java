package dev.zprestige.mud.ui.alt;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.shader.impl.ShadowShader;
import dev.zprestige.mud.ui.drawables.Drawable;
import dev.zprestige.mud.util.MC;
import dev.zprestige.mud.util.impl.MathUtil;
import dev.zprestige.mud.util.impl.RenderUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public class AltManagerScreen implements MC {
    private final ResourceLocation
            resourceLocation = new ResourceLocation("textures/icons/other/logo.png"),
            left = new ResourceLocation("textures/icons/other/left.png"),
            right = new ResourceLocation("textures/icons/other/right.png"),
            microsoft = new ResourceLocation("textures/icons/other/microsoft.png"),
            minecraft = new ResourceLocation("textures/icons/other/minecraft.png"),
            exit = new ResourceLocation("textures/icons/other/exit.png");
    private final TypeBar
            emailUser = new TypeBar("Email", 130.0f, 20.0f),
            password = new TypeBar("Password", 130.0f, 20.0f),
            search = new TypeBar("Search", 200.0f, 20.0f);
    private final ArrayList<AltButton> altButtons = new ArrayList<>();
    private final FakeWorld fakeWorld;
    private EntityOtherPlayerMP player;
    private static long delta, lastFrame;
    public boolean addCracked, rendering;
    private float textureOffset, addAnim,
            hoverExit, hoverDirect, hoverAdd;
    private int clicked;
    private float yaw;

    public AltManagerScreen() {
        WorldInfo info = new WorldInfo();
        info.setSpawn(new BlockPos(0, 0, 0));
        WorldProvider provider = new WorldProvider() {
            @SuppressWarnings("NullableProblems")
            @Override
            public DimensionType getDimensionType() {
                return null;
            }
        };
        fakeWorld = new FakeWorld(info, provider);
        provider.setWorld(fakeWorld);

        // NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(mc.getSession().getProfile());
        // player = new EntityOtherPlayerMP(fakeWorld, playerInfo.getGameProfile());
        // player.playerInfo = playerInfo;
        // playerInfo.setGameType(GameType.CREATIVE);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!rendering){
            return;
        }
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float width = scaledResolution.getScaledWidth(), height = scaledResolution.getScaledHeight();

        // Delta time
        delta = System.currentTimeMillis() - lastFrame;
        lastFrame = System.currentTimeMillis();

        // Top Bar Height
        float topHeight = 50.0f;

        // Background
        float offset = 30.0f;

        ShadowShader.invokeShadow();
        RenderUtil.rounded(offset, offset, width - offset, height - offset, 8.0f, Color.WHITE);
        ShadowShader.releaseShadow(10, 1);

        RenderUtil.rounded(offset, offset, width - offset, height - offset, 8.0f, shade(10));

        // Left bar
        float barX = offset + 5.0f, barY = offset + 5.0f, barWidth = 150.0f, barHeight = height - offset * 2.0f - 10.0f;

        RenderUtil.rounded(barX, barY, barX + barWidth, barY + barHeight, 8.0f, shade(5));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(barX, barY, barX + barWidth, barY + barHeight, 8.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        // Logo
        float logoX = barWidth + 45.0f, logoY = offset + 5.0f, logoBounds = 40.0f;
        RenderUtil.texture(logoX, logoY, logoX + logoBounds, logoY + logoBounds, Color.WHITE, resourceLocation);

        ShadowShader.invokeShadow();
        RenderUtil.texture(logoX, logoY, logoX + logoBounds, logoY + logoBounds, Color.WHITE, resourceLocation);
        ShadowShader.releaseShadow(2, 1);


        // Exit Button
        float exitX = width - offset - 35.0f, exitY = offset + 15.0f, exitWidth = 30.0f, exitHeight = 25.0f;

        RenderUtil.rounded(exitX, exitY, exitX + exitWidth, exitY + exitHeight, 8.0f, shade(7));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(exitX, exitY, exitX + exitWidth, exitY + exitHeight, 8.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        RenderUtil.texture(exitX + 7.5f, exitY + 5.0f, exitX + exitWidth - 7.5f, exitY + exitHeight - 5.0f, new Color(1.0f, 1.0f, 1.0f, hoverExit), exit);

        boolean exitHover = mouseX > exitX && mouseX < exitX + exitWidth && mouseY > exitY && mouseY < exitY + exitHeight;
        hoverExit = MathUtil.lerp(hoverExit, exitHover ? 1.0f : 0.5f, getDelta());
        if (exitHover && clicked == 0) {
            rendering = false;
        }

        // Account list
        float accountsX = barX + barWidth + 5.0f, accountsY = offset + 5.0f + topHeight, accountsWidth = (width - offset) - (barX + barWidth + 15.0f), accountsHeight = height - offset - 5.0f - accountsY;

        RenderUtil.rounded(accountsX, accountsY, accountsX + accountsWidth, accountsY + accountsHeight, 8.0f, shade(7));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(accountsX, accountsY, accountsX + accountsWidth, accountsY + accountsHeight, 8.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);


        // Search
        float searchX = accountsX + accountsWidth - 230.0f, searchY = offset + 15.0f, searchWidth = 200.0f, searchHeight = 25.0f;

        RenderUtil.rounded(searchX, searchY, searchX + searchWidth, searchY + searchHeight, 8.0f, shade(7));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(searchX, searchY, searchX + searchWidth, searchY + searchHeight, 8.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        search.setX(searchX);
        search.setY(searchY);
        search.drawScreen(mouseX, mouseY, partialTicks);

        if (clicked != -1) {
            search.mouseClicked(mouseX, mouseY, clicked);
        }

        // Login
        float loginX = barX + 5.0f, loginY = barY + 20.0f, loginWidth = barWidth - 10.0f, loginHeight = 100.0f;

        RenderUtil.rounded(loginX, loginY, loginX + loginWidth, loginY + loginHeight, 8.0f, shade(7));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(loginX, loginY, loginX + loginWidth, loginY + loginHeight, 8.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        Mud.fontManager.stringHud("Login", barX + barWidth / 2.0f - Mud.fontManager.stringWidth("Login") / 2.0f, loginY - Mud.fontManager.stringHeight() - 7.5f, Color.WHITE);

        // Fields
        float fieldX = loginX + 5.0f, fieldY = loginY + 5.0f, fieldWidth = loginWidth - 10.0f, fieldHeight = 20.0f, fieldOffset = 25.0f;

        RenderUtil.rounded(fieldX, fieldY, fieldX + fieldWidth, fieldY + fieldHeight, 5.0f, shade(5));
        RenderUtil.rounded(fieldX, fieldY + fieldOffset, fieldX + fieldWidth, fieldY + fieldOffset + fieldHeight, 5.0f, shade(5));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(fieldX, fieldY, fieldX + fieldWidth, fieldY + fieldHeight, 5.0f, Color.WHITE);
        RenderUtil.rounded(fieldX, fieldY + fieldOffset, fieldX + fieldWidth, fieldY + fieldOffset + fieldHeight, 5.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        // Render Text
        emailUser.setAlias(addCracked ? "Username" : "Email");
        emailUser.setX(fieldX);
        emailUser.setY(fieldY);
        emailUser.drawScreen(mouseX, mouseY, partialTicks);

        password.setX(fieldX);
        password.setY(fieldY + fieldOffset);
        password.drawScreen(mouseX, mouseY, partialTicks);

        if (clicked != -1) {
            emailUser.mouseClicked(mouseX, mouseY, clicked);
            password.mouseClicked(mouseX, mouseY, clicked);
        }

        addAnim = MathUtil.lerp(addAnim, !emailUser.getText().isEmpty() && !password.getText().isEmpty() ? 1.0f : 0.5f, getDelta());

        // Circles
        float circleBounds = 7.5f, circleY = fieldY + fieldOffset + fieldHeight + 10.0f, circleLeftX = loginX + 10.0f, circleRightX = loginX + loginWidth - 10.0f - circleBounds;

        RenderUtil.circle(circleLeftX, circleY, circleBounds, shade(5));
        RenderUtil.circle(circleRightX, circleY, circleBounds, shade(5));

        ShadowShader.invokeShadow();
        RenderUtil.circle(circleLeftX, circleY, circleBounds, shade(5));
        RenderUtil.circle(circleRightX, circleY, circleBounds, shade(5));
        ShadowShader.releaseShadow(5, 1);


        // Circle textures
        float textureBounds = 15.0f, textureOffset = textureBounds / 2.0f, circleHeight = circleY - textureOffset + textureBounds;
        RenderUtil.texture(circleLeftX, circleY, circleLeftX - textureOffset + textureBounds, circleHeight, new Color(1.0f, 1.0f, 1.0f, 0.5f + this.textureOffset / 220.0f), left);
        RenderUtil.texture(circleRightX, circleY, circleRightX - textureOffset + textureBounds, circleHeight, new Color(1.0f, 1.0f, 1.0f, 1.0f - this.textureOffset / 220.0f), right);

        // Clicking between Microsoft/Cracked
        if (clicked == 0) {
            if (mouseY > circleY && mouseY < circleY - textureOffset + textureBounds) {
                if (addCracked) {
                    if (mouseX > circleLeftX && mouseX < circleLeftX - textureOffset + textureBounds) {
                        addCracked = false;
                    }
                } else {
                    if (mouseX > circleRightX && mouseX < circleRightX - textureOffset + textureBounds) {
                        addCracked = true;
                    }
                }
            }
        }

        // Cracked/Microsoft switch
        this.textureOffset = MathUtil.lerp(this.textureOffset, addCracked ? 110.0f : 0.0f, getDelta());

        float iconX = loginX + loginWidth / 2.0f - Mud.fontManager.stringWidth("Microsoft") / 2.0f - this.textureOffset, iconY = circleY + 5.5f - Mud.fontManager.stringHeight();

        RenderUtil.prepareScissor(circleLeftX + circleBounds, circleY, circleRightX, circleHeight + 5.0f);

        RenderUtil.texture(iconX - 2.0f, iconY, iconX + 7.5f, iconY + 7.5f, Color.WHITE, microsoft);
        Mud.fontManager.string("Microsoft", iconX + 8.5f, iconY + 1.0f, Color.WHITE);

        iconX = loginX + loginWidth / 2.0f - Mud.fontManager.stringWidth("Cracked") / 2.0f + 110.0f - this.textureOffset;
        RenderUtil.texture(iconX - 2.0f, iconY, iconX + 6.5f, iconY + 8.5f, Color.WHITE, minecraft);
        Mud.fontManager.string("Cracked", iconX + 8.5f, iconY + 1.0f, Color.WHITE);

        RenderUtil.releaseScissor();

        // Direct/Add
        float buttonY = loginY + loginHeight - 25.0f, buttonHeight = 20.0f, buttonWidth = loginWidth / 2.0f - 10.0f;
        float leftButtonX = loginX + 5.0f, rightButtonX = loginX + loginWidth / 2.0f + 5.0f;

        RenderUtil.rounded(leftButtonX, buttonY, leftButtonX + buttonWidth, buttonY + buttonHeight, 8.0f, shade(5));
        RenderUtil.rounded(rightButtonX, buttonY, rightButtonX + buttonWidth, buttonY + buttonHeight, 8.0f, shade(5));

        RenderUtil.rounded(leftButtonX, buttonY, leftButtonX + buttonWidth, buttonY + buttonHeight, 8.0f, new Color(0.0f, 0.0f, 0.0f, hoverDirect * 0.2f));
        RenderUtil.rounded(rightButtonX, buttonY, rightButtonX + buttonWidth, buttonY + buttonHeight, 8.0f, new Color(0.0f, 0.0f, 0.0f, hoverAdd* 0.2f));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(leftButtonX, buttonY, leftButtonX + buttonWidth, buttonY + buttonHeight, 8.0f, shade(5));
        RenderUtil.rounded(rightButtonX, buttonY, rightButtonX + buttonWidth, buttonY + buttonHeight, 8.0f, shade(5));
        ShadowShader.releaseShadow(5, 1);

        boolean hoverButtons = mouseY > buttonY && mouseY < buttonY + buttonHeight;
        boolean hoverDirect = hoverButtons && mouseX > leftButtonX && mouseX < leftButtonX + buttonWidth;
        boolean hoverAdd = hoverButtons && mouseX > rightButtonX && mouseX < rightButtonX + buttonWidth;

        this.hoverDirect = MathUtil.lerp(this.hoverDirect, hoverDirect ? 1.0f : 0.0f, getDelta());
        this.hoverAdd = MathUtil.lerp(this.hoverAdd, hoverAdd ? 1.0f : 0.0f, getDelta());

        if (clicked == 0) {
            if (hoverButtons) {
                if (hoverDirect) {
                    Alt alt = new Alt(emailUser.getText(), password.getText(), addCracked ? Alt.AltType.CRACKED : Alt.AltType.MICROSOFT);
                    alt.login();
                    emailUser.setText("");
                    password.setText("");
                }
                if (hoverAdd) {
                    Alt alt = new Alt(emailUser.getText(), password.getText(), addCracked ? Alt.AltType.CRACKED : Alt.AltType.MICROSOFT);
                    alt.login();
                    altButtons.add(new AltButton(alt, 170.0f, 92.5f));
                    emailUser.setText("");
                    password.setText("");
                }
            }
        }


        // Button text
        float scale = 0.8f;
        RenderUtil.invokeScale(scale);
        String direct = "Direct Login";
        Mud.fontManager.string(direct, (leftButtonX + buttonWidth / 2.0f - Mud.fontManager.stringWidth(direct) * scale / 2.0f) / scale, (buttonY + buttonHeight / 2.0f - Mud.fontManager.stringHeight() * scale) / scale, new Color(1.0f, 1.0f, 1.0f, addAnim));

        String add = "Add Account";
        Mud.fontManager.string(add, (rightButtonX + buttonWidth / 2.0f - Mud.fontManager.stringWidth(add) * scale / 2.0f) / scale, (buttonY + buttonHeight / 2.0f - Mud.fontManager.stringHeight() * scale) / scale, new Color(1.0f, 1.0f, 1.0f, addAnim));

        RenderUtil.resetScale();

        // Player
        float playerY = barHeight - 150.0f, playerHeight = barHeight + 5.0f;
        RenderUtil.rounded(loginX + 5.0f, playerY, loginX + loginWidth - 5.0f, playerHeight, 8.0f, shade(4));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(loginX + 5.0f, playerY, loginX + loginWidth - 5.0f, playerHeight, 8.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        // Draw entity last to not fuck up shaders...

        // Name tag
        float nameX = loginX + 5.0f, nameWidth = loginX + loginWidth - 5.0f, nameY = playerY - 30.0f, nameHeight = playerY - 10.0f;

        RenderUtil.rounded(nameX, nameY, nameWidth, nameHeight, 8.0f, shade(4));

        ShadowShader.invokeShadow();
        RenderUtil.rounded(nameX, nameY, nameWidth, nameHeight, 8.0f, Color.WHITE);
        ShadowShader.releaseShadow(5, 1);

        Mud.fontManager.stringHud(mc.getSession().getUsername(), nameX + (nameWidth - nameX) / 2.0f - Mud.fontManager.stringWidth(mc.getSession().getUsername()) / 2.0f, nameY + 10.0f - Mud.fontManager.stringHeight() / 2.0f, Color.WHITE);


        // Alts
        float deltaX = accountsX + 10.0f;
        float deltaY = accountsY + 15.0f;
        for (AltButton altButton : altButtons) {
            altButton.x = deltaX;
            altButton.y = deltaY;
            altButton.drawScreen(mouseX, mouseY, partialTicks);
            deltaX += altButton.width + 10.0f;
            if (deltaX >= accountsX + accountsWidth - 10.0f) {
                deltaY += altButton.height + 5.0f;
                deltaX = accountsX + 10.0f;
            }
        }


        // Draw entity at the very end:
        RenderUtil.drawEntityOnScreen(loginX + loginWidth / 2.0f, barHeight, 75, yaw, 0, player);

        for (AltButton altButton : altButtons){
            altButton.drawEntity();
        }

        // Entity yaw
        yaw += getDelta() * 10.0f;
        if (yaw >= 360.0f) {
            yaw = 0.0f;
        }

        // Reset clicked
        clicked = -1;
    }

    public void keyTyped(char typedChar, int keyCode) {
        emailUser.keyTyped(typedChar, keyCode);
        password.keyTyped(typedChar, keyCode);
        search.keyTyped(typedChar, keyCode);
    }

    public void setClicked(int clicked) {
        this.clicked = clicked;
    }

    public static Color shade(int i) {
        return new Color(28 + i, 31 + i, 44 + i);
    }

    private static float getDelta() {
        return delta * 0.01f;
    }

    private static long sys = 0L;

    private static String typingIcon() {
        if (System.currentTimeMillis() - sys > 1000) {
            sys = System.currentTimeMillis();
            return "";
        }
        if (System.currentTimeMillis() - sys > 500) {
            return "_";
        }
        return "";
    }

    private class AltButton extends Drawable {
        private final Alt alt;
        private final float width, height;
        private float x, y;
        private final EntityOtherPlayerMP entity;

        public AltButton(Alt alt, float width, float height) {
            this.alt = alt;
            this.width = width;
            this.height = height;
            NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(alt.getAltSession().getProfile());
            entity = new EntityOtherPlayerMP(fakeWorld, playerInfo.getGameProfile());
            entity.playerInfo = playerInfo;
            playerInfo.setGameType(GameType.CREATIVE);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            RenderUtil.rounded(x, y, x + width, y + height, 8.0f, shade(10));

            ShadowShader.invokeShadow();
            RenderUtil.rounded(x, y, x + width, y + height, 8.0f, Color.WHITE);
            ShadowShader.releaseShadow(5, 1);

            RenderUtil.rounded(x + 5.0f, y + 5.0f, x + width - 5.0f, y + 25.0f, 3.0f, shade(7));
            RenderUtil.rounded(x + 5.0f, y + 30.0f, x + width - 5.0f, y + 50.0f, 3.0f, shade(7));

            ShadowShader.invokeShadow();
            RenderUtil.rounded(x + 5.0f, y + 5.0f, x + width - 5.0f, y + 25.0f, 3.0f, Color.WHITE);
            RenderUtil.rounded(x + 5.0f, y + 30.0f, x + width - 5.0f, y + 50.0f, 3.0f, Color.WHITE);
            ShadowShader.releaseShadow(5, 1);

            Mud.fontManager.stringHud(alt.getLogin(), x + 10.0f, y + 15.0f - Mud.fontManager.stringHeight() / 2.0f, Color.WHITE);

            StringBuilder pass = new StringBuilder();
            for (char ignored : alt.getPassword().toCharArray()){
                pass.append("*");
            }
            Mud.fontManager.stringHud(pass.toString(), x + 10.0f, y + 40.0f - Mud.fontManager.stringHeight() / 2.0f, Color.WHITE);


        }

        public void drawEntity(){
            RenderUtil.prepareScissor(x, y, x + width,y + height);
            RenderUtil.drawEntityOnScreen(x + 40.0f, y + height * 1.6f, 50, -35, 0, entity);
            RenderUtil.releaseScissor();
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        }
    }

    private static class TypeBar extends Drawable {
        private float x, y, anim;
        private final float width, height;
        private String alias, text = "";
        private boolean searching;

        public TypeBar(String alias, float width, float height) {
            this.alias = alias;
            this.width = width;
            this.height = height;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            anim = MathUtil.lerp(anim, text.isEmpty() ? 0.5f : 1.0f, getDelta());
            String t = text.isEmpty() ? alias : text;

            RenderUtil.prepareScissor(x, y - 2.0f, x + width - 5.0f, y + height + 2.0f);

            if (alias.equals("Password") && !text.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (char ignore : t.toCharArray()) {
                    sb.append("*");
                }
                t = sb.toString();
            }

            Mud.fontManager.string(t + (searching ? typingIcon() : ""), x + 5.0f, y + height / 2.0f - Mud.fontManager.stringHeight() / 2.0f, new Color(1.0f, 1.0f, 1.0f, anim));

            RenderUtil.releaseScissor();
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            if (mouseButton == 0 && inside(mouseX, mouseY)) {
                setSearching(!searching);
            } else {
                searching = false;
            }
        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {
            if (searching) {
                text = type(text, typedChar, keyCode);
            }
        }

        private boolean inside(int mouseX, int mouseY) {
            return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        }

        private String type(String string, char typedChar, int keyCode) {
            String newString = string;
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                        newString = "";
                    }
                    if (newString.length() > 0) {
                        newString = newString.substring(0, newString.length() - 1);
                    }
                    break;
                case Keyboard.KEY_RETURN:
                    searching = false;
                    break;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        newString = newString + typedChar;
                        break;
                    }
            }
            return newString;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }

        public void setSearching(boolean searching) {
            this.searching = searching;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @SuppressWarnings("All")
    public static class FakeWorld extends World {
        public FakeWorld(WorldInfo info, WorldProvider worldProvider) {
            super(null, info, worldProvider, null, false);
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return null;
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return false;
        }
    }
}
