package io.github.cameronward301.communication_scheduler.sms_gateway.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import io.github.cameronward301.communication_scheduler.sms_gateway.properties.TwilioProperties;
import lombok.Generated;
import org.springframework.stereotype.Service;

@Service
@Generated
public class TwilioService {

    private final TwilioProperties twilioProperties;

    public TwilioService(TwilioProperties twilioProperties) {
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
        this.twilioProperties = twilioProperties;
    }

    public Message sendSms(String toNumber, String body) {
        return Message.creator(
                        new com.twilio.type.PhoneNumber(toNumber),
                        new com.twilio.type.PhoneNumber("+" + twilioProperties.getFromPhoneNumber()),
                        body)
                .create();
    }

    public Message getMessageBySid(String sid) {
        return Message.fetcher(sid).fetch();
    }
}
