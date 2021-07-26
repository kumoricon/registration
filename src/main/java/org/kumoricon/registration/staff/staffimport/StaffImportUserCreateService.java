package org.kumoricon.registration.staff.staffimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.kumoricon.registration.model.role.RoleRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.kumoricon.registration.model.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Component
public class StaffImportUserCreateService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final Map<String, List<String>> regRoleMapping;

    public StaffImportUserCreateService(RoleRepository roleRepository,
                                        UserRepository userRepository,
                                        UserService userService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.regRoleMapping = mapRegRoles();
    }

    private Map<String, List<String>> mapRegRoles() {
        Map<String, List<String>> roleMap = Map.of(
                "Administrator", Arrays.asList("Registration Software Development Coordinator", "Registration Software Development Manager",
                                                    "Registration Software Development Staff"),
                "Coordinator", Arrays.asList("Attendee Registration Coordinator", "Attendee Registration Coordinator (in Training)",
                                                "Staff Registration Check-In Coordinator"),
                "Coordinator - Specialty Badges", Arrays.asList("Specialty Registration Coordinator"),
                "Coordinator - VIP Badges", Arrays.asList("Specialty and VIP Registration Staff", "VIP and Accessibility Registration Coordinator"),
                "Director", Arrays.asList("Assistant Director of Membership", "Director of Membership"),
                "Manager", Arrays.asList("Attendee Registration Assistant Manager", "Attendee Registration Coordinator Lead",
                                            "Attendee Registration Manager", "Specialty Registration Manager",
                                            "Staff Registration Check-In Assistant Manager", "Staff Registration Check-In Manager"),
                "MSO", Arrays.asList("Staff Registration Check-In Staff"),
                "Staff", Arrays.asList("Attendee Registration Staff")
        );

        return roleMap;
    }

    // if getting by online id is null, create unique username
    public void createUserFromStaff(StaffImportFile.Person person){
        Integer roleId = getStaffRoleId(person.getPositions());

        if(roleId == -1)
            return;

        User newUser = createNewUser(person, roleId);
        newUser.setOnlineId(person.getId());

        if(userRepository.findOneByOnlineId(newUser.getOnlineId()) != null) {
            return;
        }

        newUser.setUsername(createUniqueUsername(newUser));
        saveUser(newUser);
    }

    private User createNewUser(StaffImportFile.Person person, Integer roleId) {
        User newUser = userService.newUser(person.getNamePreferredFirst(), person.getNamePreferredLast());
        newUser.setRoleId(roleId);

        return newUser;
    }

    /**
     * Checks if position title of Person is present in regRoleMapping
     * If present, returns roleId associated with the role title
     * @return roleId or -1 for invalid role
     */
    private Integer getStaffRoleId(List<StaffImportFile.Position> positions) {
        for(StaffImportFile.Position p : positions) {
            if(p.department.equals("Membership")){ // roles present in regRoleMapping are all from Membership department
                for(String key : this.regRoleMapping.keySet()) {
                    if(this.regRoleMapping.get(key).contains(p.title))
                        return roleRepository.findByNameIgnoreCase(key).getId();
                }
            }
        }

        return -1;
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
