import java.util.List;


public class Reaction {
	public static final int SPECIES_ID_INDEX = 0;
	public static final int COEFFICIENT_INDEX = 1;
	private List<int[]> reactants;
	private List<int[]> products;
	private double reactionRate;
	private Simulation simulation;
	
	public Reaction(Simulation simulation){
		setSimulation(simulation);
	}
	
	/**
	 * 
	 */
	public void fire(){
		for(int[] reactionTerm : reactants){
			simulation.incrementPopulation(reactionTerm[SPECIES_ID_INDEX], -reactionTerm[COEFFICIENT_INDEX]);
		}
		for(int[] reactionTerm : products){
			simulation.incrementPopulation(reactionTerm[SPECIES_ID_INDEX], reactionTerm[COEFFICIENT_INDEX]);
		}
	}
	
	
	
	/**
	 * @return the reactants
	 */
	public List<int[]> getReactants() {
		return reactants;
	}
	/**
	 * @param reactants the reactants to set
	 */
	public void setReactants(List<int[]> reactants) {
		this.reactants = reactants;
	}
	/**
	 * @return the products
	 */
	public List<int[]> getProducts() {
		return products;
	}
	/**
	 * @param products the products to set
	 */
	public void setProducts(List<int[]> products) {
		this.products = products;
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
	 * @return the simulation
	 */
	public Simulation getSimulation() {
		return simulation;
	}

	/**
	 * @param simulation the simulation to set
	 */
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}
	
}
