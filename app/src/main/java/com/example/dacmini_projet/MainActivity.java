package com.example.dacmini_projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                = new DownloadRecyclerViewAdapter(downloadsList,this);

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
                for (int i = 0; i< downloadsList.size();i++){
                    downloadRecyclerViewAdapter.viewHolders.get(i).downloadBtn.callOnClick();
                }
            }
        });


        if (!NetChecker.isConnected(this)){
            NetChecker.showCostumDialog(this,"هذا التطبيق يستلزم وجود اتصال بالانترنت الرجاء التحقق من اتصالك.\nThis app needs network connection to function please check your connection. ",2);
        }




        /*OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(15, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(15, TimeUnit.SECONDS)   // Write timeout
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://helloworld-2zrw.onrender.com/hello/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiService service = retrofit.create(MyApiService.class);

        Call<String> call = service.getData1();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String data = response.body();
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();

                } else {
                    // Handle the error
                    Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    Log.d("neterror",response.toString());
                    String data = response.body();
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle network failures
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();

            }
        });



*/











    }

}