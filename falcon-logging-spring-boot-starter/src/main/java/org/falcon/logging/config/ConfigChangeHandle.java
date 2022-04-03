package org.falcon.logging.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.falcon.common.constant.FalconConstant;
import org.falcon.common.enums.BooleanEnum;
import org.falcon.common.help.SpringBeanHelper;
import org.falcon.logging.alarm.LoggingAlarm;
import org.falcon.logging.constant.ConfigConstant;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

import java.util.Map;

@Slf4j
public class ConfigChangeHandle {

    static void change(Map<String, String> changeKeyValue) {
        if (changeKeyValue == null) {
            return;
        }
        changeConfig(changeKeyValue);
        changeLogLevel(changeKeyValue);
        reconnectionMail(changeKeyValue);
    }

    private static void changeConfig(Map<String, String> changeKeyValue) {
        LoggingConfig loggingConfig = ConfigReader.getLoggingConfig();

        String debug = changeKeyValue.get(ConfigConstant.DEBUG);
        if (StringUtils.isNotBlank(debug)) {
            BooleanEnum booleanEnum = BooleanEnum.getByValue(debug);
            if (booleanEnum != null) {
                loggingConfig.setDebug(booleanEnum.getBooleanValue());
            } else {
                log.warn("{}配置错误：{}", ConfigConstant.DEBUG, debug);
            }
        }

        LoggingConfig.Mail mailConfig = loggingConfig.getMail();
        if (mailConfig != null) {
            String frequency = changeKeyValue.get(ConfigConstant.MAIL_FREQUENCY);
            if (StringUtils.isNotBlank(frequency)) {
                try {
                    mailConfig.setFrequency(Integer.parseInt(frequency));
                } catch (Exception e) {
                    log.warn("{}配置错误：{}", ConfigConstant.MAIL_FREQUENCY, frequency);
                }
            }

            String receiverAccounts = changeKeyValue.get(ConfigConstant.MAIL_RECEIVER_ACCOUNTS);
            if (StringUtils.isNotBlank(receiverAccounts)) {
                mailConfig.setReceiverAccounts(receiverAccounts.split(FalconConstant.ARRAY_SPLIT));
            }

            String senderAccount = changeKeyValue.get(ConfigConstant.MAIL_SENDER_ACCOUNT);
            if (StringUtils.isNotBlank(senderAccount)) {
                mailConfig.setSenderAccount(senderAccount);
            }

            String senderPassword = changeKeyValue.get(ConfigConstant.MAIL_SENDER_PASSWORD);
            if (StringUtils.isNotBlank(senderPassword)) {
                mailConfig.setSenderPassword(senderPassword);
            }
        }
    }

    private static void reconnectionMail(Map<String, String> changeKeyValue) {
        String senderAccount = changeKeyValue.get(ConfigConstant.MAIL_SENDER_ACCOUNT);
        String senderPassword = changeKeyValue.get(ConfigConstant.MAIL_SENDER_PASSWORD);
        if (StringUtils.isBlank(senderAccount) && StringUtils.isBlank(senderPassword)) {
            return;
        }
        LoggingConfig.Mail mailConfig = ConfigReader.getLoggingConfig().getMail();
        if (mailConfig != null) {
            LoggingAlarm.reconnectionMail();
        }
    }

    private static void changeLogLevel(Map<String, String> changeKeyValue) {
        for (Map.Entry<String, String> entry : changeKeyValue.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.startsWith("logging.level.") && StringUtils.isNotBlank(value)) {
                String loggerName = key.replace("logging.level.", "");
                LogLevel logLevel = LogLevel.valueOf(value.toUpperCase());
                if (logLevel != null) {
                    SpringBeanHelper.getBean(LoggingSystem.class).setLogLevel(loggerName, logLevel);
                }
            }
        }
    }

}
