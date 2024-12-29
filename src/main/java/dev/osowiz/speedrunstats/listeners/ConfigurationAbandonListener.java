package dev.osowiz.speedrunstats.listeners;

import org.bukkit.conversations.ConversationAbandonedListener;

public class ConfigurationAbandonListener implements ConversationAbandonedListener {

    @Override
    public void conversationAbandoned(org.bukkit.conversations.ConversationAbandonedEvent event) {
        event.getContext().getForWhom().sendRawMessage("Conversation abandoned.");
    }

}
