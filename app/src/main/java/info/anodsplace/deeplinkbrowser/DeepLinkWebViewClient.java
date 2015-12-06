package info.anodsplace.deeplinkbrowser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class DeepLinkWebViewClient extends WebViewClient {
    private final String mHost;
    private final Listener mListener;
    private Context mContext;

    public interface Listener {
        void onUrlChange(String url);
    }

    public DeepLinkWebViewClient(Context context, String host, Listener listener) {
        mContext = context;
        mHost = host;
        mListener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (TextUtils.isEmpty(mHost) || !Uri.parse(url).getHost().contains(mHost)) {
            mListener.onUrlChange(url);
            // This is my web site, so do not override; let my WebView load the page
            return false;
        }
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(intent);
        return true;
    }
}
