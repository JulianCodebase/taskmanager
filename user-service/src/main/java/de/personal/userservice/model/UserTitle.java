package de.personal.userservice.model;

public enum UserTitle {
    ROOKIE(0, 99),
    ACHIEVER(100, 299),
    MASTER(300, Integer.MAX_VALUE);

    private final int minXp;
    private final int maxXp;

    UserTitle(int minXp, int maxXp) {
        this.minXp = minXp;
        this.maxXp = maxXp;
    }

    /**
     * Determines the appropriate UserTitle based on given XP points.
     */
    public static UserTitle fromXpPoints(int xpPoints) {
        for (UserTitle title : values()) {
            if (xpPoints >= title.minXp && xpPoints <= title.maxXp) {
                return title;
            }
        }

        throw new IllegalArgumentException("The provided xpPoints isn't acceptable.");
    }
}
