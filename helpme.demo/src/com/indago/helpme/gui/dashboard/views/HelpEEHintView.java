package com.indago.helpme.gui.dashboard.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.indago.helpme.R;
import com.indago.helpme.gui.dashboard.statemachine.HelpEEStateMachine;
import com.indago.helpme.gui.dashboard.statemachine.IStateAwareView;
import com.indago.helpme.gui.dashboard.statemachine.IStateMachine;
import com.indago.helpme.gui.dashboard.statemachine.STATES;

public class HelpEEHintView extends TextView implements IStateAwareView {
	private static final String LOGTAG = HelpEEHintView.class.getSimpleName();

	private STATES mState;

	public HelpEEHintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public HelpEEHintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HelpEEHintView(Context context) {
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
		if(sm == null || !(sm instanceof HelpEEStateMachine)) {
			return;
		}

		switch((STATES) sm.getState()) {
			case SHIELDED:
				mState = STATES.SHIELDED;
				setTextAsHTML(getResources().getString(R.string.text_hint_help_ee_press_green_button));
				break;
			case PART_SHIELDED:
				mState = STATES.PART_SHIELDED;
				setTextAsHTML(getResources().getString(R.string.text_hint_help_ee_press_yellow_button));
				break;
			case UNSHIELDED:
				mState = STATES.UNSHIELDED;
				setTextAsHTML(getResources().getString(R.string.text_hint_help_ee_press_red_button));
				break;
			case PRESSED:
				mState = STATES.PRESSED;
				setTextAsHTML(getResources().getString(R.string.text_hint_help_ee_press_red_button_pressed));
				break;
			case LOCKED:
				mState = STATES.LOCKED;
				setTextAsHTML(getResources().getString(R.string.text_hint_help_ee_wait_for_helper));
				break;
			case CALLCENTER:
				mState = STATES.CALLCENTER;
				setTextAsHTML(getResources().getString(R.string.text_hint_help_ee_press_call_button));
				break;
			case CALLCENTER_PRESSED:
				mState = STATES.CALLCENTER_PRESSED;
				setTextAsHTML(getResources().getString(R.string.text_hint_help_ee_wait_for_callcenter));
				break;
			case HELP_INCOMMING:
				mState = STATES.HELP_INCOMMING;
				setTextAsHTML(getResources().getString(R.string.help_is_underway));
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
