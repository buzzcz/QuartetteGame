package cz.zcu.kiv.ups.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import java.util.List;
import java.util.Optional;

/**
 * @author Jaroslav Klaus
 */
public class AlertsAndDialogs {

	private AlertsAndDialogs() {
	}

	private static Alert createAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
	}

	public static Optional<ButtonType> showAndWaitAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = createAlert(type, title, header, content);
		return alert.showAndWait();
	}

	public static void showAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = createAlert(type, title, header, content);
		alert.show();
	}

	public static <T> Optional<T> showAndWaitChoiceDialog(T defaultValue, List<T> choices, String title, String
			header, String content) {
		ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultValue, choices);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		return dialog.showAndWait();
	}

}
