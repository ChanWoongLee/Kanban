package com.intern.kanban.history.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intern.kanban.history.model.LoggedEventForAdmin;
import com.intern.kanban.history.model.LoggedEventForClient;
import com.intern.kanban.history.service.HistoryService;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

	@Autowired
	private HistoryService historyService;

	@GetMapping("/admin")
	public List<LoggedEventForAdmin> findAllHistoryForAdmin(@RequestParam int offset,
		@RequestParam(required = false) String modifiedEntity,
		@RequestParam(required = false) String actionName) {
		return historyService.findForAdmin(offset, modifiedEntity, actionName);
	}

	@GetMapping("/client")
	public List<LoggedEventForClient> findAllHistoryForClient(@RequestParam int offset) {
		return historyService.findAllForClient(offset);
	}

}
