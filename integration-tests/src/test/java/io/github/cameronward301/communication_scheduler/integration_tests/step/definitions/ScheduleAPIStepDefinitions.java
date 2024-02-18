package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.model.schedule.*;
import io.github.cameronward301.communication_scheduler.integration_tests.world.World;
import io.temporal.client.schedules.ScheduleCalendarSpec;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.common.SearchAttributeKey;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScheduleAPIStepDefinitions {
    private final World world;
    private final RestTemplate restTemplate;
    private final ScheduleEntity scheduleEntity;
    private final ScheduleClient scheduleClient;
    private final ModelMapper modelMapper = new ModelMapper();

    private CreateScheduleDTO createScheduleRequest;
    private SchedulePatchDTO patchDTORequest;
    private ResponseEntity<ScheduleDescriptionDTO> responseResponseEntity;
    private ResponseEntity<ModifiedDTO> modifiedDTOResponseEntity;
    private ResponseEntity<ScheduleListDescriptionDTO> scheduleListResponseEntity;
    private ResponseEntity<CountDTO> countDTOResponseEntity;
    private ResponseEntity<Void> deleteResponseEntity;

    private String userIdFilter;
    private String gatewayIdFilter;
    private String pageNumber;
    private String pageSize;

    @Value("${schedule-api.address}")
    private String scheduleAPIURL;

    public ScheduleAPIStepDefinitions(World world, RestTemplate restTemplate, ScheduleEntity scheduleEntity, ScheduleClient scheduleClient) {
        this.world = world;
        this.restTemplate = restTemplate;
        this.scheduleEntity = scheduleEntity;
        this.scheduleClient = scheduleClient;
    }

    @Given("I have a schedule with the following details:")
    public void iHaveAScheduleWithTheFollowingDetails(DataTable schedule) {
        createScheduleRequest = CreateScheduleDTO.builder()
                .paused(Boolean.parseBoolean(schedule.asMaps().get(0).get("paused")))
                .gatewayId(schedule.asMaps().get(0).get("gatewayId"))
                .userId(schedule.asMaps().get(0).get("userId"))
                .build();
        if (schedule.asMaps().get(0).get("calendar") != null) {
            createScheduleRequest.setCalendar(getCalendarSpecDTO(schedule.asMaps().get(0).get("calendar")));
        }
        if (schedule.asMaps().get(0).get("interval") != null) {
            createScheduleRequest.setInterval(getIntervalSpecDTo(schedule.asMaps().get(0).get("interval")));
        }
        if (schedule.asMaps().get(0).get("cron") != null) {
            createScheduleRequest.setCronExpression(schedule.asMaps().get(0).get("cron"));
        }
        if (schedule.asMaps().get(0).get("scheduleId") != null) {
            if (!Objects.equals(schedule.asMaps().get(0).get("scheduleId"), "null")) {
                createScheduleRequest.setScheduleId(schedule.asMaps().get(0).get("scheduleId"));
            }
        } else {
            createScheduleRequest.setScheduleId(scheduleEntity.getScheduleId());
        }
    }

    @And("I set the pageSize to be {string}")
    public void iSetThePageSizeToBe(String pageSize) {
        this.pageSize = pageSize;
    }

    @And("I set the pageNumber to be {string}")
    public void iSetThePageNumberToBe(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    @And("I have the following patch DTO:")
    public void iHaveTheFollowingPatchDTO(DataTable dataTable) {
        patchDTORequest = SchedulePatchDTO.builder()
                .paused(Boolean.parseBoolean(dataTable.asMaps().get(0).get("paused")))
                .gatewayId(dataTable.asMaps().get(0).get("gatewayId"))
                .build();

    }

    @And("I set the userId filter to be: {string}")
    public void iSetTheUserIdFilterToBe(String userId) {
        userIdFilter = userId;
    }

    @And("I set the gatewayId filter to be: {string}")
    public void iSetTheGatewayIdFilterToBe(String gatewayId) {
        gatewayIdFilter = gatewayId;
    }

    @When("I get the schedule by id")
    public void iGetTheScheduleById() {
        try {
            responseResponseEntity = restTemplate.exchange(scheduleAPIURL + "/" + scheduleEntity.getScheduleId(), HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), ScheduleDescriptionDTO.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I get the schedule by an unknown id: {string}")
    public void iGetTheScheduleByAnUnknownId(String id) {
        try {
            responseResponseEntity = restTemplate.exchange(scheduleAPIURL + "/" + id, HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), ScheduleDescriptionDTO.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I create the schedule")
    public void iCreateTheSchedule() {
        try {
            responseResponseEntity = restTemplate.exchange(scheduleAPIURL, HttpMethod.POST, new HttpEntity<>(createScheduleRequest, world.getHttpHeaders()), ScheduleDescriptionDTO.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I update the schedule")
    public void iUpdateTheSchedule() {
        try {
            responseResponseEntity = restTemplate.exchange(scheduleAPIURL, HttpMethod.PUT, new HttpEntity<>(createScheduleRequest, world.getHttpHeaders()), ScheduleDescriptionDTO.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I get all schedules")
    public void iGetAllSchedules() throws URISyntaxException, MalformedURLException, InterruptedException {
        try {
            URIBuilder uriBuilder = getUriBuilder();
            Thread.sleep(2000); // wait for entities to be created before querying
            scheduleListResponseEntity = restTemplate.exchange(uriBuilder.build().toURL().toString(), HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), ScheduleListDescriptionDTO.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I batch update existing schedules")
    public void batchUpdateExistingSchedules() throws URISyntaxException, MalformedURLException, InterruptedException {
        try {
            URIBuilder uriBuilder = getUriBuilder();
            Thread.sleep(2000); // wait for entities to be created before querying
            modifiedDTOResponseEntity = restTemplate.exchange(uriBuilder.build().toURL().toString(), HttpMethod.PATCH, new HttpEntity<>(patchDTORequest, world.getHttpHeaders()), ModifiedDTO.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I batch delete schedules")
    public void iBatchDeleteSchedules() throws URISyntaxException, InterruptedException, MalformedURLException {
        try {
            URIBuilder uriBuilder = getUriBuilder();
            Thread.sleep(2000); // wait for entities to be created before deleting
            modifiedDTOResponseEntity = restTemplate.exchange(uriBuilder.build().toURL().toString(), HttpMethod.DELETE, new HttpEntity<>(patchDTORequest, world.getHttpHeaders()), ModifiedDTO.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I get the schedule count")
    public void iGetTheScheduleCount() throws URISyntaxException, InterruptedException, MalformedURLException {
        try {
            URIBuilder uriBuilder = getUriBuilder();
            uriBuilder.setPath("schedule/count");
            Thread.sleep(2000); // wait for entities to be created before deleting
            countDTOResponseEntity = restTemplate.exchange(uriBuilder.build().toURL().toString(), HttpMethod.GET, new HttpEntity<>(world.getHttpHeaders()), CountDTO.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I delete the schedule by id")
    public void iDeleteTheScheduleById() {
        try {
            deleteResponseEntity = restTemplate.exchange(scheduleAPIURL + "/" + scheduleEntity.getScheduleId(), HttpMethod.DELETE, new HttpEntity<>(world.getHttpHeaders()), Void.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @When("I delete the schedule by an unknown id: {string}")
    public void iDeleteTheScheduleByAnUnknownId(String id) {
        try {
            deleteResponseEntity = restTemplate.exchange(scheduleAPIURL + "/" + id, HttpMethod.DELETE, new HttpEntity<>(world.getHttpHeaders()), Void.class);
        } catch (HttpClientErrorException e) {
            world.setHttpClientErrorException(e);
        }
    }

    @Then("the schedule is returned with a status code of {int}")
    public void theScheduleIsReturnedWithAStatusCodeOf(int status) {
        assertEquals(status, responseResponseEntity.getStatusCode().value());
        assertEquals(scheduleEntity.getScheduleId(), Objects.requireNonNull(responseResponseEntity.getBody()).getId());
        assertEquals(scheduleEntity.getScheduleOptions().getTypedSearchAttributes().getUntypedValues().get(SearchAttributeKey.forKeyword("userId")), responseResponseEntity.getBody().getSearchAttributes().get("userId").get(0));
        assertEquals(scheduleEntity.getScheduleOptions().getTypedSearchAttributes().getUntypedValues().get(SearchAttributeKey.forKeyword("gatewayId")), responseResponseEntity.getBody().getSearchAttributes().get("gatewayId").get(0));
        assertEquals(scheduleEntity.getScheduleOptions().getTypedSearchAttributes().getUntypedValues().get(SearchAttributeKey.forKeyword("scheduleId")), responseResponseEntity.getBody().getSearchAttributes().get("scheduleId").get(0));
        assertEquals(Objects.requireNonNull(scheduleEntity.getSchedule().getState()).isPaused(), responseResponseEntity.getBody().getSchedule().getState().isPaused());

        if (scheduleEntity.getSchedule().getSpec().getCalendars() == null) {
            assertEquals(List.of(), responseResponseEntity.getBody().getSchedule().getSpec().getCalendars());
        } else {
            assertEquals(scheduleEntity.getSchedule().getSpec().getCalendars().get(0), modelMapper.map(responseResponseEntity.getBody().getSchedule().getSpec().getCalendars().get(0), ScheduleCalendarSpec.class));
        }

        if (scheduleEntity.getSchedule().getSpec().getIntervals() == null) {
            assertEquals(List.of(), responseResponseEntity.getBody().getSchedule().getSpec().getIntervals());
        } else {
            assertEquals(scheduleEntity.getSchedule().getSpec().getIntervals(), responseResponseEntity.getBody().getSchedule().getSpec().getIntervals());
        }

        if (scheduleEntity.getSchedule().getSpec().getCronExpressions() == null) {
            assertEquals(List.of(), responseResponseEntity.getBody().getSchedule().getSpec().getCronExpressions());
        } else {
            assertEquals(scheduleEntity.getSchedule().getSpec().getCronExpressions(), responseResponseEntity.getBody().getSchedule().getSpec().getCronExpressions());
        }
    }

    @Then("a modified DTO is returned with status {int} and message {string} and totalModified is {int}")
    public void aModifiedDTOIsReturnedWithStatusAndMessageAndTotalModifiedIs(int status, String message, int totalModified) {
        assertEquals(status, modifiedDTOResponseEntity.getStatusCode().value());
        assertEquals(message, Objects.requireNonNull(modifiedDTOResponseEntity.getBody()).getMessage());
        assertEquals(totalModified, modifiedDTOResponseEntity.getBody().getTotalModified());
    }

    @Then("the new or updated schedule is returned with status code of {int}")
    public void theNewScheduleIsReturnedWithStatusCodeOf(int status) {
        assertEquals(status, responseResponseEntity.getStatusCode().value());
        assertEquals(createScheduleRequest.isPaused(), Objects.requireNonNull(responseResponseEntity.getBody()).getSchedule().getState().isPaused());
        assertEquals(createScheduleRequest.getUserId(), Objects.requireNonNull(responseResponseEntity.getBody()).getSearchAttributes().get("userId").get(0));
        assertEquals(createScheduleRequest.getGatewayId(), responseResponseEntity.getBody().getSearchAttributes().get("gatewayId").get(0));
        assertNotNull(responseResponseEntity.getBody().getSearchAttributes().get("scheduleId").get(0));
        assertNotNull(responseResponseEntity.getBody().getId());

        if (createScheduleRequest.getCalendar() != null) {
            assertEquals(createScheduleRequest.getCalendar(), responseResponseEntity.getBody().getSchedule().getSpec().getCalendars().get(0));
        }
        if (createScheduleRequest.getInterval() != null) {
            assertEquals(Duration.parse(createScheduleRequest.getInterval().getEvery()), responseResponseEntity.getBody().getSchedule().getSpec().getIntervals().get(0).getEvery());
            assertEquals(Duration.parse(createScheduleRequest.getInterval().getOffset()), responseResponseEntity.getBody().getSchedule().getSpec().getIntervals().get(0).getOffset());
        }
        if (createScheduleRequest.getCronExpression() != null) {
            assertNotNull(responseResponseEntity.getBody().getSchedule().getSpec().getCalendars().get(0));
        }
    }

    @Then("I receive a page of schedules with {int} items with status code {int}")
    public void iReceiveAPageOfSchedulesWithItems(int numberOfItems, int statusCode) {
        assertEquals(statusCode, scheduleListResponseEntity.getStatusCode().value());
        assertEquals(numberOfItems, Objects.requireNonNull(scheduleListResponseEntity.getBody()).getNumberOfElements());
    }

    @Then("the schedule is deleted with response code {int}")
    public void theScheduleIsDeletedWithResponseCode(int statusCode) {
        assertEquals(statusCode, deleteResponseEntity.getStatusCode().value());
    }

    @And("the test framework removes the schedule")
    public void theTestFrameworkRemovesTheSchedule() {
        scheduleClient.getHandle(Objects.requireNonNull(responseResponseEntity.getBody()).getId()).delete();
    }


    private CreateScheduleDTO.ScheduleIntervalSpecDTO getIntervalSpecDTo(String interval) {
        if (interval.equals("10 seconds")) {
            return new CreateScheduleDTO.ScheduleIntervalSpecDTO("PT10S", "PT0S");
        }
        throw new RuntimeException(format("Interval specification not implemented in step definition for '%s'", interval));

    }

    private CreateScheduleDTO.ScheduleCalendarSpecDTO getCalendarSpecDTO(String calendar) {
        if (calendar.equals("every year")) {
            return CreateScheduleDTO.ScheduleCalendarSpecDTO.builder()
                    .seconds(List.of(getScheduleRange(0, 0, 1)))
                    .minutes(List.of(getScheduleRange(0, 0, 1)))
                    .hour(List.of(getScheduleRange(0, 0, 1)))
                    .dayOfMonth(List.of(getScheduleRange(1, 1, 1)))
                    .month(List.of(getScheduleRange(1, 1, 1)))
                    .year(List.of())
                    .dayOfWeek(List.of(getScheduleRange(0, 6, 1)))
                    .build();
        }
        throw new RuntimeException(format("Calendar specification not implemented in step definition for '%s'", calendar));
    }


    @SuppressWarnings("SameParameterValue")
    private CreateScheduleDTO.ScheduleRangeDTO getScheduleRange(int start, int end, int step) {
        return new CreateScheduleDTO.ScheduleRangeDTO(start, end, step);
    }

    @NotNull
    private URIBuilder getUriBuilder() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(scheduleAPIURL);
        if (pageNumber != null) {
            uriBuilder.addParameter("pageNumber", pageNumber);
        }
        if (pageSize != null) {
            uriBuilder.addParameter("pageSize", pageSize);
        }
        if (userIdFilter != null) {
            uriBuilder.addParameter("userId", userIdFilter);
        }

        if (gatewayIdFilter != null) {
            uriBuilder.addParameter("gatewayId", gatewayIdFilter);
        }
        return uriBuilder;
    }


    @Then("a CountDTO is returned with total: {int} and status code {int}")
    public void aCountDTOIsReturnedWithTotal(int total, int statusCode) {
        assertEquals(statusCode, countDTOResponseEntity.getStatusCode().value());
        assertEquals(total, Objects.requireNonNull(countDTOResponseEntity.getBody()).getTotal());
    }


}
