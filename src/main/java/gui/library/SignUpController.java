package gui.library;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;


public class SignUpController {
    public TextField textFirstName;
    public TextField textLastName;
    public TextField textPin;
    public TextField textUserId;
    public Label lblWarning;

    /**
     * Create a user in the member database with given first name, last name, PIN, and user ID
     */
    public void createUser(ActionEvent actionEvent) {
        String firstName = textFirstName.getText();
        String lastName = textLastName.getText();
        String pin = textPin.getText();
        String id = textUserId.getText();
        if(checkUser(id)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("This User ID already exists!");
            alert.setContentText("Please change it and try again");
            alert.showAndWait();
        }else{
            addUser(firstName, lastName, pin, id);
            textFirstName.clear();
            textLastName.clear();
            textPin.clear();
            textUserId.clear();
            Stage stage = (Stage) textFirstName.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Add a user into the table in the member database
     * @param firstName User first name as a string
     * @param lastName User last name as a string
     * @param pinNumber User pin as a string
     * @param userId User id as a string
     */
    public static void addUser(String firstName, String lastName, String pinNumber, String userId){
        String qu = "INSERT INTO "+ MemberHandler.tableName +" (firstName, lastName, pinNumber, userId) VALUES (" +
                "'" + firstName + "'," +
                "'" + lastName + "'," +
                "'" + pinNumber + "'," +
                "'" + userId + "')";
        Main.memberHandler.execAction(qu);
    }

    /**
     * Check if User with the same User ID already exist.
     * @param tempId User ID as a string
     * @return True or False
     */
    public static boolean checkUser(String tempId) {
        String qu = "SELECT * FROM MEMBER";
        ResultSet resultSet = Main.memberHandler.execQuery(qu);
        try{
            while (resultSet.next()){
                String id = resultSet.getString("userId");
                if(tempId.equals(id)){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
