package com.xing.imheres.controller;

/**
 * @author xinG
 * @date 2018/8/2 0002 17:12
 */

import com.xing.imheres.entity.Message;
import com.xing.imheres.repository.MessageRepository;
import com.xing.imheres.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MessageController {

    @Autowired
    private MessageRepository msgRepository;

    @GetMapping(value="/getMessages")
    public List<Message> getMessages(@RequestParam double lat,@RequestParam double lon){
        List<Message> all = msgRepository.findAll();
        List<Message> msgs = new ArrayList<>();
        for(Message msg : all)
            if(CommonUtils.getDistance(lat,lon,msg.getLat(),msg.getLon()) < 1000)
                msgs.add(msg);
        return msgs;
    }


    @PostMapping(value="/addMessage")
    public Message addMessage(@RequestBody Message msg){
        return msgRepository.save(msg);
    }

}
