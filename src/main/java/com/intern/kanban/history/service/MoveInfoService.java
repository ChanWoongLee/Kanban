package com.intern.kanban.history.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intern.kanban.history.dao.MovedInfoDao;
import com.intern.kanban.history.model.MovedInfo;

@Service
public class MoveInfoService {

	@Autowired
	MovedInfoDao movedInfoDao;

	public void saveMoveInfo(MovedInfo movedInfo) {
		long eventId = movedInfoDao.insert(movedInfo);
	}
}
