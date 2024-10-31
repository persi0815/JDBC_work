import java.sql.SQLException;
import controller.EmployeeController;

public class Main {
    public static void main(String[] args) throws SQLException {
        EmployeeController employeeController = new EmployeeController();
        employeeController.run();
    }
}
