package org.kumoricon.registration.staff.staffimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.kumoricon.registration.model.role.Role;
import org.kumoricon.registration.model.role.RoleRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.kumoricon.registration.model.user.UserService;
import org.springframework.stereotype.Component;


@Component
public class StaffImportUserCreateService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public StaffImportUserCreateService(RoleRepository roleRepository,
                                        UserRepository userRepository,
                                        UserService userService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // if getting by online id is null, create unique username
    public void createUserFromStaff(StaffImportFile.Person person){
        User newUser = createNewUser(person);
        newUser.setOnlineId(person.getId());

        if(userRepository.findOneByOnlineId(newUser.getOnlineId()) != null) {
            return;
        }

        newUser.setUsername(createUniqueUsername(newUser));
        saveUser(newUser);
    }

    private User createNewUser(StaffImportFile.Person person) {
        User newUser = userService.newUser(person.getNamePreferredFirst(), person.getNamePreferredLast());
        Role staffRole = roleRepository.findByNameIgnoreCase("Staff");
        newUser.setRoleId(staffRole.getId());

        return newUser;
    }

    private void saveUser(User newUser) {
        if(userRepository.findOneByUsernameIgnoreCase(newUser.getUsername()) == null) {
            log.info("Create user for {} {}", newUser.getFirstName(), newUser.getLastName());
            userRepository.save(newUser);
        }
    }

    /**
     * Returns new username for person if username is already in system.
     * Continues to add letters from first name until username can be used.
     */
    private String createUniqueUsername(User newUser) {
        User tempUser = userRepository.findOneByUsernameIgnoreCase(newUser.getUsername());
        String username = newUser.getUsername();
        int initialCounter = 1;
        int appendedNumber = 1; // if IndexOutOfBounds, append number.

        while(tempUser != null) {
            if(tempUser.getOnlineId().equals(newUser.getOnlineId())) {
                return username;
            }

            try {
                String firstInitial = newUser.getFirstName().toLowerCase().substring(0, initialCounter);
                username = firstInitial + newUser.getLastName().toLowerCase();
            } catch (StringIndexOutOfBoundsException e) {
                username = newUser.getFirstName().toLowerCase() + newUser.getLastName().toLowerCase() + appendedNumber;
                appendedNumber++;
            }

            initialCounter++;
            tempUser = userRepository.findOneByUsernameIgnoreCase(username);
        }

        return username;
    }
}
