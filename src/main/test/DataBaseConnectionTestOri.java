import com.cab302.wellbeing.DataBaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;


class DataBaseConnectionTestOri {

    private Connection connection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    private DataBaseConnection dbConnection;

    @BeforeEach
    void setUp() throws SQLException {
        // Initialize mocks and real database connection
        MockitoAnnotations.openMocks(this);
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        setupDatabaseSchema();
        dbConnection = new DataBaseConnection();
        dbConnection.databaseLink = connection; // Directly inject the actual connection
    }

    private void setupDatabaseSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
//            statement.execute("DROP TABLE IF EXISTS PwdQuestions1;");
//            statement.execute("DROP TABLE IF EXISTS PwdQuestions2;");
//            statement.execute("DROP TABLE IF EXISTS useraccount;");

            // Recreate the tables
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS PwdQuestions1 (" +
                            "QuestionID_1 INT AUTO_INCREMENT PRIMARY KEY, " +
                            "Question VARCHAR(255) NOT NULL);"
            );
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS PwdQuestions2 (" +
                            "QuestionID_2 INT AUTO_INCREMENT PRIMARY KEY, " +
                            "Question VARCHAR(255) NOT NULL);"
            );
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS useraccount (" +
                            "userId INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                            "userName VARCHAR(255) UNIQUE NOT NULL, " +
                            "emailAddress VARCHAR(255) UNIQUE NOT NULL CHECK (emailAddress LIKE '%@%'), " +
                            "firstName VARCHAR(255) NOT NULL, " +
                            "lastName VARCHAR(255) NOT NULL, " +
                            "passwordHash VARCHAR(255) NOT NULL, " +
                            "accType ENUM('Admin', 'General') NOT NULL, " +
                            "DateCreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "QuestionID_1 INT NOT NULL, " +
                            "QuestionID_2 INT NOT NULL, " +
                            "Answer_1 VARCHAR(255) NOT NULL, " +
                            "Answer_2 VARCHAR(255) NOT NULL, " +
                            "FOREIGN KEY (QuestionID_1) REFERENCES PwdQuestions1(QuestionID_1), " +
                            "FOREIGN KEY (QuestionID_2) REFERENCES PwdQuestions2(QuestionID_2));"
            );

            // Insert initial data for questions without specifying the ID
            statement.execute("INSERT INTO PwdQuestions1 (Question) VALUES ('Your favorite color?');");
            statement.execute("INSERT INTO PwdQuestions2 (Question) VALUES ('Your birth city?');");
        }
    }
    @Test
    void testDatabaseConnection() {
        assertNotNull(dbConnection.databaseLink);
        assertDoesNotThrow(() -> assertTrue(dbConnection.databaseLink.isValid(5)));
    }
    @Test
    void testInsertUser() throws SQLException {
        String insertSql = "INSERT INTO useraccount (userName, emailAddress, firstName, lastName, passwordHash, accType, QuestionID_1, QuestionID_2, Answer_1, Answer_2) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            // Set each parameter correctly
            pstmt.setString(1, "testUser");
            pstmt.setString(2, "test@example.com");
            pstmt.setString(3, "Test");
            pstmt.setString(4, "User");
            pstmt.setString(5, "hashedpassword");
            pstmt.setString(6, "General");
            pstmt.setInt(7, 1); // Assuming you have a valid QuestionID_1 in your PwdQuestions1 table
            pstmt.setInt(8, 1); // Assuming you have a valid QuestionID_2 in your PwdQuestions2 table
            pstmt.setString(9, "Blue"); // Example answer for QuestionID_1
            pstmt.setString(10, "New York"); // Example answer for QuestionID_2

            int affectedRows = pstmt.executeUpdate();
            assertEquals(1, affectedRows, "One row should be inserted");

            try (ResultSet resultSet = pstmt.getGeneratedKeys()) {
                assertTrue(resultSet.next(), "Generated keys should be available.");
                int userId = resultSet.getInt(1);
                assertTrue(userId > 0, "Generated user ID should be greater than 0");
            }
        }
    }
    @Test
    void testCreateTables() throws SQLException {
        // Ensure tables are created successfully
        setupDatabaseSchema(); // Make sure this is being called correctly in setup or here

        // Fetch metadata to verify table creation
        DatabaseMetaData dbMetaData = connection.getMetaData();
        try (ResultSet rs = dbMetaData.getTables(null, null, null, new String[]{"TABLE"})) {
            boolean foundUserAccount = false;
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("Found table: " + tableName);
                if ("useraccount".equalsIgnoreCase(tableName)) {
                    foundUserAccount = true;
                }
            }
            assertTrue(foundUserAccount, "Table useraccount should exist.");
        }
    }
    @Test
    void testPasswordHashing() {
        String password = "testPassword123";
        String hashedPassword = dbConnection.hashPassword(password);
        assertNotNull(hashedPassword);
        assertTrue(BCrypt.checkpw(password, hashedPassword));
    }
    @AfterEach
    void tearDown() throws SQLException {
        //connection.close();
    }

}
//class DataBaseConnectionTest {
//
//    @Mock
//    private Connection mockConnection;
//
//    @Mock
//    private Statement mockStatement;
//
//    @Mock
//    private PreparedStatement mockPreparedStatement;
//
//    @Mock
//    private ResultSet mockResultSet;
//    private DataBaseConnection dbConnection;
//    @BeforeEach
//    void setUp() {
//        dbConnection = new DataBaseConnection();
//    }
//    @AfterEach
//    void tearDown() {
//        // Close the database connection after each test
//        if (dbConnection.databaseLink != null) {
//            try {
//                dbConnection.databaseLink.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    @Test
//    void testCreateDatabase() {
//        dbConnection.createDatabase();
//        // Verify if the database is created
//        Connection connection = dbConnection.getConnection();
//        assertNotNull(connection);
//    }
//    @Test
//    void testGetConnection() {
//        Connection connection = dbConnection.getConnection();
//        assertNotNull(connection);
//    }
//    @Test
//    void testCreateTables() {
//        dbConnection.initializeAndInsertUser();
//        // Verify if the tables are created
//        try (Statement statement = dbConnection.databaseLink.createStatement();
//             ResultSet resultSet = statement.executeQuery("SHOW TABLES")) {
//            assertTrue(resultSet.next());
//            assertEquals("browsingdata", resultSet.getString(1));
//            assertTrue(resultSet.next());
//            assertEquals("limits", resultSet.getString(1));
//            assertTrue(resultSet.next());
//            assertEquals("mediafiles", resultSet.getString(1));
//            assertTrue(resultSet.next());
//            assertEquals("notifications", resultSet.getString(1));
//            assertTrue(resultSet.next());
//            assertEquals("pwdquestions1", resultSet.getString(1));
//            assertTrue(resultSet.next());
//            assertEquals("pwdquestions2", resultSet.getString(1));
//            assertTrue(resultSet.next());
//            assertEquals("useraccount", resultSet.getString(1));
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//    void testInsertUser() {
//        dbConnection.initializeAndInsertUser();
//        // Verify if the user is inserted
//        try (Statement statement = dbConnection.databaseLink.createStatement();
//             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM useraccount")) {
//            assertTrue(resultSet.next());
//            assertEquals(1, resultSet.getInt(1));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//}