package gptpro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class DatabaseInitializer {

    public static void initializeDatabase() {
       
       String username = "user";
       String password = "1234";
       String JDBC_Url = "jdbc:h2:~/memo_db12";
       
       Display display = PlatformUI.getWorkbench().getDisplay();
      DatabaseConnectionDialog dialog = new DatabaseConnectionDialog(display.getActiveShell());
        
      if (dialog.open() == Dialog.OK) {
            // 사용자가 연결 정보를 입력하고 확인 버튼을 누른 경우,
            // 다시 연결을 시도하고 성공하면 'connection' 객체를 얻습니다.
            // 'connection' 객체를 사용하여 데이터베이스 작업을 수행합니다.
           
         
         if (DatabaseConnectionDialog.shouldAutoStartServer()) {   // db로..
             H2ServerStarter.startServer();
          }
            
        } else {
            // 사용자가 연결 설정을 취소한 경우, 프로그램 종료 또는 다른 작업 수행
        }
        
        try (Connection connection = DriverManager.getConnection(JDBC_Url, username, password)) {
            // 데이터베이스가 이미 존재하면 초기화하지 않음
            if (!databaseExists(connection)) {
                createTables(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean databaseExists(Connection connection) throws SQLException {
        // 데이터베이스가 이미 존재하는지 확인
        Statement statement = connection.createStatement();
        try {
            statement.executeQuery("SELECT 1 FROM memos");
            return true; // memos 테이블이 존재하면 데이터베이스가 이미 생성되었다고 가정
        } catch (SQLException e) {
            return false; // memos 테이블이 존재하지 않으면 데이터베이스가 없다고 가정
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        // 테이블 생성 SQL을 작성하여 실행
        String createTableSQL = "CREATE TABLE IF NOT EXISTS memos (" +
                "line INT PRIMARY KEY," +
                "content VARCHAR(255) NOT NULL," +
                "has_icon BOOLEAN" +
                ")";
        Statement statement = connection.createStatement();
        statement.executeUpdate(createTableSQL);
    }
}