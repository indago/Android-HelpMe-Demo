package com.indago.helpme.gui.dashboard.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.indago.helpme.R;
import com.indago.helpme.gui.dashboard.statemachine.HelpERStateMachine;
import com.indago.helpme.gui.dashboard.statemachine.IStateAwareView;
import com.indago.helpme.gui.dashboard.statemachine.IStateMachine;
import com.indago.helpme.gui.dashboard.statemachine.STATES;

public class HelpERHintView extends TextView implements IStateAwareView {
	private static final String LOGTAG = HelpERHintView.class.getSimpleName();

	private STATES mState;

	public HelpERHintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public HelpERHintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HelpERHintView(Context context) {
		super(context);
		init(context);
	}

	public void setTextAsHTML(String text) {
		setText(Html.fromHtml(text));
	}

	private void init(Context context) {
		//		setState(STATES.SHIELDED);
	}

	@Override
	public void setState(IStateMachine sm) {
		if(sm == null || !(sm instanceof HelpERStateMachine)) {
			return;
		}

		switch((STATES) sm.getState()) {
			case DEFAULT:
				mState = STATES.DEFAULT;
				setTextAsHTML(getResources().getString(R.string.text_hint_providing_incomming_call));
				break;
			default:
				break;
		}

	}

	@Override
	public STATES getState() {
		return mState;
	}

}
