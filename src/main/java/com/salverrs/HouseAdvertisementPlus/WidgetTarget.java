package com.salverrs.HouseAdvertisementPlus;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.widgets.Widget;

@Getter
@Setter
public class WidgetTarget {
    private Widget widget;
    private String key;

    public WidgetTarget(String key, Widget widget)
    {
        this.widget = widget;
        this.key = key;
    }
}
