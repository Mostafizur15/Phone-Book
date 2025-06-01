package com.dsi.project.phoneBook.controller;

import com.dsi.project.phoneBook.customExceptions.*;
import com.dsi.project.phoneBook.dto.EmailFormDTO;
import com.dsi.project.phoneBook.dao.UserRepository;
import com.dsi.project.phoneBook.dto.PasswordUpdateDTO;
import com.dsi.project.phoneBook.entities.Contact;
import com.dsi.project.phoneBook.entities.User;
import com.dsi.project.phoneBook.messages.message;
import com.dsi.project.phoneBook.service.ContactService;
import com.dsi.project.phoneBook.service.EmailService;
import com.dsi.project.phoneBook.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactService contactService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.getUserByEmail(username);
        model.addAttribute("user", user);
    }
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    @GetMapping("/addContact")
    public String addContact(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact";
    }

    @PostMapping("/processAddContact")
    public String processAddContact(
            Model model,
            @ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile file, RedirectAttributes redirectAttributes,
            Principal principal) throws AddFailedException, FileStorageException, DuplicateEntryException, UpdateContactException {
            boolean checkSuccess = false;
            checkSuccess = this.contactService.addContact(contact,file,principal);
            if(checkSuccess) {
                redirectAttributes.addFlashAttribute("message", new message("Action performed successfully", "alert-success"));
            }
        return "redirect:/user/showContacts";
    }

    @GetMapping("/showContacts")
    public String showAllContact(Model model, Principal principal, HttpSession session) {
        model.addAttribute("title", "Contacts");
        List<Contact> listOfContacts = this.contactService.getContacts(principal);
        model.addAttribute("contacts", listOfContacts);
        return "normal/show_contacts";
    }

    @GetMapping("/contact/{cId}")
    public String displayContact(Model model, Principal principal, @PathVariable("cId") Integer cId){
        Contact contact = this.contactService.getContactById(principal,cId);
        if (this.userRepository.getUserByEmail(principal.getName()).getId().equals(contact.getUser().getId()))
            model.addAttribute("contact", contact);
        return "normal/display_contact";
    }

    @PostMapping("/delete/{cId}")
    public String deleteContact(Model model, Principal principal, @PathVariable("cId") Integer cId) throws DeleteContactException, FileCanNotBeDeletedException {
        this.contactService.deleteContact(principal,cId);
        return "redirect:/user/showContacts";
    }

    @GetMapping("/update/{cId}")
    public String updateContact(Model model, Principal principal, @PathVariable("cId") Integer cId) throws UpdateContactException {
        Contact contact = this.contactService.getContactById(principal,cId);
        if (this.userRepository.getUserByEmail(principal.getName()).getId().equals(contact.getUser().getId()))
            model.addAttribute("contact", contact);
        return "normal/update_contact";
    }

    @GetMapping("/profile")
    public String displayProfile(Model model) {
        model.addAttribute("title", "Profile");
        return "normal/profile";
    }

    @GetMapping("/showContactsForTransfer")
    public String showAllContactForTransfer(Model model, Principal principal) {
        model.addAttribute("title", "Transfer Contacts");
        model.addAttribute("emailFormDTO",new EmailFormDTO());
        List<Contact> listOfContacts = this.contactService.getContacts(principal);
        model.addAttribute("contacts", listOfContacts);
        return "normal/transfer_contacts";
    }
    @PostMapping("/sendMail")
    public String sendMail(@Valid @ModelAttribute("emailFormDTO") EmailFormDTO emailFormDTO,
                           BindingResult result, Model model, Principal principal,
                           RedirectAttributes redirectAttributes,
                           @RequestParam("toEmail") String to){
        if(result.hasErrors()) {
            if(result.hasFieldErrors("selectedContacts")) {
                redirectAttributes.addFlashAttribute("message", !result.hasFieldErrors("toEmail")?new message("Please select at-least one contact!", "alert-danger")
                        :new message("Please select at-least one contact and provide the email!", "alert-danger"));
            }else{
                redirectAttributes.addFlashAttribute("message", new message("Please provide valid email!", "alert-danger"));
            }
            return "redirect:/user/showContactsForTransfer";
        }
        boolean chkSuccess = this.emailService.dataTransferByEmail(emailFormDTO,principal,to);
        redirectAttributes.addFlashAttribute("message", chkSuccess ? new message("Email send successfully!", "alert-success")
                : new message("Email send failed!", "alert-danger"));
        return "redirect:/user/showContacts";
    }
    @GetMapping("/setting")
    public String setting(Model model) {
        model.addAttribute("title", "Setting");
        model.addAttribute("passwordUpdateDTO", new PasswordUpdateDTO());
        return "normal/setting";
    }
    @PostMapping("/changePassword")
    public String changePass(@Valid @ModelAttribute("passwordUpdateDTO") PasswordUpdateDTO passwordUpdateDTO,
                             BindingResult result,RedirectAttributes redirectAttributes,
                             @RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal) {
        if(result.hasErrors()){
            return "normal/setting";
        }
        this.userService.changePassword(oldPassword,newPassword,principal.getName());
        redirectAttributes.addFlashAttribute("message", new message("Password changed successfully!", "alert-success"));
        return "redirect:/user/setting";
    }
}
