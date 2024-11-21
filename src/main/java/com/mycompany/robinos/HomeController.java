package com.mycompany.robinos;

import java.io.IOException;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    private void switchToSignIn() throws IOException {
        App.setRoot("signin");
    }
    
    @FXML
    private void switchToSJF() throws IOException {
        App.setRoot("sjf");
    }
    
    @FXML
    private void switchToRR() throws IOException {
        App.setRoot("rr");
    }
}