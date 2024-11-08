package model;

import static view.EmployeeView.getAttribute;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import view.EmployeeView;
import java.sql.ResultSet;

public class EmployeeService {
    private static final Scanner scanner = new Scanner(System.in);

    // 10. 직원 급여 인상
    public void raiseEmployeeSalary() {
        System.out.println("\n======= 급여 인상 대상 직원의 Ssn을 입력하세요 =======");
        System.out.print("Ssn 입력: ");
        String ssn = scanner.nextLine().trim();

        System.out.print("인상률을 입력하세요 (예: 10을 입력하면 10% 인상): ");
        double rate = scanner.nextDouble();
        scanner.nextLine();

        // Math.ceil을 사용하여 올림 처리하고 정수로 저장하게 했음.
        String sql = String.format(
                "UPDATE EMPLOYEE SET Salary = CEIL(Salary * (1 + %f / 100)) WHERE Ssn = '%s'",
                rate, ssn
        );

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            int updatedRows = stmt.executeUpdate(sql);
            if (updatedRows > 0) {
                System.out.println("해당 직원의 급여가 인상되었습니다.");

                // 결과로 인상된 직원 정보 출력
                String resultSql = String.format(
                        "SELECT Fname, Lname, Ssn, Salary FROM EMPLOYEE WHERE Ssn = '%s'", ssn
                );
                ResultSet rs = stmt.executeQuery(resultSql);
                System.out.println("< 급여 인상 결과 - 대상 직원 >");
                System.out.println("-------------------------------------------");
                System.out.printf("| %-10s | %-10s | %-10s |\n", "이름", "직원번호", "Salary");
                System.out.println("-------------------------------------------");

                if (rs.next()) {
                    String name = rs.getString("Fname") + " " + rs.getString("Lname");
                    int salary = rs.getInt("Salary");
                    System.out.printf("| %-10s | %-10s | %-10d |\n", name, rs.getString("Ssn"), salary);
                }
                System.out.println("-------------------------------------------");

            } else {
                System.out.println("직원을 찾을 수 없습니다.");
            }
        } catch (SQLException e) {
            System.out.println("급여 인상 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 11. 부양가족 추가
    public void addDependent() {
        System.out.println("\n======= 부양가족을 추가할 직원의 Ssn을 입력하세요 =======");
        System.out.print("Ssn 입력: ");
        String ssn = scanner.nextLine().trim();

        System.out.print("부양가족 이름: ");
        String dependentName = scanner.nextLine().trim();
        System.out.print("부양가족 성별 (M/F): ");
        String sex = scanner.nextLine().trim();
        System.out.print("부양가족 생년월일 (YYYY-MM-DD): ");
        String bdate = scanner.nextLine().trim();
        System.out.print("관계 (예: Son, Daughter, Spouse 등): ");
        String relationship = scanner.nextLine().trim();

        String sql = String.format(
                "INSERT INTO DEPENDENT (Essn, Dependent_name, Sex, Bdate, Relationship) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s')",
                ssn, dependentName, sex, bdate, relationship
        );

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("부양가족이 추가되었습니다.");

            // 결과로 DEPENDENT 테이블 보여줌
            String resultSql = "SELECT * FROM DEPENDENT";
            ResultSet rs = stmt.executeQuery(resultSql);
            System.out.println("< 부양가족 추가 결과 - DEPENDENT TABLE >");
            System.out.println("-------------------------------------------------------------");
            System.out.printf("| %-10s | %-15s | %-5s | %-10s | %-10s |\n",
                    "Essn", "Dependent_name", "Sex", "Bdate", "Relationship");
            System.out.println("-------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("| %-10s | %-15s | %-5s | %-10s | %-10s |\n",
                        rs.getString("Essn"),
                        rs.getString("Dependent_name"),
                        rs.getString("Sex"),
                        rs.getString("Bdate"),
                        rs.getString("Relationship"));
            }
            System.out.println("-------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("부양가족 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 12. 프로젝트 추가
    public void addProject() {
        System.out.println("\n======= 추가할 새 프로젝트의 정보를 입력하세요 =======");
        System.out.print("프로젝트 이름 (Pname): ");
        String pname = scanner.nextLine().trim();
        System.out.print("프로젝트 번호 (Pnumber): ");
        int pnumber = scanner.nextInt();
        scanner.nextLine();
        System.out.print("프로젝트 위치 (Plocation): ");
        String plocation = scanner.nextLine().trim();
        System.out.print("부서 번호 (Dnum): ");
        int dnum = scanner.nextInt();
        scanner.nextLine();

        String sql = String.format(
                "INSERT INTO PROJECT (Pname, Pnumber, Plocation, Dnum) VALUES ('%s', %d, '%s', %d)",
                pname, pnumber, plocation, dnum
        );

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("프로젝트가 추가되었습니다.");

            // 결과로 PROJECT 테이블 보여줌
            String resultSql = "SELECT * FROM PROJECT";
            ResultSet rs = stmt.executeQuery(resultSql);
            System.out.println("< 프로젝트 추가 결과 - PROJECT TABLE >");
            System.out.println("--------------------------------------------------");
            System.out.printf("| %-15s | %-8s | %-10s | %-5s |\n", "Pname", "Pnumber", "Plocation", "Dnum");
            System.out.println("--------------------------------------------------");

            while (rs.next()) {
                System.out.printf("| %-15s | %-8d | %-10s | %-5d |\n",
                        rs.getString("Pname"),
                        rs.getInt("Pnumber"),
                        rs.getString("Plocation"),
                        rs.getInt("Dnum"));
            }
            System.out.println("--------------------------------------------------");

            System.out.print("새 프로젝트에 직원을 배치하겠습니까? (yes/no): ");
            String assignEmployee = scanner.nextLine().trim();
            if (assignEmployee.equalsIgnoreCase("yes")) {
                System.out.print("새 프로젝트에 배치할 직원의 Ssn을 입력하세요: ");
                String ssn = scanner.nextLine().trim();

                String assignSql = String.format(
                        "INSERT INTO WORKS_ON (Essn, Pno, Hours) VALUES ('%s', %d, 0)",
                        ssn, pnumber
                );
                stmt.executeUpdate(assignSql);
                System.out.println("직원이 새 프로젝트에 배치되었습니다.");

                // 결과로 WORKS_ON 테이블도 보여줌
                resultSql = "SELECT * FROM WORKS_ON";
                rs = stmt.executeQuery(resultSql);
                System.out.println("< 직원 배치 결과 - WORKS_ON TABLE >");
                System.out.println("----------------------------------------");
                System.out.printf("| %-10s | %-5s | %-5s |\n", "Essn", "Pno", "Hours");
                System.out.println("----------------------------------------");

                while (rs.next()) {
                    System.out.printf("| %-10s | %-5d | %-5.1f |\n",
                            rs.getString("Essn"),
                            rs.getInt("Pno"),
                            rs.getDouble("Hours"));
                }
                System.out.println("----------------------------------------");
            }
        } catch (SQLException e) {
            System.out.println("프로젝트 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 1. 보고서 전체 -> 출력
    public void printAllEmployees(Statement stmt, List<String> selectedAttributes) throws SQLException {
        String sql = "select * from employee E join department D on E.Dno = D.Dnumber";
        EmployeeView.executeQueryAndDisplayEmployee(sql, stmt, selectedAttributes, "< 보고서 출력 결과 - EMPLOYEE TABLE >");
    }

    // 2. 조건 검색 -> 출력
    public void printEmployeeWithCondition(Statement stmt, List<String> selectedAttributes) throws SQLException {
        String attribute = getAttribute("검색");
        String sql = "select * from employee E join department D on E.Dno = D.Dnumber ";
        sql = getSql(attribute, sql);
        while (sql.equals("")) { // 재입력
            attribute = getAttribute("검색");
            sql = "select * from employee E join department D on E.Dno = D.Dnumber ";
            sql = getSql(attribute, sql);
        }
        EmployeeView.executeQueryAndDisplayEmployee(sql, stmt, selectedAttributes, "< 조건 검색 결과 - EMPLOYEE TABLE >");
    }

    // 3. 조건 삭제 -> 나머지 출력
    public void deleteEmployeeWithCondition(Statement stmt, List<String> selectedAttributes) throws SQLException {
        String attribute = getAttribute("삭제");
        String sql = "delete from employee ";
        sql = getSql(attribute, sql);
        int affectedTuples = 0;
        try{
            affectedTuples = stmt.executeUpdate(sql);
        } catch (SQLException e){
            System.out.println("0~3 중에 선택바랍니다.");
        } finally {
            System.out.println("--- " + affectedTuples + "개의 행이 삭제되었습니다 ---");
            displayEmployee(stmt, selectedAttributes, "< 조건 삭제 결과 - EMPLOYEE TABLE >");
        }
    }

    // 4. ssn으로 단일 삭제 -> 나머지 출력
    public void deleteEmployeeBySsn(Statement stmt, List<String> selectedAttributes) throws SQLException {
        System.out.println("\n======= 삭제할 직원의 Ssn을 입력하세요. =======");
        System.out.print("입력 : ");
        String Ssn = scanner.nextLine();
        String sql = "delete from employee where ssn = " + Ssn;
        int deleted = stmt.executeUpdate(sql);
        if (deleted == 1) {
            System.out.println("--- 해당 직원이 삭제되었습니다 ---");
        } else {
            System.out.println("--- 삭제된 직원이 없습니다 ---");
        }
        displayEmployee(stmt, selectedAttributes, "< 단일 삭제 결과 - EMPLOYEE TABLE >");
    }

    // 받은 attribute 종류와, attribute 값에 맞는 sql 반환
    public static String getSql(String attribute, String sql) {
        if (attribute.equalsIgnoreCase("Sex") || attribute.equals("1")) {
            sql = bySex(sql);

        } else if (attribute.equalsIgnoreCase("Salary") || attribute.equals("2")) {
            sql = bySalary(sql);

        } else if (attribute.equalsIgnoreCase("Department") || attribute.equals("3")) {
            sql = byDepartment(sql);
        } else if (attribute.equalsIgnoreCase("전체") || attribute.equals("0")) {
            sql = sql;
        } else {
            sql = "";
        }

        return sql;
    }

    // condition 붙여서 sql 만듦
    public static String bySex(String sql) {
        System.out.println("--- 직원의 성별을 입력하세요 ---");
        System.out.println("1. Male\n2. Female\n");
        System.out.print("입력 : ");

        String value = scanner.nextLine();
        value = EmployeeService.getSexAttribute(value);

        while (value.equals("else")) { // 재입력 받기
            System.out.print("입력 : ");
            value = scanner.nextLine();
            value = EmployeeService.getSexAttribute(value);
        }
        sql += "where Sex = " + value;
        return sql;
    }

    public static String bySalary(String sql) {
        System.out.println("--- 직원의 연봉 범위를 설정하세요 ---");
        System.out.print("최소 연봉(미설정 희망 시 enter 입력) : ");
        String minSalary = scanner.nextLine();
        System.out.print("최대 연봉(미설정 희망 시 enter 입력) : ");
        String maxSalary = scanner.nextLine();

        sql += EmployeeService.getSalaryStatement(minSalary, maxSalary);
        return sql;
    }

    public static String byDepartment(String sql) {
        System.out.println("--- 직원의 부서명을 입력하세요 ---");
        System.out.println("1. Research\n2. Administration\n3. Headquarters");
        System.out.print("입력 : ");
        String value = scanner.nextLine();
        value = EmployeeService.getDNameAttribute(value);

        while (value.equals("else")) { // 재입력 받기
            System.out.print("입력 : ");
            value = scanner.nextLine();
            value = EmployeeService.getDNameAttribute(value);
        }
        sql += "where Dno = " + value;
        return sql;
    }

    // 대소문자, 숫자 모두 인풋 가능하도록
    public static String getSexAttribute(String value) {
        if (value.equalsIgnoreCase("m") || value.equalsIgnoreCase("male") || value.equals("1")) {
            value = "'M'";
        } else if (value.equalsIgnoreCase("f") || value.equalsIgnoreCase("female") || value.equals("2")) {
            value = "'F'";
        } else {
            value = "";
        }
        return value;
    }

    public static String getSalaryStatement(String minSalary, String maxSalary) {
        if (minSalary.isEmpty() && maxSalary.isEmpty()) {
            return "";
        } else if (minSalary.isEmpty()) {
            return "where Salary <= " + maxSalary;
        } else if (maxSalary.isEmpty()) {
            return "where " + minSalary + " <= Salary";
        } else {
            return "where " + minSalary + " <= Salary  and Salary <= " + maxSalary;
        }
    }

    public static String getDNameAttribute(String value) {
        if (value.equalsIgnoreCase("research") || value.equals("1")) {
            value = "'Research'";
        } else if (value.equalsIgnoreCase("administrator") || value.equals("2")) {
            value = "'Administration'";
        } else if (value.equalsIgnoreCase("headquarters") || value.equals("3")) {
            value = "'Headquarters'";
        } else {
            value = "";
        }

        if (!value.equals("")) {
            return "(select Dnumber from department where Dname = " + value + ")";
        } else {
            return value;
        }
    }

    public void displayEmployee(Statement stmt, List<String> selectedAttributes, String result)
            throws SQLException { // 삭제, 추가 연산 이후
        String sql = "select * from employee E join department D on E.Dno = D.Dnumber";
        EmployeeView.executeQueryAndDisplayEmployee(sql, stmt, selectedAttributes, result);
    }

    // 5. 직원 추가 -> 출력
    public void addNewEmployee(Statement stmt, List<String> selectedAttributes) throws SQLException {
        System.out.println("--- 추가할 직원 정보를 입력하세요 ---");
        System.out.print("이름(Fname): ");
        String firstName = scanner.nextLine();
        System.out.print("중간 이니셜(Minit): ");
        String initial = scanner.nextLine();
        System.out.print("성(Lname): ");
        String lastName = scanner.nextLine();
        System.out.print("고유 번호(Ssn): ");
        String ssn = scanner.nextLine();
        System.out.print("생년월일(Bdate): ");
        String birthDate = scanner.nextLine();
        System.out.print("주소(Address): ");
        String address = scanner.nextLine();
        System.out.print("성별(Sex): ");
        String sex = scanner.nextLine();
        System.out.print("연봉(Salary): ");
        double salary = scanner.nextDouble();
        scanner.nextLine(); // 줄바꿈 제거
        System.out.print("상사 SSN(Super_ssn): ");
        String supervisorSsn = scanner.nextLine();
        System.out.print("부서 번호(Dno): ");
        String departmentNo = scanner.nextLine();

        String sql = getInsertQuery(firstName, initial, lastName, ssn, birthDate, address, sex, salary, supervisorSsn, departmentNo);
        try {
            stmt.executeUpdate(sql);
            System.out.println("직원이 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            System.out.println("직원 추가 연산에서 오류가 발생했습니다.");
        }
        displayEmployee(stmt, selectedAttributes, "< 직원 추가 결과 - EMPLOYEE TABLE >");
    }

    private static String getInsertQuery(String fName, String mInit, String lName, String ssn, String bDate, String address, String sex, double salary,
                                         String superSsn, String dNo) {
        return "insert into employee values("
                + getBrace(fName) + ","
                + getBrace(mInit) + ","
                + getBrace(lName) + ","
                + getBrace(ssn) + ","
                + getBrace(bDate) + ","
                + getBrace(address) + ","
                + getBrace(sex) + ","
                + salary + ","
                + getBrace(superSsn) + ","
                + dNo + ","
                + "CURRENT_TIMESTAMP()" + ","
                + "CURRENT_TIMESTAMP()" + ")";
    }

    private static String getBrace(String attribute) {
        return "'" + attribute + "'";
    }

    // 6. 그룹별 평균 급여 -> 출력
    public void printAverageSalaryByGroup(Statement stmt, String groupCriteria) throws SQLException {
        String sql;
        String resultTitle;

        switch (groupCriteria) {
            case "Sex" -> {
                sql = "SELECT Sex AS 기준, AVG(Salary) AS AvgSalary FROM employee GROUP BY Sex";
                resultTitle = "< 성별 그룹별 평균 급여 >";
            }
            case "Dname" -> {
                sql = """
                        SELECT D.Dname AS 기준, AVG(E.Salary) AS AvgSalary
                        FROM employee E
                        JOIN department D ON E.Dno = D.Dnumber
                        GROUP BY D.Dname
                        """;
                resultTitle = "< 부서 그룹별 평균 급여 >";
            }
            case "Super_ssn" -> {
                sql = "SELECT Super_ssn AS 기준, AVG(Salary) AS AvgSalary FROM employee GROUP BY Super_ssn";
                resultTitle = "< 상급자 그룹별 평균 급여 >";
            }
            default -> {
                System.out.println("유효하지 않은 그룹 기준입니다. 1~3 중 하나의 수를 선택해주세요.");
                return;
            }
        }
        EmployeeView.displayGroupedAverageSalary(sql, stmt, resultTitle);
    }
}