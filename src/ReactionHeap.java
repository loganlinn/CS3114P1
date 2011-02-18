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
	
	public void setNextReactionTime(int reactionId, double reactionTau) {

	}

	public int getNextReactionId() {

		return 0;
	}

	public double getNextReactionTime() {
		// TODO:decrement all nodes by return value
		return 0;
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
