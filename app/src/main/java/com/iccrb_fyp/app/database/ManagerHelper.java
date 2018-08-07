package com.iccrb_fyp.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

/**
 * @author Lukundo Kileha (kileha3)
 *         lkileha@furahitech.co.tz
 */

abstract class ManagerHelper {

    /**See SQLiteOpenHelper documentation
     */
    abstract public void onCreate(SQLiteDatabase db);
    /**See SQLiteOpenHelper documentation
     */
    abstract public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, Context context);
    /**Optional.
     * *
     */
    void onOpen(SQLiteDatabase db){}
    /**Optional.
     *
     */
    void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    /**Optional
     *
     */
    void onConfigure(SQLiteDatabase db){}



    /** The SQLiteOpenHelper class is not actually used by your application.
     *
     */
    static private class DBSQLiteOpenHelper extends SQLiteOpenHelper {

        ManagerHelper managerHelper;
        private Context context;
        private AtomicInteger counter = new AtomicInteger(0);

        DBSQLiteOpenHelper(Context context, String name, int version, ManagerHelper managerHelper) {
            super(context, name, null, version);
            this.managerHelper = managerHelper;
            this.context=context;
        }

        void addConnection(){
            counter.incrementAndGet();
        }
        void removeConnection(){
            counter.decrementAndGet();
        }
        int getCounter() {
            return counter.get();
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            managerHelper.onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            managerHelper.onUpgrade(db, oldVersion, newVersion,context);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            managerHelper.onOpen(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            managerHelper.onDowngrade(db, oldVersion, newVersion);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            managerHelper.onConfigure(db);
        }
    }

    private static final ConcurrentHashMap<String,DBSQLiteOpenHelper> dbMap = new ConcurrentHashMap<>();

    private static final Object lockObject = new Object();


    private DBSQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase db;
    private Context context;

    /** Instantiate a new DB Helper.
     * <br> SQLiteOpenHelpers are statically cached so they (and their internally cached SQLiteDatabases) will be reused for concurrency
     *
     * @param context Any {@link Context} belonging to your package.
     * @param name The database name. This may be anything you like. Adding a file extension is not required and any file extension you would like to use is fine.
     * @param version the database version.
     */
    ManagerHelper(Context context, String name, int version, boolean isWritable) {
        String dbPath = context.getApplicationContext().getDatabasePath(name).getAbsolutePath();
        synchronized (lockObject) {
            sqLiteOpenHelper = dbMap.get(dbPath);
            if (sqLiteOpenHelper==null) {
                sqLiteOpenHelper = new DBSQLiteOpenHelper(context, name, version, this);
                dbMap.put(dbPath,sqLiteOpenHelper);
            }
           if(isWritable)
               db = sqLiteOpenHelper.getWritableDatabase();
           else
            db=sqLiteOpenHelper.getReadableDatabase();
        }
        this.context = context.getApplicationContext();
    }

    public Context getContext(){
        return context;
    }
    /**Get the writable SQLiteDatabase
     */
    SQLiteDatabase getDb(){
        return db;
    }

    /** Check if the underlying SQLiteDatabase is open
     *
     * @return whether the DB is open or not
     */
    public boolean isOpen(){
        return (db!=null&&db.isOpen());
    }


    /** Lowers the DB counter by 1 for any {@link ManagerHelper}s referencing the same DB on disk
     *  <br />If the new counter is 0, then the database will be closed.
     *  <br /><br />This needs to be called before application exit.
     * <br />If the counter is 0, then the underlying SQLiteDatabase is <b>null</b> until another ManagerHelper is instantiated or you call {@link #open()}
     *
     * @return true if the underlying {@link SQLiteDatabase} is closed (counter is 0), and false otherwise (counter > 0)
     */
    public boolean close(){
        sqLiteOpenHelper.removeConnection();
        if (sqLiteOpenHelper.getCounter()==0){
            synchronized (lockObject){
                if (db.inTransaction())db.endTransaction();
                if (db.isOpen())db.close();
                db = null;
            }
            return true;
        }
        return false;
    }
    /** Increments the internal db counter by one and opens the db if needed
     *
     */
    private void open(){
        sqLiteOpenHelper.addConnection();
        if (db==null||!db.isOpen()){
            synchronized (lockObject){
                db = sqLiteOpenHelper.getWritableDatabase();
            }
        }
    }
}
