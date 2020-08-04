package com.ajdeveloper.instadownloader.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.List;

import com.ajdeveloper.instadownloader.Adapters.ImageViewerAdaptor;
import com.ajdeveloper.instadownloader.Interfaces.CustomCallbackListener;
import com.ajdeveloper.instadownloader.R;


public class ImageViewerActivity extends AppCompatActivity {

    private ViewPager viewPager;
    public static int position;
    public static List<File> files;
    ImageViewerAdaptor adapter;
    TextView textviewposition;
    public static RelativeLayout img_back, img_delete, img_farword, text_layout;
    public static RelativeLayout hidethis;
    int count = 0;
    private static CustomCallbackListener customCallbackListener;

    public static void customImageViewer(Context context, List<File> Files, int Position, CustomCallbackListener customCallbackListener) {
        files = Files;
        position = Position;
        ImageViewerActivity.customCallbackListener = customCallbackListener;
        try {
            if (context != null) {
                context.startActivity(new Intent(context, ImageViewerActivity.class));
            }
        } catch (Exception ignored) {
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        betteryBarColor();
        setContentView(R.layout.activity_image_viewer);
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {

        }
        viewPager = (ViewPager) findViewById(R.id.pager);
        textviewposition = findViewById(R.id.textviewposition);
        img_delete = findViewById(R.id.img_delete);
        img_farword = findViewById(R.id.img_farword);
        img_back = findViewById(R.id.img_back);
        hidethis = findViewById(R.id.hidethis);
        text_layout = findViewById(R.id.text_layout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hidethis.setVisibility(View.INVISIBLE);
            }
        }, 1500);
        text_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        textviewposition.setText(position + 1 + "/" + files.size());
        if (files != null) {
            adapter = new ImageViewerAdaptor(this, files);
            viewPager.setAdapter(adapter);
        }
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                try {
                    textviewposition.setText((position + 1) + "/" + files.size());
                } catch (Exception ignored) {
                }
                try {
                    ImageViewerActivity.position = position;
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ImageViewerActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert
                    );
                } else {
                    builder = new AlertDialog.Builder(ImageViewerActivity.this);
                }
                builder.setMessage("Delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    count = 1;
                                } catch (Exception ignored) {
                                }
                                int tempPos = position;
                                File filedelete = files.get(position);
                                if (filedelete.exists()) {
                                    filedelete.delete();
                                    files.remove(position);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        Intent mediaScanIntent = new Intent(
                                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        Uri contentUri = Uri.fromFile(new File(String.valueOf(filedelete)));
                                        mediaScanIntent.setData(contentUri);
                                        ImageViewerActivity.this.sendBroadcast(mediaScanIntent);
                                    } else {
                                        ImageViewerActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                                    }
                                    ImageViewerActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(String.valueOf(filedelete)))));
                                    if (tempPos == files.size()) {
                                        onBackPressed();
                                    } else {
                                        adapter = new ImageViewerAdaptor(ImageViewerActivity.this, files);
                                        viewPager.setAdapter(adapter);
                                        viewPager.setCurrentItem(tempPos);
                                        textviewposition.setText(tempPos + "/" + files.size());
                                    }
                                    customCallbackListener.onItemClicked(0);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
        img_farword.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        File fileshare = files.get(position);
                        shareFiles(ImageViewerActivity.this, fileshare);
                        return null;
                    }
                }.execute();
            }
        });
    }

    private void shareFiles(Context context, File file) {
        Uri imageUri = FileProvider.getUriForFile(ImageViewerActivity.this, ImageViewerActivity.this.getApplicationContext().getPackageName() + ".my.package.name.provider", file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("*/*");
        context.startActivity(Intent.createChooser(shareIntent, "Share files to.."));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void betteryBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

}

