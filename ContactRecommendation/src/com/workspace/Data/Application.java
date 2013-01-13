package com.workspace.Data;

import android.graphics.drawable.Drawable;

public class Application 
{
	private String appname;
    private String pname;
    private String versionName;
    private int versionCode;
    private Drawable icon;
    
	public Application(String appname, String pname, String versionName,
			int versionCode, Drawable icon) 
	{
		this.appname = appname;
		this.pname = pname;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.icon = icon;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
