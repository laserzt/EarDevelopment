package com.eardevelopment;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
class Interval
{
	int interval;
	int note;
};

public class IntervalPractice extends ActionBarActivity implements onMyClick{
	boolean allreadyInit=false;
	Button[] NotesButtons;
	TextView RightOrWrong;
	Interval[][] intervalsAll;
	int[] intervalsOptions;
	Button replayButton;
	PianoView pianoView;
	int numOfTries;
	int numOfSuccesses;
	long cumulativeSuccessTime;
	long start_of_try;
	int noteDiff;
	int lastIntervalIndPlayed;
	int lastIntervalInd2Played;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interval_practice);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.interval_practice, menu);
		return true;
	}
	@Override
	public void onStart()
	{
		super.onStart();
		if (allreadyInit)
			return;
		numOfTries = 0;
		numOfSuccesses = 0;			
		noteDiff=0;
		if (MainActivity.Bbinstrument)
			noteDiff=2;
		if (MainActivity.Ebinstrument)
			noteDiff=-3;
		int numOfIntervals=0;
		int minNote=MainActivity.minNote+noteDiff;
		int maxNote=MainActivity.maxNote+noteDiff;
		if (MainActivity.onlyOneOctave)
		{
			minNote=12+noteDiff;
			maxNote=23+noteDiff;
		}
		if (MainActivity.practiceLargerIntervals)
		{
			numOfIntervals=0;
			for (int interval=0;interval<8;++interval)
				if (MainActivity.intervalsToPractice[interval] || MainActivity.intervalsToPractice[interval+12])
					++numOfIntervals;
			for (int interval=8;interval<12;++interval)
				if (MainActivity.intervalsToPractice[interval])
					++numOfIntervals;
			intervalsOptions = new int[numOfIntervals];
			numOfIntervals=0;
			for (int interval=0;interval<8;++interval)
				if (MainActivity.intervalsToPractice[interval] || MainActivity.intervalsToPractice[interval+12])
					intervalsOptions[numOfIntervals++]=interval;
			for (int interval=8;interval<12;++interval)
				if (MainActivity.intervalsToPractice[interval])
					intervalsOptions[numOfIntervals++]=interval;
			intervalsAll = new Interval[numOfIntervals][];			
			for (int interval=0;interval<=maxNote;++interval)
			{
				numOfIntervals=0;			
				for (int note=minNote;note<maxNote;++note)
					if (((MainActivity.intervalsUseOnlySingleNotes && MainActivity.notesToPractice[note%12]) || !MainActivity.intervalsUseOnlySingleNotes)
					&& (MainActivity.intervalsToPractice[interval%12] || (interval%12<8 && MainActivity.intervalsToPractice[interval%12+12])) 
						&& note+interval<=maxNote && (interval<18 && note-noteDiff>=MainActivity.lowerIntervalLimits[interval]))
						numOfIntervals++;
				intervalsAll[interval] = new Interval[numOfIntervals];
			}
			for (int interval=0;interval<=maxNote;++interval)
			{
				numOfIntervals=0;
				for (int note=minNote;note<maxNote;++note)
					if (((MainActivity.intervalsUseOnlySingleNotes && MainActivity.notesToPractice[note%12]) || !MainActivity.intervalsUseOnlySingleNotes)
					&& (MainActivity.intervalsToPractice[interval%12] || (interval%12<8 && MainActivity.intervalsToPractice[interval%12+12])) 
							&& note+interval<=maxNote && (interval<18 && note-noteDiff>=MainActivity.lowerIntervalLimits[interval]))
					{
						intervalsAll[interval][numOfIntervals]=new Interval();
						intervalsAll[interval][numOfIntervals].interval = interval;
						intervalsAll[interval][numOfIntervals].note = note-noteDiff;
						++numOfIntervals;
					}
			}
		}
		else
		{
			numOfIntervals=0;
			for (int interval=0;interval<20;++interval)
				if (MainActivity.intervalsToPractice[interval])
					++numOfIntervals;
			intervalsOptions = new int[numOfIntervals];
			numOfIntervals=0;
			for (int interval=0;interval<20;++interval)
				if (MainActivity.intervalsToPractice[interval])
					intervalsOptions[numOfIntervals++]=interval;
			intervalsAll = new Interval[numOfIntervals][];
			
			for (int intervalInd=0;intervalInd<intervalsOptions.length;++intervalInd)
			{
				numOfIntervals=0;
				int interval=intervalsOptions[intervalInd];
				for (int note=minNote;note<maxNote;++note)				
					if (((MainActivity.intervalsUseOnlySingleNotes && MainActivity.notesToPractice[note%12]) || !MainActivity.intervalsUseOnlySingleNotes)
						&& MainActivity.intervalsToPractice[interval] && note+interval<=maxNote && note-noteDiff>=MainActivity.lowerIntervalLimits[interval])
							numOfIntervals++;
				intervalsAll[intervalInd] = new Interval[numOfIntervals];
			}
			for (int intervalInd=0;intervalInd<intervalsOptions.length;++intervalInd)
			{
				int interval=intervalsOptions[intervalInd];
				numOfIntervals=0;
				for (int note=minNote;note<maxNote;++note)				
					if (((MainActivity.intervalsUseOnlySingleNotes && MainActivity.notesToPractice[note%12]) || !MainActivity.intervalsUseOnlySingleNotes)
					&& MainActivity.intervalsToPractice[interval] && note+interval<=maxNote && note-noteDiff>=MainActivity.lowerIntervalLimits[interval])
					{
						intervalsAll[intervalInd][numOfIntervals]=new Interval();
						intervalsAll[intervalInd][numOfIntervals].interval = interval;
						intervalsAll[intervalInd][numOfIntervals].note = note-noteDiff;
						++numOfIntervals;
					}
			}
		}		
		
		LinearLayout ll0 = (LinearLayout)findViewById(R.id.interval_practive_layout);
		ll0.setBackgroundColor(MainActivity.BackGroundColor);
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.interval_button_layout_right);
		RelativeLayout ll = (RelativeLayout)findViewById(R.id.interval_button_layout_left);
		
		RightOrWrong = (TextView)findViewById(R.id.text_interval_RightOrWrong);
		
		((Button)findViewById(R.id.button_interval_replay)).setTextColor(MainActivity.ForGroundColor);		
		((Button)findViewById(R.id.button_interval_replay_lower)).setTextColor(MainActivity.ForGroundColor);
		((Button)findViewById(R.id.button_interval_replay_upper)).setTextColor(MainActivity.ForGroundColor);
		((TextView)findViewById(R.id.text_interval_score)).setTextColor(MainActivity.ForGroundColor);
//		t.setText("Success so far " + numOfSuccesses + " / " + numOfTries);
		
		NotesButtons = new Button[intervalsOptions.length];
		for (int i=0;i<intervalsOptions.length;++i)
		{
			NotesButtons[i]=new Button(this);
			NotesButtons[i].setText(MainActivity.IntervalsNames[intervalsOptions[i]]);
			NotesButtons[i].setOnClickListener(new ButtonListener(this,i));
			NotesButtons[i].setId(i+1);
			NotesButtons[i].setTextColor(MainActivity.ForGroundColor);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			if (i>=2)
				lp.addRule(RelativeLayout.BELOW, NotesButtons[i-2].getId());
			NotesButtons[i].setLayoutParams(lp);
			if (i%2==0)
				ll.addView(NotesButtons[i]);
			else
				rl.addView(NotesButtons[i]);
		}
		final LinearLayout main_layout = (LinearLayout)findViewById(R.id.interval_main_layout);
		final IntervalPractice thisP = this;
		ViewTreeObserver vto = main_layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override
		    public void onGlobalLayout() {
				if (pianoView != null && (pianoView.doneDraw || pianoView.stillDrawing || !pianoView.failedDraw))
					return;
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);										 	
				LinearLayout.LayoutParams lp;
				if (pianoView != null)
					System.out.println(size.y/4 +","+MainActivity.pianoViewRequestedHeight +"," + pianoView + "," + pianoView.failedDraw);
				else
					System.out.println(size.y/4 +","+MainActivity.pianoViewRequestedHeight + "," + pianoView);
				if (pianoView != null && pianoView.failedDraw && MainActivity.pianoViewRequestedHeight >0) 					
					lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, MainActivity.pianoViewRequestedHeight ,1f);
				else
					lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, size.y/4 ,1f);
				if (pianoView != null)
					main_layout.removeView(pianoView);
				pianoView = new PianoView(thisP, false, MainActivity.numOfNotesOnPianoInPractises);
				
				main_layout.addView(pianoView,lp);
			}
		});
				
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				playNextInterval();
			}
		},1000);
		allreadyInit=true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();
		//if (id == R.id.action_settings) {
//			return true;
		//}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_interval_practice, container, false);
			return rootView;
		}
	}
	private Random r = new Random(System.currentTimeMillis());
	private void playInterval(int note1, int note2)
	{
		if (note2>MainActivity.maxNote)
		{
			note2-=12;
			note1-=12;				
		}
		if (note1<MainActivity.minNote)
		{
			note1+=12;
			note2+=12;
		}
		MainActivity.singeltonPointer.playNotes(new int[] {note1, note2}, false);
	}
	private void playNextInterval()
	{		
		lastIntervalIndPlayed = r.nextInt(intervalsAll.length); 
		lastIntervalInd2Played = r.nextInt(intervalsAll[lastIntervalIndPlayed].length);
		playInterval(intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note, intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note+intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].interval);
		Calendar c = Calendar.getInstance();
		start_of_try = c.getTimeInMillis();			
	}

	@Override
	public void ButtonPressed(int buttonInd) {
		++numOfTries;
		if ((intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].interval)==intervalsOptions[buttonInd])
		{
			++numOfSuccesses;
			Calendar c = Calendar.getInstance();
			cumulativeSuccessTime += (c.getTimeInMillis()- start_of_try);
			RightOrWrong.setText("Right!");
			RightOrWrong.setBackgroundColor(Color.GREEN);
			playInterval(intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note, intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note+intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].interval);
			Timer timer = new Timer();
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					playNextInterval();
				}
			},MainActivity.noteTimeInMilliseconds);
		}
		else
		{
			RightOrWrong.setBackgroundColor(0xFFFF3030);
			RightOrWrong.setText("Wrong! Try again.");
			
			playInterval(intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note, intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note+intervalsOptions[buttonInd]); 
		}
		TextView t = (TextView)findViewById(R.id.text_interval_score);
		t.setText("Success so far " + numOfSuccesses + "/" + numOfTries + " ("+String.format("%.2f", ((float)numOfSuccesses)/numOfTries)+")\nAverage success time " + String.format("%.2f",((float)cumulativeSuccessTime)/numOfTries/1000) + " seconds");		
	}
	public void replayLastNote(View arg0) {
		playInterval(intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note, intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note+intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].interval);		
	}
	public void replayLower(View arg0) {
		MainActivity.singeltonPointer.playNotes(new int[] {intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note}, false);		
	}
	public void replayUpper(View arg0) {
		MainActivity.singeltonPointer.playNotes(new int[] {intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].note+intervalsAll[lastIntervalIndPlayed][lastIntervalInd2Played].interval}, false);		
	}
	

}
