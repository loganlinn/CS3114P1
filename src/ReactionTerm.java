/**
 * Represents a single term in a reaction
 * @author loganlinn
 *
 */
public class ReactionTerm {
	private int speciesId;
	private int coefficient;
	
	public ReactionTerm(int speciesId, int coefficient){
		setSpeciesId(speciesId);
		setCoefficient(coefficient);
	}

	/**
	 * @return the speciesId
	 */
	public int getSpeciesId() {
		return speciesId;
	}

	/**
	 * @param speciesId the speciesId to set
	 */
	public void setSpeciesId(int speciesId) {
		this.speciesId = speciesId;
	}

	/**
	 * @return the coefficient
	 */
	public int getCoefficient() {
		return coefficient;
	}

	/**
	 * @param coefficient the coefficient to set
	 */
	public void setCoefficient(int coefficient) {
		this.coefficient = coefficient;
	}
}
