package com.notbestlord.core.rpg.quests;

public interface IQuest {
	boolean updateProgress(Object o);

	void completeQuest();

	boolean isQuestComplete();

	float getCompletePercentage();

	String getDescription();

	String getRawDescription();

	QuestID getQuestID();

}
