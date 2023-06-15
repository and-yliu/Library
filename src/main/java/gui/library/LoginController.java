package gui.library;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    public TextField textUserName;
    public TextField textPassword;
    public static String id;
    public static String firstName;
    public static String lastName;
    public static String pin;

    /**
     * Login to the library with username and PIN
     */
    public void login(ActionEvent actionEvent) {
        id = textUserName.getText();
        pin = textPassword.getText();
        boolean hasUser = false;
        String qu = "SELECT * FROM MEMBER";
        ResultSet resultSet = Main.memberHandler.execQuery(qu);
        try{
            while (resultSet.next()){
                String tempId = resultSet.getString("userId");
                String tempPin = resultSet.getString("pinNumber");
                //Check if there is a user with this username and PIN
                if(id.equals(tempId) && pin.equals(tempPin)){
                    hasUser = true;
                    firstName = resultSet.getString("firstName");
                    lastName = resultSet.getString("lastName");
                    Stage stage = (Stage) textUserName.getScene().getWindow();
                    stage.close();
                    loadWindow("main.fxml","Library");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!hasUser){
            //Pop up window if username and PIN are incorrect
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please Try Again or Sign Up");
            alert.setHeaderText("Invalid User ID or PIN Number");
            alert.showAndWait();
        }
    }

    /**
     *Launch the signup window by clicking the signup button
     */
    public void signUp(ActionEvent actionEvent) {
        loadWindow("signUp.fxml", "Sign Up");
    }

    /**
     * Load a new window with given location and title
     * @param location the name of the fxml file
     * @param title the title of the new window
     */
    private void loadWindow(String location, String title) {
        try{
            Parent parent = FXMLLoader.load(getClass().getResource("/gui/library/" + location));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(textUserName.getScene().getWindow());
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}