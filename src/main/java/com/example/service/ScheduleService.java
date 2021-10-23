package com.example.service;

import com.example.controller.ScheduleController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    AuthUserService authUserService;

}
