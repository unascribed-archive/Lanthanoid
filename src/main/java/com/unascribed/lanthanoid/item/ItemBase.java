package com.unascribed.lanthanoid.item;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.unascribed.lanthanoid.Lanthanoid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class ItemBase extends Item {
	public ItemBase() {
		setCreativeTab(Lanthanoid.inst.creativeTab);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		super.addInformation(stack, player, list, advanced);
		String loreKey = getUnlocalizedName(stack)+".lore";
		if (StatCollector.canTranslate(loreKey)) {
			int code = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
			if (Keyboard.isKeyDown(code)) {
				list.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(StatCollector.translateToLocal(loreKey), 256));
				list.add("");
			} else {
				list.add(StatCollector.translateToLocalFormatted("ui.lore_hint", "\u00A7e"+GameSettings.getKeyDisplayString(code)+"\u00A77"));
			}
		}
		if (StatCollector.canTranslate(getUnlocalizedName(stack)+".help.1")) {
			int code = Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode();
			if (Keyboard.isKeyDown(code)) {
				int i = 1;
				do {
					String key = getUnlocalizedName(stack)+".help."+i;
					list.add("");
					list.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(StatCollector.translateToLocal(key), 256));
					i++;
				} while (StatCollector.canTranslate(getUnlocalizedName(stack)+".help."+i));
			} else {
				list.add(StatCollector.translateToLocalFormatted("ui.help_hint", "\u00A7b"+GameSettings.getKeyDisplayString(code)+"\u00A77"));
			}
		}
	}
	@Override
	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		return Strings.nullToEmpty(getUnlocalizedName(stack));
	}
	protected NBTTagCompound getCompound(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}
}
