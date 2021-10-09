package org.kumoricon.registration.model.user;

import org.kumoricon.registration.model.loginsession.LoginRepository;
import org.kumoricon.registration.model.role.RightRepository;
import org.kumoricon.registration.settings.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible for User objects. It implements UserDetailsService (and User implements UserDetails) so that they
 * can be used by Spring Security
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;
    private final RightRepository rightRepository;
    private final PasswordEncoder passwordEncoder;
    private final SettingsService settingsService;
    private static final Integer INITIAL_BADGE_NUMBER = 1183; // This is the first badge number issued by each user.
                                                              // it's a generic high number because an early director
                                                              // got tired of people bugging him to get a low badge
                                                              // numbers, and the tradition has stuck
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository,
                       LoginRepository loginRepository,
                       RightRepository rightRepository,
                       PasswordEncoder passwordEncoder,
                       SettingsService settingsService) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
        this.rightRepository = rightRepository;
        this.passwordEncoder = passwordEncoder;
        this.settingsService = settingsService;
    }

    public User findById(Integer id) {
        User user = userRepository.findOneById(id);

        if (user == null) {
            throw new UserIdNotFoundException(id);
        }
        return user;
    }

    public User findByUsername(String username) {
        User user = userRepository.findOneByUsernameIgnoreCase(username);
        if (user == null) {
            throw new UserIdNotFoundException(null);
        }
        return user;
    }

    @Transactional
    public User resetPassword(Integer userId) {
        User u = findById(userId);
        if (u == null) {
            throw new UserIdNotFoundException(userId);
        }

        u.setPassword(passwordEncoder.encode(settingsService.getCurrentSettings().getDefaultPassword()));
        u.setForcePasswordChange(true);
        userRepository.save(u);

        loginRepository.deleteLoginSessionsForUsername(u.getUsername());
        return u;
    }

    @Transactional
    public void updateUser(User updates) {
        User current;
        if (updates.getId() == null) {
            current = newUser();
        } else {
            current = findById(updates.getId());
        }
        current.setUsername(updates.getUsername());
        current.setFirstName(updates.getFirstName());
        current.setLastName(updates.getLastName());
        current.setEnabled(updates.getEnabled());
        current.setRoleId(updates.getRoleId());

        userRepository.save(current);
    }

    /**
     * Generate a blank User record with random password salt and default password set
     * Must be used to initialize required fields
     * @return User
     */
    public User newUser() {
        User user = new User();
        user.setId(null);
        user.setOnlineId(null);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(settingsService.getCurrentSettings().getDefaultPassword()));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setForcePasswordChange(true);
        user.setLastBadgeNumberCreated(INITIAL_BADGE_NUMBER);  // Start at an arbitrary number instead of 0
        return user;
    }

    /**
     * Generate a new User record with the given first and last names and the default password.
     * Username will be first initial + last name, lower case.
     * @param firstName First Name
     * @param lastName Last Name
     * @return User
     */
    public User newUser(String firstName, String lastName) {
        User u = newUser();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setUsername(generateUserName(firstName, lastName));
        return u;
    }

    /**
     * Remove anything that isn't a letter and combine to first initial + last name. Doesn't do
     * uniqueness checking with existing users
     * @param firstName First Name
     * @param lastName Last Name
     * @return username
     */
    protected static String generateUserName(String firstName, String lastName) {
        String username;
        if (firstName == null) { firstName = ""; }
        if (lastName == null) { lastName = ""; }

        firstName = firstName.replaceAll("[^\\p{L}]", "");
        if (firstName.length() > 0) {
            firstName = firstName.substring(0, 1);
        }

        lastName = lastName.replaceAll("[^\\p{L}]", "");

        username = String.format("%s%s", firstName, lastName);
        return username.trim().toLowerCase();
    }


    @Transactional
    public void setPassword(Integer userId, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.setPassword(userId, false, encodedPassword);
    }


    public void setPasswordOnUserObject(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }

    /**
     * Loads a User object WITH Rights attached from the database. This is used by Spring Security, so needs to have
     * the Rights attached as well as the basic object properties, which are usually not added to the User object
     * because they're not needed.
     * @param username User name
     * @return Object that implements UserDetails
     */
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

    public boolean validateOverridePassword(User override, String overridePassword) {
        if (override.getPassword() == null || overridePassword == null) return false;

        if (!(override.isEnabled() && override.isAccountNonExpired() && override.isAccountNonLocked() && override.isCredentialsNonExpired())) {
            log.warn("tried to use {} for an override but that account is disabled/expired/etc", override);
            return false;
        }
        boolean passwordMatches = passwordEncoder.matches(overridePassword, override.getPassword());
        if (passwordMatches) {
            return true;
        } else {
            log.info("tried to use {} for an override but override password doesn't match", override);
            return false;
        }
    }
}
