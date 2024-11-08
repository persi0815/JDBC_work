package view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class EmployeeView {
    private static final Scanner scanner = new Scanner(System.in);

    public void showMenu() {
        System.out.println("\n====== 원하는 작업의 번호를 선택하세요 ======");
        System.out.println("1. 보고서 출력\n2. 조건 검색\n3. 조건 삭제\n4. 단일 삭제\n5. 직원 추가\n6. 그룹별 평균 급여 출력\n7. 뷰 관리\n8. 직원 상사 검색\n9. 프로젝트 별 인건비 검색\n10. 직원 급여 인상\n11. 부양가족 추가\n12. 프로젝트 추가\n0. 프로그램 종료");
        System.out.print("입력 : ");
    }

    public void showExitMessage() {
        System.out.println("프로그램을 종료합니다.");
    }

    public List<String> selectAttributes() {
        List<String> attributes = new ArrayList<>();
        System.out.println("\n====== 출력할 attribute 항목을 선택하세요 (콤마로 구분) ======");
        System.out.println("1. NAME\n2. SSN\n3. BDATE\n4. ADDRESS\n5. SEX\n6. SALARY\n7. SUPER_SSN\n8. DNAME");
        System.out.print("입력: ");
        String[] choices = scanner.nextLine().split(",");

        for (String choice : choices) {
            switch (choice.trim()) {
                case "1" -> attributes.add("NAME");
                case "2" -> attributes.add("SSN");
                case "3" -> attributes.add("BDATE");
                case "4" -> attributes.add("ADDRESS");
                case "5" -> attributes.add("SEX");
                case "6" -> attributes.add("SALARY");
                case "7" -> attributes.add("SUPER_SSN");
                case "8" -> attributes.add("DNAME");
                default -> System.out.println("잘못된 입력입니다: " + choice);
            }
        }
        return attributes;
    }

    public static String getAttribute(String option) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n======= " + option + "할 조건의 attribute를 입력하세요 =======");
        System.out.println("1. Sex\n2. Salary\n3. Department Name\n0. 전체");
        System.out.print("입력 : ");
        return sc.nextLine();
    }

    public String selectGroupCriteria() {
        System.out.println("\n====== 그룹 평균 급여를 계산할 기준을 선택하세요 ======");
        System.out.println("1. 성별\n2. 부서\n3. 상급자\n");
        System.out.print("입력 : ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        return switch (choice) {
            case 1 -> "Sex";
            case 2 -> "Dname";
            case 3 -> "Super_ssn";
            default -> "";
        };
    }

    public static void displayGroupedAverageSalary(String sql, Statement stmt, String result) throws SQLException {
        System.out.println(result);
        System.out.println("-------------------------------------");
        System.out.printf("| %-19s | %-10s |\n", "기준", "AVG SALARY");
        System.out.println("-------------------------------------");

        try (ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String criteria = rs.getString("기준");
                double avgSalary = rs.getDouble("AvgSalary");

                System.out.printf("| %-20s | %-10.2f |\n", criteria, avgSalary);
            }
        }
        System.out.println("-------------------------------------");
    }


    public static void executeQueryAndDisplayEmployee(String sql, Statement stmt, List<String> selectedAttributes,
                                                      String result) throws SQLException {

        System.out.println(result);

        // 동적으로 구분선 생성
        StringBuilder divider = new StringBuilder();
        for (String attr : selectedAttributes) {
            switch (attr) {
                case "NAME" -> divider.append("----------------------");
                case "SSN" -> divider.append("-----------");
                case "BDATE" -> divider.append("------------");
                case "ADDRESS" -> divider.append("---------------------------");
                case "SEX" -> divider.append("-----");
                case "SALARY" -> divider.append("----------");
                case "SUPER_SSN", "DNAME" -> divider.append("---------------");
            }
        }
        System.out.println(divider.append("------"));

        // 헤더 출력
        for (String attr : selectedAttributes) {
            if (Objects.equals(attr, "NAME")) {
                System.out.printf("| %-20s ", attr);
            }
            if (Objects.equals(attr, "SSN")) {
                System.out.printf("| %-9s ", attr);
            }
            if (Objects.equals(attr, "BDATE")) {
                System.out.printf("| %-10s ", attr);
            }
            if (Objects.equals(attr, "ADDRESS")) {
                System.out.printf("| %-25s ", attr);
            }
            if (Objects.equals(attr, "SEX")) {
                System.out.printf("| %-3s ", attr);
            }
            if (Objects.equals(attr, "SALARY")) {
                System.out.printf("| %-8s ", attr);
            }
            if (Objects.equals(attr, "SUPER_SSN")) {
                System.out.printf("| %-13s ", attr);
            }
            if (Objects.equals(attr, "DNAME")) {
                System.out.printf("| %-14s ", attr);
            }

        }
        System.out.println("|");
        System.out.println(divider);

        // 데이터 행 출력
        try (ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                for (String attr : selectedAttributes) {
                    switch (attr) {
                        case "NAME" -> System.out.printf("| %-20s ",
                                rs.getString("Fname") + " " + rs.getString("Minit") + " " + rs.getString("Lname"));
                        case "SSN" -> System.out.printf("| %-9s ", rs.getString("Ssn"));
                        case "BDATE" -> System.out.printf("| %-10s ", rs.getString("Bdate"));
                        case "ADDRESS" -> System.out.printf("| %-25s ", rs.getString("Address"));
                        case "SEX" -> System.out.printf("| %-3s ", rs.getString("Sex"));
                        case "SALARY" -> System.out.printf("| %-8.2f ", rs.getDouble("Salary"));
                        case "SUPER_SSN" -> System.out.printf("| %-13s ", rs.getString("Super_ssn"));
                        case "DNAME" -> System.out.printf("| %-14s ", rs.getString("Dname"));
                    }
                }
                System.out.println("|");
            }
            System.out.println(divider);
        }
    }
}
