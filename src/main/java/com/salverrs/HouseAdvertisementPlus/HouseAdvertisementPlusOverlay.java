package com.salverrs.HouseAdvertisementPlus;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;


public class HouseAdvertisementPlusOverlay extends Overlay {

    @Inject
    private HouseAdvertisementPlusPlugin plugin;

    public HouseAdvertisementPlusOverlay() {
        setLayer(OverlayLayer.MANUAL);
        drawAfterLayer(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_CONTAINER_ID);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        plugin.renderHighlights(graphics);
        return null;
    }
}
