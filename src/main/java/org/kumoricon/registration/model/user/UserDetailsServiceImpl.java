package org.kumoricon.registration.model.user;

import org.kumoricon.registration.model.role.RightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * This service provides user data to Spring Security. As such, it has to do more work than the regular
 * UserRepository -- user objects need to have their Rights populated. To cut down on database calls,
 * that isn't done in UserRepository, only here.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RightRepository rightRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, RightRepository rightRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rightRepository = rightRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findOneByUsernameIgnoreCase(username);
        user.setRights(rightRepository.findAllRightsByUserId(user.getId()));

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}