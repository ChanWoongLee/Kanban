package com.intern.kanban.version.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intern.kanban.card.dao.CardDAO;
import com.intern.kanban.card.model.Card;
import com.intern.kanban.version.dao.VersionDAO;
import com.intern.kanban.version.model.Version;
import com.intern.kanban.version.model.dto.BasicVersionInfo;
import com.intern.kanban.version.model.dto.DetailVersionInfo;

@Service
public class VersionService {

	private static final float THRESHOLD = 0.0F;

	@Autowired
	VersionDAO versionDAO;

	@Autowired
	CardDAO cardDAO;

	public List<BasicVersionInfo> findVersionInfos(long cardId) {
		return versionDAO.selectVersionInfos(cardId);
	}

	public DetailVersionInfo findDetailVersionInfo(long cardId, long versionNum) {
		Map<String, Object> param = new HashMap<>();
		param.put("cardId", cardId);
		param.put("versionNum", versionNum);
		return versionDAO.selectDetailVersionInfo(param);
	}

	@Transactional
	public List<BasicVersionInfo> updateRollback(long cardId, long rollbackVersionNum) {

		Map<String, Object> param = new HashMap<>();
		param.put("cardId", cardId);
		param.put("versionNum", rollbackVersionNum);

		DetailVersionInfo detailVersionInfo = versionDAO.selectDetailVersionInfo(param);
		param.put("title", detailVersionInfo.getTitle());
		param.put("content", detailVersionInfo.getContent());

		long recentVersionNum;
		synchronized (this) {
			Card recentCard = cardDAO.selectById(cardId);
			recentVersionNum = recentCard.getVersionNum();

			param.put("versionNum", recentVersionNum + 1);

			cardDAO.updateVersion(param);
		}
		Version newVersion = new Version(cardId, recentVersionNum + 1, detailVersionInfo.getTitle(),
			detailVersionInfo.getContent(), detailVersionInfo.getModifier(), LocalDateTime.now());
		versionDAO.insert(newVersion);

		return versionDAO.selectVersionInfos(cardId);
	}
}
