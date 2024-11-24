/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.robinos;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Vector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
    private ChoiceBox<String> direction; // Choice box for direction
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
        
        // Initialize the ChoiceBox
        direction.setItems(FXCollections.observableArrayList("Left", "Right"));
        direction.setValue("Left"); // Default selection

        

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
    @FXML
    public void onEnterButtonClick() {
        int numRequests = numberRequest.getValue();

        // Clear existing items
        requestList.clear();

        // Populate the list with new processes
        for (int i = 0; i < numRequests; i++) {
            SCANProcess process = new SCANProcess(i + 1, 0); // Assign placeholder location
            requestList.add(process);
        }

        // Set items to the table
        tableView.setItems(requestList);

        // Debug
        System.out.println("TableView Items: " + tableView.getItems());

        // Disable enter button and spinner
        enterButton.setDisable(true);
        numberRequest.setDisable(true);

        // Enable run button
        runButton.setDisable(false);
    }

    
    @FXML
    public void onRunButtonClick() {
        // Disable the buttons while the task is running
        runButton.setDisable(true);

        // Create a background task to run the Round Robin scheduling
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Perform RR Scheduling (Calculate waiting and turnaround time)
                performSCAN(requestList);
                return null;
            }
        };

        // Start the task in a background thread
        new Thread(task).start();
    }
    
    private void performSCAN(ObservableList<SCANProcess> requests) {
    int scount = 0;
    int distance, current;
    String d = direction.getValue();
    int n = requests.size(); // Get actual number of requests
    int cp = Integer.parseInt(currentPosition.getText());
    int ts = Integer.parseInt(trackSize.getText());

    Vector<Integer> left = new Vector<>();
    Vector<Integer> right = new Vector<>();
    Vector<Integer> seq = new Vector<>();
    
    // Add boundaries explicitly
    left.add(0);
    right.add(ts - 1);

    // Distribute requests into left or right of the current position
    for (SCANProcess request : requests) {
        if (request.getLocation() < cp)
            left.add(request.getLocation());
        else if (request.getLocation() > cp)
            right.add(request.getLocation());
    }

    // Sort both sides
    Collections.sort(left);
    Collections.sort(right);

    // Process requests
    int run = 2;
    while (run-- > 0) {
        if (d.equals("Left")) {
            // Process left side
            for (int i = left.size() - 1; i >= 0; i--) {
                current = left.get(i);
                seq.add(current);
                distance = Math.abs(current - cp);
                scount += distance;
                cp = current;
            }
            d = "Right"; // Switch direction
        } else if (d.equals("Right")) {
            // Process right side
            for (int i = 0; i < right.size(); i++) {
                current = right.get(i);
                seq.add(current);
                distance = Math.abs(current - cp);
                scount += distance;
                cp = current;
            }
            d = "Left"; // Switch direction
        }
    }

    // Update UI with results
    totalHeadMovement.setText("Total seek operations: " + scount);
    executionOrderText.setText("Seek Sequence: " + seq.toString());
}

    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
}
