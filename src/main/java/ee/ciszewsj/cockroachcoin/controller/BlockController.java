package ee.ciszewsj.cockroachcoin.controller;

import ee.ciszewsj.cockroachcoin.data.BlockDto;
import ee.ciszewsj.cockroachcoin.service.BlockService;
import ee.ciszewsj.cockroachcoin.service.MinerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/block")
@RequiredArgsConstructor
public class BlockController {
	private final BlockService blockService;
	private final MinerService minerService;

	@GetMapping
	public List<BlockDto> getBlockList() {
		return blockService.getBlockList();
	}

	@GetMapping("/validate")
	public boolean getBlockValidate() {
		return BlockService.validateBlockChain(blockService.getBlockList());
	}

	@PostMapping
	public void postBlocks(@RequestBody List<BlockDto> blockChain) {
		blockService.onNewBlockChainReceived(blockChain);
	}

	@PostMapping("/new")
	public void newBlock(@RequestBody BlockDto blockDto) throws InterruptedException {
		blockService.onNewBlockReceived(blockDto);
	}

	@PostMapping("/miner")
	public void mining(@RequestBody Boolean mine) {
		if (mine) {
			minerService.startMiner();
		} else {
			minerService.stopMiner();
		}
	}
}
