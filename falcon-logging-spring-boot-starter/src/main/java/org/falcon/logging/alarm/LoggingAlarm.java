package org.falcon.logging.alarm;

import org.falcon.common.help.SpringBeanHelper;
import org.falcon.common.redis.RedisTemplatePlus;
import org.falcon.logging.config.ConfigReader;
import org.falcon.logging.config.LoggingConfig;
import org.falcon.logging.constant.CacheConstant;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class LoggingAlarm {

    private static MailMessenger mailMessenger;
    private static RedisTemplatePlus redisTemplatePlus;

    public static void alarm(String loggingBody, String loggingSign) {
        LoggingConfig loggingConfig = ConfigReader.getLoggingConfig();
        LoggingConfig.Mail mailConfig = loggingConfig.getMail();
        if (mailConfig == null) {
            return;
        }

        Integer frequency = mailConfig.getFrequency();
        boolean isLimitAlarmFrequency = frequency != null && frequency > 0;
        String redisKey = null;

        if (isLimitAlarmFrequency) {
            redisKey = MessageFormat.format(CacheConstant.ALARM_RECORD, loggingSign);
            String alarmRecord = getRedisTemplatePlus().get(redisKey);
            if (alarmRecord != null) {
                return;
            }
        }

        String[] receiverAccounts = mailConfig.getReceiverAccounts();
        String subject = loggingConfig.getApp() + "告警";
        getMailMessenger().send(receiverAccounts, subject, loggingBody);

        if (isLimitAlarmFrequency) {
            getRedisTemplatePlus().setEx(redisKey, "1", frequency, TimeUnit.SECONDS);
        }
    }

    public static void reconnectionMail() {
        getMailMessenger().reconnection();
    }

    private static MailMessenger getMailMessenger() {
        if (mailMessenger == null) {
            synchronized (LoggingAlarm.class) {
                if (mailMessenger == null) {
                    mailMessenger = new MailMessenger();
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
