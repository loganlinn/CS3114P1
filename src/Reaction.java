import java.util.List;


public class Reaction {
	private ReactionTerm[] reactants;
	private ReactionTerm[] products;
	private double reactionRate;
	private Simulation simulation;
	
	public Reaction(Simulation simulation){
		setSimulation(simulation);
	}
	
	/**
	 * 
	 */
	public void fire(){
		for(ReactionTerm reactionTerm : reactants){
			simulation.incrementPopulation(reactionTerm.getSpeciesId(), -reactionTerm.getCoefficient());
		}
		for(ReactionTerm reactionTerm : products){
			simulation.incrementPopulation(reactionTerm.getSpeciesId(), reactionTerm.getCoefficient());
		}
	}
	
	public double getPropensity(){
		double propensity = getReactionRate();
		for(ReactionTerm reactantTerm : reactants){
			propensity *= simulation.getPopulation(reactantTerm.getSpeciesId());
		}
		return propensity;
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

	/**
	 * @return the reactants
	 */
	public ReactionTerm[] getReactants() {
		return reactants;
	}

	/**
	 * @param reactants the reactants to set
	 */
	public void setReactants(ReactionTerm[] reactants) {
		this.reactants = reactants;
	}

	/**
	 * @return the products
	 */
	public ReactionTerm[] getProducts() {
		return products;
	}

	/**
	 * @param products the products to set
	 */
	public void setProducts(ReactionTerm[] products) {
		this.products = products;
	}
	
}
