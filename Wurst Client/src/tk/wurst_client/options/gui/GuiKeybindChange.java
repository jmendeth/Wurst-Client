/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.options.gui;

import java.io.IOException;
import java.util.Map.Entry;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

import tk.wurst_client.WurstClient;

public class GuiKeybindChange extends GuiScreen
{
	private GuiScreen prevMenu;
	private GuiTextField commandBox;
	private Entry<String, String> entry;
	private String key = "NONE";
	
	public GuiKeybindChange(GuiScreen prevMenu, Entry<String, String> entry)
	{
		this.prevMenu = prevMenu;
		this.entry = entry;
		if(entry != null)
			key = entry.getKey();
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		commandBox.updateCursorCounter();
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, 60, "Change Key"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 72,
			"Save"));
		buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 96,
			"Cancel"));
		commandBox =
			new GuiTextField(0, fontRendererObj, width / 2 - 100, 100, 200, 20);
		commandBox.setMaxStringLength(128);
		commandBox.setFocused(true);
		if(entry != null)
			commandBox.setText(entry.getValue());
	}
	
	/**
	 * "Called when the screen is unloaded. Used to disable keyboard repeat events."
	 */
	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void actionPerformed(GuiButton clickedButton)
	{
		if(clickedButton.enabled)
			if(clickedButton.id == 0)
				mc.displayGuiScreen(new GuiKeybindPressAKey(this));
			else if(clickedButton.id == 1)
			{
				if(entry != null)
					WurstClient.INSTANCE.keybinds.remove(entry.getKey());
				WurstClient.INSTANCE.keybinds.put(key, commandBox.getText());
				WurstClient.INSTANCE.fileManager.saveKeybinds();
				mc.displayGuiScreen(prevMenu);
				WurstClient.INSTANCE.analytics.trackEvent("keybinds", "set",
					key);
			}else if(clickedButton.id == 2)
				mc.displayGuiScreen(prevMenu);
	}
	
	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	public void keyTyped(char par1, int par2)
	{
		commandBox.textboxKeyTyped(par1, par2);
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		commandBox.mouseClicked(par1, par2, par3);
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawBackground(0);
		drawCenteredString(fontRendererObj, (entry != null ? "Edit" : "Add")
			+ " Keybind", width / 2, 20, 16777215);
		drawString(fontRendererObj, "Key: " + key, width / 2 - 100, 47,
			10526880);
		drawString(fontRendererObj, "Command", width / 2 - 100, 87, 10526880);
		commandBox.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
