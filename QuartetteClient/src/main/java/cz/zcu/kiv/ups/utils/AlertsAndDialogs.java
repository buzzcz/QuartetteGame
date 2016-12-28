package cz.zcu.kiv.ups.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import java.util.List;
import java.util.Optional;

/**
 * Utility to create and show alerts and dialogs.
 *
 * @author Jaroslav Klaus
 */
public class AlertsAndDialogs {

	/**
	 * Private constructor to disable instantiation.
	 */
	private AlertsAndDialogs() {
	}

	/**
	 * Creates alert with specified attributes.
	 * @param type type of alert.
	 * @param title title of alert.
	 * @param header header of alert.
	 * @param content content of alert.
	 * @return created alert.
	 */
	private static Alert createAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
	}

	/**
	 * Creates, shows alert and waits for alert to be dismissed.
	 * @param type type of alert.
	 * @param title title of alert.
	 * @param header header of alert.
	 * @param content content of alert.
	 * @return result of showAndWait.
	 */
	public static Optional<ButtonType> showAndWaitAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = createAlert(type, title, header, content);
		return alert.showAndWait();
	}

	/**
	 * Creates and shows alert.
	 * @param type type of alert.
	 * @param title title of alert.
	 * @param header header of alert.
	 * @param content content of alert.
	 */
	public static void showAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = createAlert(type, title, header, content);
		alert.show();
	}

	/**
	 * Creates, shows Choice Dialog and waits for dialog to be dismissed.
	 * @param defaultValue default value to select.
	 * @param choices choices to select from.
	 * @param title title of dialog.
	 * @param header header of dialog.
	 * @param content content of dialog.
	 * @return result of showAndWait.
	 */
	public static <T> Optional<T> showAndWaitChoiceDialog(T defaultValue, List<T> choices, String title, String
			header, String content) {
		ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultValue, choices);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		return dialog.showAndWait();
	}

}
