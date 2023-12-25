package appeng.client.gui.layout;

import appeng.client.guidebook.document.LytSize;

class MutableSize {
    public int width;
    public int height;

    public MutableSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static MutableSize of(LytSize size) {
        return new MutableSize(size.width(), size.height());
    }

    public LytSize toLytSize() {
        return new LytSize(width, height);
    }
}
