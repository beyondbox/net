package com.appjumper.silkscreen.view.pulltorefresh;

import android.view.View;

/**
 * Interface that allows PullToRefreshBase to hijack the call to
 * AdapterView.setEmptyView()
 * 
 * @author chris
 */
public interface PullToRefreshEmptyViewMethodAccessor {

	/**
	 * Calls upto AdapterView.setEmptyView()
	 * 
	 * @param：View
	 */
	public void setEmptyViewInternal(View emptyView);

	/**
	 * Should call PullToRefreshBase.setEmptyView() which will then
	 * automatically call through to setEmptyViewInternal()
	 * 
	 * @param：View
	 */
	public void setEmptyView(View emptyView);

}
