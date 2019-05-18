package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import model.modelClass.Model;
import model.refactor.Representation;
import model.refactor.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseFileController {

    @FXML
    public ComboBox fileComboBox;
    @FXML
    public Button nextButton;
    @FXML
    public ComboBox classesComboBox;
    @FXML
    public ComboBox statementsComboBox;

    private Stage stage;

    private Model model;

    public ChooseFileController() {
        this.fileComboBox = new ComboBox();
        this.nextButton = new Button();
        this.classesComboBox = new ComboBox();
        this.statementsComboBox = new ComboBox();
        this.model = new Model();
    }

    @FXML
    private void initialize()
    {
        setUpComboBox();
    }

    private void setUpComboBox()
    {
        fileComboBox.setItems(FXCollections.observableArrayList(model.getFilesInProjectDirectory()));
    }

    public void nextButtonClicked(ActionEvent actionEvent) {

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void sourceFileChanged(ActionEvent actionEvent) {
        String chosenOption = (String)fileComboBox.getValue();
        List<Representation> representations = model.getRepresentationsInFile(chosenOption);
        classesComboBox.setItems(FXCollections.observableArrayList(representations.stream().map(rep -> {
            StringBuilder classPath = new StringBuilder();
            rep.getOuterClassesOrInterfaces().forEach(name -> classPath.append(name).append("."));
            classPath.append(rep.getName());
            return classPath;
        }).collect(Collectors.toList())));
    }

    public void sourceClassChanged(ActionEvent actionEvent) {
        String chosenOption = (String)fileComboBox.getValue();
        List<Statement> statements = model.getStatementsInFileInClass((String)fileComboBox.getValue(), chosenOption);
    }

    public void statementChanged(ActionEvent actionEvent) {
    }
}
