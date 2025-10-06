package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        
        Optional<User> user = userRepository.findByEmailOrUsername(identifier);
        
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with identifier: " + identifier);
        }
        
        return user.get();
    }

}
