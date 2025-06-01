package com.dsi.project.phoneBook.controller;

import com.dsi.project.phoneBook.dao.UserRepository;
import com.dsi.project.phoneBook.dto.ContactsDTO;
import com.dsi.project.phoneBook.dto.EmailDTO;
import com.dsi.project.phoneBook.entities.Contact;
import com.dsi.project.phoneBook.entities.User;
import com.dsi.project.phoneBook.messages.message;
import com.dsi.project.phoneBook.service.GmailContactService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class GoogleContactsLoadController {

    @Autowired
    private GmailContactService gmailContactService;
    @Autowired
    private UserRepository userRepository;
    private final String clientId="";
    private final String redirectUri="";
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.getUserByEmail(username);
        model.addAttribute("user", user);
    }
    @GetMapping("/syncContact")
    public String syncContact(Model model) {
        model.addAttribute("title", "Sync Contact");
        model.addAttribute("emailDTO", new EmailDTO());
        return "normal/sync_contact";
    }
    @GetMapping("/googleLogin")
    public void googleLogin(Model model, HttpServletResponse response) throws IOException {
        String url = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "scope=https://www.googleapis.com/auth/contacts.readonly&" +
                "access_type=online&" +
                "include_granted_scopes=true&" +
                "response_type=code&" +
                "redirect_uri=" + redirectUri + "&" +
                "client_id=" + clientId + "&" +
                "prompt=consent";

        response.sendRedirect(url);
    }

    @GetMapping("/google/callback")
    public String handleGoogleCallback(@RequestParam("code") String code, Model model, HttpSession session, Principal principal) throws Exception {

        List<Contact> allContacts = this.gmailContactService.loadContact(code,clientId,redirectUri,principal.getName());

        model.addAttribute("contacts", allContacts);
        session.setAttribute("contacts", allContacts);
        model.addAttribute("contactsDTO",new ContactsDTO());
        model.addAttribute("title", "Load Contacts");
        return "normal/load_contacts";
    }
    @GetMapping("/SaveGmailContacts")
    public String SaveGmailContacts(@Valid @ModelAttribute("contactsDTO") ContactsDTO contactsDTO, BindingResult result, Model model, HttpSession session, Principal principal) {
        //this.gmailContactService.saveContacts(());
        if(result.hasErrors()) {
            model.addAttribute("contacts", session.getAttribute("contacts"));
            model.addAttribute("message",new message("Please select at least one contact!","alert-danger"));
            return "normal/load_contacts";
        }
        List<String> contacts = contactsDTO.getSelectedContacts();
        if(session.getAttribute("contacts") == null) {
            model.addAttribute("message",new message("Something went wrong!","alert-danger"));
            return "normal/sync_contact";
        }
        List<Contact> allContacts = ((List<Contact>) session.getAttribute("contacts"))
                .stream().filter(contact -> contacts.contains(contact.getPhoneNumber())).collect(Collectors.toList());
        session.removeAttribute("contacts");
        this.gmailContactService.saveContacts(allContacts);
        return "redirect:/user/showContacts";
    }





















}
