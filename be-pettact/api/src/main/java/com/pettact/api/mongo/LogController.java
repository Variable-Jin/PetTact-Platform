package com.pettact.api.mongo;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/logs")
@RequiredArgsConstructor
public class LogController {

    private final MongoTemplate mongoTemplate;

    @GetMapping
    public List<Document> getLogs(
    		@RequestParam(name = "uri", required = false) String uri,
    		@RequestParam(name = "ip", required = false) String ip,
    		@RequestParam(name = "userId",required = false) Long userId
    ) {
        Query query = new Query();

        if (uri != null && !uri.isEmpty()) {
            query.addCriteria(Criteria.where("uri").regex(uri));
        }
        if (ip != null && !ip.isEmpty()) {
            query.addCriteria(Criteria.where("ip").regex(ip));
        }
        if (userId != null) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }

        query.with(Sort.by(Sort.Direction.DESC, "timestamp")).limit(100);
        return mongoTemplate.find(query, Document.class, "httplogs");
    }
}
