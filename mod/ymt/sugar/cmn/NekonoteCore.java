/**
 * Copyright 2015 Yamato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mod.ymt.sugar.cmn;

import java.util.logging.Logger;
import net.minecraft.world.World;

public abstract class NekonoteCore implements Runnable {
	public final Logger logger = Logger.getLogger(getClass().getName());
	private boolean running = false;
	private long lastClickTime = 0;

	public boolean isRunning() {
		return running;
	}

	public void logFine(String format, Object... args) {
		Trace.logFine(logger, format, args);
	}

	public void logFine(Throwable th, String format, Object... args) {
		Trace.logFineThrow(logger, th, format, args);
	}

	public void logInfo(String format, Object... args) {
		Trace.logInfo(logger, format, args);
	}

	public void logSevere(String format, Object... args) {
		Trace.logSevere(logger, format, args);
	}

	public void logWarning(String format, Object... args) {
		Trace.logWarning(logger, format, args);
	}

	@Override
	public final void run() {
		synchronized (this) {
			if (running) {
				return;
			}
			init();
			running = true;
		}
	}

	public boolean tryInteractServer(World world) {
		if (Utils.isServerSide(world)) {
			long time = world.getTotalWorldTime();
			if (12 < Math.abs(lastClickTime - time)) {
				this.lastClickTime = time;
				return true;
			}
		}
		return false;
	}

	protected abstract void init();
}
