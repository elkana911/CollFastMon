package id.co.ppu.collfastmon.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.co.ppu.collfastmon.pojo.CollJob;
import id.co.ppu.collfastmon.pojo.LKPDataMonitoring;
import id.co.ppu.collfastmon.pojo.UserData;
import id.co.ppu.collfastmon.pojo.master.MasterMonData;
import id.co.ppu.collfastmon.pojo.master.MstDelqReasons;
import id.co.ppu.collfastmon.pojo.master.MstLDVClassifications;
import id.co.ppu.collfastmon.pojo.master.MstLDVParameters;
import id.co.ppu.collfastmon.pojo.master.MstLDVStatus;
import id.co.ppu.collfastmon.pojo.master.MstParam;
import id.co.ppu.collfastmon.pojo.master.MstPotensi;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVComments;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetails;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVDetailsPK;
import id.co.ppu.collfastmon.pojo.trn.TrnLDVHeader;
import id.co.ppu.collfastmon.pojo.trn.TrnRVColl;
import id.co.ppu.collfastmon.pojo.trn.TrnRVCollPK;
import id.co.ppu.collfastmon.pojo.trn.TrnRepo;

/**
 * Created by Eric on 24-Feb-17.
 */

public class DemoUtil {
    public static boolean isDemo(UserData userData) {
        return userData != null
                && userData.getCollectorType() != null
                && userData.getCollectorType().equals(Utility.ROLE_DEMO);
    }

    public static boolean isDemo(Context ctx) {
        UserData currentUser = (UserData) Storage.getPrefAsJson(Storage.KEY_USER, UserData.class, null);

        return isDemo(currentUser);
    }

    public static UserData buildDemoUser() {
        UserData demo = new UserData();

        demo.setUserId("demo");
        demo.setBranchId("71000");
        demo.setBranchName("Gading Serpong");
        demo.setEmailAddr("elkana911@radanafinance.co.id");
        demo.setJabatan("Demo Supervisor");
        demo.setNik("F.01.99.1234");
        demo.setAlamat("Faraday Utara 39");
        demo.setPhoneNo("555-1234");
        demo.setCollectorType(Utility.ROLE_DEMO);
        demo.setUserPwd("demo");
        demo.setBirthPlace("Banten");
        demo.setBirthDate(Utility.convertStringToDate("09/08/2016", "dd/MM/yyyy"));
        demo.setMobilePhone("555-123");
        demo.setFullName("John Doe");
        demo.setBussUnit("NMC");

//        UserConfig config = new UserConfig();
//        config.setUid(java.util.UUID.randomUUID().toString());
//        config.setDeviceId(Utility.getDeviceId(MainActivity.this));
//        config.setImeiDevice();

//        demo.setConfig(config);

        return demo;
    }

    private static TrnLDVDetails generateTrnLDVDetail(String ldvNo, long seqNo, String period, String contractNo, String custNo, String custName, Long ovdInstNo, Date ovdDueDate, Date dueDate, Long instNo, Long prncAmbc, Long intrAmbc, Long penaltyAmbc, Long prncAC, Long intrAC, Long penaltyAC, String ldvFlag, String workStatus, Long prncOTS, Date startedTimestamp, String createdBy, Date createdTimestamp, String occupation, String subOccupation, Long monthInst, Long daysIntrAmbc, Long collectionFee, Date lastPaidDate, Long dpd) {
        TrnLDVDetails obj = new TrnLDVDetails();

        TrnLDVDetailsPK pk = new TrnLDVDetailsPK();
        pk.setLdvNo(ldvNo);
        pk.setSeqNo(seqNo);
        obj.setPk(pk);

        obj.setPeriod(period);
        obj.setContractNo(contractNo);
        obj.setCustNo(custNo);
        obj.setCustName(custName);
        obj.setOvdInstNo(ovdInstNo);
        obj.setOvdDueDate(ovdDueDate);
        obj.setDueDate(dueDate);
        obj.setInstNo(instNo);
        obj.setPrincipalAmount(null);
        obj.setInterestAmount(null);
        obj.setPrincipalAMBC(prncAmbc);
        obj.setInterestAMBC(intrAmbc);
        obj.setPenaltyAMBC(penaltyAmbc);
        obj.setPrincipalAmountCollected(prncAC);
        obj.setInterestAmountCollected(intrAC);
        obj.setPenaltyAmountCollected(penaltyAC);
        obj.setLdvFlag(ldvFlag);
        obj.setWorkStatus(workStatus);
        obj.setPrincipalOutstanding(prncOTS);
        obj.setStartedTimestamp(startedTimestamp);
        obj.setCreatedBy(createdBy);
        obj.setCreatedTimestamp(createdTimestamp);
        obj.setOccupation(occupation);
        obj.setSubOccupation(subOccupation);
        obj.setPalNo(null);
        obj.setFlagToEmrafin(null);
        obj.setDateToEmrafin(null);
        obj.setFlagDone(null);
        obj.setDateDone(null);
        obj.setMonthInst(monthInst);
        obj.setDaysIntrAmbc(daysIntrAmbc);
        obj.setCollectionFee(collectionFee);
        obj.setLastPaidDate(lastPaidDate);
        obj.setDpd(dpd);

        return obj;
    }


    private static MstLDVStatus generateMstLDVStatus(String lkpStatus, String statusDesc, Long statusLevel) {
        MstLDVStatus mst = new MstLDVStatus();
        mst.setLkpStatus(lkpStatus);
        mst.setStatusDesc(statusDesc);
        mst.setStatusLevel(statusLevel);
        mst.setStartedTimestamp(new Date());
        mst.setCreatedBy("1120017");
        mst.setCreatedTimestamp(new Date());

        return mst;
    }

    private static MstParam generateMstParam(Long moduleId, String key, String value, String notes) {
        MstParam mst = new MstParam();

        mst.setModuleId(moduleId);
        mst.setKey(key);
        mst.setValue(value);
        mst.setNotes(notes);
        mst.setCreatedTimestamp(new Date());

        return mst;
    }

    private static MstLDVParameters generateMstLDVParams(String lkpFlag, String description, String needComment, String needDate, String workFlag, String needCollect, String isActive, Long maxPromiseDays) {
        MstLDVParameters mst = new MstLDVParameters();
        mst.setLkpFlag(lkpFlag);
        mst.setDescription(description);
        mst.setNeedComment(needComment);
        mst.setNeedDate(needDate);
        mst.setWorkFlag(workFlag);
        mst.setNeedCollect(needCollect);
        mst.setIsActive(isActive);
        mst.setMaxPromiseDays(maxPromiseDays);
        mst.setCreatedBy("ERIC");
        mst.setCreatedTimestamp(new Date());

        return mst;
    }

    private static MstLDVClassifications generateMstLDVClassification(String classCode, String description, String needSpecialCollect, String visible) {
        MstLDVClassifications mst = new MstLDVClassifications();

        mst.setClassCode(classCode);
        mst.setDescription(description);
        mst.setNeedSpecialCollect(needSpecialCollect);
        mst.setVisible(visible);
        mst.setStartedTimetamp(new Date());
        mst.setCreatedBy("HD1");
        mst.setCreatedTimestamp(new Date());
        return mst;
    }

    private static MstDelqReasons generateMstDelqReason(String delqCode, String description, String visible) {
        MstDelqReasons mst = new MstDelqReasons();

        mst.setDelqCode(delqCode);
        mst.setDescription(description);
        mst.setVisible(visible);
        mst.setCreatedTimestamp(new Date());
        mst.setCreatedBy("ERIC");

        return mst;
    }

    private static MstPotensi generateMstPotensi(String delqId, String classCode, Long seqNo, Long potensi, String potensiDesc, String isActive) {
        MstPotensi mst = new MstPotensi();
        mst.setDelqId(delqId);
        mst.setClassCode(classCode);
        mst.setSeqNo(seqNo);
        mst.setPotensi(potensi);
        mst.setPotensiDesc(potensiDesc);
        mst.setIsActive(isActive);
        return mst;
    }

    public static MasterMonData buildMasterData() {
        MasterMonData md = new MasterMonData();

        List<MstParam> params = new ArrayList<MstParam>();
        params.add(generateMstParam(5L, "DANA SOSIAL SYARIAH", "5000", "DANA SOSIAL CONTRACT SYARIAH"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "1", "Flow Up"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "2", "Stay"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "3", "Pick Up"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "4", "Pelunasan Normal"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "5", "Pelunasan Pretermination"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "6", "Flow Down"));
        params.add(generateMstParam(6L, "MIN_PENALTY_RV", "30000", "Minimal penerimaan denda angsuran outdoor / RV Collector"));
        md.setParams(params);

        List<MstLDVStatus> ldpStatuses = new ArrayList<MstLDVStatus>();
        ldpStatuses.add(generateMstLDVStatus("U", "UNASSIGN", 0L));
        ldpStatuses.add(generateMstLDVStatus("A", "ASSIGN", 1L));
        ldpStatuses.add(generateMstLDVStatus("V", "VISITED", 2L));
        ldpStatuses.add(generateMstLDVStatus("W", "WORK", 3L));
        ldpStatuses.add(generateMstLDVStatus("S", "SELF CURE", 5L));
        ldpStatuses.add(generateMstLDVStatus("P", "APPROVED", 4L));
        md.setLdpStatus(ldpStatuses);


        List<MstLDVParameters> ldpParameters = new ArrayList<MstLDVParameters>();
        ldpParameters.add(generateMstLDVParams("PTP", "Promised To Pay", "Y", "Y", "W", "N", "Y", 7L));
        ldpParameters.add(generateMstLDVParams("PTC", "Promised To Be Collected", "Y", "Y", "W", "Y", "N", 7L));
        ldpParameters.add(generateMstLDVParams("BCO", "Not Meet Customer", "Y", "N", "V", "N", "Y", 7L));
        ldpParameters.add(generateMstLDVParams("UNV", "Unvisited", "N", "N", "A", "N", "Y", 7L));
        ldpParameters.add(generateMstLDVParams("NEW", "New Assignment", "N", "N", "A", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("PTD", "Pay Today Directly", "N", "N", "V", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("COL", "Collected", "N", "N", "W", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("PCU", "Tarik Barang", "Y", "Y", "W", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("PRE", "Pretermination", "Y", "Y", "W", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("BPH", "Bad Phone", "Y", "N", "V", "Y", "N", 7L));
        ldpParameters.add(generateMstLDVParams("UTC", "Unable To Contact", "N", "N", "A", "Y", "N", 7L));
        ldpParameters.add(generateMstLDVParams("MSG", "Message", "Y", "N", "A", "Y", "N", 7L));
        ldpParameters.add(generateMstLDVParams("PTD-D", "Pay To Day-Deskcall", "N", "N", "W", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("FRZ", "Freeze", "N", "Y", "V", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("COL2", "Collected not input amount", "Y", "N", "W", "N", "Y", 7L));
        ldpParameters.add(generateMstLDVParams("SKIP", "Skip", "Y", "Y", "W", "N", "Y", 7L));
        md.setLdpParameters(ldpParameters);

        List<MstLDVClassifications> ldpClassifications = new ArrayList<MstLDVClassifications>();
        ldpClassifications.add(generateMstLDVClassification("1", "Nasabah ada, unit ada", "N", "Y"));
        ldpClassifications.add(generateMstLDVClassification("2", "Nasabah ada, unit tidak ada", "N", "Y"));
        ldpClassifications.add(generateMstLDVClassification("3", "Nasabah tidak ada, unit ada", "N", "Y"));
        ldpClassifications.add(generateMstLDVClassification("4", "Nasabah tidak ada, unit tidak ada", "Y", "Y"));
        md.setLdpClassifications(ldpClassifications);

        List<MstDelqReasons> delqReasons = new ArrayList<MstDelqReasons>();
        delqReasons.add(generateMstDelqReason("101", "Konsumen belum terima gaji/Pendapatan", "Y"));
        delqReasons.add(generateMstDelqReason("104", "Konsumen menunggu kiriman uang", "Y"));
        delqReasons.add(generateMstDelqReason("107", "Penurunan usaha konsumen", "Y"));
        delqReasons.add(generateMstDelqReason("110", "Konsumen ke luar kota ", "Y"));
        delqReasons.add(generateMstDelqReason("113", "Customer/keluarga terkena musibah (sakit, kecelakaan dll)", "Y"));
        delqReasons.add(generateMstDelqReason("116", "Uang terpakai kebutuhan penting lainnya", "Y"));
        delqReasons.add(generateMstDelqReason("119", "Customer pindah alamat dan di ketahui", "Y"));
        delqReasons.add(generateMstDelqReason("122", "Customer pindah alamat dan tidak di ketahui", "Y"));
        delqReasons.add(generateMstDelqReason("125", "Susah bayar karena Tempat pembayaran jauh", "Y"));
        delqReasons.add(generateMstDelqReason("128", "Selalu minta di tagih", "Y"));
        delqReasons.add(generateMstDelqReason("131", "Konsumen di PHK", "Y"));
        delqReasons.add(generateMstDelqReason("134", "Usaha konsumen bangkrut", "Y"));
        delqReasons.add(generateMstDelqReason("201", "Tanggal Jatuh Tempo salah", "Y"));
        delqReasons.add(generateMstDelqReason("204", "Angsuran salah (TOP atau Amount)", "Y"));
        delqReasons.add(generateMstDelqReason("207", "STNK / BPKB belum jadi", "Y"));
        delqReasons.add(generateMstDelqReason("210", "Salah nama STNK/BPKB", "Y"));
        delqReasons.add(generateMstDelqReason("213", "PPK belum di terima customer", "Y"));
        delqReasons.add(generateMstDelqReason("216", "Telp tidak update", "Y"));
        delqReasons.add(generateMstDelqReason("219", "No Telp. Salah", "Y"));
        delqReasons.add(generateMstDelqReason("222", "Konsumen tidak layak kredit", "Y"));
        delqReasons.add(generateMstDelqReason("225", "Alamat tidak jelas", "Y"));
        delqReasons.add(generateMstDelqReason("228", "Alamat tidak pernah ada / Alamat Fiktif", "Y"));
        delqReasons.add(generateMstDelqReason("231", "Kontrak fiktif", "Y"));
        delqReasons.add(generateMstDelqReason("301", "pembayaran Payment point belum update (dispute)", "Y"));
        delqReasons.add(generateMstDelqReason("304", "Sudah bayar masuk titipan angsuran", "Y"));
        md.setDelqReasons(delqReasons);

        List<MstPotensi> potensis = new ArrayList<MstPotensi>();
        potensis.add(generateMstPotensi("G", "1", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("G", "1", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("G", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("G", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("G", "2", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));

        potensis.add(generateMstPotensi("H", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("H", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("H", "2", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));

        potensis.add(generateMstPotensi("I", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("I", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("I", "2", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("J", "1", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("J", "1", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("J", "1", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("J", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("J", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("J", "2", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("J", "3", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("J", "3", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("J", "3", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));

        potensis.add(generateMstPotensi("K", "1", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("K", "1", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("K", "1", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("K", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("K", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        md.setPotensi(potensis);
        return md;

    }

    private static CollJob generateCollJob(String ldvNo, Date lkpDate, String collCode, String collName, String collType, String lastLatitude, String lastLongitude, String lastTask, Date lastTaskTime) {
        CollJob obj = new CollJob();

        obj.setCollCode(collCode);
        obj.setCountLKP(5L);
        obj.setCountVisited(3L);
        obj.setCollName(collName);
        obj.setCollType(collType);
        obj.setLdvNo(ldvNo);
        obj.setLkpDate(lkpDate);
        obj.setLastLatitude(lastLatitude);
        obj.setLastLongitude(lastLongitude);
        obj.setLastTask(lastTask);
        obj.setLastTaskTime(lastTaskTime);

        return obj;
    }

    /**
     *
     * @param lkpDate override lkpDate
     * @param json
     * @return
     */
    private static CollJob generateCollJob(Date lkpDate, String json) {
        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();

        CollJob collJob = gson.fromJson(json, CollJob.class);
        collJob.setLkpDate(lkpDate);

        return collJob;
    }

    public static List<CollJob> buildCollectors() {

        List<CollJob> list = new ArrayList<>();

        Date lkpDate = new Date();

        list.add(generateCollJob(lkpDate, "{\"collCode\":\"21160292\",\"collNa`me\":\"RIKI RISWANTO\",\"collType\":\"CRC\",\"ldvNo\":\"1211020170223021160292\",\"countVisited\":0,\"countLKP\":3,\"lkpDate\":\"23-02-2017 00:00:00\"}"));
        list.add(generateCollJob(lkpDate, "{\"collCode\":\"RDN0588\",\"collType\":\"CRC\",\"ldvNo\":\"12110201702230RDN0588\",\"countVisited\":0,\"countLKP\":5,\"lkpDate\":\"23-02-2017 00:00:00\"}"));
        list.add(generateCollJob(lkpDate, "{\"collCode\":\"21160292\",\"collName\":\"RIKI RISWANTO\",\"collType\":\"CRC\",\"ldvNo\":\"1211020170223021160292\",\"countVisited\":0,\"countLKP\":3,\"lkpDate\":\"23-02-2017 00:00:00\"}"));
        list.add(generateCollJob(lkpDate, "{\"collCode\":\"RDN0588\",\"collType\":\"CRC\",\"ldvNo\":\"12110201702230RDN0588\",\"countVisited\":0,\"countLKP\":5,\"lkpDate\":\"23-02-2017 00:00:00\"}"));
        list.add(generateCollJob(lkpDate, "{\"collCode\":\"21150663\",\"collName\":\"MAHDUM JAUHANA\",\"collType\":\"CRC\",\"ldvNo\":\"1211020170223021150663\",\"countVisited\":0,\"countLKP\":4,\"lkpDate\":\"23-02-2017 00:00:00\"}"));
        list.add(generateCollJob(lkpDate, "{\"collCode\":\"21140598\",\"collName\":\"NIECKO FRANA HERLANGGA\",\"collType\":\"CRC\",\"ldvNo\":\"1211020170223021140598\",\"countVisited\":2,\"countLKP\":4,\"lkpDate\":\"23-02-2017 00:00:00\"}"));
        list.add(generateCollJob(lkpDate, "{\"collCode\":\"RDN0588\",\"collType\":\"CRC\",\"ldvNo\":\"12110201702230RDN0588\",\"countVisited\":0,\"countLKP\":5,\"lkpDate\":\"23-02-2017 00:00:00\"}"));
        list.add(generateCollJob(lkpDate, "{\"collCode\":\"RDN0588\",\"collType\":\"CRC\",\"ldvNo\":\"12110201702230RDN0588\",\"countVisited\":0,\"countLKP\":5,\"lkpDate\":\"23-02-2017 00:00:00\"}"));

        return list;
    }

    public static LKPDataMonitoring buildLKPData(String collCode, String officeCode, String ldvNo, Date lkpDate, String createdBy) {

//        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();

//        CollJob collJob = gson.fromJson(json, CollJob.class);

        LKPDataMonitoring data = new LKPDataMonitoring();
//        String json = "{\"ldvNo\":\"6200020170223026160192\",\"ldvDate\":\"23-02-2017 08:31:16\",\"officeCode\":\"62000\",\"collCode\":\"26160192\",\"unitTotal\":18,\"prncAMBC\":6297167,\"prncAC\":0,\"intrAMBC\":5325833,\"intrAC\":0,\"ambcTotal\":11623000,\"acTotal\":0,\"workFlag\":\"A\",\"startedTimestamp\":\"23-02-2017 09:40:00\",\"flagToEmrafin\":\"N\",\"createdBy\":\"JOB20170223\",\"createdTimestamp\":\"23-02-2017 09:40:00\"}";

        TrnLDVHeader header = new TrnLDVHeader();
        header.setCreatedTimestamp(new Date());
        header.setLdvNo(ldvNo);
        header.setLdvDate(lkpDate);
        header.setAcTotal(0L);
        header.setAmbcTotal(11623000L);
//        header.setApprovedDate();
//        header.setCloseBatch();
        header.setCollCode(collCode);
        header.setCreatedBy(createdBy);
//        header.setDateDone();
//        header.setDateToEmrafin();
//        header.setFlagDone();
//        header.setFlagToEmrafin();
        header.setIntrAC(0L);
        header.setIntrAMBC(5325833L);
//        header.setLastupdateBy();
//        header.setLastupdateTimestamp();
        header.setOfficeCode(officeCode);
        header.setPrncAC(0L);
        header.setPrncAMBC(6297167L);
        header.setStartedTimestamp(new Date());
        header.setUnitTotal(4L);
        header.setWorkFlag("A");

        data.setHeader(header);

        String period = Utility.convertDateToString(lkpDate, "yyyyMM");
        Date ovdDueDate = new Date();
        Date dueDate = new Date();
        Date createdTimestamp = new Date();
        Date startedTimestamp = new Date();

        List<TrnLDVDetails> details = new ArrayList<>();
        //1
        details.add(generateTrnLDVDetail(header.getLdvNo(), 86L, period, "71000000008115", "71000150000027", "MALAN"
                , 17L, ovdDueDate, dueDate, 16L, 1193882L, 1446118L, 350300L, 0L, 0L, 0L, "NEW", "A", 281350L, startedTimestamp, createdBy, createdTimestamp
                , "Pekerja Lepas/Freelance (>=1)", "FREELANCE"
                , 660000L, 336600L, null, null, 102L));
        //2
        details.add(generateTrnLDVDetail(header.getLdvNo(), 87L, period, "71000000069115", "71000150000555", "RUDI EFENDI"
                , 14L, ovdDueDate, dueDate, 13L, 951098L, 1488902L, 678500L, 0L, 0L, 0L, "COL", "V", 223658L, startedTimestamp, createdBy, createdTimestamp
                , "Kry Swasta Tetap", "MEKANIK"
                , 610000L, 353800L, 10000L, null, 116L));
        //3
        details.add(generateTrnLDVDetail(header.getLdvNo(), 88L, period, "71000000034615", "71000150000254", "RASIDIN"
                , 16L, ovdDueDate, dueDate, 15L, 934575L, 1465425L, 417000L, 0L, 0L, 0L, "PTP", "V", 219758L, startedTimestamp, createdBy, createdTimestamp
                , "Pekerja Lepas/Freelance (>=1)", "PEMBANTU RUMAH TANGGA"
                , 600000L, 309000L, 10000L, null, 103L));
        //4
        details.add(generateTrnLDVDetail(header.getLdvNo(), 89L, period, "71000900029814", "71000140002255", "SUNIK"
                , 22L, ovdDueDate, dueDate, 21L, 1966547L, 929453L, 1161760L, 0L, 0L, 0L, "NEW", "A", 461931L, startedTimestamp, createdBy, createdTimestamp
                , "Kry Swasta Kontrak (>=1)", "Perusahaan Swasta"
                , 724000L, 427160L, null, null, 118L));
        data.setDetails(details);

        List<TrnRVColl> rvColls = new ArrayList<TrnRVColl>();
        rvColls.add(DataUtil.createTrnRVColl(null, lkpDate, collCode, officeCode, header.getLdvNo(), "71000000069115", "1234567890123456", 1L, "K", 0L, 11L, 30000L, 10000L, 1500000L, "-6.1768285", "106.7290386", "komentar1"));

        data.setRvColl(rvColls);

        List<TrnLDVComments> ldvComments = new ArrayList<TrnLDVComments>();
        data.setLdvComments(ldvComments);

        List<TrnRepo> repos = new ArrayList<TrnRepo>();
        data.setRepo(repos);

//        data.setBuckets();

        return data;
    }

}
