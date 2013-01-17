package org.cuju.guesstheword;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {

	String word = null;
	ArrayList<String> letters = null;
	Integer tries_left = 0;
	String joker = "*";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		//Get the word to guess for a resumed state of the bundle of the intent
		word = savedInstanceState != null ? savedInstanceState.getString("word"):null;
		if(word == null){
			Bundle extras = getIntent().getExtras();
			word = extras != null ? extras.getString("word") : null;
		}

		letters = new ArrayList<String>();
		tries_left = 10;

		TextView word_button = (TextView) findViewById(R.id.word_to_guess);
		boolean international_mode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("international_guess", false);
		if(international_mode){
			StringBuilder word_obfuscated = new StringBuilder();
			for (int i = 0; i < word.length(); i++) {
				if(word.charAt(i) == ' ' || word.charAt(i) == '-'){
					word_obfuscated.append(word.charAt(i));
				}
				else{
					word_obfuscated.append("*");
				}
			}
			word_button.setText(word_obfuscated.toString());
		}
		else{
			word_button.setText(deAccent(word).replaceAll("[a-zA-Z]", "*"));
		}

		TextView letters_entry = (TextView) findViewById(R.id.already_typed_letters);
		letters_entry.setText("");

		updateInfos();

		final Button confirm_button = (Button) findViewById(R.id.confirm_button);
		confirm_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText input = (EditText) findViewById(R.id.letters_entry);
				if(input.getText().length() > 0){
					char letter = deAccent(input.getText().toString().toUpperCase()).charAt(0);
					if(!letters.contains(Character.toString(letter))){
						letters.add(Character.toString(letter));
		
						TextView word_button = (TextView) findViewById(R.id.word_to_guess);
						StringBuilder current_word = new StringBuilder(word_button.getText().toString());
						String word_upper = deAccent(word.toUpperCase());
		
						boolean letter_found = false;
						for (int i = 0; i < word.length(); i++) {
							if(word_upper.charAt(i) == letter){
								letter_found = true;
								current_word.setCharAt(i, word.charAt(i));
							}
						}
						word_button.setText(current_word.toString());
						updateLettersTried();
		
						if(!letter_found){
							tries_left -= 1;
							updateInfos();
							if (tries_left < 1){
								endDialog(getResources().getString(R.string.you_lost));
							}
						}
						if(!current_word.toString().contains("*")){
							endDialog(getResources().getString(R.string.you_won));
						}
					}else{
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.already_typed), Toast.LENGTH_SHORT).show();
					}
					input.setText("");
				}else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_empty), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	public void updateInfos(){
		TextView infos = (TextView) findViewById(R.id.infos);
		if(tries_left > 1){
			infos.setText(tries_left.toString() +  " " + getResources().getString(R.string.left));
		}
		else if (tries_left == 1){
			infos.setText(getResources().getString(R.string.last_try));
		}
		else{
			infos.setText("=(");
		}
	}
	public void updateLettersTried(){
		TextView infos = (TextView) findViewById(R.id.already_typed_letters);
		StringBuilder string = new StringBuilder();
		for (int i = 0; i < letters.size(); i++) {
			string.append(letters.get(i));
			if(i!=letters.size()-1){
				string.append(" - ");
			}
		}
		infos.setText(string.toString());
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void endDialog(String text){
		AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
		alert.setTitle(text);
		alert.setMessage(getResources().getString(R.string.the_word_was) + ' '+'"' + word + '"');
		alert.setNeutralButton(getResources().getString(R.string.ok), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				NavUtils.navigateUpFromSameTask(GameActivity.this);
			}
		});
		alert.show();
	}


	
	@SuppressLint("NewApi")
	public String deAccent(String str) {
		if (Build.VERSION.SDK_INT<Build.VERSION_CODES.GINGERBREAD) { //Fuck Froyo
			return str;
		}else{
			String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			return pattern.matcher(nfdNormalizedString).replaceAll("");
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	areYouSureToQuit();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	public void areYouSureToQuit() {
		AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
		alert.setTitle(getResources().getString(R.string.stop_the_current_game));
		alert.setPositiveButton(getResources().getString(R.string.yes_to_quit), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				NavUtils.navigateUpFromSameTask(GameActivity.this);
			}
		});
		alert.setNegativeButton(getResources().getString(R.string.cancel),null);
		alert.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			areYouSureToQuit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
