public class Simulation {

	private int currentTime;
	private int totalTime;
	
	private Species[] species;
	
	public Simulation(int simulationLength, Species[] species, int numReactions) {
		setTotalTime(simulationLength);
		setSpecies(species);
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
	 * @return the species
	 */
	public Species getSpecies(int index) {
		if(index >= species.length || index < 0){
			return null;
		}
		return species[index];
	}
	
	/**
	 * @return the species
	 */
	public Species[] getSpecies() {
		return species;
	}

	/**
	 * @param species the species to set
	 */
	public void setSpecies(Species[] species) {
		this.species = species;
	}
}