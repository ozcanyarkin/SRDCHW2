package com.SRDCHW2.services;

import com.SRDCHW2.models.Message;
import com.SRDCHW2.models.User;
import com.SRDCHW2.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    public Page<Message> getInbox(String username, Pageable pageable) {
        return messageRepository.findByReceiver(username, pageable);
    }

    public Page<Message> getOutbox(String username, Pageable pageable) {
        return messageRepository.findBySender(username, pageable);
    }

    public Page<Message> getFilteredMessages(String field, String value, Pageable pageable) {
        return messageRepository.findMessagesByFieldAndValue(field, value, pageable);
    }

    public Page<Message> getFilteredInboxMessages(String username, String field, String value, Pageable pageable) {
        return messageRepository.findInboxMessagesByFieldAndValue(username, field, value, pageable);
    }


}
