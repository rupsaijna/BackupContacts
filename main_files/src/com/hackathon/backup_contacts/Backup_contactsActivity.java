package com.hackathon.backup_contacts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Backup_contactsActivity extends Activity {
	/** Called when the activity is first created. */
	Button mAll;
	EditText et;
	OutputStreamWriter myOutWriter;
	FileOutputStream fOut;
	String root = Environment.getExternalStorageDirectory().getPath();
	String det = "\"Name\",\"Primary Phone\",\"Alternate Phone\",\"Primary Email\",\"Alternate Email\",\n";
	int cnt, pcnt, ecnt, phones = 0, ids = 0;
	String phone, name, email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAll = (Button) findViewById(R.id.all);
		et = (EditText) findViewById(R.id.editText1);
		mAll.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {

					Toast.makeText(getBaseContext(), root, Toast.LENGTH_LONG)
							.show();
					Toast.makeText(getBaseContext(), "NOT MOUNTED",
							Toast.LENGTH_SHORT).show();
				}

				ContentResolver cr = getContentResolver();
				Cursor c1 = cr.query(ContactsContract.Contacts.CONTENT_URI,
						null, null, null, null);
				cnt=c1.getCount();
								if (c1.getCount() > 0) {
					try {
						File f1 = new File(root, "BackupFile.csv");
						f1.createNewFile();
						while (c1.moveToNext()) {
							String id = c1.getString(c1
									.getColumnIndex(ContactsContract.Contacts._ID));
							name = c1.getString(c1
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

							Cursor cPhone = cr
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ "=?",
											new String[] { id }, null);
							if(cPhone.getCount()>2)
								phones+=2;
							else
								phones+=cPhone.getCount();
									
							pcnt=0;
							while (cPhone.moveToNext() && pcnt<2) {
								pcnt++;
								if(pcnt==1)
								phone = cPhone.getString(cPhone
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								else
									phone +=","+cPhone.getString(cPhone
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							}
							if(pcnt==1)
								phone +=",";
							ecnt=0;
							Cursor cEmail = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",new String[] { id }, null);
								if(cEmail.getCount()>2)
									ids+=2;
								else
									ids+=cEmail.getCount();
								while (cEmail.moveToNext() && ecnt<2) {
									ecnt++;
									if(ecnt==1)
									email ="\""+ cEmail.getString(cEmail
											.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))+"\"";
									else
										email +=",\""+cEmail.getString(cEmail
												.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))+"\"";
							}
							if(ecnt==1)
								email +=",\"\"";	
							
							
							
							
							det += "\"" + name + "\"," + phone + ","+email+",\n";
						}

						fOut = new FileOutputStream(f1);
						myOutWriter = new OutputStreamWriter(fOut);
						myOutWriter.write(det);

						myOutWriter.close();
						fOut.close();
						et.setText("Backup file successfully created.\n\nLocation:"+root+"/BackupFile.csv\n\nProcessed\n"+cnt+" Contacts\n\nContaining...\n"+phones+" Phone Numbers \n"+ids+" Email ids");
					} 
					catch (Exception e) {
						Toast.makeText(getBaseContext(), e.getMessage() + root,
								Toast.LENGTH_SHORT).show();
					}

				}
			}
		});

	}
}