package com.unated.askincht_beta.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BusMessages.MessageItem;
import com.unated.askincht_beta.Pojo.RequestItem;

import java.util.List;

import nl.littlerobots.cupboard.tools.convert.ListFieldConverterFactory;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class DatabaseHelper extends SQLiteOpenHelper {

    private String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "ask.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper databaseHelper;
    private SQLiteDatabase mDb;
    private DatabaseCompartment mDatabaseCompartment;

    static {
        CupboardFactory.setCupboard(new CupboardBuilder().
                registerFieldConverterFactory(new ListFieldConverterFactory(new Gson())).build());
        cupboard().register(RequestItem.class);
        cupboard().register(MessageItem.class);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDb = getWritableDatabase();
        mDatabaseCompartment = cupboard().withDatabase(mDb);
    }

    public static DatabaseHelper getInstance() {
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(AppMain.getContext());
        return databaseHelper;
    }

    @Override
    public synchronized void close() {
        mDb.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        cupboard().withDatabase(sqLiteDatabase).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        cupboard().withDatabase(sqLiteDatabase).upgradeTables();
    }

    public void putRequests(RequestItem[] itemList) {
        for (int i = 0; i < itemList.length; i++) {
            mDatabaseCompartment.put(itemList[i]);
        }
    }

    public void putMessages(MessageItem[] messageItems) {
        /*for (int i = 0; i < messageItems.length; i++) {
            mDatabaseCompartment.put(messageItems[i]);
        }*/
        mDatabaseCompartment.put(messageItems);
        Log.d(TAG, "all messages saved");
    }

    public int getLastRequestId() {
        return mDatabaseCompartment.query(RequestItem.class).orderBy("id desc").limit(1).get().getId();
    }

    public int getLastMsgId() {
        return mDatabaseCompartment.query(MessageItem.class).orderBy("id desc").limit(1).get().getId();
    }

    public int getMessagesCount() {
        return mDatabaseCompartment.query(MessageItem.class).list().size();
    }

    public int getRequestsCount() {
        return mDatabaseCompartment.query(RequestItem.class).list().size();
    }

    public List<RequestItem> getUserRequests() {
        return mDatabaseCompartment.query(RequestItem.class).list();
    }

    public List<MessageItem> getUserMessages() {
        return mDatabaseCompartment.query(MessageItem.class).list();
    }

    public int getMessagesCountById(int requestId) {
        return mDatabaseCompartment.query(MessageItem.class).withSelection("request_id = ?", String.valueOf(requestId)).list().size();
    }

    public List<MessageItem> getMessagesById(String requestId, String shopId) {
        return mDatabaseCompartment.query(MessageItem.class).withSelection("request_id = ? and shop_id = ?", requestId, shopId).list();
    }
}
