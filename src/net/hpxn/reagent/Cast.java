package net.hpxn.reagent;

import java.util.Date;

public class Cast {
	private boolean initialized;
	private String name;
	private Date lastUsed;
	private Date dateInitialized;
	private boolean success;
	private String result;

	public Cast(String name) {
		this.name = name;
		this.initialized = true;
		this.dateInitialized = new Date();
	}

	public Date getDateInitialized() {
		return dateInitialized;
	}

	public void setDateInitialized(Date dateInitialized) {
		this.dateInitialized = dateInitialized;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
