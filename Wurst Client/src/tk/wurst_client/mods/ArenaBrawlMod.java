/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;

import org.darkstorm.minecraft.gui.component.BoundedRangeComponent.ValueDisplay;
import org.darkstorm.minecraft.gui.component.Frame;
import org.darkstorm.minecraft.gui.component.Label;
import org.darkstorm.minecraft.gui.component.Label.TextAlignment;
import org.darkstorm.minecraft.gui.component.basic.BasicFrame;
import org.darkstorm.minecraft.gui.component.basic.BasicLabel;
import org.darkstorm.minecraft.gui.component.basic.BasicSlider;
import org.darkstorm.minecraft.gui.layout.GridLayoutManager;
import org.darkstorm.minecraft.gui.layout.GridLayoutManager.HorizontalGridConstraint;
import org.darkstorm.minecraft.gui.theme.wurst.WurstTheme;

import tk.wurst_client.WurstClient;
import tk.wurst_client.events.ChatInputEvent;
import tk.wurst_client.events.listeners.ChatInputListener;
import tk.wurst_client.events.listeners.DeathListener;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.utils.BlockUtils;
import tk.wurst_client.utils.EntityUtils;
import tk.wurst_client.utils.MiscUtils;
import tk.wurst_client.utils.RenderUtils;

@Info(category = Category.MISC,
	description = "Makes ArenaBrawl on mc.hypixel.net a lot easier.\n"
		+ "This is a collection of mods that have been optimized\n"
		+ "for ArenaBrawl. It will bypass everything that Hypixel\n"
		+ "has to offer.",
	name = "ArenaBrawl")
public class ArenaBrawlMod extends Mod implements ChatInputListener,
	DeathListener, RenderListener, UpdateListener
{
	private EntityLivingBase friend;
	public static float range = 4.25F;
	public static ArrayList<String> scoreboard = new ArrayList<String>();
	private ArrayList<int[]> matchingBlocks = new ArrayList<int[]>();
	private ArrayList<int[]> enemyTotems = new ArrayList<int[]>();
	private ArrayList<int[]> friendTotems = new ArrayList<int[]>();
	private String friendsName;
	private Frame frame;
	private int target;
	private TargetType targetType;
	private EntityLivingBase entityTarget;
	private int[] blockTarget;
	private long lastAttack = 0L;
	public int level = 40;
	
	@Override
	public String getRenderName()
	{
		if(friendsName != null)
			return "ArenaBrawl with " + friendsName;
		else
			return "ArenaBrawl";
	}
	
	@Override
	public void initSliders()
	{
		sliders.add(new BasicSlider("ArenaBrawl level", level, 20, 100, 10,
			ValueDisplay.INTEGER));
	}
	
	@Override
	public void updateSettings()
	{
		level = (int)sliders.get(0).getValue();
	}
	
	@Override
	public void onEnable()
	{
		reset();
		WurstClient.INSTANCE.eventManager.add(ChatInputListener.class, this);
		WurstClient.INSTANCE.eventManager.add(DeathListener.class, this);
		WurstClient.INSTANCE.eventManager.add(RenderListener.class, this);
		WurstClient.INSTANCE.eventManager.add(UpdateListener.class, this);
	}
	
	@Override
	public void onRender()
	{
		if(targetType == TargetType.BLOCK_E)
		{
			double x = blockTarget[0];
			double y = blockTarget[1];
			double z = blockTarget[2];
			RenderUtils.box(x, y, z, x + 1, y + 2, z + 1, new Color(255, 0, 0,
				64));
		}else if(targetType == TargetType.BLOCK_F)
		{
			double x = blockTarget[0];
			double y = blockTarget[1];
			double z = blockTarget[2];
			RenderUtils.box(x, y, z, x + 1, y + 2, z + 1, new Color(0, 255, 0,
				64));
		}else if(targetType == TargetType.ENTITY_E && entityTarget != null)
		{
			double x = entityTarget.posX;
			double y = entityTarget.posY;
			double z = entityTarget.posZ;
			RenderUtils.box(x - 0.35, y, z - 0.35, x + 0.35, y + 1.9, z + 0.35,
				new Color(255, 0, 0, 64));
		}else if(targetType == TargetType.ENTITY_F && entityTarget != null)
		{
			double x = entityTarget.posX;
			double y = entityTarget.posY;
			double z = entityTarget.posZ;
			RenderUtils.box(x - 0.35, y, z - 0.35, x + 0.35, y + 1.9, z + 0.35,
				new Color(0, 255, 0, 64));
		}
		if(EntityUtils.searchEntityByNameRaw(formatSBName(5)) != null)
		{
			RenderUtils.entityESPBox(
				EntityUtils.searchEntityByNameRaw(formatSBName(5)),
				RenderUtils.target);
			RenderUtils.tracerLine(
				EntityUtils.searchEntityByNameRaw(formatSBName(5)),
				RenderUtils.target);
		}
		if(EntityUtils.searchEntityByNameRaw(formatSBName(4)) != null)
		{
			RenderUtils.entityESPBox(
				EntityUtils.searchEntityByNameRaw(formatSBName(4)),
				RenderUtils.target);
			RenderUtils.tracerLine(
				EntityUtils.searchEntityByNameRaw(formatSBName(4)),
				RenderUtils.target);
		}
		if(friend != null)
		{
			RenderUtils.entityESPBox(friend, RenderUtils.team);
			RenderUtils.tracerLine(friend, RenderUtils.team);
		}
		if(!enemyTotems.isEmpty())
			for(int[] totem : enemyTotems)
			{
				double x = totem[0];
				double y = totem[1];
				double z = totem[2];
				RenderUtils.frame(x, y, z, x + 1, y + 2, z + 1, new Color(255,
					0, 0, 128));
				RenderUtils.tracerLine((int)x, (int)y, (int)z, new Color(255,
					0, 0, 128));
			}
		if(!friendTotems.isEmpty())
			for(int[] totem : friendTotems)
			{
				double x = totem[0];
				double y = totem[1];
				double z = totem[2];
				RenderUtils.frame(x, y, z, x + 1, y + 2, z + 1, new Color(0,
					255, 0, 128));
			}
	}
	
	@Override
	public void onUpdate()
	{
		if(scoreboard != null
			&& (scoreboard.size() == 13 || scoreboard.size() == 11))
		{// If you are in the lobby:
			WurstClient.INSTANCE.chat.message("You need to be in a 2v2 arena.");
			setEnabled(false);
			return;
		}
		if(scoreboard == null)
			return;
		if(frame == null && scoreboard.size() == 8)
			try
			{
				setupFrame();
			}catch(Exception e)
			{
				e.printStackTrace();
				frame = null;
				return;
			}
		if(friend == null || friend.isDead)
			friend = EntityUtils.searchEntityByName(friendsName);
		updateMS();
		try
		{
			scanTotems();
			getTarget();
			updateFrame();
			if(!Minecraft.getMinecraft().thePlayer.isCollidedHorizontally
				&& Minecraft.getMinecraft().thePlayer.moveForward > 0
				&& !Minecraft.getMinecraft().thePlayer.isSneaking())
			{// Built-in AutoSprint and BunnyHop:
				Minecraft.getMinecraft().thePlayer.setSprinting(true);
				if(Minecraft.getMinecraft().thePlayer.onGround
					&& Minecraft.getMinecraft().thePlayer.isSprinting())
					Minecraft.getMinecraft().thePlayer.jump();
			}
			if(targetType == TargetType.BLOCK_E)
			{
				float distX =
					(float)(blockTarget[0] - Minecraft.getMinecraft().thePlayer.posX);
				float distY =
					(float)(blockTarget[1] - Minecraft.getMinecraft().thePlayer.posY);
				float distZ =
					(float)(blockTarget[2] - Minecraft.getMinecraft().thePlayer.posZ);
				if(BlockUtils.getBlockDistance(distX, distY, distZ) <= 4.25)
				{// If the target is an enemy totem in range:
					faceTarget();
					attackTarget();
				}else
				{
					KeyBinding.setKeyBindState(
						Minecraft.getMinecraft().gameSettings.keyBindAttack
							.getKeyCode(), false);
					KeyBinding.setKeyBindState(
						Minecraft.getMinecraft().gameSettings.keyBindUseItem
							.getKeyCode(), false);
				}
			}else if(targetType == TargetType.ENTITY_E)
			{
				if(Minecraft.getMinecraft().thePlayer
					.getDistanceToEntity(entityTarget) <= 4.25)
				{// If the target is an enemy in range:
					faceTarget();
					attackTarget();
				}else
				{
					KeyBinding.setKeyBindState(
						Minecraft.getMinecraft().gameSettings.keyBindAttack
							.getKeyCode(), false);
					KeyBinding.setKeyBindState(
						Minecraft.getMinecraft().gameSettings.keyBindUseItem
							.getKeyCode(), false);
				}
			}else
			{
				KeyBinding.setKeyBindState(
					Minecraft.getMinecraft().gameSettings.keyBindAttack
						.getKeyCode(), false);
				KeyBinding.setKeyBindState(
					Minecraft.getMinecraft().gameSettings.keyBindUseItem
						.getKeyCode(), false);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable()
	{
		WurstClient.INSTANCE.eventManager.remove(ChatInputListener.class, this);
		WurstClient.INSTANCE.eventManager.remove(DeathListener.class, this);
		WurstClient.INSTANCE.eventManager.remove(RenderListener.class, this);
		WurstClient.INSTANCE.eventManager.remove(UpdateListener.class, this);
		Minecraft.getMinecraft().gameSettings.keyBindForward.pressed = false;
		if(friendsName != null)
			WurstClient.INSTANCE.chat
				.message("No longer playing ArenaBrawl with " + friendsName
					+ ".");
		reset();
	}
	
	@Override
	public void onReceivedMessage(ChatInputEvent event)
	{
		String message = event.getComponent().getUnformattedText();
		if(message.startsWith("[Arena]: ")
			&& message.endsWith(" has won the game!"))
		{
			event.cancel();
			WurstClient.INSTANCE.chat.message(message.substring(9));
			setEnabled(false);
		}
	}
	
	@Override
	public void onDeath()
	{
		Minecraft.getMinecraft().thePlayer.respawnPlayer();
		Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		WurstClient.INSTANCE.chat.message("You died.");
		setEnabled(false);
	}
	
	private void setupFrame()
	{
		friendsName = formatSBName(0);
		WurstClient.INSTANCE.chat.message("Now playing ArenaBrawl with "
			+ friendsName + ".");
		frame = new BasicFrame("ArenaBrawl");
		frame.setTheme(new WurstTheme());
		frame.setLayoutManager(new GridLayoutManager(2, 0));
		frame.setClosable(false);
		frame.setMinimized(false);
		frame.setMinimizable(false);
		frame.setPinnable(true);
		frame.setPinned(true);
		frame.setWidth(137);
		frame.add(new BasicLabel("NAME"), HorizontalGridConstraint.LEFT);
		frame.add(new BasicLabel("HEALTH"), HorizontalGridConstraint.RIGHT);
		frame.add(new BasicLabel(formatSBName(1)),
			HorizontalGridConstraint.LEFT);
		frame
			.add(new BasicLabel("???? / 2000"), HorizontalGridConstraint.RIGHT);
		frame.add(new BasicLabel(friendsName), HorizontalGridConstraint.LEFT);
		frame
			.add(new BasicLabel("???? / 2000"), HorizontalGridConstraint.RIGHT);
		frame.add(new BasicLabel(formatSBName(5)),
			HorizontalGridConstraint.LEFT);
		frame
			.add(new BasicLabel("???? / 2000"), HorizontalGridConstraint.RIGHT);
		frame.add(new BasicLabel(formatSBName(4)),
			HorizontalGridConstraint.LEFT);
		frame
			.add(new BasicLabel("???? / 2000"), HorizontalGridConstraint.RIGHT);
		frame.setHeight(frame.getTheme().getUIForComponent(frame)
			.getDefaultSize(frame).height);
		frame.layoutChildren();
		WurstClient.INSTANCE.guiManager.addFrame(frame);
		frame.setBackgroundColor(new Color(64, 64, 64, 224));
		((Label)frame.getChildren()[0]).setForegroundColor(Color.CYAN);
		((Label)frame.getChildren()[1]).setForegroundColor(Color.CYAN);
		((Label)frame.getChildren()[2]).setForegroundColor(Color.GREEN);
		((Label)frame.getChildren()[4]).setForegroundColor(Color.GREEN);
		((Label)frame.getChildren()[6]).setForegroundColor(Color.BLUE);
		((Label)frame.getChildren()[8]).setForegroundColor(Color.BLUE);
		frame.setVisible(true);
	}
	
	private void updateFrame()
	{
		ScaledResolution sr =
			new ScaledResolution(Minecraft.getMinecraft(),
				Minecraft.getMinecraft().displayWidth,
				Minecraft.getMinecraft().displayHeight);
		int width = sr.getScaledWidth();
		int height = sr.getScaledHeight();
		frame.setX(width - frame.getWidth() - 1);
		frame.setY((height - frame.getHeight()) / 2 - 16);
		frame.setDragging(false);
		frame.setPinned(true);
		updateLabel(2, 1);
		updateLabel(4, 0);
		updateLabel(6, 5);
		updateLabel(8, 4);
		while(frame.getChildren().length > 10)
			frame.remove(frame.getChildren()[10]);
		for(int i = 0; i < friendTotems.size(); i++)
		{
			frame.add(new BasicLabel("Totem " + (i + 1)),
				HorizontalGridConstraint.LEFT);
			frame.add(new BasicLabel(""), HorizontalGridConstraint.RIGHT);
			((Label)frame.getChildren()[8 + (i + 1) * 2])
				.setForegroundColor(Color.GREEN);
		}
		for(int i = 0; i < enemyTotems.size(); i++)
		{
			frame.add(new BasicLabel("Totem " + (friendTotems.size() + i + 1)),
				HorizontalGridConstraint.LEFT);
			frame.add(new BasicLabel(""), HorizontalGridConstraint.RIGHT);
			((Label)frame.getChildren()[8 + (friendTotems.size() + i + 1) * 2])
				.setForegroundColor(Color.BLUE);
		}
	}
	
	private Color getColorForHealth(String health)
	{
		if(health.endsWith(" / 2000"))
			health = health.substring(0, health.length() - 7);
		if(!MiscUtils.isInteger(health) || Integer.valueOf(health) == 0)
			return Color.BLACK;
		else
			return new Color(0, Integer.valueOf(health) * 255 / 2200,
				255 - Integer.valueOf(health) * 255 / 2200);
	}
	
	private String formatSBName(int index)
	{
		try
		{
			return scoreboard.get(index).split(" ")[0].substring(2, scoreboard
				.get(index).split(" ")[0].length() - 2);
		}catch(Exception e)
		{
			return null;
		}
	}
	
	private String formatSBHealth(int index)
	{
		try
		{
			String health = scoreboard.get(index).split(" ")[1];
			if(!MiscUtils.isInteger(health))
				return health;
			else
				return health + " / 2000";
		}catch(Exception e)
		{
			return "???? / 2000";
		}
	}
	
	private void updateLabel(int labelIndex, int sbIndex)
	{
		((Label)frame.getChildren()[labelIndex]).setText((target == labelIndex
			? ">" : "") + formatSBName(sbIndex));
		((Label)frame.getChildren()[labelIndex])
			.setHorizontalAlignment(TextAlignment.LEFT);
		((Label)frame.getChildren()[labelIndex + 1])
			.setText(formatSBHealth(sbIndex));
		((Label)frame.getChildren()[labelIndex + 1])
			.setHorizontalAlignment(TextAlignment.RIGHT);
		((Label)frame.getChildren()[labelIndex + 1])
			.setForegroundColor(getColorForHealth(formatSBHealth(sbIndex)));
	}
	
	private void scanTotems()
	{
		matchingBlocks.clear();
		for(int y = 3; y >= -3; y--)
			for(int x = 50; x >= -50; x--)
				for(int z = 50; z >= -50; z--)
				{
					int posX =
						(int)(Minecraft.getMinecraft().thePlayer.posX + x);
					int posY =
						(int)(Minecraft.getMinecraft().thePlayer.posY + y);
					int posZ =
						(int)(Minecraft.getMinecraft().thePlayer.posZ + z);
					if(Block.getIdFromBlock(Minecraft.getMinecraft().theWorld
						.getBlockState(new BlockPos(posX, posY, posZ))
						.getBlock()) == Block.getIdFromBlock(Block
						.getBlockFromName("wool")))
						matchingBlocks.add(new int[]{posX, posY, posZ});
				}
		enemyTotems.clear();
		for(int i = 0; i < matchingBlocks.size(); i++)
		{
			IBlockState blockState =
				Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(
					matchingBlocks.get(i)[0], matchingBlocks.get(i)[1] + 1,
					matchingBlocks.get(i)[2]));
			if(blockState.getBlock().getMetaFromState(blockState) == 14// red
				&& Block.getIdFromBlock(blockState.getBlock()) != 0)
				enemyTotems.add(new int[]{matchingBlocks.get(i)[0],
					matchingBlocks.get(i)[1] + 1, matchingBlocks.get(i)[2]});
		}
		friendTotems.clear();
		for(int i = 0; i < matchingBlocks.size(); i++)
		{
			IBlockState blockState =
				Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(
					matchingBlocks.get(i)[0], matchingBlocks.get(i)[1] + 1,
					matchingBlocks.get(i)[2]));
			if(blockState.getBlock().getMetaFromState(blockState) == 5// lime
				&& Block.getIdFromBlock(blockState.getBlock()) != 0)
				friendTotems.add(new int[]{matchingBlocks.get(i)[0],
					matchingBlocks.get(i)[1] + 1, matchingBlocks.get(i)[2]});
		}
	}
	
	private void getTarget()
	{
		blockTarget = null;
		entityTarget = null;
		target = -1;
		targetType = null;
		if(!enemyTotems.isEmpty())
		{// If there is an enemy totem:
			int[] closestTotem = null;
			float dist = 999999999;
			for(int[] totem : enemyTotems)
			{
				float distX =
					(float)(totem[0] - Minecraft.getMinecraft().thePlayer.posX);
				float distY =
					(float)(totem[1] - Minecraft.getMinecraft().thePlayer.posY);
				float distZ =
					(float)(totem[2] - Minecraft.getMinecraft().thePlayer.posZ);
				dist = BlockUtils.getBlockDistance(distX, distY, distZ);
				if(closestTotem == null)
					closestTotem = totem;
				else
				{
					float distXC =
						(float)(closestTotem[0] - Minecraft.getMinecraft().thePlayer.posX);
					float distYC =
						(float)(closestTotem[1] - Minecraft.getMinecraft().thePlayer.posY);
					float distZC =
						(float)(closestTotem[2] - Minecraft.getMinecraft().thePlayer.posZ);
					float distC =
						BlockUtils.getBlockDistance(distXC, distYC, distZC);
					if(dist < distC)
						closestTotem = totem;
				}
			}
			target =
				8 + (friendTotems.size() + enemyTotems.indexOf(closestTotem) + 1) * 2;
			targetType = TargetType.BLOCK_E;
			blockTarget = closestTotem;
			if(dist <= 4.25)
				return;
		}
		if(EntityUtils.searchEntityByName(formatSBName(4)) != null
			|| EntityUtils.searchEntityByName(formatSBName(5)) != null)
		{// If one of the enemies can be seen:
			EntityLivingBase enemy1 =
				EntityUtils.searchEntityByName(formatSBName(5));
			EntityLivingBase enemy2 =
				EntityUtils.searchEntityByName(formatSBName(4));
			if(enemy2 == null)
			{
				entityTarget = enemy1;
				target = 6;
			}else if(enemy1 == null)
			{
				entityTarget = enemy2;
				target = 8;
			}else if(Minecraft.getMinecraft().thePlayer
				.getDistanceToEntity(enemy1) <= Minecraft.getMinecraft().thePlayer
				.getDistanceToEntity(enemy2))
			{
				entityTarget = enemy1;
				target = 6;
			}else
			{
				entityTarget = enemy2;
				target = 8;
			}
			targetType = TargetType.ENTITY_E;
			if(Minecraft.getMinecraft().thePlayer
				.getDistanceToEntity(entityTarget) <= 4.25)
				return;
		}// Enemies have a lower priority than enemy totems.
		if(!friendTotems.isEmpty())
		{// If there is a friend totem:
			int[] closestTotem = null;
			float dist = 999999999;
			for(int[] totem : friendTotems)
			{
				float distX =
					(float)(totem[0] - Minecraft.getMinecraft().thePlayer.posX);
				float distY =
					(float)(totem[1] - Minecraft.getMinecraft().thePlayer.posY);
				float distZ =
					(float)(totem[2] - Minecraft.getMinecraft().thePlayer.posZ);
				dist = BlockUtils.getBlockDistance(distX, distY, distZ);
				if(closestTotem == null)
					closestTotem = totem;
				else
				{
					float distXC =
						(float)(closestTotem[0] - Minecraft.getMinecraft().thePlayer.posX);
					float distYC =
						(float)(closestTotem[1] - Minecraft.getMinecraft().thePlayer.posY);
					float distZC =
						(float)(closestTotem[2] - Minecraft.getMinecraft().thePlayer.posZ);
					float distC =
						BlockUtils.getBlockDistance(distXC, distYC, distZC);
					if(dist < distC)
						closestTotem = totem;
				}
			}
			target = 8 + (friendTotems.indexOf(closestTotem) + 1) * 2;
			targetType = TargetType.BLOCK_F;
			blockTarget = closestTotem;
			return;
		}// Friend totems have a lower priority than enemies in range, but a
			// higher priority than enemies out of range.
		if(target == -1)
		{// If there is no other target:
			entityTarget = friend;
			target = 4;
			targetType = TargetType.ENTITY_F;
			return;
		}// The friend has the lowest priority.
	}
	
	private enum TargetType
	{
		BLOCK_E,
		BLOCK_F,
		ENTITY_E,
		ENTITY_F;
	}
	
	private void faceTarget()
	{
		if(targetType == TargetType.BLOCK_E)
			BlockUtils.faceBlockClient(new BlockPos(blockTarget[0],
				blockTarget[1], blockTarget[2]));
		else if(targetType == TargetType.ENTITY_E
			|| targetType == TargetType.ENTITY_F)
			EntityUtils.faceEntityClient(entityTarget);
	}
	
	private void attackTarget()
	{
		if(targetType == TargetType.BLOCK_E)
		{// Attacks the totem with the sword:
			if(System.currentTimeMillis() >= lastAttack + 50)
			{
				Minecraft.getMinecraft().gameSettings.keyBindAttack.pressed =
					!Minecraft.getMinecraft().gameSettings.keyBindAttack.pressed;
				lastAttack = System.currentTimeMillis();
				Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed =
					false;
			}
		}else if(targetType == TargetType.ENTITY_E)
			if(System.currentTimeMillis() >= lastAttack + 100)
			{
				if(Minecraft.getMinecraft().thePlayer.experienceLevel >= level)
					Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed =
						!Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed;
				else
				{
					Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed =
						false;
					Minecraft.getMinecraft().thePlayer.swingItem();
					Minecraft.getMinecraft().playerController.attackEntity(
						Minecraft.getMinecraft().thePlayer, entityTarget);
				}
				lastAttack = System.currentTimeMillis();
			}
	}
	
	private void reset()
	{
		Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed = false;
		matchingBlocks.clear();
		enemyTotems.clear();
		friendTotems.clear();
		WurstClient.INSTANCE.guiManager.removeFrame(frame);
		frame = null;
		friend = null;
		entityTarget = null;
		blockTarget = null;
		targetType = null;
		friendsName = null;
	}
}
