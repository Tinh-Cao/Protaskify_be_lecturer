package com.protaskify.protaskify_api.service;

import com.protaskify.protaskify_api.model.enity.Messages;
import com.protaskify.protaskify_api.model.request.SendMessageRequest;
import com.protaskify.protaskify_api.repository.LecturerRepository;
import com.protaskify.protaskify_api.repository.MessagesRepository;
import com.protaskify.protaskify_api.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessagesRepository messagesRepository;

    public String saveMessageFromJSON(Messages messages){
        messagesRepository.save(messages);
        return messages.getContent();
    }

//    public Messages getMessageInfo(SendMessageRequest request) {
//        return new Messages().builder()
//                .content(request.getContent())
//                .lecturer(lecturerRepository.findById(request.getLecturerId()).get())
//                .student(studentRepository.findById(request.getStudentId()).get())
//                .date(request.getDate())
//                .fromId(request.getFromId())
//                .status(false)
//                .build();
//    }
}
