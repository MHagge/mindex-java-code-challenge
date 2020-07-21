package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    private int getNumOfReports(Employee employee) {
        LOG.debug("Retriving number of reports for employee [{}]", employee.getEmployeeId());

        // Get a list of who reports to this employee
        List<Employee> reports = employee.getDirectReports(); // Note: Despite this being a List of Employees, the only info in this is employeeIds. Not the full employee data.
        
        int numReports = (reports != null) ? reports.size() : 0;
        int totalReports = numReports;

        for (int i = 0; i < numReports; i++){
            //Get the full employee info by id
            Employee emp = employeeRepository.findByEmployeeId(reports.get(i).getEmployeeId());
            totalReports += getNumOfReports(emp);
        }

        return totalReports;
    }

    @Override
    public ReportingStructure getStructure(String id) {
        LOG.debug("Retrieve ReportingStructure object for employee [{}]", id);
        
        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        ReportingStructure reportingStructure = new ReportingStructure();

        reportingStructure.setEmployeeId(id); 
        reportingStructure.setNumberOfReports(getNumOfReports(employee));

        return reportingStructure;
    }
}
