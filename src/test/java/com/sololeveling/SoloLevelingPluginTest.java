package com.sololeveling;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

/**
 * Test class for the Solo Leveling plugin.
 * This class allows you to test the plugin locally before submission.
 */
public class SoloLevelingPluginTest
{
	public static void main(String[] args) throws Exception
	{
		// Add the Solo Leveling plugin to the external plugin manager
		ExternalPluginManager.loadBuiltin(SoloLevelingPlugin.class);
		
		// Start RuneLite with the plugin loaded
		RuneLite.main(args);
	}
}
