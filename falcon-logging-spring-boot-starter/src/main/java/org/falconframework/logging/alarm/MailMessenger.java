package org.falconframework.logging.alarm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.falconframework.logging.config.LoggingConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 邮件发送器
 * <br/>PS：由于ERROR级别的日志打印会调用本类，所以本类中不要打印ERROR级别的日志，以免无限递归
 */
@Slf4j
public class MailMessenger {

    private LoggingConfig.Mail mailConfig;
    private Session session;
    private Transport transport;
    private static ArrayBlockingQueue<Message> sendQueue = new ArrayBlockingQueue<>(10000);

    public MailMessenger(LoggingConfig.Mail mailConfig) {
        this.mailConfig = mailConfig;
        init(mailConfig);
        startSender();
    }

    private void init(LoggingConfig.Mail mailConfig) {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", mailConfig.getProtocol());
        props.setProperty("mail.smtp.host", mailConfig.getHost());
        props.setProperty("mail.smtp.auth", mailConfig.getAuth() + "");
        props.setProperty("mail.debug", mailConfig.getDebug() + "");

        try {
            session = Session.getInstance(props);
            transport = session.getTransport();
            transport.connect(mailConfig.getSenderAccount(), mailConfig.getSenderPassword());
        } catch (Exception e) {
            log.warn("MailMessenger初始化失败，mailConfig={}", JSON.toJSONString(mailConfig), e);
        }
    }

    private void startSender() {
        new Thread(() -> {
            while (true) {
                doSend();
            }
        }).start();
    }

    private void doSend() {
        Message message = null;
        try {
            // 拿取待发送的邮件消息
            message = sendQueue.take();
            transport.sendMessage(message, message.getAllRecipients());
            if (log.isDebugEnabled()) {
                log.debug("发送邮件成功，message={}", messageToString(message));
            }
        } catch (Exception e) {
            log.warn("发送邮件失败，message={}", messageToString(message), e);
        }
    }

    public void send(String[] receiver, String subject, String content) {
        // 创建邮件消息
        Message message = buildMessage(receiver, subject, content);
        if (message == null) {
            return;
        }
        // 加入到发送队列
        sendQueue.add(message);
    }

    /**
     * 如果账号密码有修改，可以调用这个方法重新连接
     *
     * @param senderAccount  发件人邮箱账号
     * @param senderPassword 发件人邮箱密码
     */
    public void reconnection(String senderAccount, String senderPassword) {
        try {
            transport.close();
            transport.connect(senderAccount, senderPassword);
            log.info("mail reconnection，senderAccount={}, senderPassword={}", senderAccount, senderPassword);
            mailConfig.setSenderAccount(senderAccount);
            mailConfig.setSenderPassword(senderPassword);
        } catch (MessagingException e) {
            log.warn("mail reconnection exception", e);
        }
    }

    private MimeMessage buildMessage(String[] receiver, String subject, String content) {
        String charset = "UTF-8";
        InternetAddress[] receiverAddress = new InternetAddress[receiver.length];

        try {
            for (int i = 0; i < receiver.length; i++) {
                receiverAddress[i] = new InternetAddress(receiver[i], receiver[i], charset);
            }
            MimeMessage message = new MimeMessage(session);
            // 发件人
            message.setFrom(new InternetAddress(mailConfig.getSenderAccount(), mailConfig.getSenderAccount(), charset));
            // 收件人
            message.setRecipients(MimeMessage.RecipientType.TO, receiverAddress);
            // 邮件主题
            message.setSubject(subject, charset);
            // 邮件内容
            message.setContent(content, "text/html;charset=" + charset);
            // 发件时间
            message.setSentDate(new Date());
            return message;
        } catch (Exception e) {
            log.warn("发送邮件，创建邮件异常", e);
            return null;
        }
    }

    private String messageToString(Message message) {
        if (message == null) {
            return "";
        }
        JSONObject json = new JSONObject();
        try {
            Address[] addressArray = message.getAllRecipients();
            String[] receiverAccounts = new String[addressArray.length];
            for (int i = 0; i < addressArray.length; i++) {
                Address recipients = addressArray[i];
                String address = JSON.parseObject(JSON.toJSONString(recipients)).getString("address");
                receiverAccounts[i] = address;
            }
            json.put("receiver", receiverAccounts);
            json.put("subject", message.getSubject());
            json.put("content", message.getContent());
        } catch (Exception e) {
            log.warn("messageToString error, message={}", JSON.toJSONString(message), e);
            return "messageToString error";
        }
        return json.toJSONString();
    }

}
