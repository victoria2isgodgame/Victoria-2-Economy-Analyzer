package org.victoria2.tools.vic2sgea.watcher;

import java.util.*;
import java.io.*;

public abstract class FileWatcher extends TimerTask {
	private long timeStamp;
	private File file;

	public FileWatcher(File file) {
		this.file = file;
		this.timeStamp = file.lastModified();
	}

	public final void run() {
		long timeStamp = file.lastModified();

		if (this.timeStamp != timeStamp) {
			this.timeStamp = timeStamp;
			onChanged(file);
		}
		else if(this.timeStamp == timeStamp) {
			onNotChanged(file);
		}
	}

	protected abstract void onChanged(File file);
	protected abstract void onNotChanged(File file);
}