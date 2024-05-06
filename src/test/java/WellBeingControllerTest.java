import com.cab302.wellbeing.DataBaseConnection;
import com.cab302.wellbeing.controller.MainMenuController;
import com.cab302.wellbeing.controller.WellBeingController;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WellBeingControllerTest {

    @InjectMocks
    private static WellBeingController wellBeingController;
    @InjectMocks
    private static MainMenuController mainMenuController;
    @Mock
    private DataBaseConnection mockDataBaseConnection;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;
    private static Stage loginStage;
    private static Stage mainMenuStage;
    private static AutoCloseable closeable;

    private static boolean isPlatformInitialized = false;

    @BeforeAll
    public static void setupAll() {
        try {
            if (!isPlatformInitialized) {
                Platform.startup(() -> {
                    // JavaFX initialization logic
                });
                isPlatformInitialized = true;
            }
        } catch (IllegalStateException e) {
            System.out.println("JavaFX Toolkit already initialized.");
        }
    }

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        closeable = MockitoAnnotations.openMocks(this);
        CountDownLatch latch = new CountDownLatch(1);
        //MockitoAnnotations.openMocks(this);
        Platform.runLater(() -> {
            try {
                // Load the actual FXML file just like the application does
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));  // Ensure the path is correct
                Parent loginToot = loginLoader.load();
                wellBeingController = loginLoader.getController();
                loginStage = new Stage();
                loginStage.setScene(new Scene(loginToot));
                loginStage.setTitle("Login");
                loginStage.show();

                // Ensure your controller or other necessary elements are initialized or manipulated if necessary
                assertNotNull(wellBeingController, "Controller must not be null");

            } catch (IOException e) {
                e.printStackTrace();
                fail("Failed to load FXML or initialize the controller: " + e.getMessage());
            }
        });

        // Wait for all initializations to complete
        waitForFxThreads();
    }

    private void waitForFxThreads() {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        try {
            assertTrue(latch.await(2, TimeUnit.SECONDS), "Timeout waiting for FX platform.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Interrupted during wait");
        }
    }

    @Test
    @Order(2)
    public void testValidateLoginSuccess() throws SQLException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            wellBeingController.txtUsr.setText("cab302");
            wellBeingController.txtPwd.setText("cab302");

            try {
                when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
                when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
                when(mockResultSet.next()).thenReturn(true);
                when(mockResultSet.getString("passwordHash")).thenReturn("cab302"); // Mock bcrypt hash for password
                when(mockResultSet.getInt("userId")).thenReturn(1);
                when(mockResultSet.getString("firstName")).thenReturn("cab302");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            wellBeingController.validateLogin(null);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> {
                assertEquals("Welcome cab302", wellBeingController.lblLoginMsg.getText());

                // Close the stage with the title "Main Menu"
                Stage mainMenuStage = (Stage) Stage.getWindows().stream()
                        .filter(window -> window instanceof Stage && "Main Menu".equals(((Stage) window).getTitle()))
                        .map(window -> (Stage) window)
                        .findFirst().orElse(null);

                if (mainMenuStage != null) {
                    mainMenuStage.close();
                }

                latch.countDown();
            });
            delay.play();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for FX platform.");
    }

    @Test
    @Order(2)
    public void testValidateLoginFailure() throws SQLException, InterruptedException {
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
//        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
//        when(mockResultSet.next()).thenReturn(false);
        Platform.runLater(() -> {
            wellBeingController.txtUsr.setText("john");
            wellBeingController.txtPwd.setText("wrongpassword");
            try {
                when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
                when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
                when(mockResultSet.next()).thenReturn(false); // Simulate no results found
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Create a pause transition of 2 seconds before executing the query
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            wellBeingController.validateLogin(mock(ActionEvent.class));
            pause.setOnFinished(event -> {
                assertEquals("Welcome cab302", wellBeingController.lblLoginMsg.getText());
            });
            pause.play(); // Start the pause

        });
        Thread.sleep(2000); // Wait for JavaFX operations to complete
    }

    @AfterAll
    public static void tearDown() {
        Platform.runLater(() -> {
            Stage stage = (Stage) wellBeingController.btnExit.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });
    }
}

//import com.cab302.wellbeing.DataBaseConnection;
//import com.cab302.wellbeing.controller.WellBeingController;
//import javafx.animation.PauseTransition;
//import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Group;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//import org.junit.jupiter.api.*;
//        import org.mockito.*;
//
//        import java.io.IOException;
//import java.sql.*;
//        import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//        import static org.mockito.Mockito.*;
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class WellBeingControllerTest {
//
//    @InjectMocks
//    private static WellBeingController wellBeingController;
//
//    @Mock
//    private DataBaseConnection mockDataBaseConnection;
//
//    @Mock
//    private Connection mockConnection;
//
//    @Mock
//    private PreparedStatement mockPreparedStatement;
//
//    @Mock
//    private ResultSet mockResultSet;
//
//    private static boolean isPlatformInitialized = false;
//
//    @BeforeAll
//    public static void setupAll() {
//        try {
//            Platform.startup(() -> {
//                wellBeingController = new WellBeingController();
//            });
//        } catch (IllegalStateException e) {
//            System.out.println("JavaFX Toolkit already initialized.");
//            if (wellBeingController == null) {
//                wellBeingController = new WellBeingController();
//            }
//        }
//    }
//
//    @BeforeEach
//    public void setUp() throws SQLException, IOException {
//        MockitoAnnotations.openMocks(this);
//        Platform.runLater(() -> {
//            try {
//                // Load the actual FXML file just like the application does
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));  // Ensure the path is correct
//                Parent root = loader.load();
//
//                // Get the controller from the loader
//                wellBeingController = loader.getController();
//
//                // Create a scene with the loaded FXML root
//                Scene scene = new Scene(root);
//
//                // Set up and show the stage for testing
//                Stage mockStage = new Stage();
//                mockStage.setTitle("Well Being Test");
//                mockStage.setScene(scene);
//                mockStage.show();  // Ensure the stage is shown
//
//                // Ensure your controller or other necessary elements are initialized or manipulated if necessary
//                assertNotNull(wellBeingController, "Controller must not be null");
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                fail("Failed to load FXML or initialize the controller: " + e.getMessage());
//            }
//        });
//
//        // Wait for all initializations to complete
//        waitForFxThreads();
//    }
//
//    private void waitForFxThreads() {
//        final CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(latch::countDown);
//        try {
//            assertTrue(latch.await(2, TimeUnit.SECONDS), "Timeout waiting for FX platform.");
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            fail("Interrupted during wait");
//        }
//    }
//    @Test
//    @Order(1)
//    public void testValidateLoginFailure() throws SQLException, InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//
//        Platform.runLater(() -> {
//            wellBeingController.txtUsr.setText("cab302");
//            wellBeingController.txtPwd.setText("cab302");
//
//            try {
//                when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
//                when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
//                when(mockResultSet.next()).thenReturn(true);
//                when(mockResultSet.getString("passwordHash")).thenReturn("$2a$10$eimf7Fv8shLCdZjtHf2PHOvfDpuV8TwbpQ.Yp1PmbLPrGh1KheTZG"); // Mock bcrypt hash for password
//                when(mockResultSet.getInt("userId")).thenReturn(1);
//                when(mockResultSet.getString("firstName")).thenReturn("cab302");
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//            // Simulate successful login
//            wellBeingController.validateLogin(null);
//
//            PauseTransition delay = new PauseTransition(Duration.seconds(2));
//            delay.setOnFinished(event -> {
//                assertEquals("Welcome cab302", wellBeingController.lblLoginMsg.getText());
//
//                // Close the MainMenu stage
//                Stage mainMenuStage = (Stage) Stage.getWindows().stream()
//                        .filter(window -> ((Stage) window).getTitle().equals("MainMenu"))
//                        .findFirst().orElse(null);
//
//                if (mainMenuStage != null) {
//                    mainMenuStage.close();
//                }
//
//                latch.countDown();
//            });
//            delay.play();
//        });
//
//        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for FX platform.");
//    }
//
//    @Test
//    @Order(2)
//    public void testValidateLoginSuccess() throws SQLException, InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//
//        Platform.runLater(() -> {
//            wellBeingController.txtUsr.setText("cab302");
//            wellBeingController.txtPwd.setText("cab302");
//
//            try {
//                when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
//                when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
//                when(mockResultSet.next()).thenReturn(true);
//                when(mockResultSet.getString("passwordHash")).thenReturn("$2a$10$eimf7Fv8shLCdZjtHf2PHOvfDpuV8TwbpQ.Yp1PmbLPrGh1KheTZG"); // Mock bcrypt hash for password
//                when(mockResultSet.getInt("userId")).thenReturn(1);
//                when(mockResultSet.getString("firstName")).thenReturn("cab302");
//                when(mockResultSet.getString("AccType")).thenReturn("user");
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//            wellBeingController.validateLogin(null);
//
//            PauseTransition delay = new PauseTransition(Duration.seconds(2));
//            delay.setOnFinished(event -> {
//                assertEquals("Welcome cab302", wellBeingController.lblLoginMsg.getText());
//
//                // Close the stage with the title "Main Menu"
//                Stage mainMenuStage = (Stage) Stage.getWindows().stream()
//                        .filter(window -> window instanceof Stage && "Main Menu".equals(((Stage) window).getTitle()))
//                        .map(window -> (Stage) window)
//                        .findFirst().orElse(null);
//
//                if (mainMenuStage != null) {
//                    mainMenuStage.close();
//                }
//
//                latch.countDown();
//            });
//            delay.play();
//        });
//
//        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for FX platform.");
//    }
//
//
//    @AfterAll
//    public static void tearDown() {
//        Platform.runLater(() -> {
//            Stage stage = (Stage) wellBeingController.btnExit.getScene().getWindow();
//            if (stage != null) {
//                stage.close();
//            }
//        });
//    }
//}