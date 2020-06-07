package serverNet;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Random;

public class MailVerifier {
	private final String senderMailAdd;
	private final Session session;
	private final Random rand=new Random();
	static char[] charSet=new char[62];
	private final HashMap<Integer,String> correctCode=new HashMap<>();
	public MailVerifier(String senderAddress,String password,String host) {
		senderMailAdd=senderAddress;
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.auth", "true");
		session = Session.getDefaultInstance(properties,new Authenticator(){
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderAddress, password); //发件人邮件用户名、授权码
			}
		});
		for(int i=0;i<10;i++)
			charSet[i]=(char) (i+48);
		for(int i=0;i<26;i++)
			charSet[i+10]=(char) (i+64);
		for(int i=0;i<26;i++)
			charSet[i+36]=(char) (i+96);
	}
	public void sendRandomCode(int id,String receiver) {
		char[] code=new char[6];
		for(int i=0;i<6;i++)
			code[i]=charSet[rand.nextInt(62)];
		correctCode.remove(id);
		correctCode.put(id, String.valueOf(code));
		try{
	         MimeMessage message = new MimeMessage(session);
	         message.setFrom(new InternetAddress(senderMailAdd));
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
	         message.setSubject("[Java Chatroom] Your verification code");
	         message.setText("This is your code: " + String.valueOf(code));
	         Transport.send(message);
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	}
	
	public boolean checkCode(int id,String code) {
		if(correctCode.get(id).equals(code)) {
			correctCode.remove(id);
			return true;
		}
		return false;
	}
	
}
