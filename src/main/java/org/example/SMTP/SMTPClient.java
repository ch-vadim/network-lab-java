package org.example.SMTP;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class SMTPClient {

    public static void main(String[] args) {

        //simpleMessage();

        simpleFileAttachment();
    }

    public static void simpleFileAttachment() {

        String to = ""; //fill
        String from = ""; // fill your email
        String host = "smtp.mail.ru";
        String password2 = ""; //fill password fill


        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.port", 465);
        props.put("mail.smtp.password", password2);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.mail.ru");
        props.put("mail.user", from);
        props.put("mail.password", password2);

        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                from, password2);
                    }
                });

        try {

            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            Multipart multipart = new MimeMultipart("related");
            // Create the message part
            BodyPart messageBodyPart;
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Test Message");
            messageBodyPart.setHeader("Content-Type", "text/html");
            multipart.addBodyPart(messageBodyPart);

            DataSource source;
            File file = new File("C:\\Users\\79124\\IdeaProjects\\network-lab-java\\src\\main\\resources\\test-image.jpg");
            if (file.exists()) {
                messageBodyPart = new MimeBodyPart();
                source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(file.getName());
                messageBodyPart.setHeader("Content-ID", "image/gif");
                messageBodyPart.setDisposition("inline");
                multipart.addBodyPart(messageBodyPart);
            }

            message.setContent(multipart);


            Transport.send(message);
            System.out.println("Сообщение успешно отправлено....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}