/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.robinos;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
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
public class RRController implements Initializable {

    @FXML
    private Spinner<Integer> numberProcess;  // Spinner for selecting number of processes
    @FXML
    private TextField timeSlice;  // Input field for time slice
    @FXML
    private TableView<RRProcess> tableView; // Table view to display processes
    @FXML
    private TableColumn<RRProcess, Integer> taskColumn;   // Task Column
    @FXML
    private TableColumn<RRProcess, Integer> arrivalColumn;  // Arrival Time Column
    @FXML
    private TableColumn<RRProcess, Integer> burstColumn;    // Burst Time Column
    @FXML
    private TableColumn<RRProcess, Integer> completionColumn;  // Completion Time Column
    @FXML
    private TableColumn<RRProcess, Integer> waitingColumn;  // Waiting Time Column
    @FXML
    private TableColumn<RRProcess, Integer> turnaroundColumn; // Turnaround Time Column
    @FXML
    private Button enterButton; // Button to submit input
    @FXML
    private Button runButton;  // Button to start the scheduling and computation
    @FXML
    private Text averageWaitingTimeText; // Text to display average waiting time
    @FXML
    private Text averageTurnaroundTimeText; // Text to display average turnaround time
    @FXML
    private Text executionOrderText; // Text to display the execution order of tasks


    private ObservableList<RRProcess> processList = FXCollections.observableArrayList();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 9, 2);
        numberProcess.setValueFactory(valueFactory);
        // Set a TextFormatter for the timeSlice TextField to allow only positive integers
        TextFormatter<Integer> formatter = new TextFormatter<>(new IntegerStringConverter(), 0, c -> {
            // Allow only positive integers
            return (c.getControlNewText().matches("\\d*") && 
                   (c.getControlNewText().isEmpty() || Integer.parseInt(c.getControlNewText()) > 0)) ? c : null;
        });
        timeSlice.setTextFormatter(formatter);
        // Initialize table columns to link with RRProcess fields
        taskColumn.setCellValueFactory(cellData -> cellData.getValue().taskProperty().asObject());
        arrivalColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty().asObject());
        burstColumn.setCellValueFactory(cellData -> cellData.getValue().burstTimeProperty().asObject());
        completionColumn.setCellValueFactory(cellData -> cellData.getValue().completionTimeProperty().asObject());
        waitingColumn.setCellValueFactory(cellData -> cellData.getValue().waitingTimeProperty().asObject());
        turnaroundColumn.setCellValueFactory(cellData -> cellData.getValue().turnaroundTimeProperty().asObject());
        arrivalColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        burstColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

 
        arrivalColumn.setOnEditCommit(event -> {
            int newArrivalTime = event.getNewValue();
            event.getRowValue().setArrivalTime(newArrivalTime);
        });

        burstColumn.setOnEditCommit(event -> {
            int newBurstTime = event.getNewValue();
            event.getRowValue().setBurstTime(newBurstTime);
        });
        // Initially disable the run button
        runButton.setDisable(true);
        
    }
     @FXML
    public void onEnterButtonClick() {
        int numProcesses = numberProcess.getValue();

        // Clear any existing processes in the list
        processList.clear();

        // Collect input for processes from the table (creating empty rows)
        for (int i = 0; i < numProcesses; i++) {
            RRProcess process = new RRProcess(i + 1, 0, 0);  // Placeholder values (0 for Arrival and Burst)
            processList.add(process);
        }

        // Set the table items to the processed list
        tableView.setItems(processList);

        // Disable the Enter button and Spinner after populating the table
        enterButton.setDisable(true);
        numberProcess.setDisable(true);
        timeSlice.setDisable(true);

        // Enable the Run button
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
            calculateRRTimes(processList);

            // Update table with new data on the JavaFX Application thread
            Platform.runLater(() -> {
                tableView.setItems(processList);

                // Display average times in the UI
                double avgWaitingTime = calculateAverageWaitingTime(processList);
                double avgTurnaroundTime = calculateAverageTurnaroundTime(processList);

                averageWaitingTimeText.setText("Average Waiting Time: " + String.format("%.2f", avgWaitingTime));
                averageTurnaroundTimeText.setText("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime));

                // Enable the Enter button and Spinner for reuse
                enterButton.setDisable(false);
                numberProcess.setDisable(false);
                timeSlice.setDisable(false);
                runButton.setDisable(false);  // Re-enable the Run button after the task is done
            });
            return null;
        }
    };

    // Start the task in a background thread
    new Thread(task).start();
}


    // Function to calculate completion time, waiting time, and turnaround time for RR
    private void calculateRRTimes(ObservableList<RRProcess> processes) {
    int currentTime = 0;
    int completed = 0;
    int n = processes.size();
    int timeSliceValue = Integer.parseInt(timeSlice.getText());

    boolean[] isCompleted = new boolean[n];
    StringBuilder executionOrder = new StringBuilder("Execution Order: ");
    LinkedList<RRProcess> queue = new LinkedList<>();

    // Initialize remaining burst time for all processes
    for (RRProcess process : processes) {
        process.setRemainingBurstTime(process.getBurstTime());
    }

    while (completed < n) {
        // Add processes that have arrived and are not completed yet to the queue
        for (RRProcess process : processes) {
            if (process.getArrivalTime() <= currentTime && !isCompleted[processes.indexOf(process)] && !queue.contains(process)) {
                queue.add(process);
            }
        }

        // Print current time and queue status
        System.out.println("Current Time: " + currentTime);
        System.out.println("Queue: " + queue);

        // If no processes are ready, handle idle time
        if (queue.isEmpty()) {
            executionOrder.append("| Idle | ");
            currentTime++;
            continue;
        }

        // Process the next task in the queue
        RRProcess currentProcess = queue.poll();
        System.out.println("Current Process: " + currentProcess.getTask() + " Remaining Burst: " + currentProcess.getRemainingBurstTime());

        // Execute the process for the allowed time slice or remaining burst time
        int executionTime = Math.min(currentProcess.getRemainingBurstTime(), timeSliceValue);
        currentTime += executionTime;
        currentProcess.setRemainingBurstTime(currentProcess.getRemainingBurstTime() - executionTime);

        // Append the task to the execution order
        executionOrder.append("T").append(currentProcess.getTask()).append(" -> ");

        // If the process is completed, calculate its times
        if (currentProcess.getRemainingBurstTime() == 0) {
            currentProcess.setCompletionTime(currentTime);
            currentProcess.setTurnaroundTime(currentProcess.getCompletionTime() - currentProcess.getArrivalTime());
            currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
            isCompleted[processes.indexOf(currentProcess)] = true;
            completed++;
        } else {
            // If the process is not finished, re-add it to the queue
            queue.addLast(currentProcess);
        }

        // Add newly arrived processes to the queue after executing the current process
        // (recheck arrival times after each cycle)
        for (RRProcess process : processes) {
            if (process.getArrivalTime() <= currentTime && !queue.contains(process) && !isCompleted[processes.indexOf(process)]) {
                queue.add(process);
            }
        }
    }

    // Trim the last arrow from the execution order and update the UI
    if (executionOrder.length() > 4) {
        executionOrder.setLength(executionOrder.length() - 4); // Remove trailing " -> "
    }
    executionOrderText.setText(executionOrder.toString());
}


    // Function to calculate average waiting time
    private double calculateAverageWaitingTime(ObservableList<RRProcess> processes) {
        int totalWaitingTime = 0;
        for (RRProcess process : processes) {
            totalWaitingTime += process.getWaitingTime();
        }
        return totalWaitingTime / (double) processes.size();
    }

    // Function to calculate average turnaround time
    private double calculateAverageTurnaroundTime(ObservableList<RRProcess> processes) {
        int totalTurnaroundTime = 0;
        for (RRProcess process : processes) {
            totalTurnaroundTime += process.getTurnaroundTime();
        }
        return totalTurnaroundTime / (double) processes.size();
    }

    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
}