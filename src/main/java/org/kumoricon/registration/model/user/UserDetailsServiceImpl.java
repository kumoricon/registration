package org.kumoricon.registration.model.user;

import org.kumoricon.registration.model.role.RightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service provides user data to Spring Security. As such, it has to do more work than the regular
 * UserRepository -- user objects need to have their Rights populated. To cut down on database calls,
 * that isn't done in UserRepository, only here.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RightRepository rightRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, RightRepository rightRepository) {
        this.userRepository = userRepository;
        this.rightRepository = rightRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findOneByUsernameIgnoreCase(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        user.setRights(rightRepository.findAllRightsByUserId(user.getId()));

        return user;
    }
}