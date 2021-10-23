package com.example.controller;

import com.example.exceptions.AppException;
import com.example.service.FileService;
import com.example.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@EnableAsync
@Component
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    FileService fileService;


    public void Test() throws AppException {

    }


}
