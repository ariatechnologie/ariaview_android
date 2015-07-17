/*
 * @author Oneltone Da Silva
 * @version 1 
 */

package BDD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

//Query AriaView
//table_user[int ID, String Login, String Password, String Site]
public class AriaViewSQLite extends SQLiteOpenHelper {

	private static final String TABLE_USER = "table_user";
	private static final String COL_ID = "ID";
	private static final String COL_LOGIN = "Login";
	private static final String COL_PASSWORD = "Password";
	private static final String COL_SITE = "Site";

	private static final String CREATE_BDD = "CREATE TABLE " + TABLE_USER
			+ " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_LOGIN + " TEXT NOT NULL, " + COL_PASSWORD
			+ " TEXT NOT NULL, " + COL_SITE + " TEXT NOT NULL);";

	public AriaViewSQLite(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_USER + ";");
		onCreate(db);
	}

}
