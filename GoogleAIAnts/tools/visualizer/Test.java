class Test {
	public static void main(String[] argv) throws InterruptedException {
		Thread thread = Thread.currentThread();
		synchronized(thread) {
			thread.wait();
		}
	}
}
