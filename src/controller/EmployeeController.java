package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import model.DatabaseConnection;
import model.EmployeeService;
import view.EmployeeView;

public class EmployeeController {
    private final EmployeeService employeeService = new EmployeeService();
    private final EmployeeView employeeView = new EmployeeView();
    private static final Scanner scanner = new Scanner(System.in);

    private final ViewController viewController = new ViewController();


    public void run() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        assert conn != null;
        Statement stmt = conn.createStatement();

        List<String> selectedAttributes = employeeView.selectAttributes();

        while (true) {
            employeeView.showMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> employeeService.printAllEmployees(stmt, selectedAttributes);
                    case 2 -> employeeService.printEmployeeWithCondition(stmt, selectedAttributes);
                    case 3 -> employeeService.deleteEmployeeWithCondition(stmt, selectedAttributes);
                    case 4 -> employeeService.deleteEmployeeBySsn(stmt, selectedAttributes);
                    case 5 -> employeeService.addNewEmployee(stmt, selectedAttributes);
                    case 6 -> {
                        String groupCriteria = employeeView.selectGroupCriteria();
                        employeeService.printAverageSalaryByGroup(stmt, groupCriteria);
                    }
                    case 7 -> viewController.manageView();
                    case 8 -> {
                        System.out.println("상사를 조회할 직원의 Ssn을 입력하세요: ");
                        String employeeSsn = scanner.nextLine().trim();
                        viewController.findSupervisorBySsn(conn, employeeSsn);
                    }
                    case 9 -> viewController.calculateProjectCost();
                    case 10 -> employeeService.raiseEmployeeSalary();
                    case 11 -> employeeService.addDependent();
                    case 12 -> employeeService.addProject();
                    case 0 -> {
                        employeeView.showExitMessage();
                        return;
                    }
                    default -> System.out.println("유효하지 않은 번호입니다. 다시 입력하세요.");
                }
            } catch (InputMismatchException e) {
                System.out.println("잘못된 입력입니다. 숫자를 입력하세요.");
                scanner.nextLine();
            }
        }
    }
}