package ee.ciszewsj.cockroachcoin.controller;


import ee.ciszewsj.cockroachcoin.service.MinerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MinerController {

    private final MinerService minerService;

    @PostMapping("/powerOnOff")
    public String startOrStopMining() {
        return minerService.startOrStopMining() ? "STARTED" : "STOPPED";

    }
}
