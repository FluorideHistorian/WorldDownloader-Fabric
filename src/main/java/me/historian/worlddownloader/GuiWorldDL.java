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
	private GuiButton allowMerging, saveContainers;

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		controlList.clear();
		controlList.add(allowMerging = new GuiButton(0, width / 2 - 100, height / 4 + 24 - 16, ""));
		controlList.add(saveContainers = new GuiButton(1, width / 2 - 100, height / 4 + 24 - 16 + 24, ""));
		updateSettingsText();
	}

	private void updateSettingsText() {
		allowMerging.displayString = "Allow merging: " + WorldDL.getAllowMerging();
		saveContainers.displayString = "Save containers: " + WorldDL.getSaveContainers();
	}
	
	@Override
	protected void actionPerformed(final GuiButton button) {
		switch(button.id) {
			case 0:
				WorldDL.setAllowMerging(!WorldDL.getAllowMerging());
				updateSettingsText();
				break;
			case 1:
				WorldDL.setSaveContainers(!WorldDL.getSaveContainers());
				updateSettingsText();
				break;
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
