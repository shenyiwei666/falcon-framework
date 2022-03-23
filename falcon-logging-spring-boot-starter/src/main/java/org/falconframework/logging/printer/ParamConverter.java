package org.falconframework.logging.printer;

import com.alibaba.fastjson.JSON;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ParamConverter {

    public static String formatArrayParam(Object[] params) {
        Map map = new HashMap();
        for (int i = 0; i < params.length; i++) {
            String paramKey = "arg" + (i + 1);
            String paramValue = ParamConverter.convertString(params[i]);
            if (paramValue == null) {
                continue;
            }
            map.put(paramKey, paramValue);
        }
        return JSON.toJSONString(map, true);
    }

    public static String convertString(Object param) {
        if (param instanceof ServletRequest || param instanceof ServletResponse || param instanceof MultipartFile) {
            return null;
        }
        if (param instanceof Float || param instanceof Double) {
            param = new BigDecimal(param + "");
        }
        if (param instanceof String || param instanceof Long || param instanceof Integer || param instanceof Boolean
                || param instanceof Short || param instanceof Character || param instanceof Byte) {
            return String.valueOf(param);
        } else if (param instanceof BigDecimal) {
            return ((BigDecimal) param).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        } else if(param instanceof java.util.Date) {
            return (((java.util.Date) param).getTime() + "");
        } else if(param instanceof java.sql.Date) {
            return (((java.sql.Date) param).getTime() + "");
        }  else if(param instanceof Timestamp) {
            return (((Timestamp) param).getTime() + "");
        } else {
            return JSON.toJSONString(param);
        }
    }

}
