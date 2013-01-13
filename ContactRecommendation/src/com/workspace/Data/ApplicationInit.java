package com.workspace.Data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;

public class ApplicationInit 
{
	private Context ctx;

	public ApplicationInit(Context ctx) 
	{
		this.ctx = ctx;
	}
	
	public void getPackages() 
	{
	    getInstalledApps(false); /* false = no system packages */
	}
	
	private void getInstalledApps(boolean getSysPackages) 
	{      
	    List<PackageInfo> packs = this.ctx.getPackageManager().getInstalledPackages(0);
	    
	    Application app;
	    
	    for(int i=0;i<packs.size();i++) 
	    {
	        PackageInfo p = packs.get(i);
	        
	        if ((!getSysPackages) && (p.versionName == null)) 
	        {
	            continue ;
	        }
	        
	        app = new Application(p.applicationInfo.loadLabel(this.ctx.getPackageManager()).toString(),
	        		p.packageName, p.versionName, 
	        		p.versionCode, p.applicationInfo.loadIcon(this.ctx.getPackageManager()));
	        
	        GlobalVariables.listaApplications.add(app);
	    }
	}
}
