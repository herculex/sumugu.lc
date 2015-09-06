package com.sumugu.liubo.lc;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.sumugu.liubo.lc.contract.ListContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListLineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListLineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListLineFragment extends android.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG=ListLineFragment.class.getSimpleName();
    //
    //数据：要绑定显示的列表内容，标题TITLE，内容CONTENT，和创建时间CREATED_AT
    private static final String[] FROM = {ListContract.Column.LIST_TITLE,ListContract.Column.LIST_CONTENT,ListContract.Column.LIST_CREATED_AT};

    //视图：绑定到布局中具体对应的控件
    private static final int[] TO = {R.id.list_item_text_title,R.id.list_item_text_created_at,R.id.list_item_text_content};

    private static final int LOADER_ID=11;  //这个是一个任意的ID，它帮助我们确保装载器回调的是我们发起的那个。

    private SimpleCursorAdapter mSimpleCursorAdapter;
    private static final ViewBinder VIEW_BINDER = new ViewBinder() {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            long timestamp;

            //自定义绑定  将时间戳转换成相对时间。无奈SQLite的数据类型很少。
            switch(view.getId()){
                case R.id.list_item_text_created_at:
                    timestamp=cursor.getLong(columnIndex);
                    CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp);
                    ((TextView) view).setText(relTime);
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("加载 Loading lists ...");

        //将一个自定义列表布局文件（fragment_list_line.xml），并按FROM数据 TO视图 的映射关系对应加载数据
        mSimpleCursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.fragment_list_line,null,FROM,TO,0);

        mSimpleCursorAdapter.setViewBinder(VIEW_BINDER);

        setListAdapter(mSimpleCursorAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        //TODO: 点击List条目后，打开改List下的Item列表，就是ItemLineActivity。
        //获取详情片段
//        DetailsFragment fragment = (DetailsFragment)getFragmentManager().findFragmentById(R.id.fragment_details);
//
//        //详情片段可见吗？
//        if(fragment!=null && fragment.isVisible()){
//            fragment.updateView(id);
//        }else
//        {
        //TODO:
            startActivity(new Intent(getActivity(),ItemLineActivity.class).putExtra(ListContract.Column.LIST_ID, id));
//        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListLineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListLineFragment newInstance(String param1, String param2) {
        ListLineFragment fragment = new ListLineFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ListLineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_list_line, container, false);
//    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id!=LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");
        //CursorLoader 加载来自内容提供器的数据 ListProvider
        return new CursorLoader(getActivity(),ListContract.CONTENT_URI,null,null,null,ListContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//		Log.d(TAG,"onLoadFinished with cursor: "+cursor.getCount());
//		mAdapter.swapCursor(cursor);

//		Log.d(TAG,"onLoadFinished with cursor:"+cursor.getCount());
        if(null == cursor)
            Log.d(TAG,"cursor is NULL!!");
        else
            Log.d(TAG,"onLoadFinished with cursor:"+cursor.getCount());

        mSimpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSimpleCursorAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}
