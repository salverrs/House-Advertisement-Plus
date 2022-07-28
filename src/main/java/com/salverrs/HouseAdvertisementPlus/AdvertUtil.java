package com.salverrs.HouseAdvertisementPlus;

import com.salverrs.HouseAdvertisementPlus.Filters.YesNoFilter;
import net.runelite.api.widgets.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvertUtil {

    public static boolean namesEqual(String a, String b)
    {
        String nameA = normalizeName(a);
        String nameB = normalizeName(b);
        return nameA.equals(nameB);
    }

    public static String normalizeName(String name)
    {
        return name.replaceAll("\u00a0"," ").toLowerCase().trim();
    }

    public static List<String> normalizeNames(List<String> names)
    {
        ArrayList<String> normalized = new ArrayList();
        names.forEach(n -> normalized.add(normalizeName(n)));
        return normalized;
    }

    public static String getPlayerFromOpArg(Widget widget, int argIndex)
    {
        Object[] args = widget.getOnOpListener();

        if (args == null || args.length < argIndex + 1)
            return null;

        if (args[argIndex].getClass() != String.class)
            return null;

        return (String)args[argIndex];
    }

    public static boolean yesNoPasses(YesNoFilter filter, boolean actual)
    {
        final int fVal = filter == YesNoFilter.Y ? 1 : 0;
        final int actualVal = actual ? 1 : 0;
        return actualVal >= fVal;
    }

}
