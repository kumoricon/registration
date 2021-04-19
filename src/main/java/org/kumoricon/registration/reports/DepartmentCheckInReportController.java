package org.kumoricon.registration.reports;

import org.kumoricon.registration.model.staff.StaffReportRepository;
import org.kumoricon.registration.reports.attendance.ChartDataDTO;
import org.kumoricon.registration.reports.attendance.ChartDataSetDTO;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@Controller
public class DepartmentCheckInReportController {
    private final StaffReportRepository staffReportRepository;

    public DepartmentCheckInReportController(StaffReportRepository staffReportRepository) {
        this.staffReportRepository = staffReportRepository;
    }

    @RequestMapping(value = "/reports/checkinbydepartment")
    @PreAuthorize("hasAuthority('view_check_in_by_department_report')")
    public String report() {
        return "reports/checkinbydepartment";
    }

    @RequestMapping(value = "/reports/checkinbydepartment/data.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('view_check_in_by_department_report')")
    @ResponseBody
    public ChartDataDTO data() {
        List<StaffReportRepository.DepartmentReportDTO> departments = staffReportRepository.findAll();

        List<String> departmentNames = new ArrayList<>();
        List<Integer> checkedIn = new ArrayList<>();
        List<Integer> notCheckedIn = new ArrayList<>();
        for (StaffReportRepository.DepartmentReportDTO d : departments) {
            departmentNames.add(d.getName());
            checkedIn.add(d.getCheckedIn());
            notCheckedIn.add(d.getNotCheckedIn());
        }

        ChartDataDTO chartDataDTO = new ChartDataDTO(departmentNames);
        chartDataDTO.addData(new ChartDataSetDTO("Checked In", "#9999FF", "#000000", 1.0F, checkedIn));
        chartDataDTO.addData(new ChartDataSetDTO("Not Checked In", "#776666", "#000000", 1.0F, notCheckedIn));
        return chartDataDTO;
    }

}
