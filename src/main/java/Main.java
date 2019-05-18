import gui.ChooseFileController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    private AnchorPane rootView;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Refactoring");

            FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/chooseFile.fxml"));
            rootView = loader.load();

            ChooseFileController controller = loader.getController();
            controller.setStage(primaryStage);

            primaryStage.setScene(new Scene(rootView));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}











//        try {
//            scanner.bindFile(new File("src/test/java/ParserTestSuite/StatementsTests/testFiles/shouldParseInterfaceStatements.txt"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            parser.parseFile();
//        } catch (ParsingException e) {
//            e.printStackTrace();
//        }