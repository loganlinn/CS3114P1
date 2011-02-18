import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * @author loganlinn
 * 
 */
public class ReactionDependencyTable {
	private static final Logger log = Logger.getLogger(ReactionDependencyTable.class.getName());
	private final HashMap<Integer, Simulation.Reaction[]> reactionTable = new HashMap<Integer, Simulation.Reaction[]>();
	
	/**
	 * 
	 * Point from a species to a reaction
	 * 
	 * @param numReactions
	 */
	public ReactionDependencyTable(Simulation.Reaction[] reactions) {
		map(reactions);
	}
	
	/**
	 * Construct the map of reaction dependencies;
	 * Determines which reactions are dependent on another reaction
	 * Mapping occurs at beginning of simulation since the set of reactions for a simulation is fixed
	 * 		Doing this up front is better than repetitive mapping while simulation is running
	 */
	private void map(Simulation.Reaction[] reactions){
		/*
		 * Map species to the reactions which the species appears in the reactant list
		 */
		HashMap<Integer, LinkedList<Simulation.Reaction>> speciesToReactionMap = new HashMap<Integer, LinkedList<Simulation.Reaction>>();
		
		int speciesId;	//Temporary species id used within loop
		LinkedList<Simulation.Reaction> speciesReactions; // Use linked-list to collect reactions
		
		// Loop through all reactions, looking at their reactant species
		for(Simulation.Reaction reaction : reactions){
			for(ReactionTerm reactant : reaction.getReactantTerms()){
				speciesId = reactant.getSpeciesId();
				
				//Check if the species already exists as key
				if(!speciesToReactionMap.containsKey(speciesId)){
					
					// First time getting this species, create the linked list
					speciesReactions = new LinkedList<Simulation.Reaction>();
					// Store the reaction list in map
					speciesToReactionMap.put(speciesId, speciesReactions);
					
				}else{
					speciesReactions = speciesToReactionMap.get(speciesId);
				}
				
				// Add the reaction to the linked list
				speciesReactions.add(reaction);
				
			}
		}
		
		/*
		 * Map reactions to reactions which are dependent
		 */
		int reactionId; // Temporary reaction id used within loop
		LinkedList<Simulation.Reaction> dependentReactionsList = new LinkedList<Simulation.Reaction>();
		
		// Loop through all reactions looking at their product species
		// Get reaction's product species
		// For each product species, get reactions
		// Map all product species' reactions to this reaction
		//		Avoid duplicates via taking intersection of all reaction lists
		HashSet<Simulation.Reaction> reactionSet;
		
		for(Simulation.Reaction reaction : reactions){
			
			reactionId = reaction.getReactionId();
			
			
			// Use linked list to collect all reactions, ignore duplicate references
			for(ReactionTerm product : reaction.getProductTerms()){
				speciesId = product.getSpeciesId();
				
				// Get the map from species -> reactions
				speciesReactions = speciesToReactionMap.get(speciesId);
				
				dependentReactionsList.addAll(speciesReactions);
				
			}
			
			// Reduce list of all reactions into set containing no duplicate reactions
			reactionSet = new HashSet<Simulation.Reaction>(dependentReactionsList);
			
			// Store the reduced set
			reactionTable.put(reactionId, (Simulation.Reaction[]) reactionSet.toArray());
			
			// Clear the list of dependent reactions for the next reaction
			dependentReactionsList.clear();
		}
		
	}
	
	/**
	 * Get all dependent reactions for a given reaction
	 * @param reaction
	 * @return
	 */
	public Simulation.Reaction[] getDependentReactions(Simulation.Reaction reaction){
		return reactionTable.get(reaction.getReactionId());
	}
	
	/**
	 * Outputs a string representation of the table
	 * Expensive! For debugging purposes
	 */
	public String toString(){
		return "";
	}
}
