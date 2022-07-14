package com.salverrs.HouseAdvertisementPlus;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HouseAdvertisementPlusTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HouseAdvertisementPlusPlugin.class);
		RuneLite.main(args);
	}
}