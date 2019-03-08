package org.kumoricon.registration;

import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.model.badge.BadgeFactory;
import org.kumoricon.registration.model.badge.BadgeRepository;
import org.kumoricon.registration.model.badge.BadgeType;
import org.kumoricon.registration.model.role.Right;
import org.kumoricon.registration.model.role.RightRepository;
import org.kumoricon.registration.model.role.Role;
import org.kumoricon.registration.model.role.RoleRepository;
import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.kumoricon.registration.model.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;


@Service
public class BaseDataService {
    private final RoleRepository roleRepository;
    private final RightRepository rightRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BadgeRepository badgeRepository;
    private static final Logger log = LoggerFactory.getLogger(BaseDataService.class);

    @Value("${kumoreg.trainingMode}")
    private boolean trainingMode;


    public BaseDataService(RoleRepository roleRepository,
                           RightRepository rightRepository,
                           UserRepository userRepository,
                           UserService userService,
                           BadgeRepository badgeRepository) {
        this.roleRepository = roleRepository;
        this.rightRepository = rightRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.badgeRepository = badgeRepository;
    }

    void createDefaultData() {
        if (tablesAreEmpty()) {
            createRights();
            createRoles();
            createAdminUser();
            if (trainingMode) {
                createTrainingUsers();
            }
            createFullConAttendeeBadges();
            createSpecialtyBadges();
        }
    }

    private boolean tablesAreEmpty() {
        if (rightRepository.count() > 0) {
            log.info("Rights table not empty, skipping default data creation");
            return false;
        }
        if (roleRepository.count() > 0) {
            log.info("Roles table not empty, skipping default data creation");
            return false;
        }
        if (userRepository.count() > 0) {
            log.info("Users table not empty, skipping default data creation");
            return false;
        }
        if (badgeRepository.count() > 0) {
            log.error("badges table is not empty");
            return false;
        }

        return true;
    }

    private void createAdminUser() {
        log.info("No users found. Creating default user 'admin' with password 'password'");
        User defaultUser = userService.newUser("Admin", "User");
        Role adminRole = roleRepository.findByNameIgnoreCase("Administrator");
        defaultUser.setUsername("admin");
        defaultUser.setRoleId(adminRole.getId());
        userRepository.save(defaultUser);
    }

    private void createTrainingUsers() {
        StringJoiner createdUsers = new StringJoiner(", ");
        String[][] users = {
                {"Staff", "Staff"},
                {"Coordinator", "Coordinator"},
                {"Manager", "Manager"},
                {"Director", "Director"},
                {"Ops", "Ops"},
                {"MSO", "MSO"}
        };

        for (String[] userData : users) {
            createdUsers.add(userData[0]);
            User user = userService.newUser(userData[0], "User");
            user.setUsername(userData[0]);
            Role role = roleRepository.findByNameIgnoreCase(userData[1]);
            if (role == null) log.error("Couldn't find role {} when creating user {}", userData[1], userData[0]);
            user.setRoleId(role.getId());
            userRepository.save(user);
        }
        log.info("Created training users {}", createdUsers);
    }



    private void createRights() {
        String[][] rights = {
                {"at_con_registration", "Add new attendees via At-Con Registration and close till"},
                {"at_con_registration_set_fan_name", "Set fan name during at-con check in"},
                {"at_con_registration_blacklist", "Allow at-con registration for names on the blacklist"},
                {"pre_reg_check_in", "Check in preregistered attendees"},
                {"pre_reg_check_in_edit", "Edit preregistered attendee information during check in"},
                {"attendee_search", "Search for and view attendees"},
                {"attendee_edit", "Edit attendees from mySearch results"},
                {"attendee_add_note", "Edit notes field on attendees, but no other fields"},
                {"attendee_edit_with_override", "Edit attendee if a user with attendee_edit right approves it"},
                {"attendee_override_price", "Manually set price for attendee"},
                {"print_badge", "Print badge on attendee check in"},
                {"reprint_badge", "Reprint attendee badges after attendee is checked in"},
                {"reprint_badge_with_override", "Reprint badge if a user with reprint_badge right approves it"},
                {"badge_type_weekend", "Select/check in the \"Weekend\" badge type"},
                {"badge_type_day", "Select/check in the individual day badge types"},
                {"badge_type_emerging_press", "Select/check in the \"Emerging Press\" badge type"},
                {"badge_type_standard_press", "Select/check in the \"Standard Press\" badge type"},
                {"badge_type_small_press", "Select/check in the \"Small Press\" badge type"},
                {"badge_type_vip", "Select/check in the \"VIP\" badge type"},
                {"badge_type_artist", "Select/check in the \"Artist\" badge type"},
                {"badge_type_exhibitor", "Select/check in the \"Exhibitor\" badge type"},
                {"badge_type_guest", "Select/check in the \"Guest\" badge type"},
                {"badge_type_industry", "Select/check in the \"Industry\" badge type"},
                {"badge_type_panelist", "Select/check in the \"Panelist\" badge type"},
                {"badge_type_staff", "Select/check in the \"Staff\" badge type"},
                {"view_attendance_report", "View attendance report (counts only)"},
                {"view_attendance_report_revenue", "View attendance report (with revenue totals)"},
                {"view_check_in_by_hour_report", "View attendee check ins per hour report"},
                {"view_check_in_by_user_report", "View attendee check ins per user report"},
                {"view_staff_report", "View staff report (lists name/phone numbers)"},
                {"view_role_report", "View registration system role report"},
                {"view_till_report", "View till report"},
                {"view_export", "Export information/reports"},
                {"view_active_sessions", "View currently logged in user sessions"},
                {"menu_registration", "Can see the Registration Menu"},
                {"menu_administration", "Can see the Administration Menu"},
                {"menu_report", "Can see the Reports Menu"},
                {"menu_utility", "Can see the Utility Menu"},
                {"manage_blacklist", "Add/edit blacklist entries"},
                {"manage_users", "Add/edit users and reset passwords"},
                {"manage_pass_types", "Add/edit badge types"},
                {"manage_roles", "Add/edit security roles"},
                {"manage_orders", "List/edit orders after they have been placed"},
                {"manage_devices", "Add/edit devices (computer/printer mappings)"},
                {"manage_till_sessions", "View/Close Till Sessions for other users"},
                {"import_pre_reg_data", "Import pre-registered attendees and orders"},
                {"load_base_data", "Load default data (users, roles, rights)"},
                {"pre_print_badges", "Pre-print badges for all attendees with a particular badge type"}
        };

        StringJoiner createdRights = new StringJoiner(", ");
        for (String[] rightInfo : rights) {
            createdRights.add(rightInfo[0]);
            Right right = new Right(rightInfo[0], rightInfo[1]);
            rightRepository.save(right);
        }
        log.info("Created rights {}", createdRights);
    }


    private void createRoles() {
        Map<String, String[]> roles = new LinkedHashMap<>();
        roles.put("Staff", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search", "print_badge",
                "badge_type_weekend", "badge_type_day", "attendee_add_note",
                "attendee_edit_with_override", "reprint_badge_with_override", "menu_registration", "menu_utility"
        });
        roles.put("Coordinator", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "badge_type_weekend", "badge_type_day", "print_badge", "attendee_edit",
                "attendee_add_note", "reprint_badge", "view_staff_report",
                "view_check_in_by_hour_report", "pre_reg_check_in_edit", "menu_registration", "menu_utility",
                "menu_report"});
        roles.put("Coordinator - VIP Badges", new String[] {"at_con_registration", "pre_reg_check_in",
                "attendee_search", "print_badge", "attendee_edit",
                "attendee_add_note", "reprint_badge", "view_staff_report",
                "view_check_in_by_hour_report", "badge_type_vip", "menu_registration", "menu_utility", "menu_report",
                "pre_reg_check_in_edit"});
        roles.put("Coordinator - Specialty Badges", new String[] {"at_con_registration", "pre_reg_check_in",
                "attendee_search", "print_badge", "attendee_edit",
                "attendee_add_note", "reprint_badge", "view_staff_report",
                "view_check_in_by_hour_report", "badge_type_artist",
                "badge_type_standard_press", "badge_type_emerging_press",
                "badge_type_exhibitor", "badge_type_guest",
                "badge_type_panelist", "badge_type_industry",
                "badge_type_small_press", "menu_registration", "menu_utility", "menu_report",
                "pre_reg_check_in_edit", "at_con_registration_set_fan_name"});
        roles.put("MSO", new String[] {"pre_reg_check_in",
                "attendee_search", "print_badge", "attendee_edit",
                "attendee_add_note", "reprint_badge", "view_staff_report",
                "view_check_in_by_hour_report", "badge_type_staff", "badge_type_panelist",
                "pre_reg_check_in_edit", "menu_registration", "menu_utility", "menu_report"});
        roles.put("Manager", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_add_note", "at_con_registration_blacklist",
                "badge_type_weekend", "badge_type_day",
                "badge_type_vip", "badge_type_emerging_press", "badge_type_standard_press", "badge_type_artist",
                "badge_type_exhibitor", "badge_type_guest", "badge_type_industry", "badge_type_panelist", "badge_type_small_press",
                "badge_type_staff", "attendee_override_price", "reprint_badge", "manage_users", "view_staff_report",
                "view_attendance_report", "view_check_in_by_hour_report", "view_till_report", "view_export",
                "view_check_in_by_user_report", "pre_reg_check_in_edit", "manage_orders", "manage_till_sessions",
                "at_con_registration_set_fan_name", "menu_registration", "menu_utility", "menu_report", "menu_administration"});
        roles.put("Director", new String[] {"at_con_registration", "pre_reg_check_in", "attendee_search",
                "print_badge", "attendee_edit", "attendee_add_note", "at_con_registration_blacklist",
                "attendee_override_price", "reprint_badge", "manage_users", "manage_pass_types",
                "badge_type_weekend", "badge_type_day",
                "badge_type_vip", "badge_type_emerging_press", "badge_type_standard_press", "badge_type_artist",
                "badge_type_exhibitor", "badge_type_guest", "badge_type_industry", "badge_type_panelist",
                "badge_type_small_press",
                "badge_type_staff", "view_role_report", "view_attendance_report", "view_attendance_report_revenue",
                "view_staff_report", "view_check_in_by_hour_report", "view_till_report", "pre_reg_check_in_edit",
                "view_check_in_by_user_report", "view_export", "manage_orders", "manage_till_sessions",
                "pre_print_badges", "at_con_registration_set_fan_name", "menu_registration", "menu_utility", "menu_report",
                "menu_administration", "manage_blacklist"});
        roles.put("Ops", new String[] {"attendee_search", "attendee_add_note", "menu_registration"});

        HashMap<String, Right> rightMap = getRightsHashMap();

        StringJoiner createdRoles = new StringJoiner(", ");
        for (String roleName : roles.keySet()) {
            createdRoles.add(roleName);
            Role role = new Role(roleName);
            for (String rightName : roles.get(roleName)) {
                if (rightMap.containsKey(rightName)) {
                    role.addRight(rightMap.get(rightName));
                } else {
                    log.error("Error creating role {}: Right {} not found", roleName, rightName);
                }
            }
            roleRepository.save(role);
        }

        Role admin = new Role("Administrator");
        admin.addRights(rightRepository.findAll());
        createdRoles.add("Administrator");
        roleRepository.save(admin);
        log.info("Created roles {}", createdRoles);
    }

    private void createFullConAttendeeBadges() {
        log.info("Creating badge Weekend");
        Badge weekend = BadgeFactory.createBadge("Weekend", BadgeType.ATTENDEE, "Weekend", "#000000", 65, 65, 45);
        weekend.setRequiredRight("badge_type_weekend");
        weekend.setWarningMessage("Attendee check in. See your coordinator!");
        badgeRepository.save(weekend);

        String[][] badgeList = {
                {"Friday", "#81f983", "50", "50", "30"},
                {"Saturday", "#c897fc", "50", "50", "30"},
                {"Sunday", "#fcc697", "45", "45", "20"}};
        for (String[] currentBadge : badgeList) {
            log.info("Creating badge {}", currentBadge[0]);
            Badge badge = BadgeFactory.createBadge(currentBadge[0], BadgeType.ATTENDEE,
                    currentBadge[0],
                    currentBadge[1],
                    Float.parseFloat(currentBadge[2]),
                    Float.parseFloat(currentBadge[3]),
                    Float.parseFloat(currentBadge[4]));
            badge.setRequiredRight("badge_type_day");
            badge.setWarningMessage("Attendee check in. See your coordinator!");
            badgeRepository.save(badge);
        }

        // Create badge types with security restrictions below
        log.info("Creating badge VIP");
        Badge vip = BadgeFactory.createBadge("VIP", BadgeType.ATTENDEE, "VIP", "#000000", 300, 300, 300);
        vip.setRequiredRight("badge_type_vip");
        vip.setWarningMessage("VIP check in. See your coordinator!");
        vip.setBadgeTypeText("VIP");
        badgeRepository.save(vip);
    }

    private void createSpecialtyBadges() {
        log.info("Creating badge Artist");
        Badge artist = BadgeFactory.createBadge("Artist", BadgeType.OTHER, "Artist", "#800080", 75f, 75f, 75f);
        artist.setRequiredRight("badge_type_artist");
        artist.setWarningMessage("Artist check in. See your coordinator!");
        badgeRepository.save(artist);

        log.info("Creating badge Exhibitor");
        Badge exhibitor = BadgeFactory.createBadge("Exhibitor", BadgeType.OTHER, "Exhibitor", "#00597c", 250f, 250f, 250f);
        exhibitor.setRequiredRight("badge_type_exhibitor");
        exhibitor.setWarningMessage("Exhibitor check in. See your coordinator!");
        badgeRepository.save(exhibitor);

        log.info("Creating badge Guest");
        Badge guest = BadgeFactory.createBadge("Guest", BadgeType.OTHER,"Guest", "#62F442", 0f, 0f, 0f);
        guest.setRequiredRight("badge_type_guest");
        guest.setWarningMessage("Guest check in. See your coordinator!");
        badgeRepository.save(guest);

        log.info("Creating badge Small Press");
        Badge smallPress = BadgeFactory.createBadge("Small Press", BadgeType.OTHER,"Small Press", "#007c5f", 0f, 0f, 0f);
        smallPress.setRequiredRight("badge_type_small_press");
        smallPress.setWarningMessage("Press check in. See your coordinator!");
        badgeRepository.save(smallPress);

        log.info("Creating badge Emerging Press");
        Badge ePress = BadgeFactory.createBadge("Emerging Press", BadgeType.OTHER,"E Press", "#1DE5D1", 0f, 0f, 0f);
        ePress.setRequiredRight("badge_type_emerging_press");
        ePress.setWarningMessage("Press check in. See your coordinator!");
        badgeRepository.save(ePress);

        log.info("Creating badge Standard Press");
        Badge sPress = BadgeFactory.createBadge("Standard Press", BadgeType.OTHER,"S Press", "#16b7a7", 0f, 0f, 0f);
        sPress.setRequiredRight("badge_type_standard_press");
        sPress.setWarningMessage("Press check in. See your coordinator!");
        badgeRepository.save(sPress);

        log.info("Creating badge Industry");
        Badge industry = BadgeFactory.createBadge("Industry", BadgeType.OTHER,"Industry", "#FF00FC", 0f, 0f, 0f);
        industry.setRequiredRight("badge_type_industry");
        industry.setWarningMessage("Industry check in. See your coordinator!");
        badgeRepository.save(industry);

        log.info("Creating badge Panelist");
        Badge panelist = BadgeFactory.createBadge("Panelist", BadgeType.OTHER,"Panelist", "#FFA500", 0f, 0f, 0f);
        panelist.setRequiredRight("badge_type_panelist");
        panelist.setWarningMessage("Panelist check in. See your coordinator!");
        badgeRepository.save(panelist);
    }


    @SuppressWarnings("unused")
    private void addLiteAttendeeBadges() {
        log.info("Creating badge Kumoricon Lite");
        Badge lite = BadgeFactory.createBadge("Kumoricon Lite", BadgeType.ATTENDEE, "Saturday", "#323E99", 15, 15, 15);
        badgeRepository.save(lite);
    }


    private HashMap<String, Right> getRightsHashMap() {
        HashMap<String, Right> rightHashMap = new HashMap<>();
        for (Right r : rightRepository.findAll()) {
            rightHashMap.put(r.getName(), r);
        }
        return rightHashMap;
    }


}
