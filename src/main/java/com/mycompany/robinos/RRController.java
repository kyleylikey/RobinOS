/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.robinos;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
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

 
         // Handle edit commits to update underlying RRProcess objects
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
            RRProcess process = new RRProcess(i + 1, 0, 1);  // Placeholder values (0 for Arrival and Burst)
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
        // Log the current process list
    System.out.println("Process list before Round Robin scheduling:");
    processList.forEach(process -> 
        System.out.println("Task: " + process.getTask() + 
                           ", Arrival: " + process.getArrivalTime() + 
                           ", Burst: " + process.getBurstTime()));
    // Disable the buttons while the task is running
    runButton.setDisable(true);

    // Create a background task to run the Round Robin scheduling
    Task<Void> task = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            // Perform RR Scheduling (Calculate waiting and turnaround time)
            performRRScheduling(processList);
            return null;
        }
    };

    // Start the task in a background thread
    new Thread(task).start();
}
    
private void performRRScheduling(ObservableList<RRProcess> processes) {
    int n = numberProcess.getValue();
    int tq = Integer.parseInt(timeSlice.getText());
    int timer = 0;
    float avgWait = 0, avgTT = 0;

    int[] arrival = new int[n];
    int[] burst = new int[n];
    int[] wait = new int[n];
    int[] turn = new int[n];
    int[] temp_burst = new int[n];
    boolean[] complete = new boolean[n];
    Queue<Integer> queue = new LinkedList<>();

    // Initialize arrays
    for (int i = 0; i < n; i++) {
        arrival[i] = processes.get(i).getArrivalTime();
        burst[i] = processes.get(i).getBurstTime();
        temp_burst[i] = burst[i];
        complete[i] = false;
    }

    // Sort processes by arrival time
    int[] indices = IntStream.range(0, n).boxed()
            .sorted(Comparator.comparingInt(i -> arrival[i]))
            .mapToInt(ele -> ele).toArray();

    System.out.println("Initial process states:");
    for (int i = 0; i < n; i++) {
        System.out.println("Process " + (i + 1) + ": Arrival=" + arrival[i] + ", Burst=" + burst[i]);
    }

    int index = 0;
    while (index < n && arrival[indices[index]] <= timer) {
        queue.add(indices[index]);
        index++;
    }

    while (!queue.isEmpty()) {
        System.out.println("Queue state: " + queue);
        int i = queue.poll();
        int timeSpent = Math.min(temp_burst[i], tq);
        timer += timeSpent;
        temp_burst[i] -= timeSpent;

        System.out.println("Executing process " + (i + 1) + " for " + timeSpent + " units; Remaining burst=" + temp_burst[i]);

        while (index < n && arrival[indices[index]] <= timer) {
            queue.add(indices[index]);
            index++;
        }

        if (temp_burst[i] > 0) {
            queue.add(i);
        } else {
            complete[i] = true;
            turn[i] = timer - arrival[i];
            wait[i] = turn[i] - burst[i];
            System.out.println("Process " + (i + 1) + " completed; Turnaround time=" + turn[i] + ", Waiting time=" + wait[i]);
        }
    }

    for (int i = 0; i < processes.size(); i++) {
        processes.get(i).setArrivalTime(arrival[i]);
        processes.get(i).setBurstTime(burst[i]);
        processes.get(i).setWaitingTime(wait[i]);
        processes.get(i).setTurnaroundTime(turn[i]);
    }

    final float finalAvgWait = Arrays.stream(wait).sum() / (float) n;
    final float finalAvgTT = Arrays.stream(turn).sum() / (float) n;

    System.out.println("Average Waiting Time: " + finalAvgWait);
    System.out.println("Average Turnaround Time: " + finalAvgTT);

    Platform.runLater(() -> updateUI(processes, finalAvgWait, finalAvgTT));
}

public static void queueUpdation(int queue[],int timer,int arrival[],int n, int maxProccessIndex){
        int zeroIndex = -1;
        for(int i = 0; i < n; i++){
            if(queue[i] == 0){
                zeroIndex = i;
                break;
            }
        }
        if(zeroIndex == -1)
            return;
        queue[zeroIndex] = maxProccessIndex + 1;
    }

    private void checkNewArrival(int timer, int[] arrival, int n, int maxProcessIndex, int[] queue) {
        if (timer <= arrival[n - 1]) {
            boolean newArrival = false;
            for (int j = (maxProcessIndex + 1); j < n; j++) {
                if (arrival[j] <= timer) {
                    if (maxProcessIndex < j) {
                        maxProcessIndex = j;
                        newArrival = true;
                    }
                }
            }
            if (newArrival)
                queueUpdation(queue, timer, arrival, n, maxProcessIndex);
        }
    }

    private void queueMaintenance(int[] queue, int n) {
        for(int i = 0; (i < n-1) && (queue[i+1] != 0) ; i++){
            int temp = queue[i];
            queue[i] = queue[i+1];
            queue[i+1] = temp; 
        }
    }
    
    private void updateUI(ObservableList<RRProcess> processes, float avgWait, float avgTT) {
        tableView.setItems(processes);
        averageWaitingTimeText.setText("Average Waiting Time: " + String.format("%.2f", avgWait));
        averageTurnaroundTimeText.setText("Average Turnaround Time: " + String.format("%.2f", avgTT));
        executionOrderText.setText("Execution Order: " + getExecutionOrder(processes));
        enterButton.setDisable(false);
        numberProcess.setDisable(false);
        timeSlice.setDisable(false);
        runButton.setDisable(false);
    }
    
    private String getExecutionOrder(ObservableList<RRProcess> processes) {
        // Generate and return the execution order string
        StringBuilder executionOrder = new StringBuilder();
        for (RRProcess process : processes) {
            executionOrder.append("T").append(process.getTask()).append(" -> ");
        }
        if (executionOrder.length() > 4) {
            executionOrder.setLength(executionOrder.length() - 4); // Remove trailing " -> "
        }
        return executionOrder.toString();
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