package com.chandana.urlsafetychecker.controller;

import com.chandana.urlsafetychecker.model.BlackListEntry;
import com.chandana.urlsafetychecker.model.BlackListType;
import com.chandana.urlsafetychecker.service.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlackListController {

    @Autowired
    BlackListService blackListService;

    @PostMapping("api/v1/admin/blacklist")
    public BlackListEntry addToBlackList(@RequestParam String value,
                                         @RequestParam BlackListType type){
        return blackListService.addToBlackList(value,type);
    }
}
