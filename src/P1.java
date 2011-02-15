import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 * 
 * @author loganlinn
 *
 */
public class P1 {
	/**
	 * Simulation variables
	 */
	private static final Log log = new Log(P1.class);
	private static final Simulation simulation = new Simulation();
	private static int numSpecies;
	private static int numReactions;
	private static int numChemicalSpecies;
	private static int simulationLength;
	private static int[] populations;
	
	private static final String INPUT_SEPARATOR = " ";
	
	private static boolean parseInputFile(String inputFilePath) throws Exception {
		/**
		 * Input read the input file
		 */
		File inputFile = new File(inputFilePath);
		FileReader inputFileReader = null;
		BufferedReader inputBuffer = null;
		String line = null;
		inputFileReader = new FileReader(inputFile);
		inputBuffer = new BufferedReader(inputFileReader);

		/**
		 * Line 0: Basic Simulation Information Format: N M D SimulationTime
		 */
		line = inputBuffer.readLine();
		if (line == null) {
			log.error("Missing line 0");
			return false;
		}

		String[] simulationValues = line.split(INPUT_SEPARATOR);
		if (simulationValues.length < 4) {
			log.error("Line 0 requires 4 values");
			return false;
		}
		numSpecies = Integer.parseInt(simulationValues[0]);
		numReactions = Integer.parseInt(simulationValues[1]);
		numChemicalSpecies = Integer.parseInt(simulationValues[2]);
		simulationLength = Integer.parseInt(simulationValues[3]);
		log.info("Species: " + numSpecies);
		log.info("Reactions: " + numReactions);
		log.info("Simulation Length: " + simulationLength);

		/**
		 * Line 1: Initial Populations Format: x y z ...
		 */
		line = inputBuffer.readLine();
		if (line == null) {
			log.error("Missing line 1");
			return false;
		}
		String[] populationInput = line.split(INPUT_SEPARATOR);

		// Verify that enough populations are provided
		if (populationInput.length < numSpecies) {
			log.error("Line 1, Not enough population values provided");
			return false;
		}

		populations = new int[numSpecies];
		for (int i = 0; i < numSpecies; i++) {
			populations[i] = Integer.parseInt(populationInput[i]);
			log.info("Population: x" + i + " = " + populations[i]);
		}

		/**
		 * Line 2: Indices of species included in output
		 */
		line = inputBuffer.readLine();
		if (line == null) {
			log.error("Missing line 2");
			return false;
		}
		String[] indicesInput = line.split(INPUT_SEPARATOR);

		// Verify that enough indices have been provided
		if (indicesInput.length < numChemicalSpecies) {
			log.error("Line 2, Not enough indices provided");
			return false;
		}

		for (int i = 0; i < numChemicalSpecies; i++) {
			log.info("Output Species: S" + indicesInput[i]);
		}

		/**
		 * Line 4-(numReactions+4): Reactions
		 */
		simulation.setNumberOfReactions(numReactions);
		
		for (int reactionId = 0; reactionId < numReactions; reactionId++) {
			line = inputBuffer.readLine();
			if (line == null) {
				log.error("Missing an reaction #" + reactionId);
				return false;
			}
			simulation.addReaction(reactionId, line);
		}

		return true;
	}

	public static void main(String[] args) {

		log.entry("main");
		
		if (args.length < 1) {
			log.error("Input file not specified.");
			return;
		}
		String inputFilePath = args[0];
		
		boolean parseSuccess = false;
		
		try {
			parseSuccess = parseInputFile(inputFilePath);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			parseSuccess = false;
			return;
		} catch (IOException e) {
			e.printStackTrace();
			parseSuccess = false;
			return;
		} catch (Exception e) {
			e.printStackTrace();
			parseSuccess = false;
		}
		
		if(!parseSuccess){
			return;
		}
		
		/*
		 * Run the simulation
		 */
		
		
		log.info("Completed");
		log.exit();
	}

}