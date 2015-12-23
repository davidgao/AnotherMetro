package edu.fudan.davidgao.anothermetro.core;

public enum SiteType {
    CIRCLE(0),
    TRIANGLE(1),
    SQUARE(2),
    UNIQUE1(3),
    UNIQUE2(4),
    UNIQUE3(5),
    UNIQUE4(6),
    UNIQUE5(7);

    private final int index;

    SiteType(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    public static SiteType fromInt(int index) {
        switch (index) {
            case 0: return SiteType.CIRCLE;
            case 1: return SiteType.TRIANGLE;
            case 2: return SiteType.SQUARE;
            case 3: return SiteType.UNIQUE1;
            case 4: return SiteType.UNIQUE2;
            case 5: return SiteType.CIRCLE;
            case 6: return SiteType.CIRCLE;
            case 7: return SiteType.TRIANGLE;
            default: return null; /* should never happen */
        }
    }
}
