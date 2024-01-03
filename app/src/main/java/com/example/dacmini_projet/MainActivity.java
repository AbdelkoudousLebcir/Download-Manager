package com.example.dacmini_projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private FloatingActionButton checkAllBtn;
    private ArrayList<DownloadableItem> downloadsList;
    private ImageButton addBtn;
    private RecyclerView downloadsRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private DownloadRecyclerViewAdapter downloadRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nothing);
        menuItem.setEnabled(false);

        checkAllBtn = findViewById(R.id.checkAll);

        addBtn = findViewById(R.id.Addbtn);
        downloadsList = new ArrayList<>();
        downloadsList.add(new DownloadableItem());
        downloadsRecyclerView = findViewById(R.id.DownloadsRecyclerView);

        downloadRecyclerViewAdapter
                = new DownloadRecyclerViewAdapter(downloadsList, this);

        downloadsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        downloadsRecyclerView.setAdapter(downloadRecyclerViewAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadsList.add(new DownloadableItem());
                downloadRecyclerViewAdapter.setDownloads(downloadsList);
            }
        });

        checkAllBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                for (int i = 0; i < downloadsList.size(); i++) {
                    downloadRecyclerViewAdapter.viewHolders.get(i).downloadBtn.callOnClick();
                }
            }
        });


        if (!NetChecker.isConnected(this)) {
            NetChecker.showCostumDialog(this, "هذا التطبيق يستلزم وجود اتصال بالانترنت الرجاء التحقق من اتصالك.\nThis app needs network connection to function please check your connection. ", 2);
        }



    }

}