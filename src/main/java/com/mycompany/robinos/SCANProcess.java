package com.mycompany.robinos;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Kyle
 */
public class SCANProcess {
    protected final IntegerProperty currentPosition;
    protected final IntegerProperty trackSize;
    protected final IntegerProperty seekRate;
    protected final IntegerProperty request;
    protected final IntegerProperty totalHeadMovement;
    protected final IntegerProperty seekTime;
    protected final IntegerProperty location;

    
    // Constructor
    public SCANProcess(int currentPosition, int trackSize, int seekRate, int request, int location) {
        this.currentPosition = new SimpleIntegerProperty(currentPosition);
        this.trackSize = new SimpleIntegerProperty(trackSize);
        this.seekRate = new SimpleIntegerProperty(seekRate);
        this.request = new SimpleIntegerProperty(request);
        this.location = new SimpleIntegerProperty(location);
        this.totalHeadMovement = new SimpleIntegerProperty(0);
        this.seekTime = new SimpleIntegerProperty(0);
    }
    
    // Getters and Setters for currentPosition
    public int getCurrentPosition() {
        return currentPosition.get();
    }
    
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition.set(currentPosition);
    }
    
    public IntegerProperty currentPositionProperty() {
        return currentPosition;
    }

    // Getters and Setters for trackSize
    public int getTrackSize() {
        return trackSize.get();
    }
    
    public void setTrackSize(int trackSize) {
        this.trackSize.set(trackSize);
    }
    
    public IntegerProperty trackSizeProperty() {
        return trackSize;
    }

    // Getters and Setters for seekRate
    public int getSeekRate() {
        return seekRate.get();
    }
    
    public void setSeekRate(int seekRate) {
        this.seekRate.set(seekRate);
    }
    
    public IntegerProperty seekRateProperty() {
        return seekRate;
    }

    // Getters and Setters for request
    public int getRequest() {
        return request.get();
    }
    
    public void setRequest(int request) {
        this.request.set(request);
    }
    
    public IntegerProperty requestProperty() {
        return request;
    }

    // Getters and Setters for totalHeadMovement
    public int getTotalHeadMovement() {
        return totalHeadMovement.get();
    }
    
    public void setTotalHeadMovement(int totalHeadMovement) {
        this.totalHeadMovement.set(totalHeadMovement);
    }
    
    public IntegerProperty totalHeadMovementProperty() {
        return totalHeadMovement;
    }

    // Getters and Setters for seekTime
    public int getSeekTime() {
        return seekTime.get();
    }
    
    public void setSeekTime(int seekTime) {
        this.seekTime.set(seekTime);
    }
    
    public IntegerProperty seekTimeProperty() {
        return seekTime;
    }
    
    // Getters and Setters for location
    public int getLocation() {
        return location.get();
    }
    
    public void setLocation(int location) {
        this.location.set(location);
    }
    
    public IntegerProperty locationProperty() {
        return location;
    }
}
