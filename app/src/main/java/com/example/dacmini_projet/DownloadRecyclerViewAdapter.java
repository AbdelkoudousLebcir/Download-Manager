package com.example.dacmini_projet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

// this is the recycler view adapter where most of things happen it's like a bridge between the recycler view interface and the java code
public class DownloadRecyclerViewAdapter extends RecyclerView.Adapter<DownloadRecyclerViewAdapter.ViewHolder> {
    private ArrayList<DownloadableItem> downloads;
    Context context;

    public DownloadRecyclerViewAdapter(ArrayList<DownloadableItem> downloads, Context context) {
        this.downloads = downloads;
        this.context = context;
        viewHolders = new ArrayList<>();
    }

    ArrayList<ViewHolder> viewHolders;

    public ArrayList<ViewHolder> getViewHolders() {
        return viewHolders;
    }

    // this code is to specify what is the item that will be recycler view list item and create/recycler the holder for each item
    @NonNull
    @Override
    public DownloadRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloadable_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolders.add(viewHolder);
        return viewHolder;
    }


    // then the item is bound to the holder here
    @Override
    public void onBindViewHolder(@NonNull DownloadRecyclerViewAdapter.ViewHolder holder, int position) {
        DownloadableItem downloadableItem = downloads.get(position);
    }

    @Override
    public int getItemCount() {
        return downloads.size();
    }

    // when an item is added the arraylist of the downloads is updated using the set methode then it notifies the adapter that something is changed
    public void setDownloads(ArrayList<DownloadableItem> downloads) {
        this.downloads = downloads;
        notifyDataSetChanged();

    }

    //this is the view holder where to set the behaviour of the item
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView checkBtn;
        ProgressBar progressBar;
        TextView progressText;

        ImageView downloadBtn;
        EditText urlField;
        EditText nameField;
        ImageView pause;
        ImageView resume;
        ImageView delete;
        ImageView downloadImage;
        SwipeLayout swipeLayout;
        Boolean swiped = false;
        RelativeLayout right_side;
        TextView time;
        TextView fileNameT;
        boolean downloadStarted= false;

        // first we need to link the components of the view with the code
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
            checkBtn = itemView.findViewById(R.id.checkButton);
            urlField = itemView.findViewById(R.id.urlEditText);
            nameField = itemView.findViewById(R.id.fileName);
            progressBar = itemView.findViewById(R.id.progressBar);
            progressText = itemView.findViewById(R.id.progressText);
            pause = itemView.findViewById(R.id.pause);
            resume = itemView.findViewById(R.id.resume);
            swipeLayout = itemView.findViewById(R.id.downloadableItem);
            right_side = itemView.findViewById(R.id.right_side);
            delete = itemView.findViewById(R.id.delete);
            downloadImage = itemView.findViewById(R.id.imageView);
            time = itemView.findViewById(R.id.time);
            fileNameT = itemView.findViewById(R.id.fileNameT);


            // setting the properties of the progress bar
            progressBar.setProgress(0);
            progressBar.setMax(100);

            Handler handler = new Handler();

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onStartOpen(SwipeLayout layout) {
                    swiped = true;
                }

                @Override
                public void onOpen(SwipeLayout layout) {

                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onClose(SwipeLayout layout) {
                    swiped = false;
                    progressText.setText(progressBar.getProgress() + "%");
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                }
            });




            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // when someone clicks the download we need to check first if the fields are not empty
                    String urlS;
                    if (urlField.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "The urlS field is empty", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (nameField.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "The name field is empty", Toast.LENGTH_LONG).show();
                        return;
                    }
                    urlS = urlField.getText().toString().trim();

                    String name = nameField.getText().toString().trim();
                    try {
                        // then we need to check if the link is valid
                        checkIfDownloadable(urlS, "nooo", "The link is not valid");

                        final int[] count = {0};

                        //  this is made to wait for the checking response i can't use the thread sleep methode because in android you can't sleep the main thread or the app will crash
                        final Runnable runnable = new Runnable() {
                            public void run() {
                                if (count[0]++ < 51) {
                                    if (downloadable != null) {
                                        if (downloadable) {
                                            String type[] = contentType.split("/");

                                            // if it's donloadble we start the download and we update the ui
                                            String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + name + "." + type[type.length - 1];
                                            if (new File(fileName).exists()){
                                                NetChecker.showCostumDialog(context,"يوجد ملف يحمل نفس الإسم الرجاء تغيير الإسم و المحاولة مرة أخرى.\na file with the given name already exists please change the name and try again.", 1);
                                                return;
                                            }

                                            FileDownloader fileDownloader = new FileDownloader(new FileDownloader.DownloadListener() {
                                                int p = -1;
                                                int c = 0;

                                                @Override
                                                public void onProgressUpdate(int progress) {
                                                    if (p < progress) {

                                                        if (c == 0) {
                                                            downloadBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                }
                                                            });
                                                            c++;
                                                            fileNameT.setText(name + "." + type[type.length - 1]);
                                                            right_side.setVisibility(View.VISIBLE);
                                                            checkBtn.setVisibility(View.GONE);
                                                            downloadBtn.setVisibility(View.GONE);
                                                            urlField.setVisibility(View.GONE);
                                                            nameField.setVisibility(View.GONE);
                                                            progressBar.setVisibility(View.VISIBLE);
                                                            progressText.setVisibility(View.VISIBLE);
                                                            time.setVisibility(View.VISIBLE);
                                                            fileNameT.setVisibility(View.VISIBLE);
                                                            downloadImage.setVisibility(View.VISIBLE);
                                                            ViewGroup.LayoutParams swipeLayoutParams = swipeLayout.getLayoutParams();
                                                            float scale = context.getResources().getDisplayMetrics().density;
                                                            int newHeightPX = (int) (72 * scale + 0.5f);
                                                            swipeLayoutParams.height = newHeightPX;
                                                            swipeLayout.setLayoutParams(swipeLayoutParams);

                                                            switch (contentType) {
                                                                case "video/mp4":
                                                                case "video/webm":
                                                                case "video/mpeg":
                                                                case "video/quicktime":
                                                                case "video/x-msvideo":
                                                                case "application/octet-stream":
                                                                case "multipart/x-mixed-replace":
                                                                case "application/vnd.apple.mpegurl":
                                                                case "application/x-mpegURL":
                                                                case "application/x-shockwave-flash":
                                                                    downloadImage.setImageResource(R.drawable.video);
                                                                    downloadImage.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.video)));
                                                                    break;
                                                                case "audio/mpeg":
                                                                case "audio/wav":
                                                                case "audio/midi":
                                                                case "audio/ogg":
                                                                case "audio/webm":
                                                                case "audio/aac":
                                                                case "audio/x-ms-wma":
                                                                case "audio/x-aiff":
                                                                case "audio/x-flac":
                                                                    downloadImage.setImageResource(R.drawable.audio_headphones_svgrepo_com);
                                                                    downloadImage.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.audio)));

                                                                    break;
                                                                case "image/jpeg":
                                                                case "image/png":
                                                                case "image/gif":
                                                                case "image/bmp":
                                                                case "image/webp":
                                                                case "image/tiff":
                                                                    downloadImage.setImageResource(R.drawable.image_svgrepo_com);
                                                                    downloadImage.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.image)));

                                                                    break;
                                                                case "application/pdf":
                                                                    downloadImage.setImageResource(R.drawable.book_svgrepo_com);
                                                                    downloadImage.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.pdf)));
                                                                    break;
                                                                default:
                                                                    downloadImage.setImageResource(R.drawable.text_file);
                                                                    downloadImage.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
                                                                    break;

                                                            }
                                                        }


                                                        progressBar.incrementProgressBy(progress - p);


                                                        if (!swiped)
                                                            progressText.setText(progressBar.getProgress() + "%");
                                                        p = progress;
                                                    }
                                                }

                                                @Override
                                                public void onDownloadComplete(String filePath) {
                                                    Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show();
                                                    Log.d("FileDownloader", "File downloaded to: " + filePath);
                                                    progressText.setText(progressBar.getProgress() + "%");
                                                    pause.setVisibility(View.GONE);
                                                    resume.setVisibility(View.GONE);
                                                    ViewGroup.MarginLayoutParams deleteLayoutParams = (ViewGroup.MarginLayoutParams) delete.getLayoutParams();
                                                    deleteLayoutParams.setMargins(20, 10, 10, 10);
                                                    delete.setLayoutParams(deleteLayoutParams);


                                                }

                                                @Override
                                                public void onDownloadFailed() {
                                                    if (NetChecker.isConnected(context)) {
                                                        Toast.makeText(context, " Download failed, An error has occurred", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        NetChecker.showCostumDialog(context,"فشل تحميل الملف يرجى الإتصال بالإنترنت و المحاولة مرة أخرى\n File download failed please connect to the INTERNET and try again",2);
                                                        downloadImage.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.pdf)));

                                                    }
                                                    downloadImage.setImageResource(R.drawable.failed);
                                                    time.setText("Download failed");
                                                    downloadImage.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));

                                                }
                                            }, fileLength);
                                            fileDownloader.startDownload(urlS, fileName);
                                            pause.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    fileDownloader.pause();
                                                    pause.setVisibility(View.GONE);
                                                    resume.setVisibility(View.VISIBLE);
                                                    swiped = false;
                                                    progressText.setText(progressBar.getProgress() + "%");

                                                }

                                            });
                                            resume.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    fileDownloader.resume();
                                                    resume.setVisibility(View.GONE);
                                                    pause.setVisibility(View.VISIBLE);
                                                    swiped = false;
                                                }
                                            });

                                            delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    fileDownloader.cancel(true);

                                                }
                                            });
                                        } else {
                                            return;
                                        }

                                    }
                                } else {
                                    handler.postDelayed(this, 100);
                                }
                            }

                        };

                        handler.post(runnable);


                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                }
            });

            checkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // checking for download-ablity

                    final String url;

                    if (urlField.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "The url field is empty", Toast.LENGTH_LONG).show();
                        return;
                    }
                    url = urlField.getText().toString().trim();


                    try {
                        checkIfDownloadable(url, "downloadable", "not downloadable");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


        }

        Boolean downloadable;
        int fileLength;
        String fileType;
        String contentType;


        // this is the check methode
        public void checkIfDownloadable(String urlS, String valid, String notValid) throws InterruptedException {
            //first we need to check if the text given is a url
            if (checkIfValid(urlS)) {
                // then we check if it's a downloadable file link i need to use thread also because i can't start a network related task in the main thread in andoird
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            // we start a connection and check for the type if it's application then it's a downloadable file except for json and xml responses types
                            URL url = new URL(urlS);
                            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("HEAD");
                            contentType = connection.getContentType();


                            // Use Handler to update UI on the main thread
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (contentType != null) {
                                        if (contentType.contains("application") || !contentType.contains("text/html")) {
                                            if (!contentType.contains("json") && !contentType.contains("xml")) {
                                                if (valid != "nooo")
                                                    Toast.makeText(context, valid, Toast.LENGTH_LONG).show();
                                                downloadable = new Boolean(true);
                                                fileLength = connection.getContentLength();

                                            } else {
                                                Toast.makeText(context, notValid, Toast.LENGTH_LONG).show();
                                                downloadable = new Boolean(false);
                                            }
                                        } else {
                                            Toast.makeText(context, notValid, Toast.LENGTH_LONG).show();
                                            downloadable = new Boolean(false);
                                        }
                                    }
                                }
                            });
                        } catch (final Exception e) {
                            e.printStackTrace();
                            // Handle the exception, if needed
                        }
                    }
                });
                thread.start();
                Log.d("ttt", String.valueOf(downloadable));
            } else
                return;


        }

        // this methode checks if the text if url or not using url.net library and trying to create a url object using the text
        private boolean checkIfValid(String urlS) {
            boolean v;
            try {
                URL url = new URL(urlS);
                v = true;
            } catch (MalformedURLException e) {
                v = false;
                Toast.makeText(context, "The url is not valid", Toast.LENGTH_LONG).show();

            }
            return v;
        }
    }

}
