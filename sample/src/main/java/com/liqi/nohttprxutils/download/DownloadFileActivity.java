package com.liqi.nohttprxutils.download;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liqi.nohttprxutils.R;
import com.liqi.nohttprxutils.StaticHttpUrl;
import com.liqi.nohttprxutils.utils.FileUtil;
import com.liqi.nohttputils.download.NohttpDownloadUtils;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.StorageReadWriteError;
import com.yanzhenjie.nohttp.error.StorageSpaceNotEnoughError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * 文件下载演示界面
 * 注：如果上一次下载任务没有完成或者没有清空，那么下一次点击任何下载都会继续执行上一次没有完成的任务继续下载
 *
 * 个人QQ:543945827
 * NoHttp作者群号：46523908
 * Created by LiQi on 2017/2/24.
 */

public class DownloadFileActivity extends AppCompatActivity implements View.OnClickListener, DownloadListener {
    //存储文件夹路径
    private final String FILEPATH = FileUtil.getRootPath().getAbsolutePath() + StaticHttpUrl.FILE_PATH + "/DownloadFile";
    //文件下载路径
    private String DOWNLOAD_FILE01 = "http://api.nohttp.net/download/1.apk",
            DOWNLOAD_FILE02 = "http://api.nohttp.net/download/2.apk",
            DOWNLOAD_FILE03 = "http://api.nohttp.net/download/3.apk";
    private ProgressBar download_single_progress;
    private TextView download_single_hint;
    private Button download_single_button;
    private ProgressBar download_multi_progress01, download_multi_progress02;
    private TextView download_multi_hint01, download_multi_hint02;
    private Button download_multi_button;
    //单个下载标识
    private boolean mSingleDownloadTag = true;
    private int mSingleInit = -1;
    //批量下载标识
    private boolean mMultiDownloadTag = true;
    private int mMultiInit = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_file_activity);
        download_single_progress = (ProgressBar) findViewById(R.id.download_single_progress);
        download_single_hint = (TextView) findViewById(R.id.download_single_hint);
        download_single_button = (Button) findViewById(R.id.download_single_button);
        download_single_button.setOnClickListener(this);
        download_multi_progress01 = (ProgressBar) findViewById(R.id.download_multi_progress01);
        download_multi_progress02 = (ProgressBar) findViewById(R.id.download_multi_progress02);
        download_multi_hint01 = (TextView) findViewById(R.id.download_multi_hint01);
        download_multi_hint02 = (TextView) findViewById(R.id.download_multi_hint02);
        download_multi_button = (Button) findViewById(R.id.download_multi_button);
        download_multi_button.setOnClickListener(this);
        FileUtil.initDirectory(FILEPATH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_single_button:
                //下载
                if (mSingleDownloadTag) {
                    mSingleDownloadTag = false;
                    //把要下载的请求添加到下载服务队列中，并开始下载
                    if (mSingleInit == -1) {
                        NohttpDownloadUtils.getNohttpDownloadBuild()
                                .addDownloadParameter(DOWNLOAD_FILE01, "Liqi_single_test.apk")
                                .setRange(true)
                                .setDownloadListener(this)
                                .setDeleteOld(false)
                                .setThreadPoolSize(3)
                                .setFileFolder(FILEPATH)
                                .satart(this);
                        mSingleInit = 1;
                    }
                    //从暂停中恢复下载
                    else {
                        NohttpDownloadUtils.startRequest(DOWNLOAD_FILE01);
                    }
                    download_single_button.setText("暂停下载");
                }
                //暂停
                else {
                    mSingleDownloadTag = true;
                    NohttpDownloadUtils.cancel(DOWNLOAD_FILE01);
                    download_single_button.setText("开始下载");
                }
                break;
            case R.id.download_multi_button:
                //批量下载
                if (mMultiDownloadTag) {
                    mMultiDownloadTag = false;
                    //把要下载的请求添加到下载服务队列中，并开始下载
                    if (mMultiInit == -1) {
                        NohttpDownloadUtils.getNohttpDownloadBuild()
                                .addDownloadParameter(DOWNLOAD_FILE02, "Liqi_multi_test01.apk")
                                .addDownloadParameter(DOWNLOAD_FILE03, "Liqi_multi_test02.apk")
                                .setFileFolder(FILEPATH)
                                .setRange(true)
                                .setDownloadListener(this)
                                .setDeleteOld(false)
                                .setThreadPoolSize(3)
                                .satart(this);
                        mMultiInit = 1;
                    }
                    //从暂停中恢复批量下载
                    else {
                        NohttpDownloadUtils.startAllRequest();
                    }

                    download_multi_button.setText("暂停批量下载");
                }
                //批量暂停
                else {
                    mMultiDownloadTag = true;
                    NohttpDownloadUtils.cancel();
                    download_multi_button.setText("开始批量下载");
                }
                break;
        }
    }

    @Override
    public void onDownloadError(int what, Exception exception) {
        String message = getString(R.string.download_error);
        String messageContent;
        if (exception instanceof ServerError) {
            messageContent = getString(R.string.download_error_server);
        } else if (exception instanceof NetworkError) {
            messageContent = getString(R.string.download_error_network);
        } else if (exception instanceof StorageReadWriteError) {
            messageContent = getString(R.string.download_error_storage);
        } else if (exception instanceof StorageSpaceNotEnoughError) {
            messageContent = getString(R.string.download_error_space);
        } else if (exception instanceof TimeoutError) {
            messageContent = getString(R.string.download_error_timeout);
        } else if (exception instanceof UnKnownHostError) {
            messageContent = getString(R.string.download_error_un_know_host);
        } else if (exception instanceof URLError) {
            messageContent = getString(R.string.download_error_url);
        } else {
            messageContent = getString(R.string.download_error_un);
        }
        message = String.format(Locale.getDefault(), message, messageContent);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
        int progress = 0;
        if (allCount != 0) {
            progress = (int) (rangeSize * 100 / allCount);
        }
        setProgress(what, progress, 0);
    }

    @Override
    public void onProgress(int what, int progress, long fileCount, long speed) {
        setProgress(what, progress, speed);
    }

    @Override
    public void onFinish(int what, String filePath) {
        downloadHint(what, "下载完成：存储路径>" + filePath, true);
    }

    @Override
    public void onCancel(int what) {
        downloadHint(what, "暂停下载", false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // NohttpDownloadUtils.clearAll();
    }

    /**
     * 在下载产生时间提示
     *
     * @param what 下载请求标识
     * @param hint 下载提示
     * @param tag  取消还是完成状态
     */
    private void downloadHint(int what, String hint, boolean tag) {
        if (what == NohttpDownloadUtils.getDownloadRequestsWhat(DOWNLOAD_FILE01)) {
            download_single_hint.setText(hint);
            if (tag) {
                download_single_button.setText("完成下载");
                download_single_button.setEnabled(false);
            }
        } else if (what == NohttpDownloadUtils.getDownloadRequestsWhat(DOWNLOAD_FILE02)) {
            download_multi_hint01.setText(hint);
            if (tag) {
                download_multi_button.setText("完成下载");
                download_multi_button.setEnabled(false);
            }
        } else if (what == NohttpDownloadUtils.getDownloadRequestsWhat(DOWNLOAD_FILE03)) {
            download_multi_hint02.setText(hint);
            if (tag) {
                download_multi_button.setText("完成下载");
                download_multi_button.setEnabled(false);
            }
        }
    }

    private void setProgress(int what, int progress, long speed) {
        if (what == NohttpDownloadUtils.getDownloadRequestsWhat(DOWNLOAD_FILE01)) {
            download_single_progress.setProgress(progress);
            updateProgress(download_single_hint, progress, speed);
        } else if (what == NohttpDownloadUtils.getDownloadRequestsWhat(DOWNLOAD_FILE02)) {
            download_multi_progress01.setProgress(progress);
            updateProgress(download_multi_hint01, progress, speed);
        } else if (what == NohttpDownloadUtils.getDownloadRequestsWhat(DOWNLOAD_FILE03)) {
            download_multi_progress02.setProgress(progress);
            updateProgress(download_multi_hint02, progress, speed);
        }
    }

    private void updateProgress(TextView textView, int progress, long speed) {
        double newSpeed = speed / 1024D;
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        String sProgress = getString(R.string.download_progress);
        sProgress = String.format(Locale.getDefault(), sProgress, progress, decimalFormat.format(newSpeed));
        textView.setText(sProgress);
    }
}
