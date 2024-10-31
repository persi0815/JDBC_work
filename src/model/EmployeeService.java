package model;

import static view.EmployeeView.getAttribute;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import view.EmployeeView;

public class EmployeeService {
    private static Scanner scanner = new Scanner(System.in);

    // 보고서 전체 -> 출력
    public void printAllEmployees(Statement stmt, List<String> selectedAttributes) throws SQLException {
        String sql = "select * from employee E join department D on E.Dno = D.Dnumber";
        EmployeeView.executeQueryAndDisplayEmployee(sql, stmt, selectedAttributes, "< 보고서 출력 결과 - EMPLOYEE TABLE >");
    }

    // 조건 검색 -> 출력
    public void printEmployeeWithCondition(Statement stmt, List<String> selectedAttributes) throws SQLException {
        String attribute = getAttribute("검색");
        String sql = "select * from employee E join department D on E.Dno = D.Dnumber ";
        sql = getSql(attribute, sql);
        while (sql.equals("else")) { // 재입력
            attribute = getAttribute("검색");
            sql = "select * from employee E join department D on E.Dno = D.Dnumber ";
            sql = getSql(attribute, sql);
        }
        EmployeeView.executeQueryAndDisplayEmployee(sql, stmt, selectedAttributes, "< 조건 검색 결과 - EMPLOYEE TABLE >");
    }

    // 조건 삭제 -> 나머지 출력
    public void deleteEmployeeWithCondition(Statement stmt, List<String> selectedAttributes) throws SQLException {
        String attribute = getAttribute("삭제");
        String sql = "delete from employee ";
        sql = getSql(attribute, sql);
        while (sql.equals("else")) { // 재입력
            attribute = getAttribute("삭제");
            sql = getSql(attribute, sql);
        }
        int affectedTuples = stmt.executeUpdate(sql);
        System.out.println("--- " + affectedTuples + "개의 행이 삭제되었습니다 ---");
        displayEmployee(stmt, selectedAttributes, "< 조건 삭제 결과 - EMPLOYEE TABLE >");
    }

    // ssn으로 단일 삭제 -> 나머지 출력
    public void deleteEmployeeBySsn(Statement stmt, List<String> selectedAttributes) throws SQLException {
        System.out.println("\n======= 삭제할 직원의 Ssn을 입력하세요. =======");
        System.out.print("입력 : ");
        String Ssn = scanner.nextLine();
        String sql = "delete from employee where ssn = " + Ssn;
        int deleted = stmt.executeUpdate(sql);
        if (deleted == 1) {
            System.out.println("--- 해당 직원이 삭제되었습니다 ---");
        } else {
            System.out.println("--- 삭제 된 직원이 없습니다 ---");
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
            sql = "else";
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
            value = "else";
        }
        return value;
    }

    public static String getSalaryStatement(String minSalary, String maxSalary) {
        if (minSalary.equals("") && maxSalary.equals("")) {
            return "";
        } else if (minSalary.equals("")) {
            return "where Salary <= " + maxSalary;
        } else if (maxSalary.equals("")) {
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
            value = "else";
        }

        if (!value.equals("else")) {
            return "(select Dnumber from department where Dname = " + value + ")";
        } else {
            return value;
        }
    }

    public void displayEmployee(Statement stmt, List<String> selectedAttributes, String result)
            throws SQLException { // 삭제 연산 이후
        String sql = "select * from employee E join department D on E.Dno = D.Dnumber";
        EmployeeView.executeQueryAndDisplayEmployee(sql, stmt, selectedAttributes, result);
    }

    // 직원 추가
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

        String sql = getInsertQuery(firstName, initial, lastName, ssn, birthDate, address, sex, salary, supervisorSsn,
                departmentNo);
        addEmployee(stmt, sql);
        displayEmployee(stmt, selectedAttributes, "< 직원 추가 결과 - EMPLOYEE TABLE >");
    }

    public void addEmployee(Statement stmt, String sql) {
        try {
            stmt.executeUpdate(sql);
            System.out.println("직원이 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            System.out.println("직원 추가 연산에서 오류가 발생했습니다.");
        }
    }

    private static String getInsertQuery(String fName, String mInit, String lName, String ssn, String bDate,
                                         String address, String sex, double salary,
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

}
