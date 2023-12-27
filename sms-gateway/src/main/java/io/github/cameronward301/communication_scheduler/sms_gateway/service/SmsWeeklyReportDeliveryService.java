package io.github.cameronward301.communication_scheduler.sms_gateway.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException;
import io.github.cameronward301.communication_scheduler.gateway_library.service.ContentDeliveryService;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser;
import io.github.cameronward301.communication_scheduler.sms_gateway.properties.TwilioProperties;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SmsWeeklyReportDeliveryService implements ContentDeliveryService<SmsUser, UserUsage> {

    private final TwilioProperties twilioProperties;

    public SmsWeeklyReportDeliveryService(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
    }

    @Override
    public void sendContent(SmsUser smsUser, UserUsage userUsage) throws ContentDeliveryException {
        String content = String.format("""
                %s, Your weekly report is below!
                Malware blocked: %s,
                Adverts blocked: %s,
                Sites visited: %s
                
                Thanks for using our service!
                """, smsUser.getFirstName(), userUsage.getMalwareBlocked(), userUsage.getAdvertsBlocked(), userUsage.getSitesVisited());
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(smsUser.getPhoneNumber()),
                new com.twilio.type.PhoneNumber(twilioProperties.getFromPhoneNumber()),
                content
        ).create();

        checkStatus(message.getSid());
    }

    private void checkStatus(String messageSid) throws ContentDeliveryException {


        try (ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor()) {
            executor.scheduleAtFixedRate(() -> {
                while (true) {
                    Message message = Message.fetcher(messageSid).fetch();
                    switch (message.getStatus()) {
                        case FAILED, UNDELIVERED, CANCELED -> throw new ContentDeliveryException("Message failed to send: " + message.getErrorMessage());
                        case SENT, DELIVERED, READ -> {
                            return;
                        }
                    }
                }
            }, 3, twilioProperties.getPollingInterval(), TimeUnit.SECONDS);
        }
    }
}
