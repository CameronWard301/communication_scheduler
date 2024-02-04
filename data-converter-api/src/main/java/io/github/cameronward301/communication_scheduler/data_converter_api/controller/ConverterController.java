package io.github.cameronward301.communication_scheduler.data_converter_api.controller;

import io.github.cameronward301.communication_scheduler.data_converter_api.model.CodecDTO;
import io.github.cameronward301.communication_scheduler.data_converter_api.service.CodecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/codec")
@RequiredArgsConstructor
public class ConverterController {

    private final CodecService codecService;

    @PostMapping("/encode")
    @CrossOrigin
    public ResponseEntity<CodecDTO> encode(@RequestBody CodecDTO requestDTO) {
        return ResponseEntity.ok(codecService.encrypt(requestDTO));
    }

    @PostMapping("/decode")
    @CrossOrigin
    public ResponseEntity<CodecDTO> decode(@RequestBody CodecDTO requestDTO) {
        return ResponseEntity.ok(codecService.decrypt(requestDTO));
    }
}
