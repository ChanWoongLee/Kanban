package com.intern.kanban.history.model;

public enum ActionNameCase {
	ADD("added"), REMOVE("removed"), UPDATE("updated"), MOVE("moved");

	final private String value;

	ActionNameCase(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
