package dev.zprestige.mud.manager;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.bus.EventListener;
import dev.zprestige.mud.events.impl.render.Render3DEvent;
import dev.zprestige.mud.util.MC;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;

public class FrustumManager implements MC {
    private final ICamera camera = new Frustum();

    public FrustumManager() {
        Mud.eventBus.registerListener(this);
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (mc.getRenderViewEntity() != null) {
            camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        }
    }

    public boolean isInsideFrustum(AxisAlignedBB bb) {
        return camera.isBoundingBoxInFrustum(bb.grow(2.0));
    }
}
