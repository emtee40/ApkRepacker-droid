package com.mrikso.apkrepacker.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.jecelyin.editor.v2.utils.ExtGrep;
import com.mrikso.apkrepacker.R;
import com.mrikso.apkrepacker.fragment.FindFragment;
import com.mrikso.apkrepacker.ui.findresult.ChildData;
import com.mrikso.apkrepacker.ui.findresult.ParentData;
import com.mrikso.apkrepacker.utils.FileUtil;
import com.mrikso.apkrepacker.utils.StringUtils;
import com.mrikso.apkrepacker.utils.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchStringsTask extends AsyncTask<String, Integer, Void> {

    private FindFragment mFindFragment;
    private List<ParentData> mParentList;
    private Context mContext;
    private ExtGrep mExtGrep;

    public SearchStringsTask(Context context, FindFragment findFragment) {
        mFindFragment = findFragment;
        mContext = context;
        mExtGrep = StringUtils.extGreps;
    }

    @Override
    protected Void doInBackground(String... extGreps) {
        mParentList = getList(mExtGrep.execute());
        return null;
    }

    @Override
    protected void onPreExecute() {
        mFindFragment.showProgress();
    }

    @Override
    protected void onProgressUpdate(Integer... items) {
        mFindFragment.updateProgress(items);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        mFindFragment.setStringResult(mParentList);
        mFindFragment.hideProgress();
    }

    @SuppressLint("DefaultLocale")
    private List<ParentData> getList(List<ExtGrep.Result> results) {
        File file = null;

        int findResultsKeywordColor = ViewUtils.getThemeColor(mContext, R.attr.findResultsKeyword);

        List<ParentData> parentDataList = new ArrayList<>();
        List<ChildData> childDataList = null;

        for (ExtGrep.Result res : results) {
            if (!res.file.equals(file)) {
                file = res.file;
                childDataList = new ArrayList<>();
                parentDataList.add(new ParentData(file.getAbsolutePath().substring((FileUtil.getProjectPath() + "/").length()), childDataList));
            }

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(String.format("%1$4d :", res.lineNumber));
            int start = ssb.length();
            ssb.append(res.line);

            ssb.setSpan(new ForegroundColorSpan(findResultsKeywordColor), start + res.matchStart, start + res.matchEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            childDataList.add(new ChildData(ssb, file.getAbsolutePath(), res.lineNumber));
        }

        return parentDataList;
    }
}