package com.example.xo337.try201804;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    private static MyDBHelper instance;

    public static MyDBHelper getInstance(Context ctx){
        if (instance == null){
            instance = new MyDBHelper(ctx,"expense.db", null, 1);
        }
        return instance;
    }

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE  TABLE usbDateList" +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "usbName TEXT NOT NULL , " +
                "linkID TEXT NOT NULL, " +
                "synValue TEXT NOT NULL, " +
                "usbKey TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//當使用者手機中已安裝較舊版本應用程式時，
// 在存取資料庫指令時自動 檢查到舊的資料庫檔案時，會自動執行本方法。
// onUpgrade 方法內可撰寫程 式碼以協助更新使用者舊資料，以順利移轉至新版的資料表格。
        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS usbDateList");
        onCreate(sqLiteDatabase);
    }
}
