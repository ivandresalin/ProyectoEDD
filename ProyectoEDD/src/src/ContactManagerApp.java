/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.io.File;
import java.io.IOException;

public class ContactManagerApp extends Application {
    private CircularDoubleLinkedList<Contacto> contacts = new CircularDoubleLinkedList<>();
    private ListView<String> contactListView = new ListView<>();
    private VBox detailsPanel = new VBox(10);
    private ImageView photoView = new ImageView();
    private Contacto currentContact = null;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        VBox leftPanel = new VBox(10);
        HBox navigationButtons = new HBox(10);

        // Botones de navegación de contactos
        Button nextButton = new Button("Siguiente Contacto");
        Button prevButton = new Button("Anterior Contacto");
        nextButton.setOnAction(e -> navigateNext());
        prevButton.setOnAction(e -> navigatePrevious());
        navigationButtons.getChildren().addAll(prevButton, nextButton);

        // Botones de CRUD para contactos
        Button createButton = new Button("Crear Contacto");
        Button editButton = new Button("Editar Contacto");
        Button deleteButton = new Button("Eliminar Contacto");
        createButton.setOnAction(e -> showCreateContactForm());
        editButton.setOnAction(e -> showEditContactForm());
        deleteButton.setOnAction(e -> deleteSelectedContact());

        leftPanel.getChildren().addAll(contactListView, navigationButtons, createButton, editButton, deleteButton);

        // Panel de detalles (fotos)
        Label photoLabel = new Label("Foto del Contacto");
        photoView.setFitWidth(200);
        photoView.setFitHeight(200);
        Button addPhotoButton = new Button("Agregar Foto");
        Button editPhotoButton = new Button("Editar Foto");
        Button deletePhotoButton = new Button("Eliminar Foto");

        addPhotoButton.setOnAction(e -> showAddPhotoForm(primaryStage));
        editPhotoButton.setOnAction(e -> showEditPhotoForm(primaryStage));
        deletePhotoButton.setOnAction(e -> deleteCurrentPhoto());

        detailsPanel.getChildren().addAll(photoLabel, photoView, addPhotoButton, editPhotoButton, deletePhotoButton);

        root.setLeft(leftPanel);
        root.setCenter(detailsPanel);

        // Seleccionar contacto
        contactListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                for (Contacto contact : contacts) {
                    if ((contact.getId() + " (" + contact.getType() + ")").equals(newSelection)) {
                        currentContact = contact;
                        updatePhotoView();
                        break;
                    }
                }
            }
        });

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Gestión de Contactos");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Cargar contactos
        try {
            contacts = PersistenceManager.loadContacts("contacts.dat");
            updateContactListView();
        } catch (Exception e) {
            // Archivo no existe
        }
    }

    private void navigateNext() {
        Contacto contact = contacts.siguiente();
        if (contact != null) {
            currentContact = contact;
            contactListView.getSelectionModel().select(contact.getId() + " (" + contact.getType() + ")");
            updatePhotoView();
        }
    }

    private void navigatePrevious() {
        Contacto contact = contacts.anterior();
        if (contact != null) {
            currentContact = contact;
            contactListView.getSelectionModel().select(contact.getId() + " (" + contact.getType() + ")");
            updatePhotoView();
        }
    }

    private void updatePhotoView() {
        if (currentContact != null && currentContact.getCurrentPhoto() != null) {
            try {
                File file = new File(currentContact.getCurrentPhoto().getPath());
                Image image = new Image(file.toURI().toString());
                photoView.setImage(image);
            } catch (Exception e) {
                photoView.setImage(null);
                System.out.println("Error al cargar la imagen: " + e.getMessage());
            }
        } else {
            photoView.setImage(null);
        }
    }

    private void showCreateContactForm() {
        Stage formStage = new Stage();
        VBox form = new VBox(10);
        TextField idField = new TextField();
        ChoiceBox<String> typeChoice = new ChoiceBox<>();
        typeChoice.getItems().addAll("persona", "empresa");
        TextField nameField = new TextField();
        TextField lastNameField = new TextField();
        Button saveButton = new Button("Guardar");

        saveButton.setOnAction(e -> {
            Contacto contact;
            if (typeChoice.getValue().equals("persona")) {
                contact = Contacto.createPerson(idField.getText(), nameField.getText(), lastNameField.getText(), LocalDate.now());
            } else {
                contact = Contacto.createCompany(idField.getText(), nameField.getText(), "");
            }
            contacts.add(contact);
            try {
                PersistenceManager.saveContacts(contacts, "contacts.dat");
                updateContactListView();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            formStage.close();
        });

        form.getChildren().addAll(
            new Label("ID"), idField,
            new Label("Tipo"), typeChoice,
            new Label("Nombre"), nameField,
            new Label("Apellido (solo persona)"), lastNameField,
            saveButton
        );
        formStage.setScene(new Scene(form, 300, 400));
        formStage.show();
    }

    private void showEditContactForm() {
        if (currentContact == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione un contacto.");
            alert.showAndWait();
            return;
        }
        // Persona 1: Implementar formulario de edición
        System.out.println("Editar: " + currentContact.getId());
    }

    private void deleteSelectedContact() {
        if (currentContact == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione un contacto.");
            alert.showAndWait();
            return;
        }
        contacts.remove(currentContact);
        try {
            PersistenceManager.saveContacts(contacts, "contacts.dat");
            updateContactListView();
            currentContact = null;
            updatePhotoView();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showAddPhotoForm(Stage primaryStage) {
        if (currentContact == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione un contacto.");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            currentContact.addPhoto(file.getAbsolutePath());
            try {
                PersistenceManager.saveContacts(contacts, "contacts.dat");
                updatePhotoView();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showEditPhotoForm(Stage primaryStage) {
        if (currentContact == null || currentContact.getCurrentPhoto() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione un contacto con una foto.");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Nueva Foto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            currentContact.editPhoto(currentContact.getCurrentPhoto().getPath(), file.getAbsolutePath());
            try {
                PersistenceManager.saveContacts(contacts, "contacts.dat");
                updatePhotoView();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteCurrentPhoto() {
        if (currentContact == null || currentContact.getCurrentPhoto() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione un contacto con una foto.");
            alert.showAndWait();
            return;
        }
        currentContact.removePhoto(currentContact.getCurrentPhoto().getPath());
        try {
            PersistenceManager.saveContacts(contacts, "contacts.dat");
            updatePhotoView();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateContactListView() {
        contactListView.getItems().clear();
        for (Contacto contact : contacts) {
            contactListView.getItems().add(contact.getId() + " (" + contact.getType() + ")");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}