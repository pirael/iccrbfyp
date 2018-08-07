package com.iccrb_fyp.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iccrb_fyp.app.FypMessageModel;
import com.iccrb_fyp.app.listeners.OnMessageListener;

import java.util.ArrayList;
import java.util.List;

import static com.iccrb_fyp.app.database.DatabaseQueries.MESSAGE_CONTENT;
import static com.iccrb_fyp.app.database.DatabaseQueries.MESSAGE_ID;
import static com.iccrb_fyp.app.database.DatabaseQueries.MESSAGE_SENDER;
import static com.iccrb_fyp.app.database.DatabaseQueries.MESSAGE_TIMESTAMP;
import static com.iccrb_fyp.app.database.DatabaseQueries.CREATE_TABLE_MESSAGES;
import static com.iccrb_fyp.app.database.DatabaseQueries.DROP_TABLE_MESSAGES;
import static com.iccrb_fyp.app.database.DatabaseQueries.TABLE_MESSAGES;
import static com.iccrb_fyp.app.utils.FypBaseConstants.DATABASE_NAME;
/*
 * Copyright (c) 2018 ICCRB
 *
 * Licensed under The MIT License,
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 */

public class DatabaseManager {
    private static DatabaseManager instance;
    private static SQLiteDatabase sqLiteDatabase;

    public static synchronized DatabaseManager getInstance(Context context, boolean isWritable) {
        if (instance == null) {
            instance = new DatabaseManager();
            DatabaseManagerHelper helper = new DatabaseManagerHelper(context, DATABASE_NAME,1,isWritable);
            sqLiteDatabase= helper.getDb();
        }
        return instance;
    }


    private static class DatabaseManagerHelper extends ManagerHelper{
        /**
         * Instantiate a new DB Helper.
         * <br> SQLiteOpenHelpers are statically cached so they (and their internally cached SQLiteDatabases) will be reused for concurrency
         *
         * @param context    Any {@link Context} belonging to your package.
         * @param name       The database name. This may be anything you like. Adding a file extension is not required and any file extension you would like to use is fine.
         * @param version    the database version.
         * @param isWritable isToWrite
         */
        DatabaseManagerHelper(Context context, String name, int version, boolean isWritable) {
            super(context, name, version, isWritable);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_MESSAGES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, Context context) {
            db.execSQL(DROP_TABLE_MESSAGES);
            onCreate(db);
        }
    }

    private void notifyTransaction(){
       try{
           sqLiteDatabase.setTransactionSuccessful();
           sqLiteDatabase.endTransaction();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    public void insertMessage(final FypMessageModel messageModel, final OnMessageListener messageListener) {
        sqLiteDatabase.beginTransaction();
        int counter=0;
        try{
            ContentValues values = new ContentValues();
            values.put(MESSAGE_ID,messageModel.getMessageId());
            values.put(MESSAGE_TIMESTAMP,messageModel.getMessageTime());
            values.put(MESSAGE_SENDER, messageModel.getMessageSender());
            values.put(MESSAGE_CONTENT, messageModel.getMessageContent());
            long inserted=sqLiteDatabase.insertWithOnConflict(TABLE_MESSAGES,
                    null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if(inserted!=-1){
                counter=1;
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            notifyTransaction();
            messageListener.OnMessageChange(counter == 1);
        }
    }


    public List<FypMessageModel> getConversation() {
        sqLiteDatabase.beginTransaction();
        List<FypMessageModel> messageList=new ArrayList<>();
        FypMessageModel message;
        String query = "SELECT * FROM "+ TABLE_MESSAGES;
        Cursor generalCursor = sqLiteDatabase.rawQuery(query, null);
        if (generalCursor != null) {
            try {
                generalCursor.moveToFirst();
                while (!generalCursor.isAfterLast()) {
                   message=new FypMessageModel();

                   message.setMessageId(generalCursor.getString(0));
                   message.setMessageTime(generalCursor.getString(1));
                   message.setMessageSender(generalCursor.getString(2));
                   message.setMessageContent(generalCursor.getString(3));
                   messageList.add(message);
                   generalCursor.moveToNext();
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                generalCursor.close();
                notifyTransaction();
            }
        }
        return messageList;

    }


    public void clearConversation(){
        sqLiteDatabase.beginTransaction();
        String query = "DELETE FROM "+ TABLE_MESSAGES;
        sqLiteDatabase.execSQL(query);
        notifyTransaction();
    }

}
