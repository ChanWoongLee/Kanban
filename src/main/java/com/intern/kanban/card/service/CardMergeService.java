package com.intern.kanban.card.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intern.kanban.card.dao.CardDAO;
import com.intern.kanban.card.error.exception.ConflictException;
import com.intern.kanban.card.model.Card;
import com.intern.kanban.card.model.CardOperation;
import com.intern.kanban.card.model.dto.EditCard;
import com.intern.kanban.card.model.dto.LineInfo;
import com.intern.kanban.card.model.dto.UpdatedCardInfo;
import com.intern.kanban.version.dao.VersionDAO;
import com.intern.kanban.version.model.Version;

@Service
public class CardMergeService {

	private static final float THRESHOLD = 0.1F;
	private static final int MERGE_CONTENT_IDX = 0;
	private static final int MERGE_CONFLICT_CHECK_IDX = 1;

	@Autowired
	CardDAO cardDAO;

	@Autowired
	VersionDAO versionDAO;

	DiffMatchPatch diffMatchPatch;

	@PostConstruct
	public void init() {
		diffMatchPatch = new DiffMatchPatch();
		diffMatchPatch.matchThreshold = THRESHOLD;
		diffMatchPatch.patchDeleteThreshold = THRESHOLD;
	}

	@Transactional
	public UpdatedCardInfo updateCardInfo(EditCard editCard) throws Exception {
		Card recentCard = cardDAO.selectById(editCard.getCardId());

		Map<String, Object> param = new HashMap<>();
		param.put("cardId", editCard.getCardId());
		param.put("versionNum", editCard.getVersionNum());

		long newEditVersionNum;
		if (recentCard.getVersionNum() == 0
				|| recentCard.getVersionNum() < editCard.getVersionNum()) { //can update card info

			param.put("content", editCard.getContent());
			newEditVersionNum = editCard.getVersionNum();
			saveNewVersion(editCard, newEditVersionNum, editCard.getContent());

		} else { //1st conflict & 2nd conflict detection
			//Conflict detection
			param.put("versionNum", editCard.getVersionNum() - 1);
			String baseVersionContent = versionDAO.selectContent(param);
			String recentVersionContent = recentCard.getContent();
			String autoMergeResult = autoMerge(recentCard.getVersionNum(), baseVersionContent, recentVersionContent,
					editCard.getContent());

			newEditVersionNum = recentCard.getVersionNum() + 1;

			saveNewVersion(editCard, newEditVersionNum, autoMergeResult);

			param.put("versionNum", newEditVersionNum);
			param.put("content", autoMergeResult);
		}
		updateDetailCardInfo(param);

		UpdatedCardInfo updatedCardInfo = new UpdatedCardInfo(editCard.getTitle(), (String) param.get("content"),
				versionDAO.selectVersionInfos(editCard.getCardId()));
		return updatedCardInfo;
	}

	private void saveNewVersion(EditCard editCard, long versionNum, String newContent) {
		Version newVersion = new Version(editCard.getCardId(), versionNum, editCard.getTitle(), newContent,
				editCard.getModifier(), LocalDateTime.now());
		versionDAO.insert(newVersion);
	}

	private void updateDetailCardInfo(Map<String, Object> param) {
		cardDAO.updateCardInfo(param);
	}

	private String autoMerge(long recentVersionNum, String baseVersionContent, String recentVersionContent,
							 String myContent) throws
			Exception {
		//3way merge
		LinkedList<DiffMatchPatch.Patch> patches = diffMatchPatch.patchMake(baseVersionContent, recentVersionContent);
		Object[] results = diffMatchPatch.patchApply(patches, myContent);

		boolean[] conflictChecks = (boolean[]) results[MERGE_CONFLICT_CHECK_IDX];

		for (boolean check : conflictChecks) {
			if (!check) {
				List<LineInfo> recentDiff = getDiffContentLineByLine(baseVersionContent, recentVersionContent);
				List<LineInfo> myDiff = getDiffContentLineByLine(baseVersionContent, myContent);
				throw new ConflictException(recentVersionNum, baseVersionContent, recentDiff, myDiff);//conflict error
			}
		}
		return (String) results[MERGE_CONTENT_IDX];
	}

	private List<LineInfo> getDiffContentLineByLine(String firstContent, String secondContent) {
		List<LineInfo> results = new ArrayList<>();
		int lineNum = 0;
		LinkedList<DiffMatchPatch.Diff> diffs = diffMatchPatch.diffMain(firstContent, secondContent, true);

		for (DiffMatchPatch.Diff diff : diffs) {
			String[] changes = diff.text.split("\\n");
			int resultSize = results.size();
			boolean isSkipFirstLine = false;
			CardOperation cardOperation = diff.operation != DiffMatchPatch.Operation.EQUAL ? CardOperation.CHANGE : CardOperation.EQUAL;

			if (resultSize != 0 && changes.length != 0) {
				int lastLineInfoIdx = resultSize - 1;
				LineInfo lineInfo = results.get(lastLineInfoIdx);

				if (diff.operation != DiffMatchPatch.Operation.DELETE) {
					lineInfo.setLineContent(lineInfo.getLineContent() + changes[0]);
				}

				if (diff.operation != DiffMatchPatch.Operation.EQUAL) {
					lineInfo.setOperation(CardOperation.CHANGE);
				}

				results.set(lastLineInfoIdx, lineInfo);
				isSkipFirstLine = true;
			}

			for (String change : changes) {
				if (isSkipFirstLine) {
					isSkipFirstLine = false;
				} else {
					results.add(new LineInfo(lineNum++, cardOperation, change));
				}
			}
		}

		return results;
	}
}
