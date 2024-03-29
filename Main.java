package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) throws Exception{

        controller.start();

        AnchorPane treePane = controller.getRoot();
        TreeView<Label> treeView = controller.getTreeView();
        Button connectButton = controller.getConnectButton();
        Hyperlink refreshLink = controller.getRefreshLink();
        treePane.getChildren().add(treeView);
        treePane.getChildren().add(connectButton);
        treePane.getChildren().add(refreshLink);
        AnchorPane.setTopAnchor(treeView, 0.0);
        AnchorPane.setRightAnchor(treeView, 0.0);
        AnchorPane.setLeftAnchor(treeView, 0.0);
        AnchorPane.setBottomAnchor(treeView, 50.0);
        AnchorPane.setRightAnchor(connectButton, 0.0);
        AnchorPane.setLeftAnchor(connectButton, 0.0);
        AnchorPane.setBottomAnchor(connectButton, 0.0);
        AnchorPane.setRightAnchor(refreshLink, 20.0);
        AnchorPane.setTopAnchor(refreshLink, 0.0);
        primaryStage.setTitle("Wi-Fi");
        primaryStage.setScene(new Scene(treePane, 500, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        controller.stop();
    }
}
