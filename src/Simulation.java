import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author loganlinn
 * 
 */
public class Simulation {

	private double currentTime;
	private int totalTime;
	private ReactionDependancyTable reactionDependencies;
	private ReactionHeap reactionHeap;
	private int[] populations;
	private Reaction[] reactions;
	
	public Simulation(int simulationLength, int[] populations, int numReactions) {
		setTotalTime(simulationLength);
	}

	public void start() {
		Reaction reaction;
		double tau;
		int reactionId;
		double propensity;
		double reactionTau;
		
		while (totalTime > currentTime) {
			/*
			 * 1) Pick next reaction to fire
			 */
			reactionId = reactionHeap.getNextReactionId();

			/*
			 * 2) Update simulation clock from reaction time
			 */
			tau = reactionHeap.getNextReactionTime();
			stepTime(tau);
			
			/*
			 * 3) Update populations
			 */
			reaction = reactions[reactionId];
			reaction.fire();
			
			/*
			 * 4) Calculate propensities for reaction that just fired and all dependent reactions
			 * 5) Setup next fire time
			 */
			
			propensity = reaction.getReactionRate();				// Start propensity with constant, then multiply by each species' population in the reactant
			List<Integer> dependentReactionIds = new LinkedList<Integer>();
			for(ReactionTerm reactionTerm : reaction.getReactants()){
				
				int speciesId = reactionTerm.getSpeciesId();
				//Handle when the same species occurs multiple times on one side of reaction ie) S1+S1->S2
				int termCoefficient = reactionTerm.getCoefficient();
				while(termCoefficient-- > 0){
					propensity *= populations[speciesId] - termCoefficient;
				}
				
				dependentReactionIds.addAll(reactionDependencies.getDependentReactions(speciesId));
			}
			Reaction dependentReaction;
			reactionTau = nextTau(propensity);
			reactionHeap.setNextReactionTime(reactionId, reactionTau);
			
			/*
			 * Update dependent propensities in heap
			 */
			for(Integer dependentReactionId : dependentReactionIds){
				dependentReaction = reactions[dependentReactionId];
				
				propensity = dependentReaction.getReactionRate();	// Start propensity with constant, then multiply by each species' population in the reactant
				for(ReactionTerm reactionTerm : reaction.getReactants()){
					int speciesId = reactionTerm.getSpeciesId();
					
					//Handle when the same species occurs multiple times on one side of reaction ie) S1+S1->S2
					int termCoefficient = reactionTerm.getCoefficient();
					while(termCoefficient-- > 0){
						propensity *= populations[speciesId] - termCoefficient;
					}
				}
				
				reactionTau = nextTau(propensity);
				reactionHeap.setNextReactionTime(dependentReactionId, reactionTau);
			}
			
		}
	}

	public void stepTime(double tau) {
		setCurrentTime(getCurrentTime() + tau);
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
	public double getCurrentTime() {
		return currentTime;
	}

	/**
	 * @param currentTime
	 *            the currentTime to set
	 */
	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * @return the totalTime
	 */
	public int getTotalTime() {
		return totalTime;
	}

	/**
	 * @param totalTime
	 *            the totalTime to set
	 */
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	/**
	 * Gets the population for a specific species
	 * 
	 * @param speciesId
	 * @return
	 */
	public int getPopulation(int speciesId) {
		if (speciesId < 0 || speciesId >= populations.length) {
			return 0;
		}
		return populations[speciesId];
	}

	/**
	 * Sets a specific species' population
	 * 
	 * @param speciesId
	 * @param population
	 */
	public void incrementPopulation(int speciesId, int step) {
		if (speciesId < 0 || speciesId >= populations.length) {
			return;
		}
		populations[speciesId] = populations[speciesId]+step;
	}

	/**
	 * @return the populations
	 */
	public int[] getPopulations() {
		return populations;
	}

	/**
	 * @param populations
	 *            the populations to set
	 */
	public void setPopulations(int[] populations) {
		this.populations = populations;
	}

}