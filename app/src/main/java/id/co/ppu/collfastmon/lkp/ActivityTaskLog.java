package id.co.ppu.collfastmon.lkp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.master.MstTaskType;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnTaskLog;
import id.co.ppu.collfastmon.rest.request.RequestCollJobByDate;
import id.co.ppu.collfastmon.rest.request.RequestCollJobBySpv;
import id.co.ppu.collfastmon.rest.request.RequestReopenBatch;
import id.co.ppu.collfastmon.rest.response.ResponseGetTaskLog;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.internal.Util;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklog);

        ButterKnife.bind(this);

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
    protected void onStart() {
        super.onStart();

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Getting log from server.\nPlease wait...");
        mProgressDialog.show();

        RequestCollJobByDate req = new RequestCollJobByDate();

        req.setCollCode(this.collCode);
//        req.setLdvNo(this.ldvNo);
        req.setLkpDate(this.lkpDate);

        Call<ResponseGetTaskLog> call = getAPIService().getTaskLog(req);
        call.enqueue(new Callback<ResponseGetTaskLog>() {
            @Override
            public void onResponse(Call<ResponseGetTaskLog> call, Response<ResponseGetTaskLog> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (!response.isSuccessful()) {

                    int statusCode = response.code();

                    // handle EXCEPTION
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(ActivityTaskLog.this, "Server Problem (" + statusCode + ")", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }


                final ResponseGetTaskLog responseGetTaskLog = response.body();

                if (responseGetTaskLog.getError() != null) {
                    Utility.showDialog(ActivityTaskLog.this, "Error (" + responseGetTaskLog.getError().getErrorCode() + ")", responseGetTaskLog.getError().getErrorDesc());
                } else {
                    // save db
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            boolean d = realm.where(TrnTaskLog.class)
                                    .findAll()
                                    .deleteAllFromRealm();

                            realm.copyToRealm(responseGetTaskLog.getData());

                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            loadTable();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(ActivityTaskLog.this, "Database Error", Toast.LENGTH_SHORT).show();

                            error.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseGetTaskLog> call, Throwable t) {
                Log.e("eric.onFailure", t.getMessage(), t);

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Utility.showDialog(ActivityTaskLog.this, "Server Problem", t.getMessage());
            }
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
                .setVisible(Utility.isSameDay(this.lkpDate, new Date() ));

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

            attemptReopenBatch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTableHeader(TextView textView, String text) {
        textView.setText(text);
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(this, android.R.style.TextAppearance_Small);
        } else {
            textView.setTextAppearance(android.R.style.TextAppearance_Small);
        }
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setBackgroundColor(Color.LTGRAY);

    }

    private void loadTable() {

        // clear table
        tableLayout.removeAllViews();

        // create header
        TableRow row_header = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_task_log, null);

        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_no), "No.");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_task_name), "Task Name");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_time), "Time");

        tableLayout.addView(row_header);

        RealmResults<TrnTaskLog> list = realm.where(TrnTaskLog.class)
                .equalTo("pk.userCode", collCode)
                .findAllSorted("createdTimestamp");
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
                    newList.set(adaDiRowNewList, realm.copyFromRealm(list.get(i)));
                }
            } else {
                newList.add(realm.copyFromRealm(list.get(i)));
            }
        }

        // TODO: KHUSUS UTK REOPEN DITAMPILKAN SEBELUM GETSYNC ATAU SESUDAH LOGIN(MASALAHNYA LOGIN BISA BEDA HARI KRN BERHARI2 OFFLINE)
        for (int i = 0; i < newList.size(); i++) {

            TrnTaskLog obj = newList.get(i);

            TableRow row = (TableRow) LayoutInflater.from(ActivityTaskLog.this).inflate(R.layout.row_task_log, null);

            ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(i+1) + ".");

            MstTaskType code = realm.where(MstTaskType.class)
                    .equalTo("taskCode", obj.getPk().getTaskCode())
                    .findFirst();

            ((TextView) row.findViewById(R.id.attrib_task_name)).setText(code.getShortDesc());
            ((TextView) row.findViewById(R.id.attrib_time)).setText(Utility.convertDateToString(obj.getPk().getTaskDate(), "d MMM yyyy H:mm:ss"));

            /*
            ((TextView) row.findViewById(R.id.attrib_due_date)).setText( Utility.convertDateToString(obj.getDueDate(), "dd/MM/yyyy"));

            TextView tvPaidDate = ButterKnife.findById(row, R.id.attrib_paid_date);
            if (obj.getPaidDate() != null) {
                tvPaidDate.setText(Utility.convertDateToString(obj.getPaidDate(), "dd/MM/yyyy"));
            }else
                tvPaidDate.setText(null);

            TextView tvOvdDays = ButterKnife.findById(row, R.id.attrib_ovd_days);

            if (obj.getPaidDate() != null) {
                long ovdDays = Utility.getDateDiff(obj.getDueDate(), obj.getPaidDate(), TimeUnit.DAYS);
                tvOvdDays.setText(ovdDays < 1 ? "0" : String.valueOf(ovdDays));

                if (ovdDays > 0) {
                    tvOvdDays.setTextColor(Color.RED);
                    tvPaidDate.setTextColor(Color.RED);
                }

            } else {

            }
            */

            tableLayout.addView(row);
        }

    }

    private void doReopenBatch() {

        // call API

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        RequestReopenBatch req = new RequestReopenBatch();
        req.setLdvNo(ldvNo);

        UserData currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
        req.setSpvCode(currentUser.getUserId());

        req.setYyyyMMdd(Utility.convertDateToString(lkpDate, "yyyyMMdd"));

        Call<ResponseBody> call = getAPIService().reopenBatch(req);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (!response.isSuccessful()) {
                    // handle exception from server
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    try {
                        Utility.showDialog(ActivityTaskLog.this, "Server Problem (" + statusCode + ")", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                // harusnya finish sampe home baru refresh lagi
                new AlertDialog.Builder(ActivityTaskLog.this)
                        .setTitle("Reopen Close Batch")
                        .setMessage("Success !")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.putExtra("ACTION", Utility.ACTION_RESTART_ACTIVITY);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .show()
                ;

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("eric.onFailure", throwable.getMessage(), throwable);

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Utility.throwableHandler(ActivityTaskLog.this, throwable);
            }
        });

    }

    private boolean anyTransactions(String ldvNo, String createdBy) {
        long count = this.realm.where(TrnLDVDetails.class)
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("workStatus", "V")
                .equalTo("createdBy", createdBy)
                .count();

        return count > 0;

    }

    private void attemptReopenBatch() {

        final String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        // cek dulu apa layak di cancel
        TrnLDVHeader header = realm.where(TrnLDVHeader.class)
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Reopen Batch");
        alertDialogBuilder.setMessage("This will cancel the Close Batch of:\n" + etNoLKP.getText().toString() + "\nAre you sure?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                View promptsView = LayoutInflater.from(ActivityTaskLog.this).inflate(R.layout.dialog_pwd, null);
                final EditText input = ButterKnife.findById(promptsView, R.id.password);

                new AlertDialog.Builder(ActivityTaskLog.this)
                        .setTitle("Type Your Password")
                        .setView(promptsView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String value = input.getText().toString();

                                UserData currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
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

                            }
                        })
                        .show()
                ;

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.show();


    }
}
