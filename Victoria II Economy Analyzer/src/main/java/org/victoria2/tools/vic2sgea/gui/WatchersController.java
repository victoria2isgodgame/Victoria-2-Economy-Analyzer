package org.victoria2.tools.vic2sgea.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.victoria2.tools.vic2sgea.main.PathKeeper;
import org.victoria2.tools.vic2sgea.watcher.FileWatcher;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anth on 12.02.2017.
 */
public class WatchersController extends BaseController implements Initializable {

	private TimerTask task;
	private Timer timer;
	private boolean isChanged = false;

	@FXML
	FilePrompt fpSaveFile;

	@FXML
	Button btnStart;

	public void onBtnClick() {
		if (task == null)
			startWatcher();
		else
			stopWatcher();
	}

	private void startWatcher() {
		task = new FileWatcher(new File(fpSaveFile.getPath())) {
			protected void onChanged(File file) {
				System.out.println("File " + file.getName() + " have change !");
				isChanged = true;
				
			}
			protected void onNotChanged(File file) {
				if(isChanged)
				{
					isChanged = false;
					Main.getMainWindowController().LoadSave(new File(fpSaveFile.getPath()));
				}
			}
		};
		timer = new Timer();
		timer.schedule(task, 5000, 3000);
		btnStart.setText("중지");

	}

	private void stopWatcher() {
		timer.cancel();
		timer = null;
		task = null;
		btnStart.setText("시작");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String savePath = PathKeeper.SAVE_PATH;
		fpSaveFile.setPath(savePath);

	}
}
