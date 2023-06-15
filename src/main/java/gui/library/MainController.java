package gui.library;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    public TableView bookTable;
    public TableColumn titleCol;
    public TableColumn authorCol;
    public TableColumn genreCol;
    public TableColumn quantityCol;
    public TableColumn idCol;
    public ObservableList<Book> books = FXCollections.observableArrayList();
    public ObservableList<Book> borrowed = FXCollections.observableArrayList();
    public TextField textTitle;
    public TextField textAuthor;
    public TextField textGenre;
    public TextField textQuantity;
    public TableView borrowedBook;
    public TableColumn colTitle;
    public TableColumn colAuthor;
    public TableColumn colGenre;
    public TableColumn colQuantity;
    public TableColumn colId;
    public Button btnReturn;
    public Button btnBorrow;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCol();
        loadData();
    }

    private void loadData() {
        borrowed.clear();
        books.clear();
        readLibrary();
        readBorrowedBook();
        bookTable.getItems().setAll(books);
        borrowedBook.getItems().setAll(borrowed);
        btnBorrow.setDisable(true);
        btnReturn.setDisable(true);
    }

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("borrowedQuantity"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

    }

    /**
     * Read books from the library database and put them as book objects into the books list
     */
    public void readLibrary(){
        String qu = "SELECT * FROM LIBRARY";
        ResultSet resultSet = Main.libraryHandler.execQuery(qu);
        try{
            while (resultSet.next()){
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String genre = resultSet.getString("genre");
                String quantity = resultSet.getString("quantity");
                String id = resultSet.getString("id");
                books.add(new Book(title,author,genre, quantity,id,"0"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read books from the member database and put them as book objects into the borrowed list
     */
    public void readBorrowedBook(){
        String qu = "SELECT * FROM MEMBER";
        ResultSet resultSet = Main.memberHandler.execQuery(qu);
        try{
            while (resultSet.next()){
                if(resultSet.getString("title") != null && resultSet.getString("userId").equals(LoginController.id)){
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    String genre = resultSet.getString("genre");
                    String borrowedQuantity = resultSet.getString("quantity");
                    String id = resultSet.getString("id");
                    borrowed.add(new Book(title,author,genre, "1",id, borrowedQuantity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a book into the library database by inputting the title, author, genre, quantity of a book.
     */
    public void addBook(ActionEvent actionEvent) {
        String title = textTitle.getText();
        String author = textAuthor.getText();
        String genre = textGenre.getText();
        String quantity = textQuantity.getText();
        String id = String.valueOf(bookTable.getItems().size() +1);
        String qu = "INSERT INTO "+ LibraryHandler.tableName +" (title, author, genre, quantity, id) VALUES (" +
                "'" + title + "'," +
                "'" + author + "'," +
                "'" + genre + "'," +
                "'" + quantity + "'," +
                "'" + id + "')";
        Main.libraryHandler.execAction(qu);
        loadData();
        textTitle.clear();
        textAuthor.clear();
        textGenre.clear();
        textQuantity.clear();
    }

    /**
     * Borrow a book from the library database and add it to the user database.
     */
    public void borrowBook(ActionEvent actionEvent) {
        Book temp = (Book) bookTable.getSelectionModel().getSelectedItem();
        String qu = "SELECT * FROM MEMBER";
        String bo = "SELECT * FROM LIBRARY";
        ResultSet resultSet = Main.memberHandler.execQuery(qu);
        ResultSet resultSet1 = Main.libraryHandler.execQuery(bo);
        boolean hasBook = true;
        try{
            while (resultSet.next()){
                String id = resultSet.getString("userId");
                String title = resultSet.getString("title");
                //Check if the user has borrowed a book already
                if(title == null && id.equals(LoginController.id)){
                    hasBook = false;
                    resultSet.updateString("title", temp.getTitle());
                    resultSet.updateString("author", temp.getAuthor());
                    resultSet.updateString("genre", temp.getGenre());
                    resultSet.updateString("quantity", "1");
                    resultSet.updateString("id", temp.getId());
                    resultSet.updateRow();
                    //Change the quantity of the book
                    while(resultSet1.next()){
                        String bookId = resultSet1.getString("id");
                        if(temp.getId().equals(bookId)){
                            String quantity = Integer.toString(Integer.parseInt(resultSet1.getString("quantity"))-1);
                            resultSet1.updateString("quantity", quantity);
                            resultSet1.updateRow();
                        }
                    }
                }
            }

            loadData();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(hasBook){
            //Pop up window to warn if the user wants to borrow more than one book
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please return the book before borrowing another");
            alert.setHeaderText("You are already borrowing a book!");
            alert.showAndWait();
            loadData();
        }
    }

    /**
     * Enables the Return button
     */
    public void getBorrowedBook(MouseEvent mouseEvent) {
        btnReturn.setDisable(false);
    }
    /**
     * Enables the Borrow button
     */
    public void getLibraryBook(MouseEvent mouseEvent){
        btnBorrow.setDisable(false);
    }
    /**
     * Return a book from the user database and add it back to the library database.
     */
    public void returnBook(ActionEvent actionEvent) {
        Book temp = (Book) borrowedBook.getSelectionModel().getSelectedItem();
        String qu = "SELECT * FROM MEMBER";
        String bo = "SELECT * FROM LIBRARY";
        ResultSet resultSet = Main.memberHandler.execQuery(qu);
        ResultSet resultSet1 = Main.libraryHandler.execQuery(bo);
        try{
            while (resultSet.next()){
                String id = resultSet.getString("userId");
                if(id.equals(LoginController.id)){
                    resultSet.updateString("title", null);
                    resultSet.updateString("author", null);
                    resultSet.updateString("genre", null);
                    resultSet.updateString("quantity", null);
                    resultSet.updateString("id", null);
                    resultSet.updateRow();
                    //Change the quantity of the book
                    while(resultSet1.next()){
                        String bookId = resultSet1.getString("id");
                        if(bookId.equals(temp.getId())){
                            String quantity = Integer.toString(Integer.parseInt(resultSet1.getString("quantity"))+1);
                            resultSet1.updateString("quantity", quantity);
                            resultSet1.updateRow();
                        }
                    }
                    loadData();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows the user to log off and go back to the login page.
     */
    public void logOut(ActionEvent actionEvent) {
        Stage stage = (Stage) btnBorrow.getScene().getWindow();
        stage.close();
        loadWindow("login.fxml","Login");
    }

    /**
     * Load a new window with given location and title
     * @param location the name of the fxml file
     * @param title the title of the new window
     */
    private void loadWindow(String location, String title) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(location));
            Stage stage = new Stage(StageStyle.DECORATED);
            Scene scene = new Scene(fxmlLoader.load(), 600, 350);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Book{
        //Fields
        private final SimpleStringProperty title;
        private final SimpleStringProperty author;
        private final SimpleStringProperty genre;
        private final SimpleStringProperty quantity;
        private final SimpleStringProperty id;
        private final SimpleStringProperty borrowedQuantity;

        //Constructor
        public Book(String title, String author, String genre, String quantity, String id, String borrowedQuantity) {
            this.title = new SimpleStringProperty (title);
            this.author = new SimpleStringProperty (author);
            this.genre = new SimpleStringProperty(genre);
            this.quantity = new SimpleStringProperty(quantity);
            this.id = new SimpleStringProperty(id);
            this.borrowedQuantity = new SimpleStringProperty(borrowedQuantity);
        }

        //Getters
        public String getTitle() {
            return title.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getGenre() {
            return genre.get();
        }

        public String getQuantity() {
            return quantity.get();
        }

        public String getId() {
            return id.get();
        }

        public String getBorrowedQuantity() {
            return borrowedQuantity.get();
        }
    }
}
