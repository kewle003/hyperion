package org.rs2server.rs2.model.quests;

/**
 * Holds quest data to be saved.
 * @author Emperor
 *
 */
public class QuestStorage {
	
	/**
	 * The current quest states.
	 */
	private final int[] QUEST_STATES = new int[QuestRepository.AMOUNT_OF_QUESTS];
	
	/**
	 * Gets the quest state by quest.
	 * @param questId The quest to check.
	 * @return The quest state (0 at default).
	 */
	public int getQuestStage(Quest questId) {
        return QUEST_STATES[questId.getQuestButton()];
    }

    /**
     * Gets the quest state.
     * @param questId The quest id to check.
     * @return The quest state (0 at default).
     */
    public int getQuestStage(int questId) {
        return QUEST_STATES[questId];
    }
	/**
	 * Checks if the player has started a certain quest.
	 * @param questId The quest's id.
	 * @return {@code True} if the player has started the quest, {@code false} if not.
	 */
	public boolean hasStarted(Quest questId) {
        Quest quest = QuestRepository.get(questId.getQuestButton());
		return QUEST_STATES[questId.getQuestButton()] > 0 && QUEST_STATES[questId.getQuestButton()] < quest.getFinalStage();
	}
	
	/**
	 * Checks if the player has finished a certain quest.
	 * @param questId The quest id.
	 * @return {@code True} if so, {@code false} if not.
	 */
    public boolean hasFinished(Quest questId) {
        Quest quest = QuestRepository.get(questId.getQuestButton());
        return quest != null && quest.getFinalStage() <= QUEST_STATES[questId.getQuestButton()];
    }

	/**
	 * Sets a current quest state.
	 * @param questId The quest's id.
	 * @param state The state to set.
	 */
	public void setQuestStage(Quest questId, int state) {
		QUEST_STATES[questId.getQuestButton()] = state;
	}

    /**
     * Sets a current quest state.
     * @param questId The quest's id.
     * @param state The state to set.
     */
    public void setQuestStage(int questId, int state) {
        QUEST_STATES[questId] = state;
    }
}