package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.CustomObj;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ParatiActivity extends AppCompatActivity
{
    FirebaseFirestore db;
    String TAG = "GIO";
    ListView listview;
    public static List<CustomObj> obj = new ArrayList<CustomObj>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parati);

        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueApp)));

        db = FirebaseFirestore.getInstance();
        listview = (ListView) findViewById(R.id.s_listView);

        final TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("PARA TI");

        db.collection("promociones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            obj.clear();
                            for (DocumentSnapshot document : task.getResult())
                            {
                                //Log.i(TAG, (String) document.getData().get("imagen_url"));
                                obj.add(new CustomObj(document.getId(), "", (String) document.getData().get("imagen_url"), false, "", 0, 0,0));
                            }
                            ListAdapter_parati customAdapter = new ListAdapter_parati(ParatiActivity.this, R.layout.choose_service_tab_custom, obj);
                            listview.setAdapter(customAdapter);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Manager.getInstance().actualContext = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}



class ListAdapter_parati extends ArrayAdapter<CustomObj>
{
    List<CustomObj> objList;
    CustomObj p;
    String TAG = "GIO";

    public ListAdapter_parati(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public ListAdapter_parati(Context context, int resource, List<CustomObj> items)
    {
        super(context, resource, items);
        objList = items;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ListAdapter_parati.ViewHolder holder = null;
        if (convertView == null)
        {

            LayoutInflater vi = LayoutInflater.from(getContext());
            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.customtab, null);
            holder = new ListAdapter_parati.ViewHolder();

            holder.icon = (ImageView) convertView.findViewById(R.id.icon);

            Glide.with(holder.icon.getContext())
                    .load(objList.get(position).img)
                    .into(holder.icon);

            convertView.setTag(holder);
        }else {
            holder = (ListAdapter_parati.ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public static class ViewHolder
    {
        public ImageView icon;
    }

    public int getPositionById(String _tag)
    {
        Integer pos = 0;
        for (int i = 0; i < ChooseVActivity.obj.size(); i++)
        {
            if (Objects.equals(_tag, String.valueOf(ChooseVActivity.obj.get(i).selected)))
            {
                pos = i;
                return pos;
            }
        }
        return pos;
    }


    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
}


