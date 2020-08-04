package com.ajdeveloper.instadownloader.Components.component;

import com.ajdeveloper.instadownloader.Activities.BrowserActivity;
import com.ajdeveloper.instadownloader.FragmentsN.BrowserActivityFragment;
import com.ajdeveloper.instadownloader.FragmentsN.download_url;

/* renamed from: com.downloadvideos.videodownloader.hdvideo.anyvideodownloader.di.component.ActivityComponent */
public interface ActivityComponent {
    void inject(download_url download_url);

    void inject(BrowserActivity browserActivity);

    void inject(BrowserActivityFragment naviDrawer);
}
