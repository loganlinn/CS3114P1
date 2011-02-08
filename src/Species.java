
public class Species {
	private double reactionRate;
	private long population;
	
	
	public Species(int population, double reactionRate){
		setPopulation(population);
		setReactionRate(reactionRate);
	}
	
	/**
	 * @return the reactionRate
	 */
	public double getReactionRate() {
		return reactionRate;
	}

	/**
	 * @param reactionRate the reactionRate to set
	 */
	public void setReactionRate(double reactionRate) {
		this.reactionRate = reactionRate;
	}

	/**
	 * @return the population
	 */
	public long getPopulation() {
		return population;
	}

	/**
	 * @param population the population to set
	 */
	public void setPopulation(long population) {
		this.population = population;
	}
}
