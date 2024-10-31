package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;
import model.DatabaseConnection;
import model.EmployeeService;
import view.EmployeeView;

public class EmployeeController {
    private final EmployeeService employeeService = new EmployeeService();
    private final EmployeeView employeeView = new EmployeeView();
    private static final Scanner scanner = new Scanner(System.in);

    public void run() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();

        while (true) {
            employeeView.showMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> employeeService.printAllEmployees(stmt);
                    case 2 -> employeeService.printEmployeeWithCondition(stmt);
                    case 3 -> employeeService.deleteEmployeeWithCondition(stmt);
                    case 4 -> employeeService.deleteEmployeeBySsn(stmt);
                    case 5 -> employeeService.addNewEmployee(stmt);
                    case 0 -> employeeView.showExitMessage();
                    default -> {
                        System.out.println("유효하지 않은 번호입니다. 다시 입력하세요.");
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("잘못된 입력입니다. 숫자를 입력하세요.");
                scanner.nextLine();
            }
        }
    }
}
