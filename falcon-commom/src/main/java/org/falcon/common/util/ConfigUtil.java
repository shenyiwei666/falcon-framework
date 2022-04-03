package org.falcon.common.util;

import org.apache.commons.lang3.StringUtils;
import org.falcon.common.constant.FalconConstant;
import org.falcon.common.enums.BooleanEnum;
import org.springframework.core.env.Environment;

public class ConfigUtil {

    public static String getString(Environment environment, String key, boolean required, String defaultValue) {
        String value = environment.getProperty(key);
        if (required && StringUtils.isBlank(value) && StringUtils.isBlank(defaultValue)) {
            throw new IllegalArgumentException(key + "配置不能为空");
        }
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    public static Integer getInteger(Environment environment, String key, boolean required, String defaultValue) {
        String value = getString(environment, key, required, defaultValue);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(key + "只能配置为int类型");
        }
    }

    public static Boolean getBoolean(Environment environment, String key, boolean required, String defaultValue) {
        String value = getString(environment, key, required, defaultValue);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return BooleanEnum.getByValue(value).getBooleanValue();
        } catch (Exception e) {
            throw new IllegalArgumentException(key + "只能配置为boolean类型");
        }
    }

    public static String[] getStringArray(Environment environment, String key, boolean required, String defaultValue) {
        String value = getString(environment, key, required, defaultValue);
        try {
            return value.split(FalconConstant.ARRAY_SPLIT);
        } catch (Exception e) {
            throw new IllegalArgumentException(key + "只能配置为String[]类型，多个值使用英文逗号分隔");
        }
    }

}
