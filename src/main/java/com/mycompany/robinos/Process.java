/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.robinos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Kyle
 */
public class Process {
    protected final IntegerProperty task;
    protected final IntegerProperty arrivalTime;
    protected final IntegerProperty burstTime;
    protected final IntegerProperty completionTime;
    protected final IntegerProperty waitingTime;
    protected final IntegerProperty turnaroundTime;

    // Constructor
    public Process(int task, int arrivalTime, int burstTime) {
        this.task = new SimpleIntegerProperty(task);
        this.arrivalTime = new SimpleIntegerProperty(arrivalTime);
        this.burstTime = new SimpleIntegerProperty(burstTime);
        this.completionTime = new SimpleIntegerProperty(0);
        this.waitingTime = new SimpleIntegerProperty(0);
        this.turnaroundTime = new SimpleIntegerProperty(0);
    }

    // Getters and Setters
    public int getTask() {
        return task.get();
    }

    public void setTask(int task) {
        this.task.set(task);
    }

    public int getArrivalTime() {
        return arrivalTime.get();
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime.set(arrivalTime);
    }

    public int getBurstTime() {
        return burstTime.get();
    }

    public void setBurstTime(int burstTime) {
        this.burstTime.set(burstTime);
    }

    public int getWaitingTime() {
        return waitingTime.get();
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime.set(waitingTime);
    }
    
    // Getter and setter methods for completionTime
    public int getCompletionTime() {
        return completionTime.get();
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime.set(completionTime);
        // If needed, notify observers (for table updates)
    }

    public int getTurnaroundTime() {
        return turnaroundTime.get();
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime.set(turnaroundTime);
    }

    // Properties for data binding
    public IntegerProperty taskProperty() {
        return task;
    }

    public IntegerProperty arrivalTimeProperty() {
        return arrivalTime;
    }

    public IntegerProperty burstTimeProperty() {
        return burstTime;
    }
    
    public IntegerProperty completionTimeProperty() {
        return completionTime;
    }

    public IntegerProperty waitingTimeProperty() {
        return waitingTime;
    }

    public IntegerProperty turnaroundTimeProperty() {
        return turnaroundTime;
    }
}