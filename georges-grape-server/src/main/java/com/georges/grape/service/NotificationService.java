package com.georges.grape.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.core.io.Resource;

import com.georges.grape.data.Contact;
import com.georges.grape.data.Event;

public class NotificationService {

    private String smtpHost;
    private String smtpPort;
    private String smtpUser;
    private String smtpPassword;
    private String smtpSendFrom;
    private String emailHeaderImage;

    public void setEmailHeaderImage(Resource resource) {
        try {
            emailHeaderImage=resource.getFile().getPath();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public void setSmtpSendFrom(String smtpSendFrom) {
        this.smtpSendFrom = smtpSendFrom;
    }
 
    
    private void inviteByEmail(String invitor, String inviteMsg, Event event, List<String> emailList)
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.socketFactory.port",smtpPort);
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", smtpPort);
 
        Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUser,smtpPassword);
                }
            });
 
        try {
 
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpSendFrom));
            
            String recipients="";
            for(String s: emailList)
            {
                if(recipients=="")
                    recipients=s;
                else
                    recipients+=","+s;
            }
            
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipients));
            
            message.setSubject("Join this event", "UTF-8");
   
            //
            // This HTML mail have to 2 part, the BODY and the embedded image
            //
            MimeMultipart multipart = new MimeMultipart("related");

            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText ="";
            htmlText+="<img src=\"cid:header_image\">";
            htmlText+="<H1> "+invitor+" invite you to join the event <b>\""+ event.getSubject()+ "\"</b></H1>";
            if(inviteMsg!=null && inviteMsg!="")
            {
                htmlText+="<i>"+inviteMsg+"</i><br>";
            }
            htmlText+="Desc: "+event.getDescription()+"<br>";
            htmlText+="Date: "+event.getDate().toString()+"<br>";
            htmlText+="Address: "+event.getAddress()+"<br>";
            htmlText+="<br>";
            htmlText+="Now there are the following members:<br>";
            
            htmlText+="<br>";
            htmlText+="You can download the software from Google Play";
            
            

            messageBodyPart.setContent(htmlText, "text/html;charset=UTF-8");

            // add it
            multipart.addBodyPart(messageBodyPart);
            
            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(emailHeaderImage);
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID","<header_image>");

            // add it
            multipart.addBodyPart(messageBodyPart);

            // put everything together
            message.setContent(multipart);
   
            Transport.send(message);
 
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        
    }
    
    private void inviteBySMS(String invitor, String inviteMsg, List<String> phoneList)
    {
        
    }
    
    private void inviteByNotification(String invitor, String inviteMsg, List<String> grapeContactList)
    {
        
    }
    
    public void invitePeopleJoinEvent(String invitor,String inviteMsg, Event event, List<Contact> contacts)
    {
        List<String> emailList=new ArrayList<String>();
        List<String> phoneList=new ArrayList<String>();
        List<String> grapeContactList=new ArrayList<String>();
    
        
        for(Contact c: contacts)
        {
            if(c.getType()==Contact.Type.EMAIL)
                emailList.add(c.getContact());
            else if(c.getType()==Contact.Type.PHONE)
                phoneList.add(c.getContact());
            else
                grapeContactList.add(c.getContact());
        }
        if(emailList.size()!=0)
        {
            inviteByEmail(invitor, inviteMsg, event, emailList);
        }
       
        if(phoneList.size()!=0)
        {
            inviteBySMS(invitor, inviteMsg, phoneList);
        }
        
        if(grapeContactList.size()!=0)
        {
            inviteByNotification(invitor, inviteMsg, grapeContactList);
        }
    }
}
