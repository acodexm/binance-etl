package com.acodexm.dataloader.controllers;

import com.devtiro.book.domain.Notification;
import com.devtiro.book.services.NotificationsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationsController {

    private final NotificationsService notificationsService;

    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @GetMapping(path="/notifications")
    public Page<Notification> listNotifications(final Pageable pagable) {
        return notificationsService.listNotifications(pagable);
    }

}
