package code;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author Ronith
 */
public class Token_Ring extends Application {
    BorderPane root_pane = new BorderPane();
    HBox buttons_holder = new HBox();
    Button inform = new Button("Inform");
    Button proxy = new Button("Proxy");
    Button replication = new Button("Replication");

    @Override
    public void start(Stage primaryStage) {
        buttons_holder.setSpacing(100);
        buttons_holder.getChildren().add(inform);
        buttons_holder.getChildren().add(proxy);
        buttons_holder.getChildren().add(replication);
        buttons_holder.setAlignment(Pos.CENTER);
        root_pane.setCenter(buttons_holder);
        //inform button press
        inform.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //clear the borderpane
                root_pane.setCenter(null);
                root_pane.setRight(null);
                InformPane ip = new InformPane();
                root_pane.setCenter(ip.pn);
                root_pane.setRight(ip.label_box);
                root_pane.setBottom(buttons_holder);
            }
        });
        // replication button press
        replication.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //clear the borderpane
                root_pane.setCenter(null);
                root_pane.setRight(null);
                Replication_Pane rp = new Replication_Pane();
                root_pane.setCenter(rp);
                root_pane.setBottom(buttons_holder);
            }
        });
        //proxy button press
       proxy.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //clear the borderpane
                root_pane.setCenter(null);
                root_pane.setRight(null);
                ProxyPane pp = new ProxyPane();
                root_pane.setCenter(pp.pn);
                root_pane.setRight(pp.label_box);
                root_pane.setBottom(buttons_holder);
            }
        });
        Scene scene = new Scene(root_pane, 700, 500);
        primaryStage.setTitle("Token Ring");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
