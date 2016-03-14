package org.telegram.messenger.postgram.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ir.android.telegram.post.R;
import org.telegram.messenger.postgram.adapters.LikersListAdapter;
import org.telegram.messenger.postgram.classes.LikerList;
import org.telegram.messenger.postgram.network.MainNetwork;
import org.telegram.messenger.volley.Response;
import org.telegram.messenger.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;


public class LikerListActivity extends DialogFragment {

    private String Message_ID;
    private LikersListAdapter My_Adapter;
    private ListView message_listview;
    private RelativeLayout Home_Progressbar_layout;
    int limit = 0;
    private boolean loadingMore = true;
    TextView txtIsloading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main_likerlist, container, false);

        getDialog().setTitle(getResources().getString(R.string.liker_list));

        message_listview = (ListView) rootView.findViewById(R.id.message_listview);
        Home_Progressbar_layout = (RelativeLayout) rootView.findViewById(R.id.Home_Progressbar_layout);
        txtIsloading = (TextView) rootView.findViewById(R.id.txtIsloading);

        Message_ID = getArguments().getString("ID");

        MainNetwork.Get_Likers_List(getActivity(), Liker_Listener, Liker_ErrorListener, Message_ID,
                String.valueOf(limit));

        message_listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent Profile_Intent = new Intent(getActivity(), ProfileActivity.class);
//				Profile_Intent.putExtra("userid", String.valueOf(main_Liker_List.get(position).user_id));
//				Profile_Intent.putExtra("User_Name", main_Liker_List.get(position).user_username);
//				startActivity(Profile_Intent);
            }
        });
        message_listview.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                try {
                    int lastInScreen = firstVisibleItem + visibleItemCount;
                    if (lastInScreen == totalItemCount && !(loadingMore) && tmp_Liker_List.size() > 19) {
                        loadingMore = true;
                        MainNetwork.Get_Likers_List(getActivity(), Liker_Listener, Liker_ErrorListener, Message_ID,
                                String.valueOf(limit));
                        txtIsloading.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    List<LikerList> main_Liker_List = new ArrayList<LikerList>();
    List<LikerList> tmp_Liker_List = new ArrayList<LikerList>();

    Response.Listener<String> Liker_Listener = new Response.Listener<String>() {
        @Override
        public void onResponse(final String response) {
            if (isAdded()) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();

                                tmp_Liker_List.clear();

                                tmp_Liker_List = gson.fromJson(response, new TypeToken<List<LikerList>>() {
                                }.getType());

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        limit += 20;
                                        Home_Progressbar_layout.setVisibility(View.INVISIBLE);
                                        if (main_Liker_List.size() > 0) {
                                            main_Liker_List.addAll(tmp_Liker_List);
                                            My_Adapter.notifyDataSetChanged();
                                        } else {
                                            main_Liker_List.addAll(tmp_Liker_List);
                                            My_Adapter = new LikersListAdapter(getActivity(), main_Liker_List, false);
                                            message_listview.setAdapter(My_Adapter);
                                        }
                                        loadingMore = false;
                                        txtIsloading.setVisibility(View.INVISIBLE);
                                    }
                                });

                            } catch (Exception e) {
                                if (isAdded()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity(), getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }
                        }
                    }).start();
                } catch (Exception e) {
                }
            }
        }
    };
    Response.ErrorListener Liker_ErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (isAdded()) {
                Toast.makeText(getActivity(), getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
                txtIsloading.setVisibility(View.INVISIBLE);
                loadingMore = false;

            }

        }
    };
}