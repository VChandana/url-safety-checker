package com.chandana.urlsafetychecker.service;

import com.chandana.urlsafetychecker.model.BlackListEntry;
import com.chandana.urlsafetychecker.model.BlackListType;
import com.chandana.urlsafetychecker.repository.BlackListEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlackListService {

    @Autowired
    private BlackListEntryRepository blackListEntryRepository;

    public boolean isBlackListed(String value, BlackListType type){
       return blackListEntryRepository.existsByValueAndType(value,type);
    }

    public BlackListEntry addToBlackList(String value, BlackListType type){
        BlackListEntry entry = new BlackListEntry();
        entry.setValue(value);
        entry.setType(type);

        return blackListEntryRepository.save(entry);
    }
}
