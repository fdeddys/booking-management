package com.ddabadi.booking_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UtilService {

    Sort generateSort (String...  args){
        Sort sort = Sort.by(Sort.Order.asc(args[0]));
        if (args.length >1) {
            for (int i = 1; i<args.length; i++) {
                sort = sort.and(Sort.by(args[i]).ascending());
            }
        }
        log.info("Sort : {}", sort);
        return sort;
    }
}
