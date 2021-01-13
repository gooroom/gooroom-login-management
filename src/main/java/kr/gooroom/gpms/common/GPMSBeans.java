package kr.gooroom.gpms.common;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import kr.gooroom.gpms.common.utils.Constant;

@Configuration
public class GPMSBeans {
	
	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    
	    //mailSender.setProtocol("smtp");
	    mailSender.setHost(Constant.CFG_MAIL_HOST);
	    mailSender.setPort(Integer.parseInt(Constant.CFG_MAIL_PORT));
	     
	    mailSender.setUsername(Constant.CFG_MAIL_AUTH_USERNAME);
	    mailSender.setPassword(Constant.CFG_MAIL_AUTH_PASSWORD);
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "false");
	    if(Constant.CFG_MAIL_USESSL.equalsIgnoreCase("y")) {
	    	props.put("mail.smtp.ssl.trust",Constant.CFG_MAIL_HOST);
	    }	    
	    
	    return mailSender;
	}

}
