package org.falcon.common.enums;

public enum BooleanEnum {

    /**
     * true
     */
    TRUE(true, 1),

    /**
     * false
     */
    FALSE(false, 0);


    BooleanEnum(boolean booleanValue, int intValue) {
        this.booleanValue = booleanValue;
        this.intValue = intValue;
    }

    private boolean booleanValue;

    private int intValue;


    public static BooleanEnum getByValue(Object objectValue) {
        if (objectValue instanceof Boolean) {
            return getByBooleanValue((Boolean) objectValue);
        } else if (objectValue instanceof Integer) {
            return getByIntegerValue((Integer) objectValue);
        } else if (objectValue instanceof String) {
            return getByStringValue((String) objectValue);
        }
        return null;
    }

    private static BooleanEnum getByBooleanValue(Boolean value) {
        if (value == null) {
            return null;
        }
        for (BooleanEnum e : BooleanEnum.values()) {
            if (e.getBooleanValue() == value) {
                return e;
            }
        }
        return null;
    }

    private static BooleanEnum getByIntegerValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (BooleanEnum e : BooleanEnum.values()) {
            if (e.getIntValue() == value) {
                return e;
            }
        }
        return null;
    }

    private static BooleanEnum getByStringValue(String value) {
        if (value == null) {
            return null;
        }
        for (BooleanEnum e : BooleanEnum.values()) {
            if ((e.getBooleanValue() + "").equals(value.toLowerCase())) {
                return e;
            }
        }
        for (BooleanEnum e : BooleanEnum.values()) {
            if ((e.getIntValue() + "").equals(value)) {
                return e;
            }
        }
        return null;
    }

    public static boolean isTrue(Object objectValue) {
        BooleanEnum e = getByValue(objectValue);
        if (e == null) {
            return false;
        }
        return e.getBooleanValue();
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public int getIntValue() {
        return intValue;
    }

}
