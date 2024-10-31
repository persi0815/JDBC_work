package view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class EmployeeView {

    public void showMenu() {
        System.out.println("\n====== 원하는 작업의 번호를 선택하세요 ======");
        System.out.println("1. 보고서 출력\n2. 조건 검색\n3. 조건 삭제\n4. 단일 삭제\n5. 직원 추가\n0. 프로그램 종료");
        System.out.print("입력 : ");
    }

    public static String getAttribute(String option) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n======= " + option + "할 조건의 attribute를 입력하세요 =======");
        System.out.println("1. Sex\n2. Salary\n3. Department Name");
        System.out.print("입력 : ");
        return sc.nextLine();
    }

    public void showExitMessage() {
        System.out.println("프로그램을 종료합니다.");
    }

    public static void executeQueryAndDisplayEmployee(String sql, Statement stmt, String result) throws SQLException {
        System.out.println(result);
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "|        NAME        |    SSN    |    BDATE    |          ADDRESS          | SEX |  SALARY  |   SUPER_SSN   |     DNAME     |");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------");

        try (ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("Fname") + " " + rs.getString("Minit") + " " + rs.getString("Lname");
                String ssn = rs.getString("Ssn");
                String bDate = rs.getString("Bdate");
                String address = rs.getString("Address");
                String sex = rs.getString("Sex");
                double salary = rs.getDouble("Salary");
                String superSsn = rs.getString("Super_ssn");
                String dName = rs.getString("Dname");

                System.out.printf(
                        "| %-" + 18 + "s | %-" + 9 + "s | %-" + 10
                                + "s | %-" + 25 + "s | %-" + 3 + "s | %-"
                                + 8 + ".2f | %-" + 13 + "s | %-" + 14
                                + "s |\n",
                        name, ssn, bDate, address, sex, salary, superSsn, dName);
            }
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------------------------");
        }
    }
}
