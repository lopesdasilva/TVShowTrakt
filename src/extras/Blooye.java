package extras;

import com.jakewharton.apibuilder.ApiException;

import android.app.Activity;
import android.app.AlertDialog;

public class Blooye {

	public static void goBlooey(Activity activity,Throwable t) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setTitle("Connection Error")
				.setMessage("TV Trakt can not connect with trakt service")
				.setPositiveButton("OK", null).show();
		
	}

	public static void goBlooeyLogin(Activity activity, Exception t) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		if (t.getMessage().equals("wrong pass"))
			builder.setTitle("Error found")
					.setMessage("Invalid login information")
					.setPositiveButton("OK", null).show();
		else if (t.getClass().equals(ApiException.class))
			builder.setTitle("Connection Error")
					.setMessage(
							"Movie Trakt can not connect with trakt service")
					.setPositiveButton("OK", null).show();

		
	}

}
