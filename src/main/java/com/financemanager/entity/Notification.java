package com.financemanager.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="notifications")
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=500) private String message;
 @Column(nullable=false) private Boolean isRead=false;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private User user;
 public Notification(){} public Notification(String m,User u){message=m;user=u;} public Long getId(){return id;} public String getMessage(){return message;} public Boolean getIsRead(){return isRead;} public LocalDateTime getCreatedAt(){return createdAt;} public User getUser(){return user;}
}
