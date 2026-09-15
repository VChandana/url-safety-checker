package com.chandana.urlsafetychecker.repository;


import com.chandana.urlsafetychecker.model.BlackListEntry;
import com.chandana.urlsafetychecker.model.BlackListType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListEntryRepository extends JpaRepository<BlackListEntry,Long> {

    boolean existsByValueAndType(String value, BlackListType type);
}
