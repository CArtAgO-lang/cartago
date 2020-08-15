package cartago.infrastructure.web;

import java.util.Optional;

public class TryActionReply {
	
	private Optional<Boolean> accepted;
	
	public TryActionReply() {
		accepted = Optional.empty();
	}
	
	public synchronized void notifyResult(boolean accepted) {
		this.accepted = Optional.of(accepted);
		notifyAll();
	}
	
	public synchronized void waitForResult(int timeout) throws InterruptedException {
		while (!accepted.isPresent()) {
			wait(timeout);
		}
	}
	
	public synchronized boolean isAccepted() {
		return accepted.get();
	}
}