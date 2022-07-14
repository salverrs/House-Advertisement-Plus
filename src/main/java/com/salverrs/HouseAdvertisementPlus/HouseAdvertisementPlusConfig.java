package com.salverrs.HouseAdvertisementPlus;

import com.salverrs.HouseAdvertisementPlus.Filters.NumericFilters;
import com.salverrs.HouseAdvertisementPlus.Filters.YesNoFilter;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(HouseAdvertisementPlusPlugin.ConfigGroup)
public interface HouseAdvertisementPlusConfig extends Config
{
	@ConfigSection(
			name = "Filters",
			description = "House advertisement filters.",
			position = 0
	)
	String filtersSection = "filters";

	@ConfigSection(
			name = "Favourites",
			description = "Favourite house advertisements.",
			position = 1
	)
	String favouritesSection = "favourites";

	@ConfigSection(
			name = "Blacklist",
			description = "House advertisements to always hide.",
			position = 2
	)
	String blackListSection = "blacklist";

	@ConfigSection(
			name = "Menu Options",
			description = "House Advertisement menu options.",
			position = 3
	)
	String menuOptionsSection = "menuOptions";

	@ConfigItem(
			keyName = "useAdvertFilters",
			name = "Enable Filters",
			description = "Enable the filters feature for the advertisement board.",
			position = 0,
			section = filtersSection
	)
	default boolean useFilters() { return false; }

	@Range(
			max = NumericFilters.CONSTRUCTION_LEVEL_MAX,
			min = NumericFilters.ALL_FILTER_MIN
	)
	@ConfigItem(
			keyName = "minConstructionLvl",
			name = "Minimum Construction Level",
			description = "The minimum Construction level for a house advertisement to be shown.",
			position = 1,
			section = filtersSection
	)
	default int minConstructionLvl() { return 1; }

	@ConfigItem(
			keyName = "hasGuildedAltar",
			name = "Has Guilded Altar",
			description = "The minimum Guilded Altar requirement for a house advertisement to be shown.",
			position = 2,
			section = filtersSection
	)
	default YesNoFilter hasGuildedAltar() { return YesNoFilter.N; }

	@Range(
			max = NumericFilters.NEXUS_MAX_TIER,
			min = NumericFilters.ALL_FILTER_MIN
	)
	@ConfigItem(
			keyName = "minNexusLvl",
			name = "Minimum Nexus Tier",
			description = "The minimum Nexus tier for a house advertisement to be shown.",
			position = 3,
			section = filtersSection
	)
	default int minNexusLvl() { return 0; }

	@Range(
			max = NumericFilters.JEWELLERY_MAX_TIER,
			min = NumericFilters.ALL_FILTER_MIN
	)
	@ConfigItem(
			keyName = "minJewelleryLvl",
			name = "Minimum Jewellery Box Tier",
			description = "The minimum Jewellery Box tier for a house advertisement to be shown.",
			position = 4,
			section = filtersSection
	)
	default int minJewelleryLvl() { return 0; }

	@Range(
			max = NumericFilters.POOL_MAX_TIER,
			min = NumericFilters.ALL_FILTER_MIN
	)
	@ConfigItem(
			keyName = "minPoolLvl",
			name = "Minimum Pool Tier",
			description = "The minimum Pool tier for a house advertisement to be shown.",
			position = 5,
			section = filtersSection
	)
	default int minPoolLvl() { return 0; }


	@Range(
			max = NumericFilters.SPELL_ALTAR_MAX_TIER,
			min = NumericFilters.ALL_FILTER_MIN
	)
	@ConfigItem(
			keyName = "minSpellAltarLvl",
			name = "Minimum Spell Altar Tier",
			description = "The minimum Spell Altar tier for a house advertisement to be shown.",
			position = 6,
			section = filtersSection
	)
	default int minSpellAltarLvl() { return 0; }

	@ConfigItem(
			keyName = "hasArmourStand",
			name = "Has Armour Stand",
			description = "The minimum Armour Stand requirement for a house advertisement to be shown.",
			position = 7,
			section = filtersSection
	)
	default YesNoFilter hasArmourStand() { return YesNoFilter.N; }

	@ConfigItem(
			keyName = "useFavourites",
			name = "Enable Favourites",
			description = "Enable the favourites feature for house advertisements.",
			position = 8,
			section = favouritesSection
	)
	default boolean useFavourites()
	{
		return true;
	}

	@ConfigItem(
		keyName = "favouritePlayers",
		name = "Favourite Players",
		description = "List of favourite player house advertisements (Comma separated). These can also be added by right-clicking the arrow on advertisements.",
		position = 9,
		section = favouritesSection
	)
	default String getPlayerFavourites()
	{
		return "";
	}

	@ConfigItem(
			keyName = "favouritePlayers",
			name = "",
			description = ""
	)
	void setPlayerFavourites(String playerName);

	@ConfigItem(
			keyName = "pinFavourites",
			name = "Pin Favourites to Top",
			description = "Pin favourites to the top of the advertisement board.",
			position = 10,
			section = favouritesSection
	)
	default boolean pinFavourites()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightColor",
			name = "Highlight Colour",
			description = "The highlight colour for advertisement favorites.",
			position = 11,
			section = favouritesSection
	)
	default Color highlightColor()
	{
		return new Color(255, 255, 0);
	}

	@ConfigItem(
			keyName = "highlightEnterButton",
			name = "Highlight Advert Enter Button",
			description = "Highlight the advertisement arrow button used to enter the house.",
			position = 12,
			section = favouritesSection
	)
	default boolean highlightEnterButton()
	{
		return true;
	}

	@ConfigItem(
			keyName = "highlightEntireAdvert",
			name = "Highlight Advert Text",
			description = "Highlight the text on the advertisement row.",
			position = 13,
			section = favouritesSection
	)
	default boolean highlightAdvertText()
	{
		return true;
	}

	@ConfigItem(
			keyName = "useBlacklist",
			name = "Enable Blacklist",
			description = "Enable the blacklist feature for house advertisements.",
			position = 14,
			section = blackListSection
	)
	default boolean useBlacklist()
	{
		return true;
	}

	@ConfigItem(
			keyName = "blacklistedPlayers",
			name = "Blacklisted Players (Hidden)",
			description = "List of blacklisted player house advertisements to hide (Comma separated). These can also be added by right-clicking the arrow on advertisements.",
			position = 15,
			section = blackListSection
	)
	default String getPlayerBlacklist()
	{
		return "";
	}

	@ConfigItem(
			keyName = "blacklistedPlayers",
			name = "",
			description = ""
	)
	void setPlayerBlacklist(String playerName);

	@ConfigItem(
			keyName = "visitLastPreview",
			name = "Show Visit-Last Name Preview",
			description = "Preview the name of the last house you visited on the Visit-Last menu option (when available).",
			position = 16,
			section = menuOptionsSection
	)
	default boolean visitLastPreview()
	{
		return true;
	}

	@ConfigItem(
			keyName = "visitLastPreviewColour",
			name = "Name Preview Colour",
			description = "The colour of the Visit-Last name preview (when available).",
			position = 17,
			section = menuOptionsSection
	)
	default Color visitLastPreviewColour()
	{
		return new Color(0, 255, 0);
	}

}

