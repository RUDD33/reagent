package net.hpxn.reagent;

import java.util.Date;
import java.util.HashMap;

public class Cast {
	private boolean initialized;
	private Date lastUsed;
	private Date dateInitialized;
	private HashMap<String, Object> properties;
	private boolean success;
	private String result;

	public Cast() {
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

	public HashMap<String, Object> getProperties() {
		return properties;
	}
}
