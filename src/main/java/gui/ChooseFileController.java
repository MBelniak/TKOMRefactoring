package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.modelClass.Model;
import model.refactor.Representation;
import model.refactor.Statement;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseFileController {

    @FXML
    public ComboBox filesComboBox;
    @FXML
    public Button nextButton;
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

    private ObservableList<String> statementsItems;

    private Stage stage;

    private Model model;

    private String choosenRefactor;

    public ChooseFileController() {
        this.filesComboBox = new ComboBox();
        this.nextButton = new Button();
        this.classesComboBox = new ComboBox();
        this.model = new Model();
        this.statementsItems = FXCollections.observableArrayList(new ArrayList<>());
        this.statementsComboBox = new CheckComboBox();
        this.destinationClasses = new ComboBox();
        this.refactorComboBox = new ComboBox();
        this.checkBoxPane = new Pane();
    }

    @FXML
    private void initialize()
    {
        setUpComboBox();
        nextButton.setDisable(true);
        checkBoxPane.getChildren().add(statementsComboBox);
        statementsComboBox.setLayoutY(10);
        statementsComboBox.setLayoutX(25);
        statementsComboBox.setMaxWidth(200);
    }

    private void setUpComboBox()
    {
        filesComboBox.setItems(FXCollections.observableArrayList(model.getFilesInProjectDirectory()));
        refactorComboBox.setItems(FXCollections.observableArrayList(model.getRefactors()));
    }

    public void nextButtonClicked(ActionEvent actionEvent) {
        switch (choosenRefactor)
        {
            case "Pull up":
            {
                List<Integer> choosenIndeces = new ArrayList<>();
                for(int i = 0; i < statementsComboBox.getItems().size(); i++)
                    if(statementsComboBox.getCheckModel().isChecked(i))
                        choosenIndeces.add(i);

                List<String> warnings = model.checkForPullUpWarnings((String)filesComboBox.getValue(), (String) classesComboBox.getValue(),
                        choosenIndeces, (String)destinationClasses.getValue());
                if(!warnings.isEmpty())

                model.doPullUp((String)filesComboBox.getValue(), (String) classesComboBox.getValue(),
                        choosenIndeces, (String)destinationClasses.getValue());
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void sourceFileChanged(ActionEvent actionEvent) {
        statementsComboBox.getItems().clear();
        destinationClasses.getItems().clear();
        nextButton.setDisable(true);
        String chosenOption = (String) filesComboBox.getValue();
        List<Representation> representations = model.getRepresentationsInFile(chosenOption);
        classesComboBox.setItems(FXCollections.observableArrayList(representations.stream().map(rep -> {
            StringBuilder classPath = new StringBuilder();
            rep.getOuterClassesOrInterfaces().forEach(name -> classPath.append(name).append("."));
            classPath.append(rep.getName());
            return classPath.toString();
        }).collect(Collectors.toList())));
    }

    public void sourceClassChanged(ActionEvent actionEvent) {
        nextButton.setDisable(true);
        //fill in statements in menu button
        String chosenClass = (String)classesComboBox.getValue();
        List<Statement> statements = model.getStatementsInFileInClass((String) filesComboBox.getValue(), chosenClass);
        if(statements == null)
            return;
        List<String> result = statements.stream()
                            .map(stmnt -> stmnt.getStatementType() + ": " + stmnt.getAccessModifier() + " " + stmnt.getName() + stmnt.getExtraInfo())
                            .collect(Collectors.toList());
        statementsItems.clear();
        statementsItems.addAll(FXCollections.observableArrayList(result));
        statementsComboBox.getItems().setAll(statementsItems);
    }

    public void refactorComboBoxChanged(ActionEvent actionEvent) {
        choosenRefactor = (String) refactorComboBox.getValue();
        String chosenClass = (String)classesComboBox.getValue();
        List<Statement> statements = model.getStatementsInFileInClass((String) filesComboBox.getValue(), chosenClass);
        if(statements == null)
            return;
        List<String> result = statements.stream()
                .map(stmnt -> stmnt.getStatementType() + ": " + stmnt.getAccessModifier() + " " + stmnt.getName() + stmnt.getExtraInfo())
                .collect(Collectors.toList());
        statementsItems.clear();
        statementsItems.addAll(FXCollections.observableArrayList(result));

        statementsComboBox.getItems().setAll(statementsItems);
        destinationClasses.setItems(FXCollections.observableArrayList(model.getBasesFor((String)filesComboBox.getValue(), chosenClass)));
    }

    public void destinationClassesChanged(ActionEvent actionEvent) {
        nextButton.setDisable(false);
    }
}



