/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.robinos;

/**
 *
 * @author Kyle
 */
public class RRProcess extends Process {
    private int remainingBurstTime;

    public int getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public void setRemainingBurstTime(int remainingBurstTime) {
        this.remainingBurstTime = remainingBurstTime;
    }

    public RRProcess(int task, int arrivalTime, int burstTime) {
        super(task, arrivalTime, burstTime); // Calls the constructor of the base class
        this.remainingBurstTime = burstTime;
    }
}