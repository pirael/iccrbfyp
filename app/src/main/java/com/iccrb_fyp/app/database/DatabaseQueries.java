package com.iccrb_fyp.app.database;
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

class DatabaseQueries {
    static final String TABLE_MESSAGES = "table_messages";
    static final String MESSAGE_ID = "message_id";
    static final String MESSAGE_SENDER = "message_sender";
    static final String MESSAGE_CONTENT = "message_content";
    static final String MESSAGE_TIMESTAMP = "message_timestamp";


    static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + "("
            + MESSAGE_ID + " text primary key,"
            + MESSAGE_TIMESTAMP + " text not null,"
            + MESSAGE_SENDER + " text not null,"
            + MESSAGE_CONTENT + " text not null)";

    static final String DROP_TABLE_MESSAGES = "DROP TABLE IF EXISTS " + TABLE_MESSAGES;

}
