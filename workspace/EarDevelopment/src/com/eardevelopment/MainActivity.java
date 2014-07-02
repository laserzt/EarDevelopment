package com.eardevelopment;

import java.io.IOException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TableLayout;

class ChordStruct
{
	ChordStruct(String name, int[] intervals)
	{
		this.intervals = intervals;
		this.name=name;
	}
	int[] intervals;
	String name;
}
public class MainActivity extends ActionBarActivity implements OnCompletionListener{
	public static int pianoViewRequestedHeight;
	public final static String MY_APP_NAME = "earDevelopmentApp";
	public final static int BackGroundColor = 0xFF212121;
	public final static int ForGroundColor = 0xFFFFFFFF;
	public final static int ButtonsColor = 0xFF333333;
	public static MainActivity singeltonPointer;
	public final static String EXTRA_NOTES_TO_PRACTISE = "com.eardevelopment.NOTES_TO_PRACTISE";
	public final static String[] NotesNamesReal = new String[] {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"};
	public final static String[] NotesNamesEurope = new String[] {"Do", "Do #", "Re", "Mi b", "Mi", "Fa", "Fa #", "Sol", "La b", "La", "Si b", "Si"};
	public final static String[] IntervalNamesReal = new String[] {"Perfect Unison", "Minor 2nd", "Major 2nd", "Minor 3rd", "Major 3rd", "Perfect 4th", "Augmented 4th", "Perfect 5th", "Minor 6th", "Major 6th", "Minor 7th", "Major 7th", "Perfect Octave", "Minor 9th", "Major 9th", "Minor 10th", "Major 10th", "Perfect 11th", "Augmented 11th", "Perfect 12th"};
	public final static ChordStruct[] ChordsNamesReal = new ChordStruct[] {new ChordStruct("Major",new int[]{4,7}), new ChordStruct("Minor", new int[]{3,7}), new ChordStruct("Diminished", new int[]{3,6}), new ChordStruct("Augmented", new int[]{4,8}), new ChordStruct("Major7", new int[]{4,7,11}), new ChordStruct("Dom7", new int[]{4,7,10}), new ChordStruct("Major6", new int[]{4,7,9}), new ChordStruct("Minor maj7", new int[]{3,7,11}), new ChordStruct("Minor7", new int[]{3,7,10}), 
		new ChordStruct("Minor6", new int[]{3,7,9}), new ChordStruct("Dim maj7", new int[]{3,6,11}), new ChordStruct("Half Diminished", new int[]{3,6,10}), new ChordStruct("Dim7", new int[]{3,6,9}), new ChordStruct("Aug maj7", new int[]{4,8,11}), new ChordStruct("Aug7", new int[] {4,8,10}),
		new ChordStruct("Major add 9", new int[] {4,7,14}), new ChordStruct("Minor add 9", new int[] {3,7,14}), new ChordStruct("Dim add 9", new int[] {3,6,14}), new ChordStruct("Augadd 9", new int[] {4,8,14}), new ChordStruct( "Major7,9", new int[] {4,7,11,14}), new ChordStruct("Dom7,9", new int[] {4,7,10,14}), new ChordStruct("Dom7,b9", new int[] {4,7,10,13}), new ChordStruct("Major6,9", new int[] {4,7,9,14}), new ChordStruct( "Minor maj7,9", new int[] {3,7,11,14}), new ChordStruct("Minor7,9", new int[] {3,7,10,14}), new ChordStruct("Minor6,9", new int[] {3,7,9,14}), new ChordStruct( "Dim maj7,9", new int[] {3,6,11,14}), new ChordStruct("Minor7b5,9", new int[] {3,6,10,14}),
		new ChordStruct("Dim7,9", new int[] {3,6,9,14}), new ChordStruct("Aug maj7,9", new int[] {4,8,11,14}), new ChordStruct("Aug7,9", new int[] {4,8,10,14}), new ChordStruct("Major7#11", new int[]{4,7,11,18}), new ChordStruct("Dom sus4,7", new int[]{5,7,10}), new ChordStruct("Dom7,#11", new int[]{4,7,10,18}), new ChordStruct("Minor maj7,11", new int[]{3,7,11,17}), new ChordStruct("Minor7,11", new int[]{3,7,10,17}), 
		new ChordStruct("Minor6,11", new int[]{3,7,9,17}), new ChordStruct("Dim maj7,11", new int[]{3,6,11,17}), new ChordStruct("Minor7b5,11", new int[]{3,6,10,17}), new ChordStruct("Dim7,11", new int[]{3,6,9,17}), new ChordStruct("Aug maj7,#11", new int[]{4,8,11,18}), new ChordStruct("Aug7,#11", new int[] {4,8,10,18}),
		new ChordStruct("Major7,9,13", new int[]{4,7,9,14}), new ChordStruct("Dom7,9,13", new int[]{4,9,10,14}), new ChordStruct("Dom7,b9,13", new int[]{4,9,10,13}), new ChordStruct("Dom7,9,b13", new int[]{4,8,10,14}), new ChordStruct("Dom7,b9,b13", new int[]{4,8,10,13}), new ChordStruct("Dom7 sus4,9,13", new int[]{5,9,10,14}), new ChordStruct("Dom7 sus4,b9,13", new int[]{5,9,10,13}), new ChordStruct("Dom7 sus4,9,b13", new int[]{5,8,10,14}), new ChordStruct("Dom7 sus4,b9,b13", new int[]{5,8,10,13}),
		new ChordStruct("Dom7,9,#11,13", new int[]{4,9,10,14,18}), new ChordStruct("Dom7,b9,#11,13", new int[]{4,8,10,13,18}), new ChordStruct("Dom7,9,#11,b13", new int[]{4,8,10,14,18}), new ChordStruct("Dom7,b9,#11,b13", new int[]{4,8,10,13,18}), new ChordStruct("Minor maj7,9,13", new int[]{3,9,11,14})};
	public static String[] NotesNames = NotesNamesReal;
	public static String[] IntervalsNames = IntervalNamesReal;
	public static ChordStruct[] ChordsNames = ChordsNamesReal;
	public final static int[] lowerIntervalLimits = new int[] {-100,16,15,12,10,10,10,-2,7,5,6,5,5,-100,4,3,0,-2,-100,-100,-100,-100,-100,-100};
	final static int minNote=5;
	final static int maxNote=48;
	final static long noteTimeInMilliseconds=1500;
	public static boolean[] notesToPractice = new boolean[NotesNames.length];
	public static boolean[] intervalsToPractice = new boolean[IntervalsNames.length];
	public static boolean[] chordsToPractice = new boolean[ChordsNames.length];
	public static boolean practiceLargerIntervals = false;
	public static boolean Bbinstrument;
	public static boolean Ebinstrument;
	public static boolean europeanNoteNames;
	public static boolean chordsUseOnlySingleNotes;
	public static boolean intervalsUseOnlySingleNotes;
	public static boolean onlyOneOctave;
	public static int numOfNotesOnPianoInPractises=14;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		singeltonPointer=this;
		SharedPreferences preferences = getSharedPreferences(MY_APP_NAME, MODE_PRIVATE);
		boolean savedOnce = preferences.getBoolean("SavedOnce",false);
		if (savedOnce)
		{
			for (int i=0;i<MainActivity.intervalsToPractice.length;++i)
				intervalsToPractice[i]=preferences.getBoolean("intervalsToPractice"+i,false);
			for (int i=0;i<MainActivity.notesToPractice.length;++i)
				notesToPractice[i]=preferences.getBoolean("notesToPractice"+i,false);
			for (int i=0;i<MainActivity.chordsToPractice.length;++i)
				chordsToPractice[i]=preferences.getBoolean("chordsToPractice"+i,false);
			MainActivity.Bbinstrument=preferences.getBoolean("Bbinstrument",false);
			MainActivity.Ebinstrument=preferences.getBoolean("Ebinstrument",false);
			MainActivity.intervalsUseOnlySingleNotes=preferences.getBoolean("intervalsUseOnlySingleNotes",false);
			MainActivity.chordsUseOnlySingleNotes=preferences.getBoolean("chordsUseOnlySingleNotes",false);
			MainActivity.europeanNoteNames=preferences.getBoolean("europeanNoteNames",false);
			MainActivity.europeanNoteNames=preferences.getBoolean("europeanNoteNames",false);
			MainActivity.onlyOneOctave=preferences.getBoolean("onlyOneOctave",false);
			MainActivity.numOfNotesOnPianoInPractises=preferences.getInt("numOfNotesOnPianoInPractises", 14);
		}
		else
		{
			//the C Major scale
			notesToPractice[0]=true;
			notesToPractice[2]=true;
			notesToPractice[4]=true;
			notesToPractice[5]=true;
			notesToPractice[7]=true;
			notesToPractice[9]=true;
			notesToPractice[11]=true;
			
			intervalsToPractice[3]=true;//minor 3rd
			intervalsToPractice[4]=true;//major 3rd
			intervalsToPractice[5]=true;//perfect 4th
			intervalsToPractice[7]=true;//perfect 5th
			intervalsToPractice[8]=true;//minor 6th
			intervalsToPractice[9]=true;//major 6th
			intervalsToPractice[12]=true;//perfect octave
			
			//the four triads
			chordsToPractice[0]=true;
			chordsToPractice[1]=true;
			chordsToPractice[2]=true;
			chordsToPractice[3]=true;
		}
	}
	@Override
	public void onStop()
	{
		super.onStop();
		SharedPreferences preferences = getSharedPreferences(MainActivity.MY_APP_NAME, MODE_PRIVATE);
		SharedPreferences.Editor edit= preferences.edit();
		edit.putBoolean("SavedOnce",true);
		for (int i=0;i<MainActivity.intervalsToPractice.length;++i)
			edit.putBoolean("intervalsToPractice"+i, MainActivity.intervalsToPractice[i]);
		for (int i=0;i<MainActivity.notesToPractice.length;++i)
			edit.putBoolean("notesToPractice"+i, MainActivity.notesToPractice[i]);		
		for (int i=0;i<MainActivity.chordsToPractice.length;++i)
			edit.putBoolean("chordsToPractice"+i, MainActivity.chordsToPractice[i]);
		edit.putBoolean("Bbinstrument", MainActivity.Bbinstrument);
		edit.putBoolean("Ebinstrument", MainActivity.Ebinstrument);
		edit.putBoolean("intervalsUseOnlySingleNotes", MainActivity.intervalsUseOnlySingleNotes);
		edit.putBoolean("chordsUseOnlySingleNotes", MainActivity.chordsUseOnlySingleNotes);
		edit.putBoolean("europeanNoteNames", MainActivity.europeanNoteNames);
		edit.putBoolean("onlyOneOctave", MainActivity.onlyOneOctave);
		edit.putInt("numOfNotesOnPianoInPractises", MainActivity.numOfNotesOnPianoInPractises);
		edit.commit();
	}
	private Bitmap rescaleImage(Bitmap bitmap)
	{		
		TableLayout main_layout = (TableLayout)findViewById(R.id.main_layout);
		double mainHeight=main_layout.getHeight()*0.9;
		double mainWidth=main_layout.getWidth()*0.9;		
		double m = Math.min(mainHeight/3,mainWidth/2);
		return Bitmap.createScaledBitmap(bitmap, (int)m, (int)m, false); 
	}
	boolean readInitializers=false;
	@Override
	public void onStart()
	{
		super.onStart();
		ViewTreeObserver vto = findViewById(R.id.main_layout).getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			 @Override
			    public void onGlobalLayout() {
				 	if (readInitializers)
				 		return;
				 	readInitializers=true;
				 	ImageButton single_button = (ImageButton)findViewById(R.id.button_single_note_id);				
					ImageButton dual_button = (ImageButton)findViewById(R.id.button_dual_note_id);
					ImageButton chords_button = (ImageButton)findViewById(R.id.button_chords_id);
					ImageButton setting_button = (ImageButton)findViewById(R.id.button_settings_id);		
					ImageButton piano_roll_button = (ImageButton)findViewById(R.id.button_piano_roll_id);
					TableLayout main_layout = (TableLayout)findViewById(R.id.main_layout);
					main_layout.setBackgroundColor(BackGroundColor);
					try
					{
						single_button.setImageBitmap(rescaleImage(BitmapFactory.decodeStream(getAssets().open("Single Note.png"))));
						single_button.setBackgroundColor(BackGroundColor);			
						dual_button.setImageBitmap(rescaleImage(BitmapFactory.decodeStream(getAssets().open("Interval.png"))));
						dual_button.setBackgroundColor(BackGroundColor);			
						chords_button.setImageBitmap(rescaleImage(BitmapFactory.decodeStream(getAssets().open("chords.png"))));
						chords_button.setBackgroundColor(BackGroundColor);			
						setting_button.setImageBitmap(rescaleImage(BitmapFactory.decodeStream(getAssets().open("settings.png"))));
						setting_button.setBackgroundColor(BackGroundColor);
						piano_roll_button.setImageBitmap(rescaleImage(BitmapFactory.decodeStream(getAssets().open("pianoIcon.png"))));
						piano_roll_button.setBackgroundColor(BackGroundColor);
					}
					catch (IOException e){}	
			 	}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Settings(null);
			return true;
		}
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	} 
	public void playNotes(int[] notes, boolean waitUntilEnd)
	{
		
		MediaPlayer[] mediaPlayer = new MediaPlayer[notes.length];
		for (int i=0;i<notes.length;++i)
		{
			if (notes[i]<minNote || notes[i]>maxNote)
			{
				System.out.println("Error: cant play note " + notes[i]);
				while (notes[i]>maxNote)
					notes[i]-=12;
				while (notes[i]<minNote)
					notes[i]+=12;				
			}
			mediaPlayer[i]=new MediaPlayer();
			Integer j = (notes[i]/12);
			String filename = NotesNamesReal[notes[i]%12]+j.toString()+".mp3";
			try
			{
				AssetFileDescriptor afd = getAssets().openFd(filename);
				mediaPlayer[i].setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				mediaPlayer[i].prepare();
				mediaPlayer[i].setOnCompletionListener(this);
			}
			catch (IOException e)
			{
				System.out.println(e.toString());
			}
		}
		for (int i=0;i<notes.length;++i)
			if (mediaPlayer[i]!=null)
				mediaPlayer[i].start();
		try
		{
			if (waitUntilEnd)
				Thread.sleep(noteTimeInMilliseconds);
		}
		catch (InterruptedException e){}
	}
	
	public void StartSingle(View view) {
		Intent intent = new Intent(this, SingleNotePractice.class);		
		if (europeanNoteNames)
			NotesNames=NotesNamesEurope;
		else
			NotesNames=NotesNamesReal;
		startActivity(intent);
	}
	public void StartDual(View view) {
		Intent intent = new Intent(this, IntervalPractice.class);
		startActivity(intent);
	}
	public void StartChords(View view) {
		Intent intent = new Intent(this, ChordPractice.class);		
		startActivity(intent);
	}
	
	public void Settings(View view) {
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
	}
	public void PianoRoll(View view) {
		Intent intent = new Intent(this, PianoRoll.class);
		startActivity(intent);		
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.stop();
		mp.release();
	}
}