public class Simulation {

	private int currentTime;
	private int totalTime;
	private ReactionDependancyTable reactionDependencies;
	private ReactionHeap reactionHeap;
	private int[] populations;
	
	public Simulation(int simulationLength, int[] populations, int numReactions) {
		setTotalTime(simulationLength);
	}

	public void start() {

		while (totalTime > currentTime) {
			/*
			 * 1) Pick next reaction to fire
			 */
			
			/*
			 * 2) Update simulation clock from reaction time
			 */
			
			/*
			 * 3) Update populations
			 */
			
			/*
			 * 4) Update propensities
			 */
			
			/*
			 * 5) Setup next fire time
			 */
			
		}
	}

	public void stepTime(int delta) {
		setCurrentTime(getCurrentTime() + delta);
	}

	// calculate the offset of the time for the next reaction to fire
	public double nextTau(double propensity) {
		if (propensity == 0)
			return totalTime + 1; // A big time
		double r = Math.random(); // Generate a random number
		return (-Math.log(r) / propensity);
	}

	/**
	 * @return the currentTime
	 */
	public int getCurrentTime() {
		return currentTime;
	}

	/**
	 * @param currentTime the currentTime to set
	 */
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * @return the totalTime
	 */
	public int getTotalTime() {
		return totalTime;
	}

	/**
	 * @param totalTime the totalTime to set
	 */
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	
	/**
	 * Gets the population for a specific species
	 * @param speciesId
	 * @return
	 */
	public int getPopulation(int speciesId){
		if(speciesId < 0 || speciesId >= populations.length){
			return 0;
		}
		return populations[speciesId];
	}
	
	/**
	 * Sets a specific species' population
	 * @param speciesId
	 * @param population
	 */
	public void setPopulation(int speciesId, int population){
		if(speciesId < 0 || speciesId >= populations.length){
			return;
		}
		populations[speciesId] = population;
	}
	
	/**
	 * @return the populations
	 */
	public int[] getPopulations() {
		return populations;
	}

	/**
	 * @param populations the populations to set
	 */
	public void setPopulations(int[] populations) {
		this.populations = populations;
	}
	
}