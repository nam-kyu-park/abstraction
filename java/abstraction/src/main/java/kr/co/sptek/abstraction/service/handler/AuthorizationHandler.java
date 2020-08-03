package kr.co.sptek.abstraction.service.handler;

import kr.co.sptek.abstraction.db.dto.AuthorizationDto;
import kr.co.sptek.abstraction.kafka.topic.AuthorizationTopic;
import kr.co.sptek.abstraction.db.mapper.AuthorizationMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHandler {

    private static final Logger logger = LogManager.getLogger(AuthorizationHandler.class);

    @Autowired
    AuthorizationMapper dbMapper;

    public boolean authorization(AuthorizationTopic topic) throws Exception {
        AuthorizationDto result = dbMapper.selectExpiryDate(topic.company_id, topic.mdc_id, topic.activation_code);
        long currentDate = Long.parseLong(topic.timestamp);
        long startDate = Long.parseLong(result.getStartDate());
        long endDate = Long.parseLong(result.getEndDate());
        return (startDate <= currentDate && currentDate <= endDate);
    }
    public boolean insertExpiryDate(AuthorizationTopic topic) throws Exception {
        long startDate = Long.parseLong(topic.timestamp);
        long endDate = startDate + 1000000000;
        insertExpiryDate(
                topic.company_id,
                topic.mdc_id,
                topic.activation_code,
                String.valueOf(startDate),
                String.valueOf(endDate));
        return true;
    }

    public boolean insertExpiryDate(String company, String mdc, String code, String start, String end) throws Exception {
        dbMapper.insertExpiryDate(company, mdc, code, start, end);
        return true;
    }
}
