import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * 
 * @author loganlinn
 * 
 */
public class ReactionHeap extends MinHeap<Simulation.Reaction> {
	 private Simulation simulation;
	private final PriorityQueue<Simulation.Reaction> queue;

	public ReactionHeap(Simulation simulation) {
		this.simulation = simulation;
		queue = new PriorityQueue<Simulation.Reaction>(Arrays.asList(simulation
				.getReactions()));
	}

	public void updateReaction(Simulation.Reaction reaction){
		queue.remove(reaction);
		queue.add(reaction);
	}

	public Simulation.Reaction getNextReaction() {
		Simulation.Reaction reaction = queue.remove();
		return reaction;
	}
	
	public void refresh(){
		ArrayList<Simulation.Reaction> items = new ArrayList<Simulation.Reaction>(queue.size());
		while(!queue.isEmpty()){
			items.add(queue.remove());
		}
		while(!items.isEmpty()){
			queue.add(items.get(0));
		}
	}

	/**
	 * Print out the reaction heap for debugging
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Simulation.Reaction reaction : queue) {
			sb.append("X(t+" + reaction.getTau() + ") -> R"
					+ reaction.getReactionId() + "\n");
		}
		return sb.toString();
	}
}
