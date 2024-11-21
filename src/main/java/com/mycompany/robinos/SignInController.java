package com.mycompany.robinos;

import java.io.IOException;
import javafx.fxml.FXML;

public class SignInController {

    @FXML
    private void switchToHome() throws IOException {
        App.setRoot("home");
    }
}
