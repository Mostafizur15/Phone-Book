package com.dsi.project.phoneBook.globalExHandler;

import com.dsi.project.phoneBook.customExceptions.*;
import com.dsi.project.phoneBook.dao.UserRepository;
import com.dsi.project.phoneBook.entities.Contact;
import com.dsi.project.phoneBook.entities.User;
import com.dsi.project.phoneBook.messages.message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@ControllerAdvice
public class ExHandler {
    @Autowired
    private UserRepository userRepository;

    @ExceptionHandler({FileStorageException.class,DuplicateEntryException.class})
    public String addContactException(Model model, Throwable ex, Principal principal) {
        loadCommonDataOnModel(model, principal,"Add Contact");
        model.addAttribute("contact", new Contact());
        log.error("ERROR FROM addContactException EXCEPTION: "+ex.getMessage());
        model.addAttribute("message", new message(ex.getMessage(), "alert-danger"));
        return "normal/add_contact";
    }
    @ExceptionHandler(FileCanNotBeDeletedException.class)
    public String contactModificationException(Model model, Throwable ex, RedirectAttributes redirectAttributes, Principal principal) {
        loadCommonDataOnModel(model, principal,"Contacts");
        log.error("ERROR FROM contactModificationException EXCEPTION: "+ex.getMessage());
        redirectAttributes.addFlashAttribute("message", new message("Something went wrong!", "alert-danger"));
        return "redirect:/user/showContacts";
    }
    @ExceptionHandler({UpdateContactException.class,contactNotFoundException.class})
    public String contactUpdateException(Model model, Throwable ex, RedirectAttributes redirectAttributes, Principal principal) {
        loadCommonDataOnModel(model, principal,"Contacts");
        log.error("ERROR FROM UpdateContactException EXCEPTION: "+ex.getMessage());
        redirectAttributes.addFlashAttribute("message", new message(ex.getMessage(), "alert-danger"));
        return "redirect:/user/showContacts";
    }
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public String fileSizeLimitException(Model model, MaxUploadSizeExceededException ex, Principal principal) {
        loadCommonDataOnModel(model, principal,"Contacts");
        model.addAttribute("contact", new Contact());
        log.error("ERROR FROM MaxUploadSizeExceededException EXCEPTION: "+ex.getMessage());
        model.addAttribute("message", new message(ex.getMessage(), "alert-danger"));
        return "normal/add_contact";
    }
    @ExceptionHandler({EmailSendException.class})
    public String emailSendException(Model model, RedirectAttributes redirectAttributes, EmailSendException ex, Principal principal) {
        ex.printStackTrace();
        log.error("ERROR FROM EmailSendException EXCEPTION: "+ex.getMessage());
        loadCommonDataOnModel(model, principal,"Contacts");
        redirectAttributes.addFlashAttribute("message", new message(ex.getMessage(), "alert-danger"));
        return "redirect:/user/showContacts";
    }

    @ExceptionHandler(Throwable.class)
    public String commonException(Model model,Throwable ex,RedirectAttributes redirectAttributes, Principal principal) {
        ex.printStackTrace();
        loadCommonDataOnModel(model, principal,"Add Contact");
        log.error("ERROR FROM COMMON EXCEPTION: "+ex.getMessage());
        redirectAttributes.addFlashAttribute("message", new message("Something went wrong!", "alert-danger"));
        //model.addAttribute("message", new message(ex.getMessage(), "alert-danger"));
        return "redirect:/user/showContacts";
    }
    @ExceptionHandler(UpdatePasswordException.class)
    public String updatePasswordException(Model model, Throwable ex, RedirectAttributes redirectAttributes, Principal principal) {
        ex.printStackTrace();
        loadCommonDataOnModel(model, principal,"Update Password");
        log.error("ERROR FROM UPDATE PASSWORD EXCEPTION: "+ex.getMessage());
        redirectAttributes.addFlashAttribute("message", new message(ex.getMessage(), "alert-danger"));
        return "redirect:/user/setting";
    }
    @ExceptionHandler(GoogleConnectionException.class)
    public String googleConnectionException(Model model, Throwable ex, RedirectAttributes redirectAttributes, Principal principal) {
        ex.printStackTrace();
        loadCommonDataOnModel(model, principal,"Load Contacts");
        log.error("ERROR FROM Google Connection EXCEPTION: "+ex.getMessage());
        redirectAttributes.addFlashAttribute("message", new message(ex.getMessage(), "alert-danger"));
        return "redirect:/user/syncContact";
    }
    @ExceptionHandler(PasswordResetException.class)
    public String passwordResetException(Model model, Throwable ex, RedirectAttributes redirectAttributes, Principal principal) {
        ex.printStackTrace();
        log.error("ERROR FROM PasswordResetException EXCEPTION: "+ex.getMessage());
        redirectAttributes.addFlashAttribute("message", new message(ex.getMessage(), "alert-danger"));
        return "redirect:/login";
    }
    void loadCommonDataOnModel(Model model,Principal principal,String title) {
        model.addAttribute("title", title);
        String username = principal.getName();
        User user = userRepository.getUserByEmail(username);
        model.addAttribute("user", user);
    }
}
