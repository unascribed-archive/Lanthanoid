package com.unascribed.lanthanoid.client.gui;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.unascribed.lanthanoid.init.LBlocks;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.AchievementPage;

public class GuiLanthanoidAchievements extends GuiAchievements {

	private static final ResourceLocation achievementBackground = new ResourceLocation("textures/gui/achievement/achievement_background.png");
	
	public static final Field currentPageField = ReflectionHelper.findField(GuiAchievements.class, "currentPage");
	public static final Field parentScreenField = ReflectionHelper.findField(GuiAchievements.class, "field_146562_a", "parentScreen");
	public static final Field statFileWriterField = ReflectionHelper.findField(GuiAchievements.class, "field_146556_E", "statFileWriter");
	
	static {
		currentPageField.setAccessible(true);
		parentScreenField.setAccessible(true);
		statFileWriterField.setAccessible(true);
	}
	
	private StatFileWriter statFileWriter;
	
	public GuiLanthanoidAchievements(GuiScreen p_i45026_1_, StatFileWriter p_i45026_2_) {
		super(p_i45026_1_, p_i45026_2_);
		this.statFileWriter = p_i45026_2_;
	}
	
	
	@Override
	protected void drawAchievementScreen(int mouseX, int mouseY, float partialTicks) {
		int currentPage;
		try {
			currentPage = currentPageField.getInt(this);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (AchievementPage.getTitle(currentPage).equals("Lanthanoid")) {
			// ugh
			int k = MathHelper.floor_double(this.field_146569_s + (this.field_146567_u - this.field_146569_s) * partialTicks);
			int l = MathHelper.floor_double(this.field_146568_t + (this.field_146566_v - this.field_146568_t) * partialTicks);

			if (k < field_146572_y) {
				k = field_146572_y;
			}

			if (l < field_146571_z) {
				l = field_146571_z;
			}

			if (k >= field_146559_A) {
				k = field_146559_A - 1;
			}

			if (l >= field_146560_B) {
				l = field_146560_B - 1;
			}

			int i1 = (this.width - this.field_146555_f) / 2;
			int j1 = (this.height - this.field_146557_g) / 2;
			int k1 = i1 + 16;
			int l1 = j1 + 17;
			this.zLevel = 0.0F;
			GL11.glDepthFunc(GL11.GL_GEQUAL);
			GL11.glPushMatrix();
			GL11.glTranslatef(k1, l1, -200.0F);
			GL11.glScalef(1.0F / this.field_146570_r, 1.0F / this.field_146570_r, 0.0F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			int i2 = k + 288 >> 4;
			int j2 = l + 288 >> 4;
			int k2 = (k + 288) % 16;
			int l2 = (l + 288) % 16;
			Random random = new Random();
			float f1 = 16.0F / this.field_146570_r;
			float f2 = 16.0F / this.field_146570_r;
			int i3;
			int j3;
			int k3;

			for (i3 = 0; i3 * f1 - l2 < 155.0F; ++i3) {
				float f3 = 0.6F - (j2 + i3) / 25.0F * 0.3F;
				GL11.glColor4f(f3, f3, f3, 1.0F);

				for (j3 = 0; j3 * f2 - k2 < 224.0F; ++j3) {
					random.setSeed(this.mc.getSession().getPlayerID().hashCode() + i2 + j3 + (j2 + i3) * 16);
					k3 = random.nextInt(1 + j2 + i3) + (j2 + i3) / 2;
					IIcon iicon = Blocks.sand.getIcon(0, 0);

					if (k3 <= 37 && j2 + i3 != 35) {
						if (k3 == 22) {
							int r = random.nextInt(3);
							if (r == 0) {
								iicon = LBlocks.ore_gem.getIcon(0, LBlocks.ore_gem.getMetaForName("oreRaspite"));
							} else if (r == 1) {
								iicon = LBlocks.ore_gem.getIcon(0, LBlocks.ore_gem.getMetaForName("oreDiaspore"));
							} else if (r == 2) {
								iicon = LBlocks.ore_gem.getIcon(0, LBlocks.ore_gem.getMetaForName("oreThulite"));
							}
						} else if (k3 == 10) {
							if (random.nextBoolean()) {
								iicon = LBlocks.ore_metal.getIcon(0, LBlocks.ore_metal.getMetaForName("oreYttrium"));
							} else {
								iicon = LBlocks.ore_gem.getIcon(0, LBlocks.ore_gem.getMetaForName("oreActinolite"));
							}
						} else if (k3 == 3) {
							iicon = LBlocks.ore_metal.getIcon(0, LBlocks.ore_metal.getMetaForName("oreHolmium"));
						} else if (k3 > 4) {
							iicon = Blocks.stone.getIcon(0, 0);
						} else if (k3 > 0) {
							iicon = Blocks.dirt.getIcon(0, 0);
						}
					} else {
						iicon = Blocks.bedrock.getIcon(0, 0);
					}

					this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
					this.drawTexturedModelRectFromIcon(j3 * 16 - k2, i3 * 16 - l2, iicon, 16, 16);
				}
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			this.mc.getTextureManager().bindTexture(achievementBackground);
			int i4;
			int j4;
			int l4;

			List<Achievement> achievementList = AchievementPage.getAchievementPage(currentPage).getAchievements();
			for (i3 = 0; i3 < achievementList.size(); ++i3) {
				Achievement achievement1 = achievementList.get(i3);

				if (achievement1.parentAchievement != null && achievementList.contains(achievement1.parentAchievement)) {
					j3 = achievement1.displayColumn * 24 - k + 11;
					k3 = achievement1.displayRow * 24 - l + 11;
					l4 = achievement1.parentAchievement.displayColumn * 24 - k + 11;
					int l3 = achievement1.parentAchievement.displayRow * 24 - l + 11;
					boolean flag5 = this.statFileWriter.hasAchievementUnlocked(achievement1);
					boolean flag6 = this.statFileWriter.canUnlockAchievement(achievement1);
					i4 = this.statFileWriter.func_150874_c(achievement1);

					if (i4 <= 4) {
						j4 = -16777216;

						if (flag5) {
							j4 = -6250336;
						} else if (flag6) {
							j4 = -16711936;
						}

						this.drawHorizontalLine(j3, l4, k3, j4);
						this.drawVerticalLine(l4, k3, l3, j4);

						if (j3 > l4) {
							this.drawTexturedModalRect(j3 - 11 - 7, k3 - 5, 114, 234, 7, 11);
						} else if (j3 < l4) {
							this.drawTexturedModalRect(j3 + 11, k3 - 5, 107, 234, 7, 11);
						} else if (k3 > l3) {
							this.drawTexturedModalRect(j3 - 5, k3 - 11 - 7, 96, 234, 11, 7);
						} else if (k3 < l3) {
							this.drawTexturedModalRect(j3 - 5, k3 + 11, 96, 241, 11, 7);
						}
					}
				}
			}

			Achievement achievement = null;
			RenderItem renderitem = new RenderItem();
			float f4 = (mouseX - k1) * this.field_146570_r;
			float f5 = (mouseY - l1) * this.field_146570_r;
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			int i5;
			int j5;

			for (l4 = 0; l4 < achievementList.size(); ++l4) {
				Achievement achievement2 = achievementList.get(l4);
				i5 = achievement2.displayColumn * 24 - k;
				j5 = achievement2.displayRow * 24 - l;

				if (i5 >= -24 && j5 >= -24 && i5 <= 224.0F * this.field_146570_r && j5 <= 155.0F * this.field_146570_r) {
					i4 = this.statFileWriter.func_150874_c(achievement2);
					float f6;

					if (this.statFileWriter.hasAchievementUnlocked(achievement2)) {
						f6 = 0.75F;
						GL11.glColor4f(f6, f6, f6, 1.0F);
					} else if (this.statFileWriter.canUnlockAchievement(achievement2)) {
						f6 = 1.0F;
						GL11.glColor4f(f6, f6, f6, 1.0F);
					} else if (i4 < 3) {
						f6 = 0.3F;
						GL11.glColor4f(f6, f6, f6, 1.0F);
					} else if (i4 == 3) {
						f6 = 0.2F;
						GL11.glColor4f(f6, f6, f6, 1.0F);
					} else {
						if (i4 != 4) {
							continue;
						}

						f6 = 0.1F;
						GL11.glColor4f(f6, f6, f6, 1.0F);
					}

					this.mc.getTextureManager().bindTexture(achievementBackground);

					GL11.glEnable(GL11.GL_BLEND);// Forge: Specifically
													// enable blend because
													// it is needed here.
													// And we fix Generic
													// RenderItem's leakage
													// of it.
					if (achievement2.getSpecial()) {
						this.drawTexturedModalRect(i5 - 2, j5 - 2, 26, 202, 26, 26);
					} else {
						this.drawTexturedModalRect(i5 - 2, j5 - 2, 0, 202, 26, 26);
					}
					GL11.glDisable(GL11.GL_BLEND); // Forge: Cleanup states
													// we set.

					if (!this.statFileWriter.canUnlockAchievement(achievement2)) {
						f6 = 0.1F;
						GL11.glColor4f(f6, f6, f6, 1.0F);
						renderitem.renderWithColor = false;
					}

					GL11.glDisable(GL11.GL_LIGHTING); // Forge: Make sure
														// Lighting is
														// disabled. Fixes
														// MC-33065
					GL11.glEnable(GL11.GL_CULL_FACE);
					renderitem.renderItemAndEffectIntoGUI(this.mc.fontRendererObj, this.mc.getTextureManager(), achievement2.theItemStack, i5 + 3, j5 + 3);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glDisable(GL11.GL_LIGHTING);

					if (!this.statFileWriter.canUnlockAchievement(achievement2)) {
						renderitem.renderWithColor = true;
					}

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

					if (f4 >= i5 && f4 <= i5 + 22 && f5 >= j5 && f5 <= j5 + 22) {
						achievement = achievement2;
					}
				}
			}

			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(achievementBackground);
			this.drawTexturedModalRect(i1, j1, 0, 0, this.field_146555_f, this.field_146557_g);
			this.zLevel = 0.0F;
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			int erdio;

			for (erdio = 0; erdio < this.buttonList.size(); ++erdio) {
				((GuiButton) this.buttonList.get(erdio)).drawButton(this.mc, mouseX, mouseY);
			}

			for (erdio = 0; erdio < this.labelList.size(); ++erdio) {
				((GuiLabel) this.labelList.get(erdio)).drawLabel(this.mc, mouseX, mouseY);
			}

			if (achievement != null) {
				String s1 = achievement.getStatName().getUnformattedText();
				String s2 = achievement.getDescription();
				i5 = mouseX + 12;
				j5 = mouseY - 4;
				i4 = this.statFileWriter.func_150874_c(achievement);

				if (!this.statFileWriter.canUnlockAchievement(achievement)) {
					String s;
					int k4;

					if (i4 == 3) {
						s1 = I18n.format("achievement.unknown", new Object[0]);
						j4 = Math.max(this.fontRendererObj.getStringWidth(s1), 120);
						s = (new ChatComponentTranslation("achievement.requires", new Object[] { achievement.parentAchievement.getStatName() })).getUnformattedText();
						k4 = this.fontRendererObj.splitStringWidth(s, j4);
						this.drawGradientRect(i5 - 3, j5 - 3, i5 + j4 + 3, j5 + k4 + 12 + 3, -1073741824, -1073741824);
						this.fontRendererObj.drawSplitString(s, i5, j5 + 12, j4, -9416624);
					} else if (i4 < 3) {
						j4 = Math.max(this.fontRendererObj.getStringWidth(s1), 120);
						s = (new ChatComponentTranslation("achievement.requires", new Object[] { achievement.parentAchievement.getStatName() })).getUnformattedText();
						k4 = this.fontRendererObj.splitStringWidth(s, j4);
						this.drawGradientRect(i5 - 3, j5 - 3, i5 + j4 + 3, j5 + k4 + 12 + 3, -1073741824, -1073741824);
						this.fontRendererObj.drawSplitString(s, i5, j5 + 12, j4, -9416624);
					} else {
						s1 = null;
					}
				} else {
					j4 = Math.max(this.fontRendererObj.getStringWidth(s1), 120);
					int k5 = this.fontRendererObj.splitStringWidth(s2, j4);

					if (this.statFileWriter.hasAchievementUnlocked(achievement)) {
						k5 += 12;
					}

					this.drawGradientRect(i5 - 3, j5 - 3, i5 + j4 + 3, j5 + k5 + 3 + 12, -1073741824, -1073741824);
					this.fontRendererObj.drawSplitString(s2, i5, j5 + 12, j4, -6250336);

					if (this.statFileWriter.hasAchievementUnlocked(achievement)) {
						this.fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken", new Object[0]), i5, j5 + k5 + 4, -7302913);
					}
				}

				if (s1 != null) {
					this.fontRendererObj.drawStringWithShadow(s1, i5, j5, this.statFileWriter.canUnlockAchievement(achievement) ? (achievement.getSpecial() ? -128 : -1) : (achievement.getSpecial() ? -8355776 : -8355712));
				}
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_LIGHTING);
			RenderHelper.disableStandardItemLighting();
			return;
		}
		super.drawAchievementScreen(mouseX, mouseY, partialTicks);
	}

}
