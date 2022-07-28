package com.salverrs.HouseAdvertisementPlus;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;

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
    private boolean isAnotherLocation;
    private boolean isVisible;
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
        for (String key : AdvertID.ADVERT_WIDGET_KEYS)
        {
            swapWidgets(key, otherAdvert);
        }

        final int thisYOffset = yOffset;
        final int thisRowIndex = rowIndex;

        setYOffset(otherAdvert.getYOffset());
        setRowIndex(otherAdvert.getRowIndex());
        otherAdvert.setYOffset(thisYOffset);
        otherAdvert.setRowIndex(thisRowIndex);
    }

    private void swapWidgets(String key, HouseAdvertisement otherAdvert)
    {
        final Widget a = advertWidgets.get(key);
        final Widget b = otherAdvert.getWidget(key);

        if (a != null && b != null)
        {
            swapYOffset(a, b);
        }
        else if (a != null)
        {
            swapYOffsetWithMissingWidget(this, otherAdvert, key);
        }
        else if (b != null)
        {
            swapYOffsetWithMissingWidget(otherAdvert, this, key);
        }

    }

    private void swapYOffsetWithMissingWidget(HouseAdvertisement hasWidget, HouseAdvertisement missingWidget, String key)
    {
        final Widget target = hasWidget.getWidget(key);
        final int thisNameYOffset = hasWidget.getWidget(AdvertID.KEY_NAME).getRelativeY();
        final int otherNameYOffset = missingWidget.getWidget(AdvertID.KEY_NAME).getRelativeY();
        final int yDiff = thisNameYOffset - otherNameYOffset;

        target.setOriginalY(target.getRelativeY() + yDiff);
        target.revalidate();
    }

    private void swapYOffset(Widget widget1, Widget widget2)
    {
        final int w1Offset = widget1.getRelativeY();
        widget1.setOriginalY(widget2.getRelativeY());
        widget2.setOriginalY(w1Offset);
        widget1.revalidate();
        widget2.revalidate();
    }

    public void setIsVisible(boolean visible)
    {
        isVisible = visible;
        for (Widget w : advertWidgets.values())
        {
            w.setOpacity(visible ? 0 : 255);
            w.setFontId(visible ? AdvertID.ADVERT_DEFAULT_FONT_ID : -1);
        }
    }

}
