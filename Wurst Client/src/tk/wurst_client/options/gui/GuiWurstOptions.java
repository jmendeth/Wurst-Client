/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.options.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.wurst_client.WurstClient;
import tk.wurst_client.options.Options.GoogleAnalytics;
import tk.wurst_client.utils.MiscUtils;

import com.google.common.collect.Lists;

public class GuiWurstOptions extends GuiScreen
{
	private GuiScreen prevMenu;
	private String[] modListModes = {"Auto", "Count", "Hidden"};
	private String[] toolTips = {
		"",
		"Manage your friends by clicking them\n"
			+ "with the middle mouse button.",
		"How the mod list under the Wurst logo\n" + "should be displayed.\n"
			+ "�nModes�r:\n" + "�lAuto�r: Renders the whole list if it fits\n"
			+ "onto the screen.\n"
			+ "�lCount�r: Only renders the number of active\n" + "mods.\n"
			+ "�lHidden�r: Renders nothing.",
		"Automatically maximizes the Minecraft window.\n"
			+ "Windows & Linux only!",
		"Whether or not the Wurst news should be\n" + "shown in the main menu",
		"Sends anonymous usage statistics that help us\n"
			+ "improve the Wurst Client.", "Manager for the keybinds",
		"Manager for the blocks that X-Ray will\n" + "show", "", "", "",
		"The official Website of the Wurst Client",
		"Frequently asked questions", "", "", ""};
	private boolean autoMaximize;
	
	public GuiWurstOptions(GuiScreen par1GuiScreen)
	{
		prevMenu = par1GuiScreen;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		autoMaximize = WurstClient.INSTANCE.fileManager.loadAutoMaximize();
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 144 - 16,
			200, 20, "Back"));
		buttonList.add(new GuiButton(1, width / 2 - 154, height / 4 + 24 - 16,
			100, 20, "Click Friends: "
				+ (WurstClient.INSTANCE.options.middleClickFriends ? "ON"
					: "OFF")));
		buttonList.add(new GuiButton(2, width / 2 - 154, height / 4 + 48 - 16,
			100, 20, "Mod List: "
				+ modListModes[WurstClient.INSTANCE.options.modListMode]));
		buttonList.add(new GuiButton(3, width / 2 - 154, height / 4 + 72 - 16,
			100, 20, "AutoMaximize: " + (autoMaximize ? "ON" : "OFF")));
		buttonList.add(new GuiButton(4, width / 2 - 154, height / 4 + 96 - 16,
			100, 20, "Wurst news: "
				+ (WurstClient.INSTANCE.options.wurstNews ? "ON" : "OFF")));
		buttonList.add(new GuiButton(5, width / 2 - 154, height / 4 + 120 - 16,
			100, 20, "Analytics: "
				+ (WurstClient.INSTANCE.options.google_analytics.enabled ? "ON"
					: "OFF")));
		buttonList.add(new GuiButton(6, width / 2 - 50, height / 4 + 24 - 16,
			100, 20, "Keybinds"));
		buttonList.add(new GuiButton(7, width / 2 - 50, height / 4 + 48 - 16,
			100, 20, "X-Ray Blocks"));
		// this.buttonList.add(new GuiButton(8, this.width / 2 - 50, this.height
		// / 4 + 72 - 16, 100, 20, "???"));
		// this.buttonList.add(new GuiButton(9, this.width / 2 - 50, this.height
		// / 4 + 96 - 16, 100, 20, "???"));
		// this.buttonList.add(new GuiButton(10, this.width / 2 - 50,
		// this.height / 4 + 120 - 16, 100, 20, "???"));
		buttonList.add(new GuiButton(11, width / 2 + 54, height / 4 + 24 - 16,
			100, 20, "Wurst Website"));
		buttonList.add(new GuiButton(12, width / 2 + 54, height / 4 + 48 - 16,
			100, 20, "FAQ"));
		buttonList.add(new GuiButton(13, width / 2 + 54, height / 4 + 72 - 16,
			100, 20, "Report a Bug"));
		buttonList.add(new GuiButton(14, width / 2 + 54, height / 4 + 96 - 16,
			100, 20, "Suggest a Feature"));
		// buttonList.add(new GuiButton(15, width / 2 + 54, height / 4 + 120 -
		// 16, 100, 20, "???"));
		((GuiButton)buttonList.get(4)).enabled = !Minecraft.isRunningOnMac;
	}
	
	@Override
	public void actionPerformed(GuiButton clickedButton)
	{
		if(clickedButton.enabled)
			if(clickedButton.id == 0)
				mc.displayGuiScreen(prevMenu);
			else if(clickedButton.id == 1)
			{// Click Friends
				WurstClient.INSTANCE.options.middleClickFriends =
					!WurstClient.INSTANCE.options.middleClickFriends;
				clickedButton.displayString =
					"Click Friends: "
						+ (WurstClient.INSTANCE.options.middleClickFriends
							? "ON" : "OFF");
				WurstClient.INSTANCE.fileManager.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"click friends",
					WurstClient.INSTANCE.options.middleClickFriends ? "ON"
						: "OFF");
			}else if(clickedButton.id == 2)
			{// Mod List
				WurstClient.INSTANCE.options.modListMode++;
				if(WurstClient.INSTANCE.options.modListMode > 2)
					WurstClient.INSTANCE.options.modListMode = 0;
				clickedButton.displayString =
					"Mod List: "
						+ modListModes[WurstClient.INSTANCE.options.modListMode];
				WurstClient.INSTANCE.fileManager.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"mod list",
					modListModes[WurstClient.INSTANCE.options.modListMode]);
			}else if(clickedButton.id == 3)
			{// AutoMaximize
				autoMaximize = !autoMaximize;
				clickedButton.displayString =
					"AutoMaximize: " + (autoMaximize ? "ON" : "OFF");
				WurstClient.INSTANCE.fileManager.saveAutoMaximize(autoMaximize);
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"automaximize", autoMaximize ? "ON" : "OFF");
			}else if(clickedButton.id == 4)
			{// Wurst News
				WurstClient.INSTANCE.options.wurstNews =
					!WurstClient.INSTANCE.options.wurstNews;
				clickedButton.displayString =
					"Wurst news: "
						+ (WurstClient.INSTANCE.options.wurstNews ? "ON"
							: "OFF");
				WurstClient.INSTANCE.fileManager.saveOptions();
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"wurst news", WurstClient.INSTANCE.options.wurstNews ? "ON"
						: "OFF");
			}else if(clickedButton.id == 5)
			{// Analytics
				GoogleAnalytics analytics =
					WurstClient.INSTANCE.options.google_analytics;
				if(analytics.enabled)
					WurstClient.INSTANCE.analytics.trackEvent("options",
						"analytics", "disable");
				analytics.enabled = !analytics.enabled;
				if(analytics.enabled)
					WurstClient.INSTANCE.analytics.trackEvent("options",
						"analytics", "enable");
				clickedButton.displayString =
					"Analytics: " + (analytics.enabled ? "ON" : "OFF");
				WurstClient.INSTANCE.fileManager.saveOptions();
			}else if(clickedButton.id == 6)
				mc.displayGuiScreen(new GuiKeybindManager(this));
			else if(clickedButton.id == 7)
				mc.displayGuiScreen(new GuiXRayBlocksManager(this));
			else if(clickedButton.id == 8)
			{	
				
			}else if(clickedButton.id == 9)
			{	
				
			}else if(clickedButton.id == 10)
			{	
				
			}else if(clickedButton.id == 11)
			{
				MiscUtils.openLink("http://www.wurst-client.tk/");
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"wurst client website");
			}else if(clickedButton.id == 12)
			{
				MiscUtils.openLink("http://www.wurst-client.tk/faq");
				WurstClient.INSTANCE.analytics.trackEvent("options", "faq");
			}else if(clickedButton.id == 13)
			{
				MiscUtils.openLink("http://www.wurst-client.tk/bugs");
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"bug report");
			}else if(clickedButton.id == 14)
			{
				MiscUtils.openLink("http://www.wurst-client.tk/ideas");
				WurstClient.INSTANCE.analytics.trackEvent("options",
					"suggestion");
			}else if(clickedButton.id == 15)
			{	
				
			}
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		super.updateScreen();
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Wurst Options", width / 2, 40,
			0xffffff);
		drawCenteredString(fontRendererObj, "Settings", width / 2 - 104,
			height / 4 + 24 - 28, 0xcccccc);
		drawCenteredString(fontRendererObj, "Managers", width / 2,
			height / 4 + 24 - 28, 0xcccccc);
		drawCenteredString(fontRendererObj, "Online", width / 2 + 104,
			height / 4 + 24 - 28, 0xcccccc);
		super.drawScreen(par1, par2, par3);
		for(int i = 0; i < buttonList.size(); i++)
		{
			GuiButton button = (GuiButton)buttonList.get(i);
			if(button.isMouseOver() && !toolTips[button.id].isEmpty())
			{
				ArrayList toolTip =
					Lists.newArrayList(toolTips[button.id].split("\n"));
				drawHoveringText(toolTip, par1, par2);
				break;
			}
		}
	}
}
