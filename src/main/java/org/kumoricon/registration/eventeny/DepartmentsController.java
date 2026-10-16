package org.kumoricon.registration.eventeny;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kumoricon.registration.model.staff.PositionRepository;
import org.kumoricon.registration.model.staff.Staff;
import org.kumoricon.registration.model.staff.StaffRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DepartmentsController {
    private static final Logger log = LogManager.getLogger();
    private final PositionRepository positionRepository;
    private final StaffRepository staffRepository;

    public DepartmentsController(PositionRepository positionRepository, StaffRepository staffRepository) {
        this.positionRepository = positionRepository;
        this.staffRepository = staffRepository;
    }

    @RequestMapping(value = "/eventeny/departments")
    @PreAuthorize("hasAuthority('manage_users')")
    public String eventenyDepartments(Model model) {
        final List<String> departments = positionRepository.findDistinctDepartments();
        departments.sort(String::compareTo);
        model.addAttribute("departments", departments);
        model.addAttribute("departmentStatusMap", mapDepartmentToStatus(departments));

        return "eventeny/departments";
    }

    @RequestMapping(value = "/eventeny/department/{department}")
    @PreAuthorize("hasAuthority('manage_users')")
    public String eventenyDepartmentsStaff(Model model, @PathVariable String department) {
        final List<Staff> staff = staffRepository.findAllByDepartment(department);

        sortStaffList(staff);
        model.addAttribute("department", department);
        model.addAttribute("staffList", staff);

        return "eventeny/department/department";
    }

    private void sortStaffList(final List<Staff> staff) {
        staff.sort((s1, s2) -> s2.getId().compareTo(s1.getId()));
    }

    @RequestMapping(value = "/eventeny/department/{department}/{uuid}")
    @PreAuthorize("hasAuthority('manage_users')")
    public String displayDepartmentStaff(Model model,
                                         @PathVariable String department,
                                         @PathVariable String uuid) {
        final List<Staff> staffList = staffRepository.findAllByDepartment(department);
        final Staff staff = staffRepository.findByUuid(uuid);

        sortStaffList(staffList);
        model.addAttribute("staffList", staffList);
        model.addAttribute("staff", staff);

        return "eventeny/department/staff-id";
    }

    @RequestMapping(value = "/eventeny/department/{department}/{uuid}", method=RequestMethod.POST)
    @PreAuthorize("hasAuthority('manage_users')")
    public String markStaffCompleted(Model model,
                                         @PathVariable String department,
                                         @PathVariable String uuid) {
        final List<Staff> staffList = staffRepository.findAllByDepartment(department);
        final Staff staff = staffRepository.findByUuid(uuid);
        staff.setEventenyCheckedIn(true);
        staffRepository.save(staff);

        sortStaffList(staffList);
        model.addAttribute("staffList", staffList);
        model.addAttribute("staff", staff);
        return String.format("redirect:/eventeny/department/%s/%s", department, uuid);
    }

    private Map<String, Boolean> mapDepartmentToStatus(final List<String> departments) {
        final Map<String, Boolean> departmentStatusMap = new HashMap<>();
        departments.forEach(department -> {
            final List<Staff> deptStaff = staffRepository.findAllByDepartment(department.toLowerCase());
            boolean isComplete = true;

            for (final Staff staff : deptStaff)
                if (staff.eventenyCheckedIn() == false) {
                    isComplete = false;
                    break;
                }
            departmentStatusMap.put(department, isComplete);
        });

        return departmentStatusMap;
    }
}
