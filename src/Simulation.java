import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author loganlinn
 * 
 */
public class Simulation {
	private static final Log log = new Log(Simulation.class);
	private double currentTime;
	private int totalTime;
	private ReactionDependancyTable reactionDependencies;
	private ReactionHeap reactionHeap;
	private int[] populations;
	private Reaction[] reactions;

	/**
	 * Simulation Reaction class Uses a HashMap to store the reaction's terms
	 * with the species ID as the entry key. This allows faster access to
	 * determine which species are specified within the reaction's reactions or
	 * products.
	 * 
	 * @author loganlinn
	 * 
	 */
	public class Reaction {
		private final HashMap<Integer, ReactionTerm> reactants = new HashMap<Integer, ReactionTerm>();
		private final HashMap<Integer, ReactionTerm> products = new HashMap<Integer, ReactionTerm>();
		private double reactionRate;

		private static final String REACTANT_PRODUCT_SEPARATOR = "->";
		private static final String NULL_SEPARATOR = " ";
		private static final String SPECIES_TOKEN = "S";

		public Reaction(String reactionText) throws Exception,
				NumberFormatException {
			// Check that text exists
			if (reactionText == null || "".equals(reactionText)) {
				throw new Exception("Empty reaction text");
			}
			
			/*
			 * Clean the input string
			 */
			// Ensure the reactant/product separator gets it's own token
			reactionText = reactionText.replace(REACTANT_PRODUCT_SEPARATOR, REACTANT_PRODUCT_SEPARATOR+NULL_SEPARATOR);
			
			StringTokenizer reactionTokenizer = new StringTokenizer(
					reactionText);
			
			// Declare some temporary parsing variables
			String tempToken;
			Integer tempCoefficient = null; // use Integer object to allow
												// null state
			int tempSpeciesTokenIndex;
			int tempSpeciesId;

			// Declare parsing variables to hold state
			boolean parsedReactantProductSeparator = false;
			boolean parsedReactionRate = false;

			/*
			 * Parse the reaction by looping through the tokenized input
			 */
			while (reactionTokenizer.hasMoreTokens()) {

				tempToken = reactionTokenizer.nextToken();
				
				if (!parsedReactantProductSeparator
						&& REACTANT_PRODUCT_SEPARATOR.equals(tempToken)) {
					/*
					 * Detect the reactant/product separator
					 */
					parsedReactantProductSeparator = true;
					
				} else if (parsedReactantProductSeparator
						&& !parsedReactionRate) {
					/*
					 * Immediately after the separator (->)
					 */
					reactionRate = Double.parseDouble(tempToken);
					parsedReactionRate = true;
					
				} else if (tempToken.contains(SPECIES_TOKEN)) {
					/*
					 *  Appears to be a species identifier, ie S1
					 */

					
					tempSpeciesTokenIndex = tempToken.indexOf(SPECIES_TOKEN);
					
					// Ensure the token has characters after the species prefix
					if (tempSpeciesTokenIndex == tempToken.length() - 1) {
						throw new Exception("Could not specify Species ID");
					}

					tempSpeciesId = Integer.parseInt(tempToken
							.substring(tempSpeciesTokenIndex+1));
					
					// Capture the coefficient if it hasn't already
					if (tempCoefficient == null){
						if(tempSpeciesTokenIndex > 0) {
							tempCoefficient = Integer.parseInt(tempToken.substring(0, tempSpeciesTokenIndex));
						}else{
							tempCoefficient = 1;
						}
					}
					
					
					// Ready to create the reaction term object
					ReactionTerm newTerm = new ReactionTerm(tempSpeciesId, tempCoefficient);
					
					// Add the term to the reaction
					if(parsedReactantProductSeparator){
						addProduct(newTerm);
					}else{
						addReactant(newTerm);
					}
					
					tempCoefficient = null;	// Clear the term coefficient to avoid being reused
					
				} else {
					tempCoefficient = Integer.parseInt(tempToken);
				}
			}

			if (parsedReactantProductSeparator == false) {
				throw new Exception("Invalid reaction syntax: " + reactionText);
			}
			
			log.info("New Reaction:"+this.toString());
		}
		
		/**
		 * Object's toString format
		 */
		public String toString(){
			StringBuffer sb = new StringBuffer();
			for (ReactionTerm reactionTerm : getReactants().values()) {
				sb.append(reactionTerm.getCoefficient());
				sb.append(SPECIES_TOKEN);
				sb.append(reactionTerm.getSpeciesId());
			}
			sb.append(NULL_SEPARATOR+REACTANT_PRODUCT_SEPARATOR+NULL_SEPARATOR);
			for (ReactionTerm reactionTerm : getProducts().values()) {
				sb.append(reactionTerm.getCoefficient());
				sb.append(SPECIES_TOKEN);
				sb.append(reactionTerm.getSpeciesId());
			}
			return sb.toString();
		}
		
		/**
		 * Adds a ReactionTerm as a reactant to the reaction. Attempts to
		 * combine with existing term using the same species ID
		 * 
		 * @param term
		 */
		public void addReactant(ReactionTerm term) {
			
			if (reactants.containsKey(term.getSpeciesId())) {
				// If that species is already in the reaction, combine the
				// coefficients
				ReactionTerm existingTerm = reactants.get(term.getSpeciesId());
				existingTerm.setCoefficient(existingTerm.getCoefficient()
						+ term.getCoefficient());
			} else {
				// Insert new term
				reactants.put(term.getSpeciesId(), term);
			}
		}

		/**
		 * Adds a ReactionTerm as a product term to the reaction. Attempts to
		 * combine with existing term using the same species ID
		 * 
		 * @param term
		 */
		public void addProduct(ReactionTerm term) {
			if (products.containsKey(term.getSpeciesId())) {
				// If that species is already in the reaction, combine the
				// coefficients
				ReactionTerm existingTerm = products.get(term.getSpeciesId());
				existingTerm.setCoefficient(existingTerm.getCoefficient()
						+ term.getCoefficient());
			} else {
				// Insert new term
				products.put(term.getSpeciesId(), term);
			}
		}

		/**
		 * Fire the reaction
		 */
		public void fire() {

			for (ReactionTerm reactionTerm : getReactants().values()) {
				incrementPopulation(reactionTerm.getSpeciesId(),
						-reactionTerm.getCoefficient());
			}
			for (ReactionTerm reactionTerm : getProducts().values()) {
				incrementPopulation(reactionTerm.getSpeciesId(),
						reactionTerm.getCoefficient());
			}
		}

		/**
		 * Get's the reaction's propensity
		 * 
		 * @return
		 */
		public double getPropensity() {
			double propensity = getReactionRate();
			for (ReactionTerm reactantTerm : getReactants().values()) {
				propensity *= getPopulation(reactantTerm.getSpeciesId());
			}
			return propensity;
		}

		/**
		 * 
		 * @return the ReactionTerms of the reaction's reactants
		 */
		public Collection<ReactionTerm> getReactantTerms() {
			return reactants.values();
		}

		/**
		 * 
		 * @return the ReactionTerms of the reaction's products
		 */
		public Collection<ReactionTerm> getProductTerms() {
			return products.values();
		}

		/**
		 * @return the reactionRate
		 */
		public double getReactionRate() {
			return reactionRate;
		}

		/**
		 * @param reactionRate
		 *            the reactionRate to set
		 */
		public void setReactionRate(double reactionRate) {
			this.reactionRate = reactionRate;
		}

		/**
		 * @return the reactants
		 */
		public HashMap<Integer, ReactionTerm> getReactants() {
			return reactants;
		}

		/**
		 * @return the products
		 */
		public HashMap<Integer, ReactionTerm> getProducts() {
			return products;
		}
	}	//end Simulation.Reaction

	/**
	 * Simulation ojbect constructor
	 */
	public Simulation() {

	}

	/**
	 * 
	 * @param numReactions
	 */
	public void setNumberOfReactions(int numReactions) {
		reactions = new Simulation.Reaction[numReactions];

	}

	public void addReaction(int reactionId, String reactionText)
			throws Exception {
		reactions[reactionId] = new Simulation.Reaction(reactionText);
	}

	public void createReactionDependancyTable() {

	}

	public boolean isReady() {
		if (reactions == null) {
			return false;
		} else if (reactionDependencies == null) {
			return false;
		} else if (reactionHeap == null) {
			return false;
		} else if (populations == null) {
			return false;
		}
		return true;
	}

	public void run() {

		if (!isReady()) {
			return;
		}
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
			 * 4) Calculate propensities for reaction that just fired and all
			 * dependent reactions 5) Setup next fire time
			 */

			propensity = reaction.getReactionRate(); // Start propensity with
														// constant, then
														// multiply by each
														// species' population
														// in the reactant
			List<Integer> dependentReactionIds = new LinkedList<Integer>();
			for (ReactionTerm reactionTerm : reaction.getReactantTerms()) {

				int speciesId = reactionTerm.getSpeciesId();
				// Handle when the same species occurs multiple times on one
				// side of reaction ie) S1+S1->S2
				int termCoefficient = reactionTerm.getCoefficient();
				while (termCoefficient-- > 0) {
					propensity *= populations[speciesId] - termCoefficient;
				}

				dependentReactionIds.addAll(reactionDependencies
						.getDependentReactions(speciesId));
			}
			Reaction dependentReaction;
			reactionTau = nextTau(propensity);
			reactionHeap.setNextReactionTime(reactionId, reactionTau);

			/*
			 * Update dependent propensities in heap
			 */
			for (Integer dependentReactionId : dependentReactionIds) {
				dependentReaction = reactions[dependentReactionId];
				// Start propensity with constant, then multiply by each
				// species' population in the reactant
				propensity = dependentReaction.getReactionRate();

				for (ReactionTerm reactionTerm : reaction.getReactantTerms()) {
					int speciesId = reactionTerm.getSpeciesId();

					// Handle when the same species occurs multiple times on one
					// side of reaction ie) S1+S1->S2
					int termCoefficient = reactionTerm.getCoefficient();
					while (termCoefficient-- > 0) {
						propensity *= populations[speciesId] - termCoefficient;
					}
				}

				reactionTau = nextTau(propensity);
				reactionHeap.setNextReactionTime(dependentReactionId,
						reactionTau);
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
		populations[speciesId] = populations[speciesId] + step;
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