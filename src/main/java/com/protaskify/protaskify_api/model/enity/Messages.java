package com.protaskify.protaskify_api.model.enity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Messages {
    //--------------------Attribute--------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", columnDefinition = "INT")
    private Long id;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private Date date;

    @Column(columnDefinition = "CHAR(10)")
    private String fromId;

    private boolean status;


    //--------------------Relationship--------------------
//    @ManyToOne
//    @JsonIgnore
//    @JoinColumn(name = "lecturer_id")
    private String lecturerId;

//    @ManyToOne
//    @JsonIgnore
//    @JoinColumn(name = "student_id")
    private String studentId;
}
