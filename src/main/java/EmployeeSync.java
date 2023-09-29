import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class EmployeeSync {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private RemoteEmployeeSource remoteEmployeeSource;

    @Autowired
    private RemoteSalaryService remoteSalaryService;

    public void syncEmployees() {
        List<String> remoteEmployees = remoteEmployeeSource.getAllEmployees();
        List<Employee> resultEmployees = new ArrayList<>();
        for (String remoteEmployee : remoteEmployees) {
            String s = remoteEmployee.replaceAll("\\{", "").replaceAll("}", "");
            List<String> employeeFields = Arrays.asList(s.split(","));
            Employee employee = new Employee();
            for (String f : employeeFields) {
                String[] pair = f.split(":");
                if (pair[0].trim().equals("id")) {
                    employee.setId(Long.parseLong(pair[1]));
                }
                if (pair[0].trim().equals("fullName")) {
                    employee.setFullName(pair[1]);
                }
                if (pair[0].trim().equals("email")) {
                    employee.setEmail(pair[1]);
                }
                if (pair[0].trim().equals("status")) {
                    employee.setStatus(pair[1]);
                }
                if (pair[0].trim().equals("salary")) {
                    employee.setSalary(Float.parseFloat(pair[1]));
                }
            }
            resultEmployees.add(employee);
        }
        updateAndSave(resultEmployees);
    }

    @Transactional
    private void updateAndSave(List<Employee> resultEmployees) {
        for (Employee employee : resultEmployees) {
            if (employee.getSalary() == null) {
                employee.setSalary(remoteSalaryService.getSalaryForStatus(employee.getStatus()));
            }
        }
        repository.saveAll(resultEmployees);
    }

    @Getter
    @Setter
    class Employee {

        @Id
        private Long id;

        @Column(name = "full_name")
        private String fullName;

        @Column(name = "email")
        private String email;

        @Column(name = "status")
        private String status;

        @Column(name = "salary")
        private Float salary;
    }
}
