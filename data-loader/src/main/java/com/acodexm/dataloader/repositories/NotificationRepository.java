package com.acodexm.dataloader.repositories;

import com.devtiro.book.domain.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long>,
        PagingAndSortingRepository<Notification, Long> {
}
