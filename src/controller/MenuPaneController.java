package controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MenuPaneController implements Initializable {


    @FXML
    private MenuItem aboutItem;

    @FXML
    private MenuItem closeItem;

    @FXML
    private Menu helpMenu;

    @FXML
    private Menu fileMenu;

    public MenuItem getAboutItem() {
        return aboutItem;
    }

    public MenuItem getCloseItem() {
        return closeItem;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        configureMenu();
    }

    private void configureMenu() {
        closeItem.setOnAction(x -> Platform.exit());

        aboutItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                Parent parent = null;
                try {
                    parent = FXMLLoader.load(getClass().getResource("/view/AboutPane.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setTitle("Watch - about");
                stage.setScene(scene);
                stage.show();
            }
        });
    }
}
