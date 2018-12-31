package org.kumoricon.registration.model.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFFAULT_PASSWORD = "password";

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(Integer id) {
        User user = userRepository.findOneById(id);

        if (user == null) {
            throw new UserIdNotFoundException(id);
        }
        return user;
    }

    public User resetPassword(Integer userId) {
        User u = findById(userId);
        if (u == null) {
            throw new UserIdNotFoundException(userId);
        }

        u.setPassword(passwordEncoder.encode(DEFFAULT_PASSWORD));
        u.setCredentialsNonExpired(false);
        userRepository.save(u);
        return u;
    }

    public User updateUser(User updates) {
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
        current.setRole(updates.getRole());

        return userRepository.save(current);

    }

    /**
     * Generate a blank User record with random password salt and default password set
     * Must be used to initialize required fields
     * @return User
     */
    public User newUser() {
        User user = new User();
        user.setId(null);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(DEFFAULT_PASSWORD));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        //        u.resetPassword();                  // Set to default password and force it to be changed on login
        user.setLastBadgeNumberCreated(1213);  // Start at an arbitrary number instead of 0
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



}
