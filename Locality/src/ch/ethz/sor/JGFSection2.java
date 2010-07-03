package ch.ethz.sor;

public interface JGFSection2 {
	public void setSize(int size);

	public void initialize();

	public void runKernel();

	public void validate();

	public void tidyUp();

	public void run(int size);
}
