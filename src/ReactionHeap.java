import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * This class is used to determine which reaction should fire next in the
 * simulation. Once a reaction fires, it's propensity updates, and a new
 * next-fire time is calculated. An instance of this class will keep a sorted
 * list of reactions based on their next-fire time.
 * 
 * @author loganlinn
 * 
 */
public class ReactionHeap {
	private Simulation simulation;
	private final PriorityQueue<Simulation.Reaction> queue;

	public ReactionHeap(Simulation simulation) {
		this.simulation = simulation;
		queue = new PriorityQueue<Simulation.Reaction>(Arrays.asList(simulation
				.getReactions()));
	}

	public void updateReaction(Simulation.Reaction reaction) {
		queue.remove(reaction);
		queue.add(reaction);
	}

	public Simulation.Reaction getNextReaction() {
		Simulation.Reaction reaction = queue.remove();
		return reaction;
	}

	public void refresh() {
		ArrayList<Simulation.Reaction> items = new ArrayList<Simulation.Reaction>(
				queue.size());
		while (!queue.isEmpty()) {
			items.add(queue.remove());
		}
		while (!items.isEmpty()) {
			queue.add(items.get(0));
		}
	}

	/**
	 * Print out the reaction heap for debugging
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Simulation.Reaction reaction : queue) {
			sb.append("X(t+" + reaction.getTau() + ") -> R"
					+ reaction.getReactionId() + "\n");
		}
		return sb.toString();
	}
}
