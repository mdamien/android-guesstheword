package org.cuju.guesstheword;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Button button1 = (Button) findViewById(R.id.generate_word_button);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try{
					Dictionnary dict = new Dictionnary(MainActivity.this);
					String word = dict.randomGoodWord(MainActivity.this);
					Intent intent = new Intent(MainActivity.this, GameActivity.class);
					intent.putExtra("word", word);
					startActivity(intent);
				}catch(IOException e){
					Toast.makeText(getApplicationContext(), "IO problem :"+e.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}
		});
		final Button button2 = (Button) findViewById(R.id.choose_word_button);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            	alert.setTitle(getResources().getString(R.string.choose_word));
            	final EditText input = new EditText(MainActivity.this);
            	alert.setView(input);

            	alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
            	  Editable value = input.getText();
            	  Intent intent = new Intent(MainActivity.this, GameActivity.class);
    			  intent.putExtra("word", value.toString());
    			  startActivity(intent);
            	  }
            	});
            	alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            	  public void onClick(DialogInterface dialog, int whichButton) {
            	  }
            	});
				alert.show();
			}
		});
		final Button button3 = (Button) findViewById(R.id.settings_button);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
		            startActivity(new Intent(MainActivity.this, Preference.class));
		          }
		          else {
		            startActivity(new Intent(MainActivity.this, PreferenceHC.class));
		          }
			}
		});
		final Button button4 = (Button) findViewById(R.id.help_button);
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
		        	Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_implemented_yet), Toast.LENGTH_SHORT).show();
		          }
		          else {
		            startActivity(new Intent(MainActivity.this, HelpActivityHC.class));
		          }
			}
		});
	}

}
