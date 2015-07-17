package BDD;

import java.util.ArrayList;
import java.util.List;

import modele.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//BDD AriaView
//table_user[int ID, String Login, String Password, String Site]
public class AriaViewBDD {
	private static final int VERSION_BDD = 1;
	private static final String NOM_BDD = "ariaview.db";

	private static final String TABLE_USER = "table_user";
	private static final String COL_ID = "ID";
	private static final int NUM_COL_ID = 0;
	private static final String COL_LOGIN = "Login";
	private static final int NUM_COL_LOGIN = 1;
	private static final String COL_PASSWORD = "Password";
	private static final int NUM_COL_PASSWORD = 2;
	private static final String COL_SITE = "Site";
	private static final int NUM_COL_SITE = 3;

	private SQLiteDatabase bdd;

	private AriaViewSQLite ariaViewSQLite;

	public AriaViewBDD(Context context) {
		ariaViewSQLite = new AriaViewSQLite(context, NOM_BDD, null, VERSION_BDD);
	}

	public void open() {
		bdd = ariaViewSQLite.getWritableDatabase();
	}

	public void close() {
		bdd.close();
	}

	public SQLiteDatabase getBDD() {
		return bdd;
	}

	//insert new user with User object
	public long insertUser(User user) {

		ContentValues values = new ContentValues();

		values.put(COL_LOGIN, user.getLogin());
		values.put(COL_PASSWORD, user.getPassword());
		values.put(COL_SITE, user.getSite());

		return bdd.insert(TABLE_USER, null, values);
	}

	//update user with user id and User object
	public int updateUser(int id, User user) {

		ContentValues values = new ContentValues();

		values.put(COL_LOGIN, user.getLogin());
		values.put(COL_PASSWORD, user.getPassword());
		values.put(COL_SITE, user.getSite());

		return bdd.update(TABLE_USER, values, COL_ID + " = " + id, null);
	}

	//return all user
	public List<User> getUser() {
		List<User> User = new ArrayList<User>();

		String query = "SELECT " + COL_ID + " FROM " + TABLE_USER;

		Cursor c = bdd.rawQuery(query, null);

		while (c.moveToNext()) {
			User.add(getUserWithId(c.getInt(0)));
		}
		c.close();

		return User;
	}

	//return User with user id
	public User getUserWithId(int id) {

		Cursor c = bdd.query(TABLE_USER, new String[] { COL_ID, COL_LOGIN,
				COL_PASSWORD, COL_SITE }, COL_ID + " LIKE \"" + id + "\"",
				null, null, null, null);
		return cursorToUser(c);
	}

	//Get Cursor Set a User and return User
	private User cursorToUser(Cursor c) {

		if (c.getCount() == 0)
			return null;

		c.moveToFirst();

		User user = new User();

		user.setId(c.getInt(NUM_COL_ID));
		user.setLogin(c.getString(NUM_COL_LOGIN));
		user.setPassword(c.getString(NUM_COL_PASSWORD));
		user.setSite(c.getString(NUM_COL_SITE));

		c.close();

		return user;
	}

	//Delete All User
	public void clearUser() {
		String query = "DELETE FROM " + TABLE_USER;

		bdd.execSQL(query);

	}
}
