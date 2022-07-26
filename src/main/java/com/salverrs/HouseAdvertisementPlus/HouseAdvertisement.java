package com.salverrs.HouseAdvertisementPlus;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.widgets.Widget;

import java.util.HashMap;

@Getter
@Setter
public class HouseAdvertisement {

    private int yOffset;
    private int rowIndex;
    private String playerName;
    private String location;
    private int constructionLvl;
    private boolean hasGuildedAlter;
    private int nexusLvl;
    private int jewelleryLvl;
    private int poolLvl;
    private int spellAltarLvl;
    private boolean hasArmourStand;
    private boolean isFavourite;
    private boolean isBlacklisted;
    private boolean isHidden;
    private boolean isAnotherLocation;
    private HashMap<String, Widget> advertWidgets;

    public HouseAdvertisement(String playerName, int yOffset) {
        this.playerName = playerName;
        this.yOffset = yOffset;
        this.advertWidgets = new HashMap<String, Widget>();
        isAnotherLocation = true;
    }

    public void addWidget(String key, Widget w)
    {
        advertWidgets.put(key, w);
    }

    public Widget getWidget(String key)
    {
        return advertWidgets.get(key);
    }

    public void swapRowWith(HouseAdvertisement otherAdvert)
    {
        for (String key : advertWidgets.keySet())
        {
            final Widget a = advertWidgets.get(key);
            final Widget b = otherAdvert.getAdvertWidgets().get(key);
            swapYOffset(a, b);
        }

        final int thisYOffset = yOffset;
        final int thisRowIndex = rowIndex;

        setYOffset(otherAdvert.getYOffset());
        setRowIndex(otherAdvert.getRowIndex());
        otherAdvert.setYOffset(thisYOffset);
        otherAdvert.setRowIndex(thisRowIndex);
    }

    private void swapYOffset(Widget widget1, Widget widget2)
    {
        final int w1Offset = widget1.getRelativeY();
        widget1.setOriginalY(widget2.getRelativeY());
        widget2.setOriginalY(w1Offset);
        widget1.revalidate();
        widget2.revalidate();
    }

    public void setAsHidden(boolean hidden)
    {
        isHidden = hidden;
        for (Widget w : advertWidgets.values())
        {
            w.setHidden(hidden);
        }
    }

}
