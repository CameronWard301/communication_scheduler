package io.github.cameronward301.communication_scheduler.integration_tests.step.definitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.cameronward301.communication_scheduler.integration_tests.model.CodecDTO;
import io.github.cameronward301.communication_scheduler.workflows.communication_workflow.codec.CryptographyCodec;
import io.temporal.api.common.v1.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RequiredArgsConstructor
public class DataConverterAPIStepDefinitions {
    private final String TEST_DATA = "Test-Data";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CryptographyCodec cryptographyCodec;

    @Value("${data-converter-api.address}")
    private String gatewayAPIUrl;

    private Payload payloadRequest;
    private Payload payloadResponse;
    private ResponseEntity<CodecDTO> converterResponse;

    @Given("I have an unencrypted payload")
    public void createTestPayload() {
        payloadRequest = Payload.newBuilder().setData(ByteString.copyFromUtf8(TEST_DATA)).build();
    }

    @Given("I have an encrypted payload")
    public void iHaveADecryptedPayload() {
        createTestPayload();
        payloadRequest = cryptographyCodec.encode(List.of(payloadRequest)).get(0);
    }

    @When("I POST to data-converter {string}")
    public void iPOSTToDataConverterEncode(String method) throws InvalidProtocolBufferException, JsonProcessingException {

        converterResponse = restTemplate.exchange(gatewayAPIUrl + "/" + method, HttpMethod.POST, new HttpEntity<>(CodecDTO.builder()
                .payloads(List.of(
                        objectMapper.readTree(
                                JsonFormat.printer().print(payloadRequest))
                )).build()), CodecDTO.class);
    }

    @Then("the response returned contains {int} payload")
    public void theResponseReturnedContainsPayload(int numPayloads) {
        assertEquals(numPayloads, Objects.requireNonNull(converterResponse.getBody()).getPayloads().size());
    }

    @Then("I convert the response back to a payload")
    public void iConvertTheResponseBackToAPayload() throws InvalidProtocolBufferException {
        Payload.Builder builder = Payload.newBuilder();
        JsonFormat.parser().merge(Objects.requireNonNull(converterResponse.getBody()).getPayloads().get(0).toString(), builder);
        payloadResponse = builder.build();
    }

    @Then("the payload returned is encrypted")
    public void thePayloadReturnedIsEncrypted() {
        assertFalse(payloadResponse.getData().toStringUtf8().contains(TEST_DATA));
    }


    @And("the encrypted headers have been set")
    public void theEncryptedHeadersHaveBeenSet() {
        assertEquals("binary/encrypted", payloadResponse.getMetadataOrDefault("encoding", null).toStringUtf8());
        assertEquals("AES/GCM/NoPadding", payloadResponse.getMetadataOrDefault("encryption-cipher", null).toStringUtf8());
    }


    @And("the payload returned is decrypted")
    public void thePayloadReturnedIsDecrypted() {
        assertEquals(TEST_DATA, payloadResponse.getData().toStringUtf8());
    }

    @And("the decrypted headers have been set")
    public void theDecryptedHeadersHaveBeenSet() {
        assertEquals("json/plain", payloadResponse.getMetadataOrDefault("encoding", null).toStringUtf8());

    }
}
