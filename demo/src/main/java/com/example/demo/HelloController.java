package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HelloController {

    @FXML
    private TableView<Livre> tableView;

    @FXML
    private TableColumn<Livre, Integer> idColumn;

    @FXML
    private TableColumn<Livre, String> titleColumn;

    @FXML
    private TableColumn<Livre, String> authorColumn;

    @FXML
    private TableColumn<Livre, String> categoryColumn;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private Label totalBooksLabel;

    @FXML
    private ImageView bookIcon;

    @FXML
    private ComboBox<String> categoryField;

    private DatabaseServices databaseServices;
    private ObservableList<Livre> bookList;

    public HelloController() {
        this.databaseServices = new DatabaseServices();
        this.bookList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        bookIcon.setImage(new Image(getClass().getResourceAsStream("/livre.png")));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        tableView.setItems(bookList);
        loadBooks();
        categoryField.setItems(FXCollections.observableArrayList("Roman", "Science Fiction", "Biographie"));

        updateTotalBooksLabel();
    }

    @FXML
    private void addBook(ActionEvent event) {
        String title = titleField.getText();
        String author = authorField.getText();
        String category = categoryField.getValue();

        if (title.isEmpty() || author.isEmpty() || category.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ invalide", "SVP! Veuillez remplir toutes les informations du livre.");
            return;
        }

        Livre book = new Livre(title, author, category);
        databaseServices.addBook(book);
        loadBooks();
        clearFields();
    }

    @FXML
    private void updateBook(ActionEvent event) {
        String title = titleField.getText();
        String author = authorField.getText();
        String category = categoryField.getValue();
        Livre selectedBook = tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de sélection", "SVP! Veuillez sélectionner le livre à modifier.");
            return;
        }
        if (title.isEmpty() || author.isEmpty() || category.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ invalide", "SVP! Veuillez remplir toutes les informations du livre pour pouvoir le modifier.");
            return;
        }

        selectedBook.setTitle(title);
        selectedBook.setAuthor(author);
        selectedBook.setCategory(category);

        databaseServices.updateBook(selectedBook);
        loadBooks();
        clearFields();
    }

    @FXML
    private void deleteBook(ActionEvent event) {
        Livre selectedBook = tableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur de sélection", "Veuillez sélectionner le livre à supprimer.");
            return;
        }

        databaseServices.deleteBook(selectedBook.getId());
        loadBooks();
        clearFields();
    }

    @FXML
    public void viewAllBooks() {
        loadBooks();
    }

    @FXML
    public void viewRomanBooks() {
        ObservableList<Livre> romanBooks = FXCollections.observableArrayList(databaseServices.getBooksByCategory("Roman"));
        bookList.setAll(romanBooks);
        updateTotalBooksLabel();
    }

    @FXML
    public void viewScienceFictionBooks() {
        ObservableList<Livre> sciFiBooks = FXCollections.observableArrayList(databaseServices.getBooksByCategory("Science Fiction"));
        bookList.setAll(sciFiBooks);
        updateTotalBooksLabel();
    }

    @FXML
    public void viewBiographieBooks() {
        ObservableList<Livre> biographieBooks = FXCollections.observableArrayList(databaseServices.getBooksByCategory("Biographie"));
        bookList.setAll(biographieBooks);
        updateTotalBooksLabel();
    }

    @FXML
    private void searchBook(ActionEvent event) {
        String title = titleField.getText();
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champ invalide", "SVP! Veuillez remplir le titre du livre que vous recherchez.");
            return;
        }

        bookList.setAll(databaseServices.searchBooksByTitle(title));
        updateTotalBooksLabel();
    }

    private void loadBooks() {
        bookList.setAll(databaseServices.getAllBooks());
        tableView.refresh();
        updateTotalBooksLabel();
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        categoryField.setValue(null);
    }

    private void updateTotalBooksLabel() {
        int totalBooks = databaseServices.getTotalBooksCount();
        totalBooksLabel.setText("Total Livres : " + totalBooks);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
