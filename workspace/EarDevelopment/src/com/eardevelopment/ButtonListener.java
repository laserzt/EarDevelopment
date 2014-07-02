package com.eardevelopment;

import android.view.View;
import android.view.View.OnClickListener;

public class ButtonListener implements OnClickListener {
	
	int ButtonNum;
	onMyClick Parent;
	public ButtonListener(onMyClick parent, int buttonNum)
	{
		ButtonNum = buttonNum;
		Parent = parent;
	}
	@Override
	public void onClick(View arg0) {
		Parent.ButtonPressed(ButtonNum);
	}

}
