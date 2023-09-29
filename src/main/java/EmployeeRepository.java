import java.util.List;

public interface EmployeeRepository {

    void save(EmployeeSync.Employee employee);

    void saveAll(List<EmployeeSync.Employee> employees);

    void update(EmployeeSync.Employee employee);

    EmployeeSync.Employee findById(Long id);
}
