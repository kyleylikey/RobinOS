/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.robinos;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

/**
 * FXML Controller class
 *
 * @author Kyle
 */
public class SCANController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Spinner<Integer> numberRequest;  // Spinner for selecting number of requests
    @FXML
    private TextField currentPosition;  // Input field for current position
    @FXML
    private TextField trackSize;  // Input field for track size
    @FXML
    private TextField seekRate;  // Input field for seek rate
    @FXML
    private TableView<SCANProcess> tableView; // Table view to display requests
    @FXML
    private TableColumn<SCANProcess, Integer> requestColumn;   // Request Column
    @FXML
    private TableColumn<SCANProcess, Integer> locationColumn;  // Location Column
    @FXML
    private Button enterButton; // Button to submit input
    @FXML
    private Button runButton;  // Button to start the scheduling and computation
    @FXML
    private Text totalHeadMovement; // Text to display average waiting time
    @FXML
    private Text seekTime; // Text to display average turnaround time
    @FXML
    private Text executionOrderText; // Text to display the execution order of tasks


    private ObservableList<SCANProcess> requestList = FXCollections.observableArrayList();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        numberRequest.setValueFactory(valueFactory);
        TextFormatter<Integer> cpformatter = new TextFormatter<>(new IntegerStringConverter(), 0, c -> {
            // Allow only positive integers
            return (c.getControlNewText().matches("\\d*") && 
                   (c.getControlNewText().isEmpty() || Integer.parseInt(c.getControlNewText()) > 0)) ? c : null;
        });
        TextFormatter<Integer> tsformatter = new TextFormatter<>(new IntegerStringConverter(), 0, c -> {
            // Allow only positive integers
            return (c.getControlNewText().matches("\\d*") && 
                   (c.getControlNewText().isEmpty() || Integer.parseInt(c.getControlNewText()) > 0)) ? c : null;
        });
        TextFormatter<Integer> srformatter = new TextFormatter<>(new IntegerStringConverter(), 0, c -> {
            // Allow only positive integers
            return (c.getControlNewText().matches("\\d*") && 
                   (c.getControlNewText().isEmpty() || Integer.parseInt(c.getControlNewText()) > 0)) ? c : null;
        });
        currentPosition.setTextFormatter(cpformatter);
        trackSize.setTextFormatter(tsformatter);
        seekRate.setTextFormatter(srformatter);

        // Initialize table columns to link with SCANProcess fields
        requestColumn.setCellValueFactory(cellData -> cellData.getValue().requestProperty().asObject());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty().asObject());
        
        locationColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

 
        locationColumn.setOnEditCommit(event -> {
            int newLocation = event.getNewValue();
            event.getRowValue().setLocation(newLocation);
        });
        
        // Initially disable the run button
        runButton.setDisable(true);
        
    }    
    public void onEnterButtonClick() {
        int numRequests = numberRequest.getValue();        
        
        // Clear any existing processes in the list
        requestList.clear();

        // Collect input for processes from the table (creating empty rows)
        for (int i = 0; i < numRequests; i++) {
            SCANProcess process = new SCANProcess(i + 1, 0);  // Placeholder values for currentPosition, trackSize, seekRate, request, location
            requestList.add(process);
        }

        // Set the table items to the processed list
        tableView.setItems(requestList);

        // Disable the Enter button and Spinner after populating the table
        enterButton.setDisable(true);
        numberRequest.setDisable(true);

        // Enable the Run button
        runButton.setDisable(false);
    }
    
    
    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
}
