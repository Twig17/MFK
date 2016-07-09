package com.ferraro.mfk.mfk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ferraro.mfk.mfk.Person;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nick on 8/22/2015.
 */
public class MfkDatabase  extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "BookDB";
    private static final String table_BOOKS = "books";
    private static final String book_ID = "id";
    private static final String book_TITLE = "title";
    private static final String book_AUTHOR = "author";

    private static final String[] COLUMNS = { book_ID, book_TITLE, book_AUTHOR };

    public MfkDatabase(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE books ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "title TEXT, " + "author TEXT )";
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop books table if already exists
        db.execSQL("DROP TABLE IF EXISTS books");
        this.onCreate(db);
    }

    public void createPerson(Person person) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(book_TITLE, person.getName());
        values.put(book_AUTHOR, person.getBio());

        // insert person
        db.insert(table_BOOKS, null, values);

        // close database transaction
        db.close();
    }

    public Person readBook(int id) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getReadableDatabase();

        // get person query
        Cursor cursor = db.query(table_BOOKS, // a. table
                COLUMNS, " id = ?", new String[] { String.valueOf(id) }, null, null, null, null);

        // if results !=null, parse the first one
        if (cursor != null)
            cursor.moveToFirst();

        Person person = new Person();
        person.setId(Integer.parseInt(cursor.getString(0)));
        person.setName(cursor.getString(1));
        person.setBio(cursor.getString(2));

        return person;
    }

    public List getAllBooks() {
        List books = new LinkedList();

        // select person query
        String query = "SELECT  * FROM " + table_BOOKS;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        Person person = null;
        if (cursor.moveToFirst()) {
            do {
                person = new Person();
                person.setId(Integer.parseInt(cursor.getString(0)));
                person.setName(cursor.getString(1));
                person.setBio(cursor.getString(2));

                // Add person to books
                books.add(person);
            } while (cursor.moveToNext());
        }
        return books;
    }

    public int updateBook(Person person) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put("title", person.getName()); // get title
        values.put("author", person.getBio()); // get author

        // update
        int i = db.update(table_BOOKS, values, book_ID + " = ?", new String[] { String.valueOf(person.getId()) });

        db.close();
        return i;
    }

    // Deleting single person
    public void deleteBook(Person person) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete person
        db.delete(table_BOOKS, book_ID + " = ?", new String[] { String.valueOf(person.getId()) });
        db.close();
    }
}
