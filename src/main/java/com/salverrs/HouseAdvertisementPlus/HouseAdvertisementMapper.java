package com.salverrs.HouseAdvertisementPlus;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class HouseAdvertisementMapper {

    private Client client;
    private List<String> favourites;

    private List<String> blacklist;
    public HouseAdvertisementMapper(Client client, List<String> favourites, List<String> blacklist)
    {
        this.favourites = favourites;
        this.client = client;
        this.blacklist = blacklist;
    }

    public HashMap<String, HouseAdvertisement> GetHouseAdvertisements() {
        final Widget root = client.getWidget(AdvertID.WIDGET_GROUP_ID, 0);
        if (root == null)
            return null;

        HashMap<Integer, HouseAdvertisement> advertYOffsetMap = new HashMap();
        HashMap<String, HouseAdvertisement> advertPlayerNameMap = new HashMap();

        final Widget[] playerNameWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_NAME_INDEX).getDynamicChildren();
        final Widget[] constructionLvlWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_CONSTRUCTION_LVL_INDEX).getDynamicChildren();
        final Widget[] guildedAlterWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_GUILDED_ALTER_INDEX).getDynamicChildren();
        final Widget[] nexusLvlWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_NEXUS_INDEX).getDynamicChildren();
        final Widget[] jewelleryLvlWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_JEWELLERY_INDEX).getDynamicChildren();
        final Widget[] poolLvlWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_POOL_INDEX).getDynamicChildren();
        final Widget[] spellAltarLvlWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_SPELL_ALTER_INDEX).getDynamicChildren();
        final Widget[] armourStandWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_ARMOUR_STAND_INDEX).getDynamicChildren();
        final Widget[] enterArrowWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_ENTER_ARROW_INDEX).getDynamicChildren();
        final Widget[] locationNameWidgets = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_LOCATION_INDEX).getDynamicChildren();

        for (Widget w : playerNameWidgets)
        {
            if (w.isHidden())
                continue;

            final String playerName = AdvertUtil.normalizeName(w.getText());
            final int yOffset = w.getRelativeY();

            if (playerName == null || playerName == "")
                continue;

            final HouseAdvertisement advert = new HouseAdvertisement(playerName, yOffset);

            advert.addWidget(AdvertID.KEY_NAME, w);
            advertYOffsetMap.put(yOffset, advert);
            advertPlayerNameMap.put(playerName, advert);

            if (favourites.contains(playerName))
            {
                advert.setFavourite(true);
            }

            if (blacklist.contains(playerName))
            {
                advert.setBlacklisted(true);
            }
        }

        for (Widget w : constructionLvlWidgets)
        {
            if (w.isHidden())
                continue;

            final int yOffset = w.getRelativeY();
            final HouseAdvertisement advert = advertYOffsetMap.get(yOffset);
            if (advert == null)
                continue;

            final String text = w.getText();
            if (text == null || text.equals(""))
                continue;

            final int constructionLvl = Integer.parseInt(text);
            advert.setConstructionLvl(constructionLvl);
            advert.addWidget(AdvertID.KEY_CONSTRUCTION_LVL, w);
        }

        for (Widget w : guildedAlterWidgets)
        {
            if (w.isHidden())
                continue;

            final int yOffset = w.getRelativeY();
            final HouseAdvertisement advert = advertYOffsetMap.get(yOffset);
            if (advert == null)
                continue;

            final String text = w.getText();
            if (text == null || text.equals(""))
                continue;

            final boolean hasGuildedAlter = BooleanUtils.toBoolean(text);
            advert.setHasGuildedAlter(hasGuildedAlter);
            advert.addWidget(AdvertID.KEY_ALTAR, w);
        }

        for (Widget w : nexusLvlWidgets)
        {
            if (w.isHidden())
                continue;

            final int yOffset = w.getRelativeY();
            final HouseAdvertisement advert = advertYOffsetMap.get(yOffset);
            if (advert == null)
                continue;

            final String text = w.getText();
            if (text == null || text.equals(""))
                continue;

            final int nexusLvl = Integer.parseInt(text);
            advert.setNexusLvl(nexusLvl);
            advert.addWidget(AdvertID.KEY_NEXUS_LVL, w);
        }

        for (Widget w : jewelleryLvlWidgets)
        {
            if (w.isHidden())
                continue;

            final int yOffset = w.getRelativeY();
            final HouseAdvertisement advert = advertYOffsetMap.get(yOffset);
            if (advert == null)
                continue;

            final String text = w.getText();
            if (text == null || text.equals(""))
                continue;

            final int jewelleryLvl = Integer.parseInt(text);
            advert.setJewelleryLvl(jewelleryLvl);
            advert.addWidget(AdvertID.KEY_JEWELLERY_LVL, w);
        }

        for (Widget w : poolLvlWidgets)
        {
            if (w.isHidden())
                continue;

            final int yOffset = w.getRelativeY();
            final HouseAdvertisement advert = advertYOffsetMap.get(yOffset);
            if (advert == null)
                continue;

            final String text = w.getText();
            if (text == null || text.equals(""))
                continue;

            final int poolLvl = Integer.parseInt(text);
            advert.setPoolLvl(poolLvl);
            advert.addWidget(AdvertID.KEY_POOL_LVL,w);
        }

        for (Widget w : spellAltarLvlWidgets)
        {
            if (w.isHidden())
                continue;

            final int yOffset = w.getRelativeY();
            final HouseAdvertisement advert = advertYOffsetMap.get(yOffset);
            if (advert == null)
                continue;

            final String text = w.getText();
            if (text == null || text.equals(""))
                continue;

            final int spellAltarLvl = Integer.parseInt(text);
            advert.setSpellAltarLvl(spellAltarLvl);
            advert.addWidget(AdvertID.KEY_SPELL_ALTAR_LVL, w);
        }

        for (Widget w : armourStandWidgets)
        {
            if (w.isHidden())
                continue;

            final int yOffset = w.getRelativeY();
            final HouseAdvertisement advert = advertYOffsetMap.get(yOffset);
            if (advert == null)
                continue;

            final String text = w.getText();
            if (text == null || text.equals(""))
                continue;

            final boolean hasArmourStand = BooleanUtils.toBoolean(text);
            advert.setHasArmourStand(hasArmourStand);
            advert.addWidget(AdvertID.KEY_ARMOUR_STAND, w);
        }

        for (Widget w : enterArrowWidgets)
        {
            if (w.isHidden())
                continue;

            final String nameArg = AdvertUtil.getPlayerFromOpArg(w, AdvertID.ADVERT_ARROW_PLAYER_ARG_INDEX);
            if (nameArg == null || nameArg.equals(""))
                continue;

            final String playerName = AdvertUtil.normalizeName(nameArg);
            final HouseAdvertisement advert = advertPlayerNameMap.get(playerName);
            if (advert == null)
                continue;

            advert.setAnotherLocation(false);
            advert.addWidget(AdvertID.KEY_ENTER, w);
        }

        for (Widget w : locationNameWidgets)
        {
            if (w.isHidden())
                continue;

            final int yOffset = w.getRelativeY();
            final HouseAdvertisement advert = advertYOffsetMap.get(yOffset);
            if (advert == null)
                continue;

            final String text = w.getText();
            if (text == null || text.equals(""))
                continue;

            advert.setLocation(text);
            advert.addWidget(AdvertID.KEY_LOCATION, w);
        }

        final List<HouseAdvertisement> adverts = new ArrayList(advertPlayerNameMap.values());
        adverts.sort(Comparator.comparing(a -> a.getYOffset()));

        int rowIndex = 0;
        for (int i = 0; i < adverts.size(); i++)
        {
            HouseAdvertisement advert = adverts.get(i);
            advert.setRowIndex(rowIndex);

            rowIndex++;
        }

        return advertPlayerNameMap;
    }
}
