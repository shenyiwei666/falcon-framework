package org.falconframework.logging.alarm;

import org.falconframework.common.help.SpringBeanHelper;
import org.falconframework.common.redis.RedisTemplatePlus;
import org.falconframework.logging.config.ConfigReader;
import org.falconframework.logging.config.LoggingConfig;
import org.falconframework.logging.constant.CacheConstant;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class ErrorAlarm {

    private static MailMessenger mailMessenger;
    private static RedisTemplatePlus redisTemplatePlus;
    private static String oldSenderAccount;
    private static String oldSenderPassword;


    public static void alarm(String content, String sign) {
        LoggingConfig loggingConfig = ConfigReader.getLoggingConfig();
        LoggingConfig.Mail mailConfig = loggingConfig.getMail();
        if (mailConfig == null) {
            return;
        }

        Integer frequency = mailConfig.getFrequency();
        if (frequency != null) {
            String redisKey = MessageFormat.format(CacheConstant.ALARM_RECORD, sign);
            String alarmRecord = getRedisTemplatePlus().get(redisKey);
            if (alarmRecord != null) {
                return;
            }
        }

        reconnectionIfConfigChanged();

        String[] receiverAccounts = mailConfig.getReceiverAccounts();
        String subject = loggingConfig.getApp() + "告警";
        getMailMessenger().send(receiverAccounts, subject, content);

        if (frequency != null) {
            String redisKey = MessageFormat.format(CacheConstant.ALARM_RECORD, sign);
            getRedisTemplatePlus().setEx(redisKey, "1", frequency, TimeUnit.SECONDS);
        }
    }

    public static void reconnectionIfConfigChanged() {
        LoggingConfig.Mail mailConfig = ConfigReader.getLoggingConfig().getMail();
        if (!mailConfig.getSenderAccount().equals(oldSenderAccount)
                || !mailConfig.getSenderPassword().equals(oldSenderPassword)) {
            getMailMessenger().reconnection(mailConfig.getSenderAccount(), mailConfig.getSenderPassword());
            oldSenderAccount = mailConfig.getSenderAccount();
            oldSenderPassword = mailConfig.getSenderPassword();
        }
    }

    private static MailMessenger getMailMessenger() {
        if (mailMessenger == null) {
            synchronized (ErrorAlarm.class) {
                if (mailMessenger == null) {
                    LoggingConfig.Mail mailConfig = ConfigReader.getLoggingConfig().getMail();
                    mailMessenger = new MailMessenger(mailConfig);
                    oldSenderAccount = mailConfig.getSenderAccount();
                    oldSenderPassword = mailConfig.getSenderPassword();
                }
            }
        }
        return mailMessenger;
    }

    private static RedisTemplatePlus getRedisTemplatePlus() {
        if (redisTemplatePlus == null) {
            redisTemplatePlus = SpringBeanHelper.getBean(RedisTemplatePlus.class);
        }
        return redisTemplatePlus;
    }

}
