package io.github.cameronward301.communication_scheduler.email_gateway.model

import spock.lang.Specification

class EmailContentTest extends Specification {
    def "setter should set value"() {
        given: "EmailContent"
        EmailContent emailContent = new EmailContent()

        when: "setters are called"
        emailContent.setMinutesListenedLastMonth(1234)
        emailContent.setUserId("test-user-id")
        emailContent.setTopGenreLastMonth("test-genre")
        emailContent.setTopSongLastMonth("test-song")
        emailContent.setId("test-content-id")

        then: "values are set"
        emailContent.getMinutesListenedLastMonth() == 1234
        emailContent.getUserId() == "test-user-id"
        emailContent.getTopGenreLastMonth() == "test-genre"
        emailContent.getTopSongLastMonth() == "test-song"
        emailContent.getId() == "test-content-id"
    }

    def "content string should be correct"() {
        given: "EmailContent"
        EmailContent emailContent = new EmailContent()
        emailContent.setMinutesListenedLastMonth(1234)
        emailContent.setUserId("test-user-id")
        emailContent.setTopGenreLastMonth("test-genre")
        emailContent.setTopSongLastMonth("test-song")
        emailContent.setId("test-content-id")

        when: "toString is called"
        String contentString = emailContent.getContentString()

        then: "string is correct"
        contentString == "1234 test-genre test-song"
    }
}
