package com.sumugu.liubo.lc.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by liubo on 15/9/1.
 */
public class ItemContract extends DbContract {
    //Table specific constants
    //Table Name is "item"
    public static final String TABLE="item";
    //default sort by item_created_at
    public static final String DEFAULT_SORT=Column.ITEM_CREATED_AT+" DESC";

    //Columns specific constants
    public class Column{
        //ID，取基础列的ID名称 "_id"
        public static final String ITEM_ID = BaseColumns._ID;
        //标题
        public static final String ITEM_TITLE="item_title";
        //内容
        public static final String ITEM_CONTENT="item_content";
        //创建时间
        public static final String ITEM_CREATED_AT="item_created_at";
        //完成状态
        public static final String ITEM_IS_FINISHED="item_is_finished";
        //闹钟状态
        public static final String ITEM_HAS_CLOCK="item_has_clock";
        //设置闹钟
        public static final String ITEM_ALARM_CLOCK="item_alarm_clock";
        //所属列表的ID
        public static final String ITEM_LIST_ID="item_list_id";
        //排序的序号
        public static final String ITEM_INDEX="item_index";

    }

    // provider specific constants
    // content://com.sumugu.liubo.lc.ItemProvider/item
    public static final String AUTHORITY="com.sumugu.liubo.lc.provider.ItemProvider";
    public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/"+TABLE); //CONTENT_URI=content://com.sumugu.liubo.lc.ItemProvider/item
    public static final int ITEM_ITEM=1;
    public static final int ITEM_DIR=2;
    //单个条目
    public static final String ITEM_TYPE_ITEM="vnd.android.cursor.item/vnd.com.sumugu.liubo.lc.provider.item";
    //所有条目的目录
    public static final String ITEM_TYPE_DIR="vnd.android.cursor.dir/vnd.com.sumugu.liubo.lc.provider.item";

}
