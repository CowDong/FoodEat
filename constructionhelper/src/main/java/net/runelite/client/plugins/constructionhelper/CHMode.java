package net.runelite.client.plugins.constructionhelper;

import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;

public enum CHMode {

    OAK_LARDERS("Oak Larder", ItemID.OAK_PLANK, ObjectID.LARDER_13566, ObjectID.LARDER_SPACE, 8, CHWidget.OAK_LARDER),
    MAHOGANY_TABLE("Mahogany Table", ItemID.MAHOGANY_PLANK, ObjectID.MAHOGANY_TABLE, ObjectID.TABLE_SPACE, 6, CHWidget.MAHOGANY_TABLE),
    MYTHICAL_CAPE("Mythical Cape", ItemID.TEAK_PLANK, ObjectID.MYTHICAL_CAPE, ObjectID.GUILD_TROPHY_SPACE, 3, CHWidget.MYTHICAL_CAPE, ItemID.MYTHICAL_CAPE);

    private final String name;
    private final int plankId;
    private final int objectId;
    private final int objectSpaceId;
    private final int plankCost;
    private final CHWidget widget;
    private final int[] otherReqs;

    CHMode(String name, int plankId, int objectId, int objectSpaceId, int plankCost, CHWidget widget, int... otherReqs) {
        this.name = name;
        this.plankId = plankId;
        this.objectId = objectId;
        this.objectSpaceId = objectSpaceId;
        this.plankCost = plankCost;
        this.widget = widget;
        this.otherReqs = otherReqs;
    }

    public String getName() {
        return name;
    }

    public int getPlankId() {
        return plankId;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getObjectSpaceId() {
        return objectSpaceId;
    }

    public int getPlankCost() {
        return plankCost;
    }

    public CHWidget getWidget() {
        return widget;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int[] getOtherReqs() {
        return otherReqs;
    }
}
