/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package me.historian.worlddownloader;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

/**
 * @author historian
 * @since 1/10/2022
 */
public class GuiWorldDL extends GuiScreen {
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		controlList.clear();
		controlList.add(new GuiButton(0, width / 2 - 100, height / 4 + 24 - 16, "Allow merging: " + WorldDL.getAllowMerging()));
		controlList.add(new GuiButton(1, width / 2 - 100, height / 4 + 24 - 16 + 24, "Save containers: " + WorldDL.getSaveContainers()));
	}
	
	@Override
	protected void actionPerformed(final GuiButton button) {
		switch(button.id) {
			case 0: {
				WorldDL.setAllowMerging(!WorldDL.getAllowMerging());
				button.displayString = "Allow merging: " + WorldDL.getAllowMerging();
				break;
			}
			case 1: {
				WorldDL.setSaveContainers(!WorldDL.getSaveContainers());
				button.displayString = "Save containers: " + WorldDL.getSaveContainers();
				break;
			}
			default:break;
		}
	}
	
	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float renderPartialTicks) {
		drawDefaultBackground();
		method_1934(fontRenderer, "Download settings", width / 2, 40, 16777215);
		super.drawScreen(mouseX, mouseY, renderPartialTicks);
	}
}
