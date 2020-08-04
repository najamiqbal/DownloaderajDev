package com.ajdeveloper.instadownloader.FragmentsN;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore.Files;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.DateUtils;
import com.ajdeveloper.instadownloader.Utils.DialogUtils;
import com.ajdeveloper.instadownloader.Utils.FileSizes;
import com.ajdeveloper.instadownloader.app.AVD;
import com.ajdeveloper.instadownloader.app.Constants;
import com.ajdeveloper.instadownloader.model.WAStatus;

public class show_downloads extends Fragment {
    public static boolean IS_ALIVE = false;
    public static final String TAG = "show_downloads";
    public static show_downloads sInstance;
    public FloatingActionButton mBtnDelete;
    private Context mContext;
    private TextView mTvNoDownloads;
    private ArrayList<WAStatus> mVideosList;
    private RecyclerView recyclerView;
    View rootView;

    private class GetStatusesAsyncTask extends AsyncTask<Void, Void, Void> {
        private GetStatusesAsyncTask() {
        }

        protected void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            show_downloads.this.setAdapter();
        }

        @Override
        protected Void doInBackground(Void... voidArr) {
            getStatses(new File(Constants.SAVED_MEDIA_PATH));
            return null;
        }

        private void getStatses(File file) {
            show_downloads.this.mVideosList = new ArrayList();
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File file2 : listFiles) {
                    if (!file2.isDirectory()) {
                        WAStatus wAStatus = new WAStatus();
                        wAStatus.setFileName(file2.getName());
                        wAStatus.setDate(DateUtils.getTimeAgo(file2.lastModified()));
                        wAStatus.setStatusType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file2).toString())));
                        wAStatus.setFilePath(file2.getAbsolutePath());
                        wAStatus.setFileSize(FileSizes.getHumanReadableSize(file2.length(), show_downloads.this.getContext()));
                        StringBuilder sb = new StringBuilder();
                        sb.append(Constants.WA_STATUSES_PATH);
                        sb.append(file2.getName());
                        wAStatus.setDestPath(sb.toString());
                        show_downloads.this.mVideosList.add(wAStatus);
                    }
                }
            }
        }
    }

    public static show_downloads newInstance() {
        return new show_downloads();
    }

    public void onStart() {
        IS_ALIVE = true;
        super.onStart();
    }

    public Context getContext() {
        if (this.mContext == null) {
            this.mContext = super.getContext();
        }
        return this.mContext;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_saved, viewGroup, false);
        sInstance = this;
        IS_ALIVE = true;
        this.recyclerView = (RecyclerView) this.rootView.findViewById(R.id.recyclerView);
        this.mTvNoDownloads = (TextView) this.rootView.findViewById(R.id.tvNoDownloadsShowDownloads);
        this.mBtnDelete = (FloatingActionButton) this.rootView.findViewById(R.id.btnDeleteShowDownloads);
        this.mBtnDelete.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                show_downloads.this.showDialogToDelete();
            }
        });
        checkPermission();
        return this.rootView;
    }

    /* access modifiers changed from: private */
    public void showDialogToDelete() {
        new DialogUtils(getContext()).showInfoDialog("Are you sure?", "Selected videos will also be deleted from Gallery.", "Delete", "Cencel", "flag_delete_selected", new DialogUtils.DialogCallBack() {
            public void onNegButtonClicked(Bundle bundle) {
            }

            public void onPosButtonClicked(Bundle bundle) {
                show_downloads.this.deleteSelected();
            }
        });
    }

    /* access modifiers changed from: private */
    public void deleteSelected() {
        for (String file : AVD.getINSTANCE().getSelectedPaths()) {
            File file2 = new File(file);
            if (file2.exists()) {
                deleteFileFromMediaStore(getContext().getContentResolver(), file2);
            }
        }
        disableSelectionMode();
        new GetStatusesAsyncTask().execute(new Void[0]);
    }

    public static void deleteFileFromMediaStore(ContentResolver contentResolver, File file) {
        String str;
        try {
            str = file.getCanonicalPath();
        } catch (IOException unused) {
            str = file.getAbsolutePath();
        }
        if (contentResolver.delete(Files.getContentUri("external"), "_data=?", new String[]{str}) == 0) {
            File file2 = new File(file.getPath());
            if (!file2.exists()) {
                return;
            }
            if (file2.delete()) {
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append("file Deleted :");
                sb.append(file.getPath());
                printStream.println(sb.toString());
                return;
            }
            PrintStream printStream2 = System.out;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("file not Deleted :");
            sb2.append(file.getPath());
            printStream2.println(sb2.toString());
        }
    }

    protected void checkPermission() {
        if (VERSION.SDK_INT < 23) {
            new GetStatusesAsyncTask().execute(new Void[0]);
        } else if (getActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            new GetStatusesAsyncTask().execute(new Void[0]);
        } else if (shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
            Builder builder = new Builder(getActivity());
            builder.setMessage("Write external storage permission is required.");
            builder.setTitle("Please grant permission");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions((Activity) show_downloads.this.getContext(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
                }
            });
            builder.setNeutralButton("Cancel", null);
            builder.create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length <= 0 || iArr[0] != 0) {
            this.mTvNoDownloads.setVisibility(0);
            this.mTvNoDownloads.setText("Please grant permission. To access your downloaded videos.");
        } else if (i == 123) {
            new GetStatusesAsyncTask().execute(new Void[0]);
        }
    }

    /* access modifiers changed from: private */
    public void setAdapter() {
    }

    public void onDestroy() {
        disableSelectionMode();
        super.onDestroy();
    }

    public void disableSelectionMode() {
        this.mBtnDelete.hide();
        AVD.getINSTANCE().isMultiSelectedMode = false;
        AVD.getINSTANCE().getSelectedPaths().clear();
    }
}
