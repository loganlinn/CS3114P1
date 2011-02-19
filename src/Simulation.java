import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;
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
	private ReactionDependencyTable reactionDependencies;
	private ReactionHeap reactionHeap;
	private int[] populations;
	private Reaction[] reactions;
	private int[] speciesToOutput;

	/**
	 * Simulation Reaction class Uses a HashMap to store the reaction's terms
	 * with the species ID as the entry key. This allows faster access to
	 * determine which species are specified within the reaction's reactions or
	 * products.
	 * 
	 * @author loganlinn
	 * 
	 */
	public class Reaction implements Comparable<Reaction> {
		private final HashMap<Integer, ReactionTerm> reactants = new HashMap<Integer, ReactionTerm>();
		private final HashMap<Integer, ReactionTerm> products = new HashMap<Integer, ReactionTerm>();
		private double reactionRate;
		private double propensity;
		private final int reactionId;
		private double tau;

		private static final String REACTANT_PRODUCT_SEPARATOR = "->";
		private static final String NULL_SEPARATOR = " ";
		private static final String SPECIES_TOKEN = "S";

		/**
		 * Reaction constructor
		 * 
		 * @param reactionText
		 * @throws Exception
		 * @throws NumberFormatException
		 */
		public Reaction(int id, String reactionText) throws Exception,
				NumberFormatException {

			this.reactionId = id;

			// Sanity check that text exists
			if (reactionText == null || "".equals(reactionText)) {
				throw new Exception("Empty reaction text");
			}

			// Parse the reaction text
			parse(reactionText);
			updatePropensity();
			log.info("New Reaction: " + this.toString());
		}

		/**
		 * Reaction parsing logic
		 * 
		 * @param reactionText
		 * @throws Exception
		 */
		private void parse(String reactionText) throws Exception {
			// Clean the reaction text -- Ensure the reactant/product separator
			// gets it's own token
			reactionText = reactionText.replace(REACTANT_PRODUCT_SEPARATOR,
					REACTANT_PRODUCT_SEPARATOR + NULL_SEPARATOR);

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
					 * Appears to be a species identifier, ie S1
					 */
					tempSpeciesTokenIndex = tempToken.indexOf(SPECIES_TOKEN);

					// Ensure the token has characters after the species prefix
					if (tempSpeciesTokenIndex == tempToken.length() - 1) {
						throw new Exception("Could not specify Species ID");
					}

					tempSpeciesId = Integer.parseInt(tempToken
							.substring(tempSpeciesTokenIndex + 1));

					// Capture the coefficient if it hasn't already
					if (tempCoefficient == null) {
						if (tempSpeciesTokenIndex > 0) {
							tempCoefficient = Integer.parseInt(tempToken
									.substring(0, tempSpeciesTokenIndex));
						} else {
							tempCoefficient = 1;
						}
					}

					// Ready to create the reaction term object
					ReactionTerm newTerm = new ReactionTerm(tempSpeciesId,
							tempCoefficient);

					// Add the term to the reaction
					if (parsedReactantProductSeparator) {
						addProduct(newTerm);
					} else {
						addReactant(newTerm);
					}

					tempCoefficient = null; // Clear the term coefficient to
											// avoid being reused

				} else {
					tempCoefficient = Integer.parseInt(tempToken);
				}
			}

			if (parsedReactantProductSeparator == false) {
				throw new Exception("Invalid reaction syntax: " + reactionText);
			}

		}

		/**
		 * Fire the reaction
		 */
		public void fire() {
			// Decrement the reactant species' populations
			for (ReactionTerm reactionTerm : getReactants().values()) {
				incrementPopulation(reactionTerm.getSpeciesId(),
						-reactionTerm.getCoefficient());
			}

			// Increment the reactant species' populations
			for (ReactionTerm reactionTerm : getProducts().values()) {
				incrementPopulation(reactionTerm.getSpeciesId(),
						reactionTerm.getCoefficient());
			}
		}

		/**
		 * Get's the reaction's propensity methods
		 * 
		 * @return
		 */
		public void updatePropensity() {
			double propensity = getReactionRate();
			Stack<Integer> s = new Stack<Integer>();
			for (ReactionTerm reactantTerm : getReactants().values()) {
				// Handle when the same species occurs multiple times on one
				// side of reaction ie) S1+S1->S2
				int termCoefficient = reactantTerm.getCoefficient();
				while (termCoefficient-- > 0) {
					propensity *= getPopulation(reactantTerm.getSpeciesId())
							- termCoefficient;
					s.push(getPopulation(reactantTerm.getSpeciesId())
							- termCoefficient);
				}
			}
			setPropensity(propensity);
			generateTau();
		}

		/**
		 * Calculate an offset of time for this reaction to fire again based off
		 * of current propensity Normally called after calling
		 * updatePropensity()
		 * 
		 * @param propensity
		 * @return
		 */
		public void generateTau() {
			double propensity = getPropensity();
			if (propensity == 0) {
				setTau(totalTime + 1); // A big time
			}
			double r = Math.random(); // Generate a random number
			setTau(-Math.log(r) / propensity);
		}

		/**
		 * Object's toString format for debugging purposes
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("R" + reactionId + ":" + NULL_SEPARATOR);
			for (ReactionTerm reactionTerm : getReactants().values()) {
				sb.append(reactionTerm.getCoefficient());
				sb.append(SPECIES_TOKEN);
				sb.append(reactionTerm.getSpeciesId());
			}
			sb.append(NULL_SEPARATOR + REACTANT_PRODUCT_SEPARATOR
					+ NULL_SEPARATOR + getReactionRate() + NULL_SEPARATOR);
			for (ReactionTerm reactionTerm : getProducts().values()) {
				sb.append(reactionTerm.getCoefficient());
				sb.append(SPECIES_TOKEN);
				sb.append(reactionTerm.getSpeciesId());
			}
			sb.append(" [" + getPropensity() + "]");
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Reaction reaction) {
			return getReactionId() == reaction.getReactionId();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			// Cast object to Reaction and use other equals method
			// Currently, there is no need to check instance of Reaction
			return equals((Reaction) obj);
		}

		/**
		 * Used for priority queue
		 */
		@Override
		public int compareTo(Reaction otherReaction) {
			double tau = getTau();
			double otherTau = otherReaction.getTau();
			if (tau == otherTau) {
				return 0;
			} else if (tau < otherTau) {
				return -1;
			} else {
				return 1;
			}
		}

		// ------------ Standard getters & setters -------------------
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

		/**
		 * @return the reactionId
		 */
		public int getReactionId() {
			return reactionId;
		}

		/**
		 * @return the propensity
		 */
		public double getPropensity() {
			return propensity;
		}

		/**
		 * @param propensity
		 *            the propensity to set
		 */
		public void setPropensity(double propensity) {
			this.propensity = propensity;
		}

		/**
		 * @return the tau
		 */
		public double getTau() {
			return tau;
		}

		/**
		 * @param tau
		 *            the tau to set
		 */
		public void setTau(double tau) {
			this.tau = tau;
		}

		public void stepTau(double tau2) {
			// TODO Auto-generated method stub
			this.tau = tau - tau2;
		}

	}

	// ---------- END Simulation.Reaction -------------

	/**
	 * Simulation constructor
	 * 
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public Simulation(int simulationLength, int[] populations,
			String[] reactionDefinitions, int[] speciesToOutput)
			throws NumberFormatException, Exception {
		/*
		 * Store simulation parameters
		 */
		setTotalTime(simulationLength);
		setPopulations(populations);
		setSpeciesToOutput(speciesToOutput);
		
		/*
		 * Create reactions from reaction definitions
		 */
		int numReactions = reactionDefinitions.length;
		reactions = new Reaction[numReactions];
		for (int i = 0; i < numReactions; i++) {
			reactions[i] = new Reaction(i, reactionDefinitions[i]);
		}

		/*
		 * Create reaction dependency table
		 */
		reactionDependencies = new ReactionDependencyTable(reactions);
		System.out.println(reactionDependencies);

		/*
		 * Create heap
		 */
		reactionHeap = new ReactionHeap(this);

		System.out.println(reactionHeap);
	}

	/**
	 * Runs the simulation
	 */
	public void run() {
		log.info("Starting Simulation");
		logPopulations();
		/*
		 * Define simulation loop variables
		 */
		Reaction reaction; // Current firing reaction
		Reaction[] dependentReactions; // Reactions dependent current reaction
		int reactionId; // Temporary holder for current reaction's ID

		/*
		 * Run the main simulation loop
		 */
		while (totalTime > currentTime) {
			/*
			 * 1) Pick next reaction to fire
			 */
			
			reaction = reactionHeap.getNextReaction();
			reactionId = reaction.getReactionId();
			
			/*
			 * 2) Update simulation clock from reaction time
			 */
			stepTime(reaction.getTau());

			/*
			 * 3) Update populations
			 */
			if(reactionId == 3) log.info("t="+currentTime+", firing R"+reactionId);
			reaction.fire();

			/*
			 * 4) Calculate propensities for reaction that just fired and all
			 * dependent reactions 5) Setup next fire time
			 */
			reaction.updatePropensity();
			dependentReactions = reactionDependencies
					.getDependentReactions(reaction);
//			reactionHeap.setNextReactionTime(reaction.getReactionId(),
//					reaction.getTau());
			reactionHeap.updateReaction(reaction);
			
			for (Reaction dependentReaction : dependentReactions) {
				dependentReaction.updatePropensity();

				// Put the new tau's into queue
//				reactionHeap.setNextReactionTime(
//						dependentReaction.getReactionId(),
//						dependentReaction.getTau());
				reactionHeap.updateReaction(dependentReaction);
			}
			
//			System.out.println(reactionHeap);
			
			

		}
		logPopulations();
	}
	
	public void resetSimulation(int simulationLength,
			int[] populations, int[] speciesToOutput) {
		setTotalTime(simulationLength);
		setPopulations(populations);
		setSpeciesToOutput(speciesToOutput);
		
		reactionHeap = new ReactionHeap(this);
		
		System.out.println(reactionHeap);
	}
	
	private void logPopulations(){
		for(int i = 0; i < populations.length; i++){
			log.info("x"+(i+1)+" = "+populations[i]);
		}
	}
	
	/**
	 * Steps simulations current to by an tau value
	 * 
	 * @param tau
	 */
	public void stepTime(double tau) {
		setCurrentTime(getCurrentTime() + tau);
		for(Simulation.Reaction reaction : reactions){
			reaction.stepTau(tau);
		}
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
	private void setCurrentTime(double currentTime) {
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
	private void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	/**
	 * Gets the population for a specific species. Subtract 1 from index b/c stored in 0-based array
	 * 
	 * @param speciesId
	 * @return
	 */
	public int getPopulation(int speciesId) {
		if (speciesId < 0 || speciesId >= populations.length) {
			return 0;
		}
		return populations[speciesId-1];
	}

	/**
	 * Sets a specific species' population
	 * 
	 * @param speciesId
	 * @param population
	 */
	private void incrementPopulation(int speciesId, int step) {
		populations[speciesId-1] = populations[speciesId-1] + step;
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
	private void setPopulations(int[] populations) {
		this.populations = populations;
	}

	/**
	 * @return the reactions
	 */
	public Reaction[] getReactions() {
		return reactions;
	}

	/**
	 * @param reactions
	 *            the reactions to set
	 */
	public void setReactions(Reaction[] reactions) {
		this.reactions = reactions;
	}

	/**
	 * @return the speciesToOutput
	 */
	public int[] getSpeciesToOutput() {
		return speciesToOutput;
	}

	/**
	 * @param speciesToOutput
	 *            the speciesToOutput to set
	 */
	public void setSpeciesToOutput(int[] speciesToOutput) {
		this.speciesToOutput = speciesToOutput;
	}

}