import com.cab302.wellbeing.DataBaseConnection;
import com.cab302.wellbeing.controller.WellBeingController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.sql.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WellBeingControllerTest {

    @InjectMocks
    private static WellBeingController wellBeingController;

    @Mock
    private DataBaseConnection mockDataBaseConnection;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private static boolean isPlatformInitialized = false;

    @BeforeAll
    public static void setupAll() {
        try {
            Platform.startup(() -> {
                wellBeingController = new WellBeingController();
            });
        } catch (IllegalStateException e) {
            System.out.println("JavaFX Toolkit already initialized.");
            if (wellBeingController == null) {
                wellBeingController = new WellBeingController();
            }
        }
    }

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        Platform.runLater(() -> {
            // Create a root pane and scene for testing
            Group root = new Group();
            Scene scene = new Scene(root, 800, 600);

            // Initialize controller and UI components
            wellBeingController = new WellBeingController();
            wellBeingController.txtUsr = new TextField();
            wellBeingController.txtPwd = new PasswordField();
            wellBeingController.lblLoginMsg = new Label();
            wellBeingController.btnExit = new Button();

            // Add components to the root pane
            root.getChildren().addAll(wellBeingController.txtUsr, wellBeingController.txtPwd, wellBeingController.lblLoginMsg, wellBeingController.btnExit);

            // Set up and show the stage for testing
            Stage mockStage = new Stage();
            mockStage.setScene(scene);
            mockStage.show();  // Ensure the stage is shown

            // Manually set the controller's stage reference if necessary
            wellBeingController.stage = mockStage;
        });
        waitForFxThreads();
    }

    private void waitForFxThreads() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> latch.countDown());
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testUserLifecycle() throws InterruptedException, SQLException {
        setupMockDatabaseResponses();

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Simulate user entering correct credentials and logging in
                simulateLogin("cab302", "cab302");

                // Schedule another runLater to ensure all UI and event processing completes
                Platform.runLater(() -> {
                    // Attempt to close the stage
                    wellBeingController.btnExit.fire();

                    // Delay execution to allow UI to update
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> {
                                        try {
                                            // Check if the stage is still visible
                                            boolean visible = wellBeingController.stage.isShowing();
                                            assertFalse(visible, "Stage should not be visible after exit action.");

                                        } finally {
                                            latch.countDown();
                                        }
                                    });
                                }
                            },
                            500 // Delay in milliseconds
                    );
                });

            } catch (Exception e) {
                latch.countDown();
                throw e;
            }
        });

        latch.await();
    }

    private void simulateLogin(String username, String password) {
        wellBeingController.txtUsr.setText(username);
        wellBeingController.txtPwd.setText(password);
        wellBeingController.validateLogin(new ActionEvent());
        assertEquals("Welcome " + username, wellBeingController.lblLoginMsg.getText());
    }

    private void setupMockDatabaseResponses() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("passwordHash")).thenReturn("correctHash");
        when(mockResultSet.getInt("userId")).thenReturn(1);
    }
    @Test
    public void testValidateLoginSuccess() throws SQLException, InterruptedException {
        // Setup the mocks
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("passwordHash")).thenReturn("cab302");
        when(mockResultSet.getInt("userId")).thenReturn(1);

        // Use CountDownLatch to wait for Platform.runLater to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Run the test scenario on the JavaFX thread
        Platform.runLater(() -> {
            try {
                wellBeingController.txtUsr.setText("cab302");
                wellBeingController.txtPwd.setText("cab302");

                wellBeingController.validateLogin(mock(ActionEvent.class));

                assertEquals("Welcome cab302", wellBeingController.lblLoginMsg.getText());
            } finally {
                latch.countDown();  // Ensure the latch is counted down regardless of the test outcome
            }
        });

        // Wait for the JavaFX thread to complete the operations
        latch.await();
    }

    @Test
    public void testValidateLoginFailure() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        wellBeingController.txtUsr.setText("john");
        wellBeingController.txtPwd.setText("wrongpassword");

        wellBeingController.validateLogin(mock(ActionEvent.class));

        assertEquals("Your username or password is wrong", wellBeingController.lblLoginMsg.getText());
    }

    @AfterAll
    public static void tearDown() {
//        Platform.runLater(() -> {
//            Stage stage = (Stage) wellBeingController.btnExit.getScene().getWindow();
//            if (stage != null) {
//                stage.close();
//            }
//        });
    }
}