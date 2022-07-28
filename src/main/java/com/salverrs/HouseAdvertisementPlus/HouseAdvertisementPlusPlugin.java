package com.salverrs.HouseAdvertisementPlus;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@PluginDescriptor(
	name = "House Advertisement Plus",
	description = "QOL features for the House Advertisement board.",
	tags = {"poh", "house", "advert", "advertisement", "plus", "board", "favourites", "filter", "qol"},
	enabledByDefault = true
)
public class HouseAdvertisementPlusPlugin extends Plugin
{
	public static final String ConfigGroup = "HouseAdvertisementPlus";
	private String lastVisited = "";
	private Widget container;
	private boolean advertBoardVisible = false;
	private boolean shouldRenderBoard = false;
	private boolean shouldRenderHighlights = false;
	private boolean requiresUpdate = true;
	private List<String> favouritePlayers = new ArrayList();
	private List<String> blacklistPlayers = new ArrayList();

	private HashMap<String, HouseAdvertisement> adverts;
	private final List<WidgetTarget> widgetsToHighlight = new ArrayList();
	private final List<WidgetTarget> textToHighlight = new ArrayList();
	private final HashMap<String, Integer> defaultTextColours = new HashMap();

	@Inject
	private ClientThread clientThread;
	@Inject
	private Client client;
	@Inject
	private HouseAdvertisementPlusOverlay overlay;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private MenuManager menuManager;
	@Inject
	private HouseAdvertisementPlusConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("House Advertisement Plus started!");
		favouritePlayers = AdvertUtil.normalizeNames(Text.fromCSV(config.getPlayerFavourites()));
		blacklistPlayers = AdvertUtil.normalizeNames(Text.fromCSV(config.getPlayerBlacklist()));
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("House Advertisement Plus stopped!");
		resetAll();
		overlayManager.remove(overlay);
	}


	@Subscribe(priority = -100)
	public void onClientTick(ClientTick clientTick)
	{
		if (advertBoardVisible && requiresUpdate) {
			processHouseAdvertisements();
			requiresUpdate = false;
		}

		AddMenuEntries();
	}


	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		int id = event.getScriptId();
		if (id == AdvertID.UPDATE_ADD_LINE_SCRIPT_ID ||
			id == AdvertID.UPDATE_ADD_SORT_SCRIPT_ID ||
			id == AdvertID.UPDATE_POH_INIT_SCRIPT_ID ||
			id == AdvertID.UPDATE_RE_DRAW_SCRIPT_ID)
		{
			resetUpdateState();
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed e)
	{
		final int groupId = e.getGroupId();
		if (groupId == AdvertID.WIDGET_GROUP_ID)
		{
			advertBoardVisible = false;
			resetUpdateState();
			resetWidgetHighlights();
			refreshVisibility();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e)
	{
		final int groupId = e.getGroupId();

		if (groupId == AdvertID.WIDGET_GROUP_ID)
		{
			if (!isHouseAdvertWidgetVisible()) {
				return;
			}

			advertBoardVisible = true;
			resetUpdateState();
			refreshVisibility();
		}
	}

	@Subscribe
	public void onVarClientStrChanged(VarClientStrChanged e)
	{
		if (e.getIndex() == AdvertID.LAST_VISIT_VAR_INDEX)
		{
			lastVisited = client.getVarcStrValue(AdvertID.LAST_VISIT_VAR_INDEX);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			lastVisited = client.getVarcStrValue(AdvertID.LAST_VISIT_VAR_INDEX);
		}
	}

	private void AddMenuEntries()
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
			return;

		final MenuEntry[] entries = client.getMenuEntries();
		for (final MenuEntry entry : entries)
		{
			final String option = entry.getOption();
			if (advertBoardVisible && option.contains(AdvertID.ENTER_HOUSE_OPTION_TEXT))
			{
				addCustomMenuOptions(entry);
			}
			else if (option.contains(AdvertID.LAST_VISIT_OPTION_TEXT))
			{
				if (config.visitLastPreview())
				{
					addLastVisitName(entry);
				}
			}
		}
	}

	private void addCustomMenuOptions(MenuEntry entry)
	{
		final Widget arrowWidget = entry.getWidget();

		if (arrowWidget.getOpacity() == 255) // Hide menu option for 'hidden' (opacity=255) enter widgets.
		{
			client.setMenuEntries(new MenuEntry[0]);
			return;
		}

		String playerName = AdvertUtil.getPlayerFromOpArg(arrowWidget, AdvertID.ADVERT_ARROW_PLAYER_ARG_INDEX);
		if (playerName == null || playerName.equals(""))
			return;

		final String originalName = playerName;
		playerName = AdvertUtil.normalizeName(playerName);
		int menuInsertIndex = 2;

		if (config.useFavourites())
		{
			if (favouritePlayers.contains(playerName))
			{
				client.createMenuEntry(menuInsertIndex)
						.setOption("Remove " + originalName + " from favourites")
						.setTarget("")
						.setType(MenuAction.RUNELITE)
						.onClick(removeFromFavourites(playerName));
			}
			else
			{
				client.createMenuEntry(menuInsertIndex)
						.setOption("Add " + originalName + " to favourites")
						.setTarget("")
						.setType(MenuAction.RUNELITE)
						.onClick(addToFavourites(playerName));
			}

			menuInsertIndex--;
		}

		if (config.useBlacklist() && !favouritePlayers.contains(playerName))
		{
			client.createMenuEntry(menuInsertIndex)
					.setOption("Hide " + originalName)
					.setTarget("")
					.setType(MenuAction.RUNELITE)
					.onClick(addToBlacklist(playerName));
		}
	}

	private void addLastVisitName(MenuEntry entry)
	{
		if (lastVisited != null && !lastVisited.equals(""))
		{
			final int r = config.visitLastPreviewColour().getRed();
			final int g = config.visitLastPreviewColour().getGreen();
			final int b = config.visitLastPreviewColour().getBlue();
			String hexColor = String.format("%02x%02x%02x", r, g, b);
			entry.setTarget("<col=" + hexColor + ">" + lastVisited + "</col>");
		}
	}

	private Consumer<MenuEntry> addToBlacklist(String playerName)
	{
		return e -> {
			if (blacklistPlayers.contains(playerName))
				return;

			final ArrayList<String> newPlayerList = new ArrayList<>(blacklistPlayers);
			newPlayerList.add(playerName);
			config.setPlayerBlacklist(Text.toCSV(newPlayerList));
			resetUpdateState();
		};
	}

	private Consumer<MenuEntry> addToFavourites(String playerName)
	{
		return e -> {
			if (favouritePlayers.contains(playerName))
				return;

			final ArrayList<String> newPlayerList = new ArrayList<>(favouritePlayers);
			newPlayerList.add(playerName);
			config.setPlayerFavourites(Text.toCSV(newPlayerList));
			resetUpdateState();
		};
	}

	private Consumer<MenuEntry> removeFromFavourites(String playerName)
	{
		return e -> {
			if (!favouritePlayers.contains(playerName))
				return;

			final ArrayList<String> newPlayerList = new ArrayList<>(favouritePlayers);
			newPlayerList.removeIf(p -> p.equals(playerName));
			config.setPlayerFavourites(Text.toCSV(newPlayerList));
			resetUpdateState();
		};
	}

	private void processHouseAdvertisements()
	{
		clientThread.invoke(() ->
		{
			resetWidgetHighlights();

			final HouseAdvertisementMapper mapper = new HouseAdvertisementMapper(client, favouritePlayers, blacklistPlayers);
			adverts = mapper.GetHouseAdvertisements();

			if (adverts == null || adverts.size() == 0)
				return;

			Collection<HouseAdvertisement> ads = adverts.values();
			refreshAdvertState(ads);

			if (config.useBlacklist())
			{
				applyBlacklist(adverts);
			}

			if (config.useFilters())
			{
				applyFilters(ads);
			}

			if (config.useFavourites())
			{
				if (config.pinFavourites())
				{
					moveFavouritesToTop(ads);
				}

				if (config.highlightEnterButton() || config.highlightAdvertText())
				{
					highlightFavourites(ads);
				}
			}

			moveInvisibleToBottom(ads);
			shouldRenderBoard = true;
		});

		clientThread.invokeLater(() -> {
			refreshVisibility();
		});
	}

	private void resetAll()
	{
		if (adverts != null)
		{
			refreshAdvertState(adverts.values());
		}

		shouldRenderBoard = true;
		refreshVisibility();
		resetWidgetHighlights();
		resetUpdateState();
	}

	private void resetUpdateState()
	{
		requiresUpdate = true;
		shouldRenderBoard = false;
		shouldRenderHighlights = false;
	}

	private void refreshAdvertState(Collection<HouseAdvertisement> ads)
	{
		for (HouseAdvertisement advert : ads)
		{
			if (advert == null)
				continue;

			advert.setIsVisible(true);
		}
	}

	private void refreshVisibility()
	{
		container = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_CONTAINER_ID);
		if (container == null)
			return;

		container.setType(shouldRenderBoard ? WidgetType.LAYER : -1);

		shouldRenderHighlights = shouldRenderBoard;
	}

	private void resetWidgetHighlights()
	{
		for (WidgetTarget wt : textToHighlight)
		{
			final Widget w = wt.getWidget();
			w.setTextColor(defaultTextColours.get(wt.getKey()));
		}

		widgetsToHighlight.clear();
		textToHighlight.clear();
	}

	private void applyBlacklist(HashMap<String, HouseAdvertisement> ads)
	{
		for (String playerName : ads.keySet())
		{
			final HouseAdvertisement advert = ads.get(playerName);
			if (!advert.isBlacklisted())
				continue;

			advert.setIsVisible(false);
		}
	}


	private void applyFilters(Collection<HouseAdvertisement> ads)
	{
		for (HouseAdvertisement advert : ads)
		{
			if (passesFilter(advert))
				continue;

			advert.setIsVisible(false);
		}
	}


	private boolean passesFilter(HouseAdvertisement advert)
	{
		return (
			advert.getConstructionLvl() >= config.minConstructionLvl() &&
			AdvertUtil.yesNoPasses(config.hasGuildedAltar(), advert.isHasGuildedAlter()) &&
			advert.getNexusLvl() >= config.minNexusLvl() &&
			advert.getJewelleryLvl() >= config.minJewelleryLvl() &&
			advert.getPoolLvl() >= config.minPoolLvl() &&
			advert.getSpellAltarLvl() >= config.minSpellAltarLvl() &&
			AdvertUtil.yesNoPasses(config.hasArmourStand(), advert.isHasArmourStand())
		);
	}


	private void moveInvisibleToBottom(Collection<HouseAdvertisement> ads)
	{
		final Queue<HouseAdvertisement> nextSwapTarget = new LinkedList<>();
		final List<HouseAdvertisement> adverts = new ArrayList(ads);
		adverts.sort(Comparator.comparing(a -> a.getRowIndex()));

		for (int i = adverts.size() - 1; i >= 0; i--)
		{
			HouseAdvertisement advert = adverts.get(i);

			if (advert.isVisible())
			{
				nextSwapTarget.add(advert);
				continue;
			}

			if (nextSwapTarget.isEmpty())
				continue;

			final HouseAdvertisement target = nextSwapTarget.remove();
			advert.swapRowWith(target);
			nextSwapTarget.add(target);
		}
	}

	private void moveFavouritesToTop(Collection<HouseAdvertisement> ads)
	{
		final Queue<HouseAdvertisement> nextSwapTarget = new LinkedList<>();
		final List<HouseAdvertisement> adverts = new ArrayList(ads);
		final List<HouseAdvertisement> favourites = new ArrayList<>();

		adverts.sort(Comparator.comparing(a -> a.getRowIndex()));
		for (int i = 0; i < adverts.size(); i++)
		{
			HouseAdvertisement advert = adverts.get(i);

			if (!advert.isFavourite() || !advert.isVisible())
			{
				nextSwapTarget.add(advert);
				continue;
			}

			favourites.add(advert);

			if (nextSwapTarget.isEmpty())
				continue;

			final HouseAdvertisement target = nextSwapTarget.remove();
			advert.swapRowWith(target);
			nextSwapTarget.add(target);
		}

		// Preserve favourite list order

		int favouriteIndex = 0;
		favourites.sort(Comparator.comparing(a -> a.getRowIndex()));
		while (favouriteIndex < favourites.size())
		{
			final HouseAdvertisement advert = favourites.get(favouriteIndex);
			final int targetIndex = Math.min(favourites.size() - 1, getFavouriteIndex(advert));
			final HouseAdvertisement target = favourites.get(targetIndex);

			if (target == advert)
			{
				favouriteIndex++;
				continue;
			}

			advert.swapRowWith(target);
			favourites.sort(Comparator.comparing(a -> a.getRowIndex()));
			favouriteIndex = 0;
		}

	}

	private int getFavouriteIndex(HouseAdvertisement advert)
	{
		return favouritePlayers.indexOf(AdvertUtil.normalizeName(advert.getPlayerName()));
	}

	private void highlightFavourites(Collection<HouseAdvertisement> adverts)
	{
		for (HouseAdvertisement advert : adverts)
		{
			if (!advert.isFavourite() || !advert.isVisible())
				continue;

			if (config.highlightEnterButton())
			{
				Widget enterArrow = advert.getWidget(AdvertID.KEY_ENTER);
				if (enterArrow != null)
				{
					widgetsToHighlight.add(new WidgetTarget(AdvertID.KEY_ENTER, enterArrow));
				}
			}

			if (config.highlightAdvertText())
			{
				for (String key : advert.getAdvertWidgets().keySet())
				{
					if (key == AdvertID.KEY_ENTER)
						continue;

					final Widget w = advert.getWidget(key);
					textToHighlight.add(new WidgetTarget(key, w));

					if (!defaultTextColours.containsKey(key))
					{
						defaultTextColours.put(key, w.getTextColor());
					}
				}
			}
		}
	}

	private boolean isHouseAdvertWidgetVisible()
	{
		final Widget titleParent = client.getWidget(AdvertID.WIDGET_GROUP_ID, AdvertID.WIDGET_TITLE_PARENT_INDEX);
		final Widget title = titleParent != null ?  titleParent.getChild(AdvertID.WIDGET_TITLE_INDEX) : null;
		final String titleText = title != null ? title.getText() : null;
		return (titleText != null && titleText.equals(AdvertID.TITLE));
	}

	public void renderHighlights(Graphics2D g)
	{
		if (!advertBoardVisible || !shouldRenderHighlights)
			return;

		final Color highlightColor = config.highlightColor();
		for (WidgetTarget wt : widgetsToHighlight)
		{
			Rectangle bounds = wt.getWidget().getBounds();

			if (container != null)
			{
				bounds = bounds.intersection(container.getBounds());
			}

			final int adjustedWidth = bounds.width - (bounds.width / 20);
			final RoundRectangle2D rounded = new RoundRectangle2D.Double(bounds.x, bounds.y, adjustedWidth, bounds.height, bounds.width / 5, bounds.width / 5);
			g.setColor(highlightColor);
			g.draw(rounded);
		}

		for (WidgetTarget wt : textToHighlight)
		{
			wt.getWidget().setTextColor(highlightColor.getRGB());
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(HouseAdvertisementPlusPlugin.ConfigGroup))
		{
			return;
		}

		favouritePlayers = AdvertUtil.normalizeNames(Text.fromCSV(config.getPlayerFavourites()));
		blacklistPlayers = AdvertUtil.normalizeNames(Text.fromCSV(config.getPlayerBlacklist()));
		resetUpdateState();
	}

	@Provides
	HouseAdvertisementPlusConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HouseAdvertisementPlusConfig.class);
	}

}
