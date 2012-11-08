package com.indago.helpme.gui.dashboard.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.indago.helpme.R;
import com.indago.helpme.gui.dashboard.statemachine.HelpERStateMachine;
import com.indago.helpme.gui.dashboard.statemachine.IStateAwareView;
import com.indago.helpme.gui.dashboard.statemachine.IStateMachine;
import com.indago.helpme.gui.dashboard.statemachine.STATES;

public class HelpERButtonView extends ImageButton implements IStateAwareView {
	private static final String LOGTAG = HelpERButtonView.class.getSimpleName();

	private static final int STATESS_MAX = 3;
	private STATES mState;
	private Drawable[] drawables = new Drawable[STATESS_MAX];

	public HelpERButtonView(Context context) {
		super(context);
		init(context);
	}

	public HelpERButtonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public HelpERButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		drawables[0] = context.getResources().getDrawable(R.drawable.btn_accept_decline_default);
		drawables[1] = context.getResources().getDrawable(R.drawable.btn_accept_pressed);
		drawables[2] = context.getResources().getDrawable(R.drawable.btn_decline_pressed);

		this.setBackground(context.getResources().getDrawable(R.drawable.btn_accept_decline));

	}

	public int getStateAsInteger() {
		return mState.ordinal();
	}

	@Override
	public void setState(IStateMachine sm) {
		if(sm == null || !(sm instanceof HelpERStateMachine)) {
			return;
		}

		switch((STATES) sm.getState()) {
			case DEFAULT:
				setBackground(drawables[0]);
				break;
			case ACCEPTED:
				setBackground(drawables[1]);
				break;
			case DECLINED:
				setBackground(drawables[2]);
				break;
			default:
				break;
		}
	}

	@Override
	public Enum<?> getState() {
		return mState;
	}

}
