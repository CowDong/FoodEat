package net.runelite.client.plugins.constructionhelper;

public enum CHWidget {

    OAK_LARDER(458, 5),
    MAHOGANY_TABLE(458, 9),
    MYTHICAL_CAPE(458, 7);

    private int groupId;
    private int childId;

    CHWidget(int groupId, int childId) {
        this.groupId = groupId;
        this.childId = childId;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getChildId() {
        return this.childId;
    }
}
