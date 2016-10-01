package com.haochen.renju.calculate;

/**
 * Created by Haochen on 2016/10/1.
 */
public class ContinueType {

    public static final ContinueType EMPTY = new ContinueType(-1);
    public static final ContinueType FORBIDDEN_MOVE = new ContinueType(1);
    public static final ContinueType FIVE = new ContinueType(2);
    public static final ContinueType ALIVE_FOUR = new ContinueType(3);
    public static final ContinueType ASLEEP_FOUR = new ContinueType(4);
    public static final ContinueType ALIVE_THREE = new ContinueType(5);
    public static final ContinueType ASLEEP_THREE = new ContinueType(6);

    private int type;
    private ContinueType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContinueType that = (ContinueType) o;

        return type == that.type;

    }

    @Override
    public int hashCode() {
        return type;
    }

    @Override
    public String toString() {
        String str = "ContinueType  ";
        switch (type) {
            case -1:
                str += "[empty]";
                break;
            case 1:
                str += "[forbidden move]";
                break;
            case 2:
                str += "[five]";
                break;
            case 3:
                str += "[alive four]";
                break;
            case 4:
                str += "[asleep four]";
                break;
            case 5:
                str += "[alive three]";
                break;
            case 6:
                str += "[asleep three]";
                break;
        }
        return str;
    }
}
