package com.itlaborer.apitools.model;

/**
 * API参数类-带冻结标志
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.1
 * @since 2.0
 */

@SuppressWarnings("rawtypes")
public class ApiPar2 extends ApiPar implements Comparable {
	private String name;
	private String tip;
	private String value;
	private boolean isnull;
	private boolean frozen;

	public ApiPar2() {

	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	public ApiPar2(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * 
	 * @param name
	 * @param tip
	 * @param value
	 * @param frozen
	 */
	public ApiPar2(String name, String tip, String value, boolean frozen) {
		this.name = name;
		this.tip = tip;
		this.value = value;
		this.frozen = frozen;
	}

	/**
	 * 
	 * @param name
	 * @param tip
	 * @param value
	 * @param isnull
	 * @param frozen
	 */
	public ApiPar2(String name, String tip, String value, boolean isnull, boolean frozen) {
		this.name = name;
		this.tip = tip;
		this.value = value;
		this.isnull = isnull;
		this.frozen = frozen;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public boolean isIsnull() {
		return isnull;
	}

	public void setIsnull(boolean isnull) {
		this.isnull = isnull;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	@Override
	public int compareTo(Object o) {
		return this.getName().compareTo(((ApiPar2) o).getName());
	}

}
