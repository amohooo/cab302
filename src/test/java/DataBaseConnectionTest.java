import com.cab302.wellbeing.DataBaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DataBaseConnectionTest {

    @Test
    public void testConnectionAndInsert() {
        DataBaseConnection db = new DataBaseConnection();
        assertNotNull(db.getConnection(), "Failed to connect to the database");

        // Perform a simple insert operation
        db.initializeAndInsertUser(); // This should handle table creation, question insertion, and user insertion

        // Check if the user is inserted
        String query = "SELECT COUNT(*) FROM useraccount WHERE userName = 'cab302'";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            assertTrue(rs.next(), "Query failed to execute");
            int count = rs.getInt(1);
            assertEquals(1, count, "User was not inserted correctly");
        } catch (SQLException e) {
            fail("SQLException thrown: " + e.getMessage());
        }
    }
}