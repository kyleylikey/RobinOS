/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.robinos;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

/**
 * FXML Controller class
 *
 * @author Kyle
 */
public class SJFController implements Initializable {

    @FXML
    private Spinner<Integer> numberProcess;  // Spinner for selecting number of processes
    @FXML
    private TableView<SJFProcess> tableView; // Table view to display processes
    @FXML
    private TableColumn<SJFProcess, Integer> taskColumn;   // Task Column
    @FXML
    private TableColumn<SJFProcess, Integer> arrivalColumn;  // Arrival Time Column
    @FXML
    private TableColumn<SJFProcess, Integer> burstColumn;    // Burst Time Column
    @FXML
    private TableColumn<SJFProcess, Integer> completionColumn;  // Completion Time Column
    @FXML
    private TableColumn<SJFProcess, Integer> waitingColumn;  // Waiting Time Column
    @FXML
    private TableColumn<SJFProcess, Integer> turnaroundColumn; // Turnaround Time Column
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


    private ObservableList<SJFProcess> processList = FXCollections.observableArrayList();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 9, 2);
        numberProcess.setValueFactory(valueFactory);

        // Initialize table columns to link with SJFProcess fields
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
            SJFProcess process = new SJFProcess(i + 1, 0, 1);  // Placeholder values (0 for Arrival and Burst)
            processList.add(process);
        }

        // Set the table items to the processed list
        tableView.setItems(processList);

        // Disable the Enter button and Spinner after populating the table
        enterButton.setDisable(true);
        numberProcess.setDisable(true);

        // Enable the Run button
        runButton.setDisable(false);
    }

    @FXML
    public void onRunButtonClick() {
        // Perform SJF Scheduling (Calculate waiting and turnaround time)
        calculateSJFTimes(processList);

        // Update table with new data
        tableView.setItems(processList);

        // Display average times in the UI
        double avgWaitingTime = calculateAverageWaitingTime(processList);
        double avgTurnaroundTime = calculateAverageTurnaroundTime(processList);

        averageWaitingTimeText.setText("Average Waiting Time: " + String.format("%.2f", avgWaitingTime));
        averageTurnaroundTimeText.setText("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime));

        // Enable the Enter button and Spinner for reuse
        enterButton.setDisable(false);
        numberProcess.setDisable(false);
    }


    // Function to calculate completion time, waiting time, and turnaround time for SJF
    private void calculateSJFTimes(ObservableList<SJFProcess> processes) {
        int currentTime = 0; // Tracks the current time
        int completed = 0;   // Number of processes completed
        int n = processes.size();

        boolean[] isCompleted = new boolean[n]; // Tracks whether a process is completed
        StringBuilder executionOrder = new StringBuilder("Execution Order: "); // To track the order of execution

        int idleStart = -1;  // To track the start of an idle period

        while (completed < n) {
            // Find the process with the shortest burst time that has arrived
            int shortest = -1;
            int minBurst = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                SJFProcess process = processes.get(i);
                if (!isCompleted[i] && process.getArrivalTime() <= currentTime) {
                    if (process.getBurstTime() < minBurst) {
                        minBurst = process.getBurstTime();
                        shortest = i;
                    }
                }
            }

            if (shortest == -1) {
                // No process is ready; if we're not already tracking idle time, start it
                if (idleStart == -1) {
                    idleStart = currentTime; // Mark the start of idle time
                }
                currentTime++;  // Increment time to simulate idle
            } else {
                // If we were tracking idle time, now we need to record it
                if (idleStart != -1) {
                    executionOrder.append("Idle->");
                    idleStart = -1;  // Reset idle start time
                }

                // Process the shortest job
                SJFProcess currentProcess = processes.get(shortest);
                int startTime = currentTime;

                // Add the current task to the execution order
                executionOrder.append("T").append(currentProcess.getTask());

                // Check if this is not the last task and add "->"
                if (completed < n - 1) {
                    executionOrder.append("->");
                }

                // Calculate and set completion time
                int completionTime = currentTime + currentProcess.getBurstTime();
                currentProcess.setCompletionTime(completionTime);

                // Update waiting and turnaround times
                currentProcess.setWaitingTime(startTime - currentProcess.getArrivalTime());
                currentProcess.setTurnaroundTime(currentProcess.getWaitingTime() + currentProcess.getBurstTime());

                // Advance current time
                currentTime = completionTime;

                // Mark process as completed
                isCompleted[shortest] = true;
                completed++;
            }
        }

        // If there was idle time at the end, record it
        if (idleStart != -1) {
            executionOrder.append("Idle(").append(idleStart).append(" to ").append(currentTime - 1).append(")->");
        }

        // Remove the trailing arrow and update the execution
        executionOrderText.setText(executionOrder.toString());
    }



    // Function to calculate average waiting time
    private double calculateAverageWaitingTime(ObservableList<SJFProcess> processes) {
        int totalWaitingTime = 0;
        for (SJFProcess process : processes) {
            totalWaitingTime += process.getWaitingTime();
        }
        return totalWaitingTime / (double) processes.size();
    }

    // Function to calculate average turnaround time
    private double calculateAverageTurnaroundTime(ObservableList<SJFProcess> processes) {
        int totalTurnaroundTime = 0;
        for (SJFProcess process : processes) {
            totalTurnaroundTime += process.getTurnaroundTime();
        }
        return totalTurnaroundTime / (double) processes.size();
    }

    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
}