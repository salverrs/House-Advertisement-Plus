package com.salverrs.HouseAdvertisementPlus;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;

import java.util.HashMap;

@Getter
@Setter
public class HouseAdvertisement {

    private int originalYOffset;
    private int rowIndex;
    private int favouritePriority;
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

    public void setIsVisible(boolean visible)
    {
        isVisible = visible;
        for (Widget w : advertWidgets.values())
        {
            w.setOpacity(visible ? 0 : 255);
            w.setFontId(visible ? AdvertID.ADVERT_DEFAULT_FONT_ID : -1);
        }
    }

    public void setRow(int targetRowIndex)
    {
        for (String widgetKey : advertWidgets.keySet())
        {
            final Widget widget = advertWidgets.get(widgetKey);
            final int height = widget.getHeight();
            final int relativeY = widget.getRelativeY();

            if (widgetKey == AdvertID.KEY_ENTER)
            {
                final int numPadding = (rowIndex * 2 + 1);
                final int paddingHeight = (relativeY - (height * rowIndex)) / numPadding;
                final int targetNumPadding = (targetRowIndex * 2 + 1);
                final int targetYOffset = targetRowIndex * height + (targetNumPadding * paddingHeight);
                widget.setOriginalY(targetYOffset);
            }
            else
            {
                final int targetYOffset = targetRowIndex * widget.getHeight();
                widget.setOriginalY(targetYOffset);
            }

            widget.revalidate();
        }

        rowIndex = targetRowIndex;
    }

}
