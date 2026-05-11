package com.tyrkanych.facade;

import com.tyrkanych.dto.UserDto;
import com.tyrkanych.dto.UserRegistrationDto;
import com.tyrkanych.entity.Match;
import com.tyrkanych.entity.Message;
import com.tyrkanych.service.LikeService;
import com.tyrkanych.service.MatchService;
import com.tyrkanych.service.MessageService;
import com.tyrkanych.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HeartSyncFacade {

    private final UserService userService;
    private final LikeService likeService;
    private final MatchService matchService;
    private final MessageService messageService;

    @Autowired
    public HeartSyncFacade(UserService userService,
            LikeService likeService,
            MatchService matchService,
            MessageService messageService) {
        this.userService = userService;
        this.likeService = likeService;
        this.matchService = matchService;
        this.messageService = messageService;
    }

    public UserDto registerUser(UserRegistrationDto dto) {
        return userService.register(dto);
    }

    public void likeUser(Long fromUserId, Long toUserId) {
        likeService.addLike(fromUserId, toUserId);
        if (likeService.existsLike(toUserId, fromUserId)) {
            matchService.createMatch(fromUserId, toUserId, null);
        }
    }

    public Message sendMessage(Long senderId, Long receiverId, String text) {
        return messageService.sendMessage(senderId, receiverId, text);
    }

    public List<Match> getMatches(Long userId) {
        return matchService.findByUserId(userId);
    }
}