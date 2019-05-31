package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.filesManagement.FileSource;
import model.modelClass.Model;
import model.refactor.Representation;
import model.refactor.Statement;
import model.scanner.Scanner;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static model.refactor.Refactor.PROJECT_DIRECTORY;

public class ChooseFileController {

    @FXML
    public ComboBox sourceFilesComboBox;
    @FXML
    public Button refactorButton;
    @FXML
    public ComboBox sourceClasses;
    @FXML
    public ComboBox destinationClasses;
    @FXML
    public ComboBox refactorComboBox;
    @FXML
    public CheckComboBox statementsComboBox;
    @FXML
    public Pane checkBoxPane;
    @FXML
    public TextArea sourceClassTextArea;
    @FXML
    public TextArea destClassTextArea;
    @FXML
    public Label delegateLabel;
    @FXML
    public Pane delegatePane;
    @FXML
    public TextField delegateClassNameTextField;
    @FXML
    public TextField delegateFieldNameTextField;
    @FXML
    public Label newFieldNameLabel;

    private ObservableList<String> statementsItems;

    private Stage stage;

    private Model model;

    private String chosenRefactor;
    private String chosenSourceCorI;
    private String chosenDestCorI;
    private String chosenSourceFile;
    private List<Integer> chosenStatements;

    public ChooseFileController() {
        this.sourceFilesComboBox = new ComboBox();
        this.refactorButton = new Button();
        this.sourceClasses = new ComboBox();
        this.model = new Model("src\\main\\resources\\projectFiles\\");
        this.statementsItems = FXCollections.observableArrayList(new ArrayList<>());
        this.statementsComboBox = new CheckComboBox(statementsItems);
        this.destinationClasses = new ComboBox();
        this.refactorComboBox = new ComboBox();
        this.checkBoxPane = new Pane();
        this.sourceClassTextArea = new TextArea();
        this.destClassTextArea = new TextArea();
        this.chosenStatements = new ArrayList<>();
        this.delegateClassNameTextField = new TextField();
        this.delegateFieldNameTextField = new TextField();
        this.delegatePane = new Pane();
        this.newFieldNameLabel = new Label();
        this.delegateLabel = new Label();
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
        delegatePane.setVisible(false);
    }

    private void setUpComboBox()
    {
        sourceFilesComboBox.setItems(FXCollections.observableArrayList(model.getFilesInProjectDirectory()));
    }

    public void refactorButtonClicked(ActionEvent actionEvent) {
        updateChosenStatements();
        String newFieldName = delegateFieldNameTextField.getText();
        String newClassName = delegateClassNameTextField.getText();
        String chosenCorI = chosenSourceCorI.substring(4, chosenSourceCorI.length());

        List<String> warnings = model.checkForWarnings(chosenSourceFile, chosenCorI,
                chosenStatements, chosenDestCorI, chosenRefactor, newFieldName, newClassName);

        switch (chosenRefactor)
        {
            case Model.PULL_UP:
            {
                if(chosenStatements.isEmpty())
                    return;
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
                        model.doPushOrPull(chosenSourceFile, chosenCorI,
                                chosenStatements, chosenDestCorI, chosenRefactor);
                    }
                }
                else {
                    model.doPushOrPull(chosenSourceFile, chosenCorI,
                            chosenStatements, chosenDestCorI, chosenRefactor);
                }
                break;
            }
            case Model.PUSH_DOWN:
            {
                if(chosenStatements.isEmpty())
                    return;
                if(!warnings.isEmpty())
                {
                    String warning = prepareWarning(warnings);
                    Alert alert = new Alert(Alert.AlertType.WARNING, null, ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText("Confirm pushing down");
                    TextArea text = new TextArea(warning);
                    ScrollPane scroll = new ScrollPane();
                    scroll.setContent(text);
                    scroll.setMaxHeight(500);
                    alert.getDialogPane().setContent(scroll);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        model.doPushOrPull(chosenSourceFile, chosenCorI,
                                chosenStatements, chosenDestCorI, chosenRefactor);
                    }
                }
                else {
                    model.doPushOrPull(chosenSourceFile, chosenCorI,
                            chosenStatements, chosenDestCorI, chosenRefactor);
                }
                break;
            }
            case Model.DELEGATE:
            {
                if(!validate(newClassName))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect new class name.", ButtonType.CLOSE);
                    alert.setHeaderText("Input error.");
                    alert.showAndWait();
                    return;
                }
                if(!validate(newFieldName))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect new field name.", ButtonType.CLOSE);
                    alert.setHeaderText("Input error.");
                    alert.showAndWait();
                    return;
                }
                if(!warnings.isEmpty())
                {
                    String warning = prepareWarning(warnings);
                    Alert alert = new Alert(Alert.AlertType.WARNING, null, ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText("Confirm replacing inheritance to delegation");
                    TextArea text = new TextArea(warning);
                    ScrollPane scroll = new ScrollPane();
                    scroll.setContent(text);
                    scroll.setMaxHeight(500);
                    alert.getDialogPane().setContent(scroll);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        model.doDelegate(chosenSourceFile, chosenCorI,
                                chosenStatements, chosenDestCorI, newFieldName, newClassName);
                    }
                }
                else {
                    model.doDelegate(chosenSourceFile, chosenCorI,
                            chosenStatements, chosenDestCorI, newFieldName, newClassName);
                }
                updateSourceClasses();
                updateDestinationClasses();
                break;
            }
            default:
            {

            }
        }
        updateSourceFileTextArea();
        updateDestFileTextArea();
        updateStatements();
    }

    private boolean validate(String newName) {
        if(newName == null)
            return false;
        if(!newName.matches("^[a-zA-Z][a-zA-Z0-9_]*$"))
            return false;
        return Scanner.getKeywords().get(newName) == null;
    }

    private void updateChosenStatements()
    {
        chosenStatements.clear();
        for(int i = 0; i < statementsComboBox.getItems().size(); i++)
            if(statementsComboBox.getCheckModel().isChecked(i))
                chosenStatements.add(i);
    }

    private String prepareWarning(List<String> warnings) {
        StringBuilder result = new StringBuilder();
        result.append("Found some problems:").append("\n");
        warnings.forEach(warning -> result.append(warning).append("\n"));
        result.append("Do you want to continue?");
        return result.toString();
    }

    public void sourceFileChanged(ActionEvent actionEvent) {
        statementsComboBox.getItems().clear();
        destinationClasses.getItems().clear();
        delegateClassNameTextField.setText("");
        delegateFieldNameTextField.setText("");
        refactorComboBox.getItems().clear();
        refactorButton.setDisable(true);
        chosenSourceFile = (String) sourceFilesComboBox.getValue();
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
        if(chosenSourceFile == null)
            return;
        this.sourceClasses.setItems(FXCollections.observableArrayList(model.getClassesListWithType(chosenSourceFile)));
    }

    public void sourceClassChanged(ActionEvent actionEvent) {
        refactorButton.setDisable(true);
        chosenSourceCorI = ((String)sourceClasses.getValue());
        if(chosenSourceCorI == null)
            return;
        delegateClassNameTextField.setText("");
        delegateFieldNameTextField.setText("");
        updateRefactorComboCox();
        updateStatements();
        updateDestinationClasses();
        updateDestFileTextArea();
    }

    private void updateRefactorComboCox() {
        if(chosenSourceCorI == null || chosenSourceCorI.equals(""))
            return;
        if(chosenSourceCorI.substring(1,2).equals("I")) {
            List<String> refacts = Arrays.asList(Model.PULL_UP, Model.PUSH_DOWN);
            refactorComboBox.setItems(FXCollections.observableArrayList(refacts));
        }
        else
            refactorComboBox.setItems(FXCollections.observableArrayList(model.getRefactors()));
    }

    private void updateStatements() {

        statementsComboBox.getCheckModel().clearChecks();
        if(chosenRefactor == null) {
            statementsItems.clear();
            return;
        }

        List<Statement> statements;
        String chosenCorI = chosenSourceCorI.substring(4, chosenSourceCorI.length());
        //fill in statements in menu button
        if(chosenRefactor.equals(Model.PUSH_DOWN) || chosenRefactor.equals(Model.PULL_UP))
            statements = model.getStatementsInFileInClass(chosenSourceFile, chosenCorI);
        else
        {
            statements = model.getStatementsWithFileAndSourceClassForDelegation(chosenSourceFile, chosenCorI, chosenDestCorI);
        }

        List<String> result = statements.stream()
                .map(stmnt -> stmnt.getStatementType() + ": " + stmnt.getAccessModifier() + " " + stmnt.getName() + stmnt.getExtraInfo())
                .collect(Collectors.toList());
        statementsItems.clear();
        statementsItems.addAll(FXCollections.observableArrayList(result));
    }

    public void refactorComboBoxChanged(ActionEvent actionEvent) {
        refactorButton.setDisable(true);
        chosenRefactor = (String) refactorComboBox.getValue();
        if(chosenRefactor == null || chosenRefactor.equals(""))
        {
            delegatePane.setVisible(false);
            delegateFieldNameTextField.setText("");
            delegateClassNameTextField.setText("");
            return;
        }
        if(chosenRefactor.equals(Model.PULL_UP) || chosenRefactor.equals(Model.PUSH_DOWN))
        {
            delegatePane.setVisible(false);
            delegateFieldNameTextField.setText("");
            delegateClassNameTextField.setText("");
        }
        else
        {
            delegatePane.setVisible(true);
        }
        updateStatements();
        updateDestinationClasses();
        updateDestFileTextArea();
    }

    private void updateDestinationClasses() {
        if(chosenRefactor == null)
            return;
        destinationClasses.getItems().clear();
        String chosenCorI = chosenSourceCorI.substring(4, chosenSourceCorI.length());
        List<String> toSet = model.getBaseOrSubClassesWithTypeFor(
                chosenSourceFile, chosenCorI, chosenRefactor);
        if(toSet.isEmpty())
            return;
        destinationClasses.setItems(FXCollections.observableArrayList(toSet));
    }

    public void destinationClassChanged(ActionEvent actionEvent) {
        refactorButton.setDisable(false);
        chosenDestCorI = ((String)destinationClasses.getValue());
        if(chosenDestCorI!=null)
            chosenDestCorI = chosenDestCorI.substring(4, chosenDestCorI.length());
        updateDestFileTextArea();
        updateStatements();
    }

    private void updateDestFileTextArea() {
        if(chosenSourceCorI == null || chosenSourceCorI.equals(""))
            return;
        String chosenCorI = chosenSourceCorI.substring(4, chosenSourceCorI.length());
        Representation potentialFoundRepresentation = model.getClassOrInterfaceFor(chosenCorI,
                                                    chosenDestCorI, chosenSourceFile, chosenRefactor);
        if(potentialFoundRepresentation==null)
            return;

        String destFile = potentialFoundRepresentation.getFilePath();

        try {
            destClassTextArea.setText(FileSource.readWholeFile(PROJECT_DIRECTORY+destFile));
        } catch (IOException e) {
            e.printStackTrace();
            destClassTextArea.setText("");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}


