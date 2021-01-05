package id.co.ppu.collfastmon.screen.lkp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.listener.OnApproveListener;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.master.MstTaskType;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnTaskLog;
import id.co.ppu.collfastmon.rest.APIonBuilder;
import id.co.ppu.collfastmon.util.DemoUtil;
import id.co.ppu.collfastmon.util.NetUtil;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.RealmResults;

public class ActivityTaskLog extends BasicActivity {

    public static final String PARAM_COLL_CODE = "collector.code";
    public static final String PARAM_LKP_DATE = "lkpDate";
    public static final String PARAM_COLLNAME = "collector.name";
    public static final String PARAM_LDV_NO = "ldvNo";

    public String collCode = null;
    public Date lkpDate = null;
    public String collName = null;
    public String ldvNo = null;

    @BindView(R.id.etNoLKP)
    EditText etNoLKP;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.table)
    TableLayout tableLayout;

    public static Intent createIntent(Context ctx, String collCode, long lkpDate, String collName, String ldvNo) {
        Intent i = new Intent(ctx, ActivityTaskLog.class);
        i.putExtra(PARAM_COLL_CODE, collCode);
        i.putExtra(PARAM_LKP_DATE, lkpDate);
        i.putExtra(PARAM_COLLNAME, collName);
        i.putExtra(PARAM_LDV_NO, ldvNo);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklog);

        ButterKnife.bind(this);

        etNoLKP.setTypeface(fontGoogle);
        etTglLKP.setTypeface(fontGoogle);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Who are you ?");
            return;
        }

        this.collCode = extras.getString(PARAM_COLL_CODE);
        this.lkpDate = new Date(extras.getLong(PARAM_LKP_DATE));
        this.collName = extras.getString(PARAM_COLLNAME);
        this.ldvNo = extras.getString(PARAM_LDV_NO);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_task_log);
            getSupportActionBar().setSubtitle(this.collName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etNoLKP.setText(this.ldvNo);
        etTglLKP.setText(Utility.convertDateToString(this.lkpDate, Utility.DATE_DISPLAY_PATTERN));

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        _getTaskLogFromServer();
    }

    private void _getTaskLogFromServer() {

        if (DemoUtil.isDemo(this)) {
            return;
        }

        if (!NetUtil.isConnectedUnlessToast(this)) {
            return;
        }

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, "Getting log from server.\nPlease wait...");

        APIonBuilder.getTaskLog(this, this.collCode, this.lkpDate, (e, result) -> {

            Utility.dismissDialog(mProgressDialog);

            if (e != null) {
                Log.e("eric.onFailure", e.getMessage(), e);

                Utility.showDialog(ActivityTaskLog.this, "Problem", e.getMessage());
                return;
            }

            if (result.getError() != null) {
                Utility.showDialog(ActivityTaskLog.this, "Error (" + result.getError().getErrorCode() + ")", result.getError().getErrorDesc());
                return;
            }

            // save db
            getRealmInstance().executeTransactionAsync(realm -> {
                boolean d = realm.where(TrnTaskLog.class)
                        .findAll()
                        .deleteAllFromRealm();

                realm.copyToRealm(result.getData());

            }, () -> _drawTable(), error -> {
                Toast.makeText(ActivityTaskLog.this, "Database Error", Toast.LENGTH_SHORT).show();

                error.printStackTrace();
            });
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_tasklog, menu);

//        Drawable drawable = menu.findItem(R.id.action_reopen_batch).getIcon();
//        drawable = DrawableCompat.wrap(drawable);
//        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_reopen_batch)
//                .setIcon(drawable)
                .setVisible(Utility.isSameDay(this.lkpDate, new Date()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reopen_batch) {

            _attemptReopenBatch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void _writeTableHeader(TextView textView, String text) {
        textView.setText(text);
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(this, android.R.style.TextAppearance_Small);
        } else {
            textView.setTextAppearance(android.R.style.TextAppearance_Small);
        }
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setBackgroundColor(Color.LTGRAY);

    }

    private void _drawTable() {

        // clear table
        tableLayout.removeAllViews();

        // create header
        TableRow row_header = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_task_log, null);

        _writeTableHeader(ButterKnife.findById(row_header, R.id.attrib_no), "No.");
        _writeTableHeader(ButterKnife.findById(row_header, R.id.attrib_task_name), "Task Name");
        _writeTableHeader(ButterKnife.findById(row_header, R.id.attrib_time), "Time");

        tableLayout.addView(row_header);

        RealmResults<TrnTaskLog> list = getRealmInstance().where(TrnTaskLog.class)
                .equalTo("pk.userCode", collCode)
                .findAll().sort("createdTimestamp");
//                .findAllSorted("pk.taskCode", Sort.ASCENDING, "pk.seqNo", Sort.ASCENDING);

        // eliminate duplicate, get the latest timestamp/seqNo
        List<TrnTaskLog> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

            String _taskCode = list.get(i).getPk().getTaskCode();
            Long _seqNo = list.get(i).getPk().getSeqNo();
            int adaDiRowNewList = -1;

            for (int j = 0; j < newList.size(); j++) {
                if (_taskCode.equals(newList.get(j).getPk().getTaskCode())) {
                    adaDiRowNewList = j;
                    break;
                }
            }

            if (adaDiRowNewList > -1) {
                // cek by seqNo
                if (list.get(i).getPk().getSeqNo() > newList.get(adaDiRowNewList).getPk().getSeqNo()) {
                    newList.set(adaDiRowNewList, getRealmInstance().copyFromRealm(list.get(i)));
                }
            } else {
                newList.add(getRealmInstance().copyFromRealm(list.get(i)));
            }
        }

        // sort by task code
        Collections.sort(newList, (t1, t2) -> t1.getPk().getTaskCode().compareTo(t2.getPk().getTaskCode()));

        // TODO: KHUSUS UTK REOPEN DITAMPILKAN SEBELUM GETSYNC ATAU SESUDAH LOGIN(MASALAHNYA LOGIN BISA BEDA HARI KRN BERHARI2 OFFLINE)
        for (int i = 0; i < newList.size(); i++) {

            TrnTaskLog obj = newList.get(i);

            TableRow row = (TableRow) LayoutInflater.from(ActivityTaskLog.this).inflate(R.layout.row_task_log, null);

            ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(i + 1) + ".");
            ((TextView) row.findViewById(R.id.attrib_no)).setTypeface(fontGoogle);

            MstTaskType code = getRealmInstance().where(MstTaskType.class)
                    .equalTo("taskCode", obj.getPk().getTaskCode())
                    .findFirst();

            ((TextView) row.findViewById(R.id.attrib_task_name)).setText(code.getShortDesc());
            ((TextView) row.findViewById(R.id.attrib_task_name)).setTypeface(fontGoogle);
            ((TextView) row.findViewById(R.id.attrib_time)).setText(Utility.convertDateToString(obj.getPk().getTaskDate(), "d MMM yyyy H:mm:ss"));
            ((TextView) row.findViewById(R.id.attrib_time)).setTypeface(fontGoogle);

            tableLayout.addView(row);
        }

    }

    private void doReopenBatch() {

        if (!NetUtil.isConnectedUnlessToast(this)) {
            return;
        }

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(this, getString(R.string.message_please_wait));

        // call API
        APIonBuilder.reopenBatch(this, ldvNo, lkpDate, (e, result) -> {

            Utility.dismissDialog(mProgressDialog);

            if (e != null) {
                Log.e("eric.onFailure", e.getMessage(), e);

                Utility.throwableHandler(ActivityTaskLog.this, e, true);
                return;
            }

            // harusnya finish sampe home baru refresh lagi
            new AlertDialog.Builder(ActivityTaskLog.this)
                    .setTitle("Reopen Close Batch")
                    .setMessage("Success !")
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        Intent intent = new Intent();
                        intent.putExtra("ACTION", Utility.ACTION_RESTART_ACTIVITY);
                        setResult(RESULT_OK, intent);
                        finish();
                    })
                    .show()
            ;
        });
    }

    private boolean anyTransactions(String ldvNo, String createdBy) {
        long count = getRealmInstance().where(TrnLDVDetails.class)
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("workStatus", "V")
                .equalTo("createdBy", createdBy)
                .count();

        return count > 0;

    }

    private void _attemptReopenBatch() {

        final String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        // cek dulu apa layak di cancel
        TrnLDVHeader header = getRealmInstance().where(TrnLDVHeader.class)
                .equalTo("collCode", this.collCode)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (header == null || header.getCloseBatch() == null) {
            Toast.makeText(this, "Cannot Reopen Batch.\nThis LKP is not Closed yet", Toast.LENGTH_SHORT).show();
            return;
        }

        // kalo closed tp ada transaksi ya ga boleh juga
        // caranya cek semua ldvdetail apakah ada yg workstatusnya V

        if (anyTransactions(this.ldvNo, createdBy)) {
            Utility.showDialog(ActivityTaskLog.this, "Reopen Batch Error", "Transaction Found, cannot Reopen Batch.");
            return;
        }

        Utility.confirmDialog(this, "Reopen Batch", "This will cancel the Close Batch of:\n" + etNoLKP.getText().toString() + "\nAre you sure?", new OnApproveListener() {
            @Override
            public void onApprove() {
                View promptsView = LayoutInflater.from(ActivityTaskLog.this).inflate(R.layout.dialog_pwd, null);
                final EditText input = ButterKnife.findById(promptsView, R.id.password);

                new AlertDialog.Builder(ActivityTaskLog.this)
                        .setTitle("Type Your Password")
                        .setView(promptsView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value = input.getText().toString();

                                UserData currentUser = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);
                                if (!value.equals(currentUser.getUserPwd())) {
                                    Toast.makeText(ActivityTaskLog.this, "Invalid password !", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                doReopenBatch();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show()
                ;
            }
        });

    }
}
