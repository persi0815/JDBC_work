package controller;

import model.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

import static java.io.File.separator;

public class ViewController {

    private final Scanner scanner = new Scanner(System.in);

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
}
