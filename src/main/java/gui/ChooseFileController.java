package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.modelClass.Model;
import model.refactor.Representation;
import model.refactor.Statement;

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
    public MenuButton statementsMenuButton;
    @FXML
    public ComboBox destinationClasses;
    @FXML
    public ComboBox refactorComboBox;
    @FXML
    public AnchorPane checkBoxAnchor;

    private List<CheckMenuItem> statementsItems;

    private Stage stage;

    private Model model;

    private String choosenRefactor;

    public ChooseFileController() {
        this.filesComboBox = new ComboBox();
        this.nextButton = new Button();
        this.classesComboBox = new ComboBox();
        this.statementsMenuButton = new MenuButton();
        this.model = new Model();
        statementsItems = new ArrayList<>();
        destinationClasses = new ComboBox();
        refactorComboBox = new ComboBox();
    }

    @FXML
    private void initialize()
    {
        setUpComboBox();
        nextButton.setDisable(true);
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
                List<MenuItem> choosenStatements = statementsMenuButton.getItems();
                for(int i = 0; i < choosenStatements.size(); i++)
                    if(!choosenStatements.get(i).isDisable())
                        choosenIndeces.add(i);

                model.doPullUp((String)filesComboBox.getValue(), (String) classesComboBox.getValue(),
                        choosenIndeces, (String)destinationClasses.getValue());
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void sourceFileChanged(ActionEvent actionEvent) {
        statementsMenuButton.getItems().clear();
        destinationClasses.getItems().clear();
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
        //fill in statements in menu button
        String chosenClass = (String)classesComboBox.getValue();
        List<Statement> statements = model.getStatementsInFileInClass((String) filesComboBox.getValue(), chosenClass);
        if(statements == null)
            return;
        List<String> result = statements.stream()
                            .map(stmnt -> stmnt.getStatementType() + ": " + stmnt.getName() + stmnt.getExtraInfo())
                            .collect(Collectors.toList());
        statementsItems.clear();
        FXCollections.observableArrayList(result).forEach(e-> statementsItems.add(new CheckMenuItem(e)));
        statementsMenuButton.getItems().setAll(statementsItems);
    }

    public void refactorComboBoxChanged(ActionEvent actionEvent) {
        choosenRefactor = (String) refactorComboBox.getValue();
        String chosenClass = (String)classesComboBox.getValue();
        List<Statement> statements = model.getStatementsInFileInClass((String) filesComboBox.getValue(), chosenClass);
        if(statements == null)
            return;
        List<String> result = statements.stream()
                .map(stmnt -> stmnt.getStatementType() + ": " + stmnt.getName() + stmnt.getExtraInfo())
                .collect(Collectors.toList());
        statementsItems.clear();
        FXCollections.observableArrayList(result).forEach(e-> statementsItems.add(new CheckMenuItem(e)));
        statementsMenuButton.getItems().setAll(statementsItems);
        destinationClasses.setItems(FXCollections.observableArrayList(model.getBasesFor((String)filesComboBox.getValue(), chosenClass)));
    }

    public void destinationClassesChanged(ActionEvent actionEvent) {
        nextButton.setDisable(false);
    }
}
