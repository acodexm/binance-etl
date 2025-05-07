package com.acodexm.dataloader.services;

import com.devtiro.book.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationsService {

    Notification save(Notification book);

    Page<Notification> listNotifications(Pageable pageable);
}
