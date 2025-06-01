package com.dsi.project.phoneBook.controller;

import com.dsi.project.phoneBook.dto.EmailDTO;
import com.dsi.project.phoneBook.dto.OtpDTO;
import com.dsi.project.phoneBook.dto.PasswordResetDTO;
import com.dsi.project.phoneBook.entities.User;
import com.dsi.project.phoneBook.messages.message;
import com.dsi.project.phoneBook.service.EmailService;
import com.dsi.project.phoneBook.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ForgotPasswordController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping("/forgotPassword")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        model.addAttribute("emailDTO", new EmailDTO());
        return "forgot_password";
    }
    @GetMapping("/submitEmailForGenarateOTP")
    public String forgotPasswordSubmit(@Valid @ModelAttribute("emailDTO")EmailDTO emailDTO, BindingResult result,
                                       HttpSession session, Model model) {
        if(result.hasErrors()) {
            return "forgot_password";
        }
        StringBuffer OTP = new StringBuffer();
        if(this.userService.getUser(emailDTO.getEmail()) == null) {
            model.addAttribute("message", new message("User not found under this email!", "alert-danger"));
            return "forgot_password";
        }
        model.addAttribute("title", "Verify OTP");
        boolean chkSuccess = this.emailService.otpTransfer(OTP,"mostafiz.forhad6@gmail.com",emailDTO.getEmail());
        if(!chkSuccess) {
            model.addAttribute("message", new message("Unable to send OTP!", "alert-danger"));
            return "forgot_password";
        }
        model.addAttribute("message",new message("OTP send successfully!", "alert-success"));
        model.addAttribute("otp",OTP);
        model.addAttribute("email",emailDTO.getEmail());
        model.addAttribute("otpDTO",new OtpDTO());
        session.setAttribute("emailToResetPass",emailDTO.getEmail());
        return "verify_otp";
    }
    @GetMapping("/resendOTP")
    public String resendOTP(@RequestParam("email") String email, Model model) {
        StringBuffer OTP = new StringBuffer();
        boolean chkSuccess = this.emailService.otpTransfer(OTP,"mostafiz.forhad6@gmail.com",email);
        if(!chkSuccess) {
            model.addAttribute("message", new message("Unable to send OTP!", "alert-danger"));
            return "forgot_password";
        }
        model.addAttribute("message",new message("OTP send successfully!", "alert-success"));
        model.addAttribute("otp",OTP);
        model.addAttribute("email",email);
        model.addAttribute("otpDTO",new OtpDTO());
        return "verify_otp";
    }
    @PostMapping("/verifyOTP")
    public String verifyOTP(@Valid @ModelAttribute("otpDTO") OtpDTO otpDTO,
                            BindingResult result,
                            @RequestParam("chkOTP") String chkOTP, Model model
                            , HttpSession session) {
        if(result.hasErrors()) {
            return "verify_otp";
        }
        boolean chkSuccess = this.userService.verifyOTP(chkOTP,otpDTO.getOtp());
        if(!chkSuccess) {
            model.addAttribute("message", new message("OTP is in-correct!", "alert-danger"));
            model.addAttribute("otp",chkOTP);
            return "verify_otp";
        }
        model.addAttribute("message",new message("OTP verified!", "alert-success"));
        model.addAttribute("passwordResetDTO",new PasswordResetDTO());
        return "reset_password";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute("passwordResetDTO") PasswordResetDTO passwordResetDTO,
                                BindingResult result, Model model, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if(result.hasErrors()) {
            return "reset_password";
        }
        model.addAttribute("title", "Reset Password");
        Object emailTemp = session.getAttribute("emailToResetPass");
        String email = emailTemp!=null?emailTemp.toString():null;
        if(email == null) {
            model.addAttribute("message", new message("Session is cleared!", "alert-danger"));
            return "forgot_password";
        }
        User user = this.userService.getUser(email);
        if(!passwordResetDTO.getPassword1().equals(passwordResetDTO.getPassword2())) {
            model.addAttribute("message", new message("Please provide same password!", "alert-danger"));
            model.addAttribute("passwordResetDTO",new PasswordResetDTO());
            return "reset_password";
        }
        this.userService.resetPassword(user,passwordResetDTO.getPassword1());
        redirectAttributes.addFlashAttribute("message", new message("Password changed!", "alert-success"));
        return "redirect:/login";
    }
}
