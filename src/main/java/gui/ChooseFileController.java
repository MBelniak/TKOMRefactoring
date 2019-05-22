package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.filesManagement.FileSource;
import model.modelClass.Model;
import model.refactor.Representation;
import model.refactor.Statement;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static model.refactor.Refactor.PROJECT_DIRECTORY;

public class ChooseFileController {

    @FXML
    public ComboBox sourceFilesComboBox;
    @FXML
    public Button refactorButton;
    @FXML
    public ComboBox classesComboBox;
    @FXML
    public ComboBox destinationClasses;
    @FXML
    public ComboBox refactorComboBox;
    @FXML
    public Pane mainPane;
    @FXML
    public CheckComboBox statementsComboBox;
    @FXML
    public Pane checkBoxPane;
    @FXML
    public TextArea sourceClassTextArea;
    @FXML
    public TextArea destClassTextArea;

    private ObservableList<String> statementsItems;

    private Stage stage;

    private Model model;

    private String chosenRefactor;

    public ChooseFileController() {
        this.sourceFilesComboBox = new ComboBox();
        this.refactorButton = new Button();
        this.classesComboBox = new ComboBox();
        this.model = new Model();
        this.statementsItems = FXCollections.observableArrayList(new ArrayList<>());
        this.statementsComboBox = new CheckComboBox(statementsItems);
        this.destinationClasses = new ComboBox();
        this.refactorComboBox = new ComboBox();
        this.checkBoxPane = new Pane();
        this.sourceClassTextArea = new TextArea();
        this.destClassTextArea = new TextArea();
    }

    @FXML
    private void initialize()
    {
        setUpComboBox();
        refactorButton.setDisable(true);
        checkBoxPane.getChildren().add(statementsComboBox);
        statementsComboBox.setLayoutY(10);
        statementsComboBox.setLayoutX(0);
        statementsComboBox.setMaxWidth(statementsComboBox.getParent().getLayoutBounds().getWidth());
    }

    private void setUpComboBox()
    {
        sourceFilesComboBox.setItems(FXCollections.observableArrayList(model.getFilesInProjectDirectory()));
        refactorComboBox.setItems(FXCollections.observableArrayList(model.getRefactors()));
    }

    public void refactorButtonClicked(ActionEvent actionEvent) {
        switch (chosenRefactor)
        {
            case Model.PULL_UP:
            {
                List<Integer> chosenIndeces = new ArrayList<>();
                for(int i = 0; i < statementsComboBox.getItems().size(); i++)
                    if(statementsComboBox.getCheckModel().isChecked(i))
                        chosenIndeces.add(i);

                List<String> warnings = model.checkForPullUpWarnings((String) sourceFilesComboBox.getValue(), (String) classesComboBox.getValue(),
                        chosenIndeces, (String)destinationClasses.getValue());
                if(!warnings.isEmpty())
                {
                    String warning = prepareWarning(warnings);
                    Alert alert = new Alert(Alert.AlertType.WARNING, null, ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText("Confirm pulling up");
                    TextArea text = new TextArea(warning);
                    ScrollPane scroll = new ScrollPane();
                    scroll.setContent(text);
                    scroll.setMaxHeight(500);
                    alert.getDialogPane().setContent(scroll);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        model.doPullUp((String) sourceFilesComboBox.getValue(), (String) classesComboBox.getValue(),
                                chosenIndeces, (String) destinationClasses.getValue());
                    }
                }
                else {
                    model.doPullUp((String) sourceFilesComboBox.getValue(), (String) classesComboBox.getValue(),
                            chosenIndeces, (String) destinationClasses.getValue());
                }
            }
            case Model.PUSH_DOWN:
            {

            }
            case Model.DELEGATE:
            {

            }
            default:
            {

            }
        }
        updateSourceFileTextArea();
        updateDestFileTextArea();
        updateStatements();
    }

    private String prepareWarning(List<String> warnings) {
        StringBuilder result = new StringBuilder();
        result.append("Found some problems:").append("\n");
        warnings.forEach(warning -> result.append(warning).append("\n"));
        result.append("Do you want to continue?");
        return result.toString();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void sourceFileChanged(ActionEvent actionEvent) {
        statementsComboBox.getItems().clear();
        destinationClasses.getItems().clear();
        refactorButton.setDisable(true);
        updateSourceClasses();
        updateSourceFileTextArea();
        updateDestFileTextArea();
    }

    private void updateSourceFileTextArea() {
        String chosenSourceFile = (String)sourceFilesComboBox.getValue();
        try {
            sourceClassTextArea.setText(FileSource.readWholeFile(PROJECT_DIRECTORY+chosenSourceFile));
        } catch (IOException e) {
            e.printStackTrace();
            sourceClassTextArea.setText("");
        }
    }

    private void updateSourceClasses() {
        String chosenSourceFile = (String) sourceFilesComboBox.getValue();
        List<Representation> representations = model.getRepresentationsInFile(chosenSourceFile);
        classesComboBox.setItems(FXCollections.observableArrayList(representations.stream().map(rep -> {
            StringBuilder classPath = new StringBuilder();
            rep.getOuterClassesOrInterfaces().forEach(name -> classPath.append(name).append("."));
            classPath.append(rep.getName());
            return classPath.toString();
        }).collect(Collectors.toList())));
    }

    public void sourceClassChanged(ActionEvent actionEvent) {
        refactorButton.setDisable(true);
        updateStatements();
        updateDestinationClasses();
        updateDestFileTextArea();
    }

    private void updateStatements() {
        //fill in statements in menu button
        String chosenClass = (String)classesComboBox.getValue();
        List<Statement> statements = model.getStatementsInFileInClass((String)sourceFilesComboBox.getValue(), chosenClass);
        if(statements == null)
            return;
        List<String> result = statements.stream()
                .map(stmnt -> stmnt.getStatementType() + ": " + stmnt.getAccessModifier() + " " + stmnt.getName() + stmnt.getExtraInfo())
                .collect(Collectors.toList());
        statementsItems.clear();
        statementsItems.addAll(FXCollections.observableArrayList(result));
    }

    public void refactorComboBoxChanged(ActionEvent actionEvent) {
        refactorButton.setDisable(true);
        chosenRefactor = (String) refactorComboBox.getValue();
        updateStatements();
        updateDestinationClasses();
        updateDestFileTextArea();
    }

    private void updateDestinationClasses() {
        destinationClasses.getItems().clear();
        String chosenClass = (String)classesComboBox.getValue();
        destinationClasses.setItems(FXCollections.observableArrayList(model.getDestClassesFor((String) sourceFilesComboBox.getValue(), chosenClass, chosenRefactor)));
    }

    public void destinationClassesChanged(ActionEvent actionEvent) {
        refactorButton.setDisable(false);
        updateDestFileTextArea();
    }

    private void updateDestFileTextArea() {
        String chosenFile = (String)sourceFilesComboBox.getValue();
        String chosenClass = (String)classesComboBox.getValue();
        String chosenDestClass = model.getDestFileNameForClasses(chosenClass, (String)destinationClasses.getValue(), chosenFile);
        try {
            destClassTextArea.setText(FileSource.readWholeFile(PROJECT_DIRECTORY+chosenDestClass));
        } catch (IOException e) {
            e.printStackTrace();
            destClassTextArea.setText("");
        }
    }
}



