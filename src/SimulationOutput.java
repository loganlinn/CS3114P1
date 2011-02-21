import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * An output helper class for the simulation
 * @author loganlinn
 *
 */
public class SimulationOutput {
	public static final String DELIMITER = "\t";
	private File file;
	private OutputStreamWriter out;
	private Simulation simulation;

	/**
	 * SimulationOutput constructor
	 * @param simulation
	 * @param filePath
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public SimulationOutput(Simulation simulation, String filePath)
			throws UnsupportedEncodingException, FileNotFoundException {
		this.simulation = simulation;
		file = new File(filePath);
		out = new OutputStreamWriter(new FileOutputStream(file), "ASCII");
	}

	/**
	 * Called when one of the watched species has a change in population
	 */
	public void populationChanged() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(simulation.getCurrentTime());

		for (int population : simulation.getPopulationsToOutput()) {
			buffer.append(DELIMITER);
			buffer.append(population);
		}

		try {
			out.write(buffer.toString() + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Called when a simulation run is completed
	 */
	public void simulationComplete() {
		/**
		 * Output how many times each reaction fired
		 */
		try {
			for (long fireCount : simulation.getReactionFireCounts()) {
				out.write(fireCount + "\n");
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Direct access to writer
	 * @param output
	 */
	public void write(String output){
		try {
			out.write(output);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Direct access to writer. Writes with a new line character
	 * @param output
	 */
	public void writeln(String output){
		try {
			out.write(output+"\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Closes output stream
	 * @throws IOException
	 */
	public void closeOutput() throws IOException{
		out.close();
	}
	
	@Override
	public void finalize() throws Throwable{
		closeOutput(); // Close the output stream		
		super.finalize(); // Call super finalize because we override it
	}
	
}
