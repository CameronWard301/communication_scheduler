package io.github.cameronward301.communication_scheduler.sms_gateway.service;

import com.twilio.rest.api.v2010.account.Message;
import io.github.cameronward301.communication_scheduler.gateway_library.exception.ContentDeliveryException;
import io.github.cameronward301.communication_scheduler.gateway_library.service.ContentDeliveryService;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.SmsUser;
import io.github.cameronward301.communication_scheduler.sms_gateway.model.UserUsage;
import io.github.cameronward301.communication_scheduler.sms_gateway.properties.TwilioProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class SmsWeeklyReportDeliveryService implements ContentDeliveryService<SmsUser, UserUsage> {

    private final TwilioProperties twilioProperties;
    private final TwilioService twilioService;

    @Override
    public void sendContent(SmsUser smsUser, UserUsage userUsage) throws ContentDeliveryException {
        String content = String.format("""
                %s, Your weekly report is below!
                Malware blocked: %s,
                Adverts blocked: %s,
                Sites visited: %s
                                
                Thanks for using our service!
                """, smsUser.getFirstName(), userUsage.getMalwareBlocked(), userUsage.getAdvertsBlocked(), userUsage.getSitesVisited());
        Message message = twilioService.sendSms(smsUser.getPhoneNumber(), content);

        checkStatus(message.getSid());
    }

    void checkStatus(String messageSid) throws ContentDeliveryException {
        for (int i = 0; i < twilioProperties.getMaximumPollingAttempts(); i++) {
            log.debug("Checking message status for sid: {}", messageSid);
            Message message = twilioService.getMessageBySid(messageSid);
            switch (message.getStatus()) {
                case FAILED, UNDELIVERED, CANCELED -> throw new ContentDeliveryException("Message failed to send: " + message.getErrorMessage());
                case SENT, DELIVERED, READ -> {
                    return;
                }
                default -> {
                    try {
                        log.debug("Message status: {} waiting: {} seconds before trying again", message.getStatus(), twilioProperties.getPollingInterval());
                        //noinspection BusyWait
                        Thread.sleep(twilioProperties.getPollingInterval() * 1000L);
                    } catch (InterruptedException e) {
                        log.error("Interrupted while waiting for message status", e);
                        throw new ContentDeliveryException("Interrupted while waiting for message status");
                    }
                }
            }
        }
        throw new ContentDeliveryException("Could not resolve message status after " + twilioProperties.getMaximumPollingAttempts() + " attempts");
    }
}
