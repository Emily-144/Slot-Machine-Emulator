package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//////////////////////////////////////////////////////////////////////
//
//	Alert Class
//
//	Written by Emily Gross on 11/22/22
//
//	This class contains a show() method that displays an alert box.
//	It was made for SlotMachineEmulator, but it could be used in
//	other programs as well.
//
//////////////////////////////////////////////////////////////////////

public class Alert {
	
	public static void show(String message) {
		
		Stage alertBox = new Stage();
		alertBox.initModality(Modality.APPLICATION_MODAL); //prevents user from clicking away from alert box before clicking button
		alertBox.setResizable(false); //prevents user from resizing alert
		alertBox.setTitle("Alert");
		alertBox.setMinWidth(250);
		
		Label messageText = new Label(message);
		messageText.setStyle("-fx-text-alignment: center;" + "-fx-font-size: 15;");
		
		Button okBtn = new Button("Ok");
		okBtn.setOnAction(e -> alertBox.close());
		okBtn.setStyle("-fx-text-alignment: center;" + "-fx-font-size: 15;");
		
		VBox layout = new VBox(20);
		layout.getChildren().addAll(messageText, okBtn);
		layout.setStyle("-fx-alignment: center;" + "-fx-padding: 20;");
		
		Scene scene = new Scene(layout);
		alertBox.setScene(scene);
		alertBox.showAndWait(); //waits until window is closed before returning to main application
		
	}

}
