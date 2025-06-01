package com.dsi.project.phoneBook.config;

import com.dsi.project.phoneBook.dao.UserRepository;
import com.dsi.project.phoneBook.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if(user == null){throw new UsernameNotFoundException("User not found");}
        return new UserDetailsImpl(user);
    }
}
