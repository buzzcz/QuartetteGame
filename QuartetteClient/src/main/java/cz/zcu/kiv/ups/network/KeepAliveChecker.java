package cz.zcu.kiv.ups.network;

import cz.zcu.kiv.ups.gui.GameTableController;
import cz.zcu.kiv.ups.gui.MainWindowController;
import cz.zcu.kiv.ups.utils.AlertsAndDialogs;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Service for checking if keep-alive was receiver in time.
 *
 * @author Jaroslav Klaus
 */
@Service
public class KeepAliveChecker implements KeepAliveCheckerInterface {

	@Autowired
	private MainWindowController mainWindowController;

	@Autowired
	private GameTableController gameTableController;

	@Override
	public void checkKeepAlive() {
		LocalDateTime lastKeepAlive = gameTableController.getReceivedKeepAlive();
		if (lastKeepAlive != null && lastKeepAlive.plus(10, ChronoUnit.SECONDS).isBefore(LocalDateTime.now())) {
			gameTableController.setReceivedKeepAlive(null);
			Platform.runLater(() -> {
				AlertsAndDialogs.showAndWaitAlert(Alert.AlertType.ERROR, "Server Error", "Server not responding.",
						"Disconnecting from server because it is not responding.");
				mainWindowController.showLogin();
			});
		}
	}

}
