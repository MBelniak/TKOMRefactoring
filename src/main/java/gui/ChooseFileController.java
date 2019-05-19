package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
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
    public MenuButton statementsMenuButton;
    @FXML
    public ComboBox destinationFiles;
    @FXML
    public ComboBox destinationClasses;
    public ComboBox refactorComboBox;

    private List<CheckMenuItem> statementsItems;

    private Stage stage;

    private Model model;

    public ChooseFileController() {
        this.fileComboBox = new ComboBox();
        this.nextButton = new Button();
        this.classesComboBox = new ComboBox();
        this.statementsMenuButton = new MenuButton();
        this.model = new Model();
        statementsItems = new ArrayList<>();
        destinationClasses = new ComboBox();
        destinationFiles = new ComboBox();
        refactorComboBox = new ComboBox();
    }

    @FXML
    private void initialize()
    {
        setUpComboBox();
    }

    private void setUpComboBox()
    {
        fileComboBox.setItems(FXCollections.observableArrayList(model.getFilesInProjectDirectory()));
        refactorComboBox.setItems(FXCollections.observableArrayList(model.getRefactors()));
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
            return classPath.toString();
        }).collect(Collectors.toList())));
    }

    public void sourceClassChanged(ActionEvent actionEvent) {
        //fill in statements in menu button
        String chosenOption = (String)classesComboBox.getValue();
        List<Statement> statements = model.getStatementsInFileInClass((String)fileComboBox.getValue(), chosenOption);
        if(statements == null)
            return;
        List<String> result = statements.stream()
                            .map(stmnt -> stmnt.getStatementType() + ": " + stmnt.getName() + stmnt.getExtraInfo())
                            .collect(Collectors.toList());
        statementsItems.clear();
        FXCollections.observableArrayList(result).forEach(e-> statementsItems.add(new CheckMenuItem(e)));
        statementsMenuButton.getItems().setAll(statementsItems);

    }


    public void destFileChanged(ActionEvent actionEvent) {
    }

    public void refactorComboBoxChanged(ActionEvent actionEvent) {
    }
}
