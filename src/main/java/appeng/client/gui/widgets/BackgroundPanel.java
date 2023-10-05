package appeng.client.gui.widgets;

import appeng.client.gui.GuiWidget;

public class BackgroundPanel extends GuiWidget {
    @Override
    public void addPanels(PanelBlitter panelBlitter) {
        panelBlitter.addBounds(getScreenBounds());
    }
}
