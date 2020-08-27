package com.intern.kanban.version.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intern.kanban.version.model.dto.BasicVersionInfo;
import com.intern.kanban.version.model.dto.DetailVersionInfo;
import com.intern.kanban.version.model.dto.RollbackVersion;
import com.intern.kanban.version.service.VersionService;

@RestController
@RequestMapping("/api/card/version")
public class VersionController {

	@Autowired
	private VersionService versionService;

	@GetMapping
	public List<BasicVersionInfo> findVersionInfos(@RequestParam long cardId) {
		return versionService.findVersionInfos(cardId);
	}

	@GetMapping("/fetch")
	public DetailVersionInfo findDetailVersionInfo(@RequestParam long cardId, @RequestParam long versionNum) {
		return versionService.findDetailVersionInfo(cardId, versionNum);
	}

	@PatchMapping("/rollback")
	public List<BasicVersionInfo> updateRollback(@RequestBody RollbackVersion rollbackVersion) {
		return versionService.updateRollback(rollbackVersion.getCardId(), rollbackVersion.getRollbackVersionNum());
	}
}
