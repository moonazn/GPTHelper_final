package gptpro;


import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;




public class MemoManager {
   private Connection connection = null;
   private String JDBC_URL = "jdbc:h2:tcp://localhost/~/test9";
//   private final String JDBC_URL = "jdbc:h2:~/memo_db";
   private String JDBC_USERNAME = "user";
   private String JDBC_PASSWORD = "1234";
   
   public static Boolean isConnected = false;

   public MemoManager() {

      try {
            // H2 데이터베이스 드라이버 클래스 로드
            Class.forName("org.h2.Driver");
            
            // H2 데이터베이스 연결
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
            isConnected = true;
        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
            
            isConnected = false;
         DatabaseInitializer.initializeDatabase();
         
         try {
              // 데이터베이스 초기화 후 다시 연결 시도
            
              connection = DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
              System.out.println("Connection re-established");
              isConnected = true;
          } catch (SQLException ex) {
//              ex.printStackTrace();
          }
        }
   

   }


   public String getMemo(int line) {
      String memo = null;
      String query = "SELECT content FROM memos WHERE line = ?";
      try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
         preparedStatement.setInt(1, line);
         ResultSet resultSet = preparedStatement.executeQuery();
         if (resultSet.next()) {
            memo = resultSet.getString("content");
         }
         System.out.println("Get memo for line " + line + ": " + memo);
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return memo;
   }

   public boolean hasIcon(int line) {
      boolean hasIcon = false;
      String query = "SELECT has_icon FROM memos WHERE line = ?";
      try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
         preparedStatement.setInt(1, line);
         ResultSet resultSet = preparedStatement.executeQuery();
         if (resultSet.next()) {
            hasIcon = resultSet.getBoolean("has_icon");
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return hasIcon;
   }

   public void updateMemoWithIcon(int line, String content, boolean hasIcon) {
      String query = "UPDATE memos SET content = ?, has_icon = ? WHERE line = ?";
      try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
         preparedStatement.setString(1, content);
         preparedStatement.setBoolean(2, hasIcon);
         preparedStatement.setInt(3, line);
         int rowsUpdated = preparedStatement.executeUpdate();
         System.out.println("Rows updated: " + rowsUpdated);
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

// 메모 삭제 
   public void deleteMemo(int line) {
      String query = "DELETE FROM memos WHERE line = ?";
      try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
         preparedStatement.setInt(1, line);
         preparedStatement.executeUpdate();
         System.out.println("Deleted content for line " + line);
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public void close() {
      try {
         if (connection != null) {
            connection.close();
            System.out.println("Connection closed");
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }
// 메모 저장 및 업데이트 
   public void setMemo(int line, String memo) {
      String query = "MERGE INTO memos (line, content) KEY(line) VALUES (?, ?);";
      try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
         preparedStatement.setInt(1, line);
         preparedStatement.setString(2, memo);

         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected > 0) {
            System.out.println("Memo added or updated successfully!");
         } else {
            System.out.println("No changes made to the memo.");
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

// 키워드 포함하는 메모 찾기 
   public List<MemoDTO> findMemosByCondition(String condition) {
      List<MemoDTO> matchingMemos = new ArrayList<>();

      String query = "SELECT line, content FROM memos WHERE content LIKE ?;";
      try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
         preparedStatement.setString(1, "%" + condition + "%");
         try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
               int line = resultSet.getInt("line");
               String memo = resultSet.getString("content");
               matchingMemos.add(new MemoDTO(line, memo));
            }
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }

      return matchingMemos;
   }


   public boolean getAutoStartValue() {
      // TODO Auto-generated method stub
      return false;
   }

}