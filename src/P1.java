import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private static int numSpecies;
	private static int numReactions;
	private static int numChemicalSpecies;
	private static int simulationLength;
	private static int[] speciesToOutput;
	private static int[] populations;
	private static String[] reactionDefinitions;

	private static final String INPUT_SEPARATOR = " ";

	private static boolean parseInputFile(String inputFilePath)
			throws Exception {
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
			log.error("Line 1: Not enough population values provided. Expecting "
					+ numSpecies);
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

		String[] chemicalSpeciesInput = line.split(INPUT_SEPARATOR);

		// Verify that enough indices have been provided
		if (chemicalSpeciesInput.length < numChemicalSpecies) {
			log.error("Line 2: Not enough indices provided");
			return false;
		}

		speciesToOutput = new int[numChemicalSpecies];
		for (int i = 0; i < numChemicalSpecies; i++) {
			log.info("Will output Species, S" + chemicalSpeciesInput[i]);
			speciesToOutput[i] = Integer.valueOf(chemicalSpeciesInput[i]);
		}

		/**
		 * Line 4 to Line (numReactions+4): Reactions
		 */
		reactionDefinitions = new String[numReactions];
		for (int i = 0; i < numReactions; i++) {
			line = inputBuffer.readLine();
			if (line == null) {
				log.error("Line " + (4 + i) + ": Missing reaction " + i
						+ ". Expecting " + (numReactions - i) + " reactions.");
				return false;
			}
			reactionDefinitions[i] = line;
		}

		return true;
	}

	/*
	 * On my honor:
	 * 
	 * -	I have not used source code obtained from another student,
	 * 		or any other unauthorized source, either modified or
	 * 		unmodified.
	 * 
	 * -	All source code and documentation used in my program is
	 * 		either my original, or was derived by me from the
	 * 		source code published in the textbook for this course.
	 * 
	 * -	I have not discussed coding details about this project with
	 * 		anyone other than my partner (in the case of a join
	 * 		submission), instructor, ACM/UPE tutors or the TAs assigned
	 * 		to this course. I understand that I  may discuss the concepts
	 * 		of this program with other students, and that other students
	 * 		may help me debug my program so long as neither of us writes
	 * 		anything during the discussion or modifies any computer file
	 * 		during the discussion. I have violated neither in the spirit nor
	 * 		the letter of this restriction.
	 */
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
			
			/*
			 * Create simulation
			 */
			Simulation simulation = new Simulation(simulationLength, populations,
					reactionDefinitions, speciesToOutput);

			/*
			 * Run simulation
			 */
			simulation.run();
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			parseSuccess = false;
		} catch (IOException e) {
			e.printStackTrace();
			parseSuccess = false;
		} catch (Exception e) {
			e.printStackTrace();
			parseSuccess = false;
		}

		if (!parseSuccess) {
			log.error("Failed to start simulation");
			return;
		}

		

		log.info("Completed");
		log.exit();
	}

}