package com.recommender.mailutil;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.recommender.evaluator.CSFDEvaluator;

public class MailUtil {
	
	private final String username = "matej.lochman@hotmail.com";
    private final String password = "Lochmanci";
    private final String mailTo = "matej.lochman@gmail.com";
    private Session session;
    private Date execDate;
	
	public MailUtil() {
		Properties props = new Properties();
		props.put("mail.debug", "true");
		props.put("mail.smtp.host", "smtp.live.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		
		Authenticator auth = new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(username, password);
		    }
		};
		session = Session.getInstance(props, auth);
		execDate = new Date();
	}

    public void sendEmail(String methodName) {
    	try {
    		String filename = CSFDEvaluator.LOG_DIR + methodName + CSFDEvaluator.FILE_OUT_SUFFIX;
    		Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
            msg.setSubject("Test " + execDate + " " + methodName);
            msg.setSentDate(new Date());
            
            Multipart multipart = new MimeMultipart();
            
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText("Test " + execDate + " " + methodName); 
            multipart.addBodyPart(bodyPart);
            
            bodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            bodyPart.setDataHandler(new DataHandler(source));
            bodyPart.setFileName(filename);
            multipart.addBodyPart(bodyPart);
    
            msg.setContent(multipart);

            Transport.send(msg);
            
            System.out.println("Email sent!");

        } catch (MessagingException e) {
            System.err.println("Failed to send email.");
        }
    }
}
