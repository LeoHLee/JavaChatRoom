package com.uz.emojione.fx;/**
 * Created by UltimateZero on 9/12/2016.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EmojiConversionExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EmojiConversion.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root, 600, 300);
			primaryStage.setTitle("Emoji Conversion");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
