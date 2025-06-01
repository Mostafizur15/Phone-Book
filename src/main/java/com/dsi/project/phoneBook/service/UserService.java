package com.dsi.project.phoneBook.service;

import com.dsi.project.phoneBook.customExceptions.PasswordResetException;
import com.dsi.project.phoneBook.customExceptions.UpdatePasswordException;
import com.dsi.project.phoneBook.dao.UserRepository;
import com.dsi.project.phoneBook.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    public User saveUser(User user){
        return userRepository.save(user);
    }
    public void changePassword(String oldPassword, String newPassword, String userName) {
        try{
            User user = this.userRepository.getUserByEmail(userName);
            if(bCryptPasswordEncoder.matches(oldPassword,user.getPassword())) {
                String updatedPass = bCryptPasswordEncoder.encode(newPassword);
                user.setPassword(updatedPass);
                this.userRepository.save(user);
            }
        } catch (Exception e) {
            throw new UpdatePasswordException("Something went wrong during updating password");
        }
    }

    public boolean verifyOTP(String chkOTP, String otp) {
        try{
            return chkOTP.equals(otp);
        } catch (Exception e) {
            throw new PasswordResetException("Something went wrong!");
        }
    }

    public User getUser(String email) {
        try{
            return this.userRepository.getUserByEmail(email);
        } catch (Exception e) {
            throw new PasswordResetException("Something went wrong!");
        }
    }

    public void resetPassword(User user, @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password1) {
        try{
            user.setPassword(bCryptPasswordEncoder.encode(password1));
            this.userRepository.save(user);
        } catch (Exception e) {
            throw new PasswordResetException("Something went wrong!");
        }
    }
}
