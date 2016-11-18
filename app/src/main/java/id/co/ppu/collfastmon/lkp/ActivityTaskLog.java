package id.co.ppu.collfastmon.lkp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collfastmon.R;
import id.co.ppu.collfastmon.component.BasicActivity;
import id.co.ppu.collfastmon.pojo.master.MstTaskType;
import id.co.ppu.collfastmon.pojo.trn.TrnTaskLog;
import id.co.ppu.collfastmon.rest.ApiInterface;
import id.co.ppu.collfastmon.rest.ServiceGenerator;
import id.co.ppu.collfastmon.rest.request.RequestCollJobByDate;
import id.co.ppu.collfastmon.rest.response.ResponseGetTaskLog;
import id.co.ppu.collfastmon.util.Storage;
import id.co.ppu.collfastmon.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;
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

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        RequestCollJobByDate req = new RequestCollJobByDate();
        req.setSpvCode(this.collCode);
        req.setYyyyMMdd(Utility.convertDateToString(this.lkpDate, "yyyyMMdd"));

        Call<ResponseGetTaskLog> call = fastService.getTaskLog(req);
        call.enqueue(new Callback<ResponseGetTaskLog>() {
            @Override
            public void onResponse(Call<ResponseGetTaskLog> call, Response<ResponseGetTaskLog> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (!response.isSuccessful()) {

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
                .findAll();

        String lastTaskCode = "";
        int counter = 0;
        for (int i = 0; i < list.size(); i++) {

            TrnTaskLog obj = realm.copyFromRealm(list.get(i));

            TableRow row = (TableRow) LayoutInflater.from(ActivityTaskLog.this).inflate(R.layout.row_task_log, null);

            if (!obj.getPk().getTaskCode().equalsIgnoreCase(lastTaskCode)) {
                lastTaskCode = obj.getPk().getTaskCode();
            }else
                continue;

            counter += 1;

            ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(counter) + ".");

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
}
