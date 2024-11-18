package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/block")
@RequiredArgsConstructor
public class BlockController {
	private final BlockService blockService;

	@GetMapping
	public List<BlockDto> getBlock() {
		return blockService.getBlockList();
	}

	@PostMapping
	public void postBlocks(List<BlockDto> blockChain) {
		blockService.postNewBlockChain(blockChain);
	}
}
