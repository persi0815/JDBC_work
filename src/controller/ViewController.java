package controller;

import model.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;

import static java.io.File.separator;

public class ViewController {

    private final Scanner scanner = new Scanner(System.in);

    // @7번@ 뷰 관리 메뉴
    public void manageView() {
        while (true) {
            System.out.println("===== 뷰 작업을 선택하세요 =====");
            System.out.println("1. 뷰 목록");
            System.out.println("2. 뷰 생성");
            System.out.println("3. 뷰 삭제");
            System.out.println("4. 뷰 보기");
            System.out.println("0. 이전 메뉴로 돌아가기");
            System.out.print("입력 : ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> listViews();
                case 2 -> makeView();
                case 3 -> deleteView();
                case 4 -> viewView();
                case 0 -> {
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return;
                }
                default -> System.out.println("유효하지 않은 선택입니다. 다시 입력하세요");
            }
        }
    }

    // 1. 뷰 목록 출력
    private void listViews() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            String viewQuery = "SHOW FULL TABLES WHERE Table_type = 'VIEW'";
            ResultSet resultSet = statement.executeQuery(viewQuery);

            System.out.println("===== 뷰 목록 =====");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
        } catch (Exception error) {
            System.out.println("뷰 목록을 가져오는 중 오류가 발생했습니다: " + error.getMessage());
        }
    }

    // 2. 뷰 생성
    public void makeView() {
        try (Connection connection = DatabaseConnection. getConnection();
             Statement statement = connection.createStatement()) {

            System.out.println("===== 생성할 뷰의 이름을 입력하세요 =====");
            String viewName = scanner.nextLine().trim();
            System.out.println("첫 번째 테이블 이름을 입력하세요");
            String table1 = scanner.nextLine().trim();
            System.out.println("두 번째 테이블 이름을 입력하세요");
            String table2 = scanner.nextLine().trim();

            System.out.println("JOIN Condition을 입력하세요");
            String joinCond = scanner.nextLine().trim();


            // 컬럼 목록 가져오기
            String columns1 = fetchColumnNames(connection, table1);
            String columns2 = fetchColumnNames(connection, table2);

            // 중복 컬럼 있을 경우 ex) employee_dependent의 Sex
            StringBuilder selectColumns = new StringBuilder();
            for (String column : columns1.split(",")) {
                selectColumns.append(String.format("%s.%s AS %s_%s,", table1, column.trim(), column.trim(), table1));
            }
            for (String column : columns2.split(",")) {
                selectColumns.append(String.format("%s.%s AS %s_%s,", table2, column.trim(), column.trim(), table2));
            }
            if (selectColumns.length() > 0) {
                selectColumns.setLength(selectColumns.length() - 1);
            }

            // 테이블 join
            String makeQuery = String.format(
                    "CREATE VIEW %s AS SELECT %s FROM %s INNER JOIN %s ON %s",
                    viewName, selectColumns, table1, table2, joinCond
            );

            statement.executeUpdate(makeQuery);
            System.out.printf("뷰 %s가 생성되었습니다!\n", viewName);
        } catch (Exception error) {
            System.out.println("뷰 생성 중 오류가 발생했습니다: " + error.getMessage());

        }
    }



    // 특정 테이블의 컬럼 이름을 가져오기
    private String fetchColumnNames(Connection conn, String table) {
        StringBuilder columns = new StringBuilder();
        try (Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery("SHOW COLUMNS FROM " + table)) {
            while (resultSet.next()) {
                columns.append(resultSet.getString(1)).append(",");
            }
        } catch (Exception error) {
            System.out.println("컬럼 이름을 가져오는 중 오류가 발생했습니다: " + error.getMessage());
        }
        // 마지막 쉼표 삭제
        if (columns.length() > 0) {
            columns.setLength(columns.length() - 1);
        }
        return columns.toString();
    }

    // 3. 뷰 삭제
    private void deleteView() {
        try (Connection connection = DatabaseConnection. getConnection();
             Statement statement = connection.createStatement()) {

            System.out.println("===== 삭제할 뷰의 이름을 입력하세요 =====");
            String viewName = scanner.nextLine().trim();

            // 삭제할 뷰가 존재하는지 확인
            String checkQuery = String.format("SHOW FULL TABLES LIKE '%s'", viewName);
            ResultSet resultSet = statement.executeQuery(checkQuery);

            if(!resultSet.next()) {
                System.out.println("뷰 " + viewName + "가 존재하지 않습니다.");
                return;
            }

            String deleteQuery = String.format("DROP VIEW IF EXISTS %s", viewName);
            statement.executeUpdate(deleteQuery);
            System.out.printf("뷰 %s가 삭제되었습니다.\n", viewName);
        } catch (Exception error) {
            System.out.println("뷰 삭제 중 오류가 발생했습니다: " + error.getMessage());
        }
    }

    // 4. 뷰 보기
    private void viewView() {
        try (Connection connection = DatabaseConnection. getConnection();
             Statement statement = connection.createStatement()) {

            System.out.println("조회할 뷰 이름을 입력하세요: ");
            String viewName = scanner.nextLine().trim();

            String selectQuery = String.format("SELECT * FROM %s", viewName);
            ResultSet resultSet = statement.executeQuery(selectQuery);

            System.out.printf("===== 뷰 %s의 내용 =====\n", viewName);
            int count = resultSet.getMetaData().getColumnCount();

            StringBuilder header = new StringBuilder();
            StringBuilder seperator = new StringBuilder();

            for (int i = 1; i <= count; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                header.append(String.format("%-25s", columnName));
                seperator.append("-------------------------");
            }

            System.out.println(header.toString());
            System.out.println(separator.toString());

            // 간격둬서 출력
            while (resultSet.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= count; i++) {
                    row.append(String.format("%-25s", resultSet.getString(i)));
                }
                System.out.println(row.toString());
            }
        } catch (Exception error) {
            System.out.println("뷰 조회 중 오류가 발생했습니다: " + error.getMessage());
        }
    }





    // @8번@ 직원의 Ssn으로 상사 직원 검색 (recursive query)
    public void findSupervisorBySsn(Connection conn, String employeeSsn) {

        String sql = "SELECT Super_ssn FROM EMPLOYEE WHERE Ssn = '" + employeeSsn + "'";

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            if (rs.next()) {
                String managerSsn = rs.getString("Super_ssn");

                // 상사가 없는 경우
                if (managerSsn == null || managerSsn.isBlank()) {
                    System.out.println("이 직원은 상사가 없습니다.");
                } else {
                    boolean hasPrintedSupervisor = false; // 상사 정보가 출력되었는지 여부를 추적

                    while (managerSsn != null && !managerSsn.isBlank()) {
                        hasPrintedSupervisor = true; // 상사 정보가 출력됨
                        printSupervisor(conn, managerSsn);

                        // 다음 상사 조회
                        sql = "SELECT Super_ssn FROM EMPLOYEE WHERE Ssn = '" + managerSsn + "'";
                        try (Statement innerStatement = conn.createStatement();
                             ResultSet innerRs = innerStatement.executeQuery(sql)) {
                            if (innerRs.next()) {
                                managerSsn = innerRs.getString("Super_ssn");
                            } else {
                                break;
                            }
                        }
                    }


                    if (!hasPrintedSupervisor) {
                        System.out.println("이 직원은 상사가 없습니다.");
                    }
                }
            } else {
                System.out.println("입력한 Ssn에 해당하는 직원을 찾을 수 없습니다.");
            }
        } catch (SQLException ex) {
            System.out.println("상사 조회 중 오류 발생: " + ex.getMessage());
        }
    }

    // 상사의 세부 정보를 출력하는
    private void printSupervisor(Connection conn, String supervisorSsn) {
        String sql = "SELECT Fname, Lname FROM EMPLOYEE WHERE Ssn = '" + supervisorSsn + "'";

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            if (rs.next()) {
                System.out.printf("상사: %s %s (Ssn: %s)\n", rs.getString("Fname"), rs.getString("Lname"), supervisorSsn);
            } else {
                System.out.println("상사 정보를 찾을 수 없습니다.");
            }
        } catch (SQLException ex) {
            System.out.println("상사 정보 조회 중 오류 발생: " + ex.getMessage());
        }
    }






    // @9번@ 프로젝트 별 투입된 인건비
    public void calculateProjectCost() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("인건비를 계산할 프로젝트 번호를 입력하세요 (쉼표로 구분): ");
            String input = scanner.nextLine().trim();
            String[] projectNumbers = input.split(",");

            for (String projectNum : projectNumbers) {
                projectNum = projectNum.trim();

                // 프로젝트 이름을 가져오는 쿼리
                String nameQuery = String.format("SELECT Pname FROM PROJECT WHERE Pnumber = %s", projectNum);
                ResultSet nameResult = stmt.executeQuery(nameQuery);

                String projName = "";
                if (nameResult.next()) {
                    projName = nameResult.getString("Pname");
                } else {
                    System.out.printf("프로젝트 번호 %s는 존재하지 않습니다.\n", projectNum);
                    continue;
                }

                // 프로젝트에 참여한 직원들의 Salary 합계 계산 (Hours가 0이거나 NULL인 경우 제외)
                String salaryQuery = String.format(
                        "SELECT SUM(E.Salary) AS TotalSalary FROM EMPLOYEE E " +
                                "JOIN WORKS_ON W ON E.Ssn = W.Essn " +
                                "WHERE W.Pno = %s AND W.Hours IS NOT NULL AND W.Hours > 0", projectNum
                );

                ResultSet salaryResult = stmt.executeQuery(salaryQuery);
                if (salaryResult.next()) {
                    double totalSalary = salaryResult.getDouble("TotalSalary");
                    System.out.printf("%s의 총 인건비는 %.2f$입니다.\n", projName, totalSalary);
                } else {
                    System.out.printf("%s에 참여한 직원이 없습니다.\n", projName);
                }
            }
        } catch (Exception e) {
            System.out.println("인건비 계산 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 13. 직원의 부서 이동 기능
    public void transferEmployeeDepartment() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkDepartmentStmt = connection.prepareStatement("SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?");
             PreparedStatement checkCurrentDepartmentStmt = connection.prepareStatement("SELECT Dno, Fname, Lname FROM EMPLOYEE WHERE Ssn = ?");
             PreparedStatement updateEmployeeStmt = connection.prepareStatement("UPDATE EMPLOYEE SET Dno = ? WHERE Ssn = ?")) {

            System.out.println("===== 부서를 이동할 직원의 SSN을 입력하세요 =====");
            String employeeSsn = scanner.nextLine().trim();
            System.out.println("===== 이동할 부서명을 입력하세요 =====");
            String newDepartment = scanner.nextLine().trim();

            // 현재 부서 확인 및 직원 이름 조회
            checkCurrentDepartmentStmt.setString(1, employeeSsn);
            ResultSet currentDeptResultSet = checkCurrentDepartmentStmt.executeQuery();

            if (currentDeptResultSet.next()) {
                int currentDepartmentNumber = currentDeptResultSet.getInt("Dno");
                String firstName = currentDeptResultSet.getString("Fname");
                String lastName = currentDeptResultSet.getString("Lname");

                // 부서 유효성 검사 및 Dnumber 조회
                checkDepartmentStmt.setString(1, newDepartment);
                ResultSet newDeptResultSet = checkDepartmentStmt.executeQuery();

                if (newDeptResultSet.next()) {
                    int newDepartmentNumber = newDeptResultSet.getInt("Dnumber");

                    // 현재 부서와 이동하려는 부서가 같은지 확인
                    if (currentDepartmentNumber == newDepartmentNumber) {
                        System.out.println("현재 속한 부서와 이동하려는 부서가 같습니다.");
                        return; // 메서드 종료
                    }

                    // 직원의 부서 정보 업데이트
                    updateEmployeeStmt.setInt(1, newDepartmentNumber);
                    updateEmployeeStmt.setString(2, employeeSsn);
                    int rowsAffected = updateEmployeeStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.printf("직원 %s(%s %s)의 부서가 %s로 변경되었습니다.\n", employeeSsn, firstName, lastName, newDepartment);
                    } else {
                        System.out.println("해당 직원이 존재하지 않습니다.");
                    }
                } else {
                    System.out.println("입력한 부서가 존재하지 않습니다.");
                }
            } else {
                System.out.println("해당 직원이 존재하지 않습니다.");
            }
        } catch (Exception error) {
            System.out.println("부서 이동 중 오류가 발생했습니다: " + error.getMessage());
        }
    }


    // 14. 부서별 최대/최소 급여 및 해당 직원 정보 조회
    public void maxMinSalaryByDepartment() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // 부서별 최대 및 최소 급여 조회
            String salaryQuery = "SELECT D.Dname AS Department, MAX(E.Salary) AS MaxSalary, MIN(E.Salary) AS MinSalary " +
                    "FROM EMPLOYEE E " +
                    "JOIN DEPARTMENT D ON E.Dno = D.Dnumber " +
                    "GROUP BY D.Dname";

            ResultSet salaryResultSet = statement.executeQuery(salaryQuery);

            System.out.println("===== 부서별 최대/최소 급여 및 해당 직원 정보 =====");

            while (salaryResultSet.next()) {
                String department = salaryResultSet.getString("Department");
                double maxSalary = salaryResultSet.getDouble("MaxSalary");
                double minSalary = salaryResultSet.getDouble("MinSalary");

                // 최대 급여를 가진 직원 정보 조회
                String maxSalaryQuery = "SELECT Fname, Lname, Ssn, Salary FROM EMPLOYEE WHERE Salary = ? AND Dno = " +
                        "(SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?)";
                try (PreparedStatement maxSalaryStmt = connection.prepareStatement(maxSalaryQuery)) {
                    maxSalaryStmt.setDouble(1, maxSalary);
                    maxSalaryStmt.setString(2, department);
                    ResultSet maxSalaryResultSet = maxSalaryStmt.executeQuery();

                    // 최대 급여 직원 정보 출력
                    if (maxSalaryResultSet.next()) {
                        String maxFname = maxSalaryResultSet.getString("Fname");
                        String maxLname = maxSalaryResultSet.getString("Lname");
                        String maxSsn = maxSalaryResultSet.getString("Ssn");
                        System.out.printf("부서: %s, 최대 급여: %.2f, 직원: %s %s (ssn: %s)\n", department, maxSalary, maxFname, maxLname, maxSsn);
                    }
                }

                // 최소 급여를 가진 직원 정보 조회
                String minSalaryQuery = "SELECT Fname, Lname, Ssn, Salary FROM EMPLOYEE WHERE Salary = ? AND Dno = " +
                        "(SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?)";
                try (PreparedStatement minSalaryStmt = connection.prepareStatement(minSalaryQuery)) {
                    minSalaryStmt.setDouble(1, minSalary);
                    minSalaryStmt.setString(2, department);
                    ResultSet minSalaryResultSet = minSalaryStmt.executeQuery();

                    // 최소 급여 직원 정보 출력
                    if (minSalaryResultSet.next()) {
                        String minFname = minSalaryResultSet.getString("Fname");
                        String minLname = minSalaryResultSet.getString("Lname");
                        String minSsn = minSalaryResultSet.getString("Ssn");
                        System.out.printf("부서: %s, 최소 급여: %.2f, 직원: %s %s (ssn: %s)\n", department, minSalary, minFname, minLname, minSsn);
                    }
                }
            }
        } catch (Exception error) {
            System.out.println("부서별 급여 조회 중 오류가 발생했습니다: " + error.getMessage());
        }
    }


    // 15. 부서 별 직원 목록 조회
    public void listEmployeesByDepartment() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===== 조회할 부서 이름을 입력하세요 =====");
        String departmentName = scanner.nextLine().trim(); // 부서 이름 입력받기

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT Dnumber FROM DEPARTMENT WHERE Dname = ?")) {

            statement.setString(1, departmentName);
            ResultSet departmentResultSet = statement.executeQuery();

            if (departmentResultSet.next()) {
                int departmentNumber = departmentResultSet.getInt("Dnumber");

                // 부서 번호로 직원 목록 조회
                try (PreparedStatement employeeStatement = connection.prepareStatement("SELECT Fname, Lname, Ssn, Salary FROM EMPLOYEE WHERE Dno = ?")) {
                    employeeStatement.setInt(1, departmentNumber);
                    ResultSet resultSet = employeeStatement.executeQuery();

                    // 표 출력 시작
                    System.out.println("---------------------------------------------------------------------");
                    System.out.printf("| %-10s | %-17s | %-16s | %-11s |\n", "SSN", "이름", "성", "급여");
                    System.out.println("---------------------------------------------------------------------");

                    while (resultSet.next()) {
                        String ssn = resultSet.getString("Ssn");
                        String firstName = resultSet.getString("Fname");
                        String lastName = resultSet.getString("Lname");
                        double salary = resultSet.getDouble("Salary");
                        System.out.printf("| %-10s | %-18s | %-16s | %-12.2f |\n", ssn, firstName, lastName, salary);
                    }

                    System.out.println("---------------------------------------------------------------------");
                }
            } else {
                System.out.println("입력한 부서가 존재하지 않습니다.");
            }
        } catch (Exception error) {
            System.out.println("부서 정보를 조회하는 중 오류가 발생했습니다: " + error.getMessage());
        }
    }


}
