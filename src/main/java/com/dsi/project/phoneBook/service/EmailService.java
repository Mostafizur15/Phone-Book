package com.dsi.project.phoneBook.service;

import com.dsi.project.phoneBook.customExceptions.EmailSendException;
import com.dsi.project.phoneBook.dao.ContactRepository;
import com.dsi.project.phoneBook.dao.UserRepository;
import com.dsi.project.phoneBook.dto.EmailFormDTO;
import com.dsi.project.phoneBook.entities.Contact;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Properties;
import java.util.Random;

@Service
public class EmailService {
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    @Autowired
    public EmailService(UserRepository userRepository, ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }
    public synchronized boolean dataTransferByEmail(EmailFormDTO emailFormDTO, Principal principal, String to) throws EmailSendException {
        try {
            List<String> contactsId = emailFormDTO.getSelectedContacts();
            System.out.println(contactsId);
            List<Contact> contacts = this.contactRepository.getContactByEmails(contactsId);
            StringBuffer htmlContentMessage = new StringBuffer();
            htmlContentMessage.append("<html><body>");
            for (Contact contact : contacts) {
                formHtmlContentForMessage(htmlContentMessage, contact);
            }
            htmlContentMessage.append("</body></html>");
            System.out.println(htmlContentMessage.toString());
            return sendEmail(htmlContentMessage.toString(), principal.getName(), to, this.userRepository.getUserByEmail(principal.getName()).getVerifiedPassword());
        } catch (Exception ex) {
            throw new EmailSendException("Something went wrong during email send!");
        }
    }
    private boolean sendEmail(String content, String from, String to, String appPassword) throws MessagingException {
        String host = "smtp.gmail.com";
        Properties props = System.getProperties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(props,new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(from,appPassword);
            }
        });
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Contact Information from "+from);
        message.setContent(content, "text/html; charset=utf-8");
        Transport.send(message);
        return true;
    }
    private void formHtmlContentForMessage(StringBuffer htmlContentMessage, Contact contact) {

        htmlContentMessage.append("<div style='margin-bottom: 15px;'>");
        htmlContentMessage.append("<strong>Name:</strong> ").append(contact.getName()).append("<br>");
        htmlContentMessage.append("<strong>Nickname:</strong> ").append(contact.getSecondName()).append("<br>");
        htmlContentMessage.append("<strong>Email:</strong> ").append(contact.getEmail()).append("<br>");
        htmlContentMessage.append("<strong>Contact Number:</strong> ").append(contact.getPhoneNumber()).append("<br>");
        htmlContentMessage.append("</div>");
    }

    public Boolean otpTransfer(StringBuffer otp,String fromEmail, String toEmail) {
        try {
            Random rand = new Random();
            otp.append((String.valueOf(rand.nextInt(999999))));
            StringBuffer htmlContentMessage = new StringBuffer();
            htmlContentMessage.append("<html><body>");
            formHtmlContentForOtp(htmlContentMessage, otp);
            htmlContentMessage.append("</body></html>");
            return sendEmail(htmlContentMessage.toString(), fromEmail, toEmail, this.userRepository.getUserByEmail(fromEmail).getVerifiedPassword());
        } catch (Exception ex) {
            throw new EmailSendException("Something went wrong during email send!");
        }
    }

    private void formHtmlContentForOtp(StringBuffer htmlContentMessage, StringBuffer otp) {
        htmlContentMessage.append("<div style='margin-bottom: 15px;'>");
        htmlContentMessage.append("<strong>OTP: </strong> ").append(otp).append("<br>");
        htmlContentMessage.append("<p>Please don`t share your OTP!</p>");
        htmlContentMessage.append("</div>");
    }
}
