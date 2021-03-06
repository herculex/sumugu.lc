package com.sumugu.liubo.lc.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sumugu.liubo.lc.contract.ListContract;


/**
 * Created by liubo on 15/9/1.
 */
public class ListProvider extends ContentProvider {

    private static final String TAG=ListProvider.class.getSimpleName();
    private DbHelper mDbHelper;

    //URI 匹配器
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        sUriMatcher.addURI(ListContract.AUTHORITY,ListContract.TABLE,ListContract.LIST_DIR);    //多个结果
        sUriMatcher.addURI(ListContract.AUTHORITY,ListContract.TABLE+"/#",ListContract.LIST_ITEM); //一个结果
    }

    @Override
    public boolean onCreate() {

        //初始化成员变量
        mDbHelper = new DbHelper(getContext());
        Log.d(TAG, "sumugu,onCreate,mDbHelper.");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ListContract.TABLE);

        switch (sUriMatcher.match(uri))
        {
            //判断URI类型，DIR，多个条目；带ID的属于ITEM，单个条目，需要加入条件
            case ListContract.LIST_DIR:
                break;
            case ListContract.LIST_ITEM:
                queryBuilder.appendWhere(ListContract.Column.LIST_ID +"="+uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("sumugu,Illegal URI:"+uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder))?ListContract.DEFAULT_SORT:sortOrder;
        //查询而已，所以打开只读数据库
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        //查询结果，返回游标
        Cursor cursor = queryBuilder.query(sqLiteDatabase,projection,selection,selectionArgs,null,null,orderBy);

        //用于uri的注册变更
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG,"sumugu,queried records:"+cursor.getCount());
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri))
        {
            case ListContract.LIST_DIR:
                Log.d(TAG,"sumugu,gotType:"+ListContract.LIST_TYPE_DIR);
                return ListContract.LIST_TYPE_DIR;
            case ListContract.LIST_ITEM:
                Log.d(TAG,"sumugu,gotType:"+ListContract.LIST_ITEM);
                return ListContract.LIST_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal URI:"+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri uriRet=null;

        //判断正确的Uri，必须是CONTENT_URI,不必指定ID,既是DIR
        if(sUriMatcher.match(uri)!=ListContract.LIST_DIR){
            throw new IllegalArgumentException("sumugu,Illegal uri:"+uri);
        }

        //因为是插入动作，需要可写的数据库
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        //开始插入，获取返回的ID，用于判断成功，-1就是失败
        long rowId=sqLiteDatabase.insertWithOnConflict(ListContract.TABLE,null,values,SQLiteDatabase.CONFLICT_IGNORE);

        if (rowId!=-1)
        {
            uriRet = ContentUris.withAppendedId(uri, rowId);
            Log.d(TAG,"sumugu,inserted uri:"+uriRet);

            //通知用于这个uri的数据已经更改
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return uriRet;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int ret=0;
        String where;
        switch (sUriMatcher.match(uri))
        {
            case ListContract.LIST_DIR:
                where = (null == selection)?"1":selection;  //删除全部（有条件）
                break;
            case ListContract.LIST_ITEM:
                long id= ContentUris.parseId(uri);  //获取uri的D部分，就是ID，删除指定ID的条目
                where = ListContract.Column.LIST_ID+"="+id
                        +(TextUtils.isEmpty(selection)?"":" and ( "+selection+" )");
                break;
            default:
                throw new IllegalArgumentException("sumugu,Illegal URI:"+uri);
        }

        //要删除，一样需要可写的数据库实例
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        //开始删除，返回结果（删除条目的数量）
        ret = sqLiteDatabase.delete(ListContract.TABLE,where,selectionArgs);
        if(ret>0)
        {
            //成功删除，通知使用这个uri的数据已经更新
            getContext().getContentResolver().notifyChange(uri,null);
        }
        Log.d(TAG,"sumugu,deleted records:"+ret);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;

        switch(sUriMatcher.match(uri)){
            case ListContract.LIST_DIR:
                //不指定id，因此我们要清点更新的行数
                where = selection;
                break;
            case ListContract.LIST_ITEM:
                long id= ContentUris.parseId(uri);
                where = ListContract.Column.LIST_ID + "=" + id
                        +(TextUtils.isEmpty(selection) ? "":" and ("
                        + selection +")");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri:"+uri);
        }

        //凡事要修改数据库，都需要可写的数据库实例
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        //返回修改条目的数量
        int ret = sqLiteDatabase.update(ListContract.TABLE, values, where, selectionArgs);

        if(ret>0){
            //通知uri数据已经更改
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG,"sumugu,updated records:"+ret);
        return ret;
    }
}
