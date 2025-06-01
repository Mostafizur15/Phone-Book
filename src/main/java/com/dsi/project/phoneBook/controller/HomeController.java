package com.dsi.project.phoneBook.controller;

import com.dsi.project.phoneBook.entities.User;
import com.dsi.project.phoneBook.messages.message;
import com.dsi.project.phoneBook.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home");
        return "home";
    }
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About");
        return "about";
    }
    @GetMapping("/signUp")
    public String signUp(Model model) {
        model.addAttribute("title", "signUp");
        model.addAttribute("user", new User());
        return "signUp";
    }
    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        session.removeAttribute("emailToResetPass");
        model.addAttribute("title", "login");
        if(model.containsAttribute("message")) {
            model.addAttribute("message",new message("Password changed!", "alert-success"));
        }
        return "login";
    }
    @PostMapping("/doRegistration")
    public String doRegistration(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
                                 @RequestParam(value = "agreement",defaultValue = "false") boolean agreement,
                                Model model) {
        try{
            if(bindingResult.hasErrors()){
                model.addAttribute("user", user);
                System.out.println("Error found!!");
                throw new Exception();
            }
            if(!agreement){
                System.out.println("Please accept the terms and conditions");
                throw new Exception("Please accept the terms and conditions");
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("defalut.jpg");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User result = userService.saveUser(user);
            model.addAttribute("user", result);
            model.addAttribute("message", new message("Registration successful!", "alert-success") );
        }catch (Exception e){
            model.addAttribute("user", user);
            System.out.println(e.getLocalizedMessage());
            model.addAttribute("message", new message(!e.getMessage().equals("Please accept the terms and conditions")?"Something went wrong!":e.getMessage(), "alert-danger") );
            e.printStackTrace();
        }finally {
            return "signUp";
        }
    }

}
