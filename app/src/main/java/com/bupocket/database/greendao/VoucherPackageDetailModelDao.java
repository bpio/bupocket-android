package com.bupocket.database.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bupocket.model.Voucher_Acceptance;
import com.bupocket.model.Voucher_Issuer;
import com.bupocket.voucher.model.VoucherAcceptanceBean2;
import com.bupocket.voucher.model.VoucherIssuerBean2;

import com.bupocket.voucher.model.VoucherPackageDetailModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "VOUCHER_PACKAGE_DETAIL_MODEL".
*/
public class VoucherPackageDetailModelDao extends AbstractDao<VoucherPackageDetailModel, Void> {

    public static final String TABLENAME = "VOUCHER_PACKAGE_DETAIL_MODEL";

    /**
     * Properties of entity VoucherPackageDetailModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Address = new Property(0, String.class, "address", false, "ADDRESS");
        public final static Property ContractAddress = new Property(1, String.class, "contractAddress", false, "CONTRACT_ADDRESS");
        public final static Property EndTime = new Property(2, String.class, "endTime", false, "END_TIME");
        public final static Property FaceValue = new Property(3, String.class, "faceValue", false, "FACE_VALUE");
        public final static Property SpuId = new Property(4, String.class, "spuId", false, "SPU_ID");
        public final static Property StartTime = new Property(5, String.class, "startTime", false, "START_TIME");
        public final static Property TrancheId = new Property(6, String.class, "trancheId", false, "TRANCHE_ID");
        public final static Property VoucherAcceptance = new Property(7, String.class, "voucherAcceptance", false, "VOUCHER_ACCEPTANCE");
        public final static Property VoucherIssuer = new Property(8, String.class, "voucherIssuer", false, "VOUCHER_ISSUER");
        public final static Property VoucherIcon = new Property(9, String.class, "voucherIcon", false, "VOUCHER_ICON");
        public final static Property VoucherId = new Property(10, String.class, "voucherId", false, "VOUCHER_ID");
        public final static Property VoucherName = new Property(11, String.class, "voucherName", false, "VOUCHER_NAME");
        public final static Property VoucherSpec = new Property(12, String.class, "voucherSpec", false, "VOUCHER_SPEC");
        public final static Property Description = new Property(13, String.class, "description", false, "DESCRIPTION");
    }

    private final Voucher_Acceptance voucherAcceptanceConverter = new Voucher_Acceptance();
    private final Voucher_Issuer voucherIssuerConverter = new Voucher_Issuer();

    public VoucherPackageDetailModelDao(DaoConfig config) {
        super(config);
    }
    
    public VoucherPackageDetailModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VOUCHER_PACKAGE_DETAIL_MODEL\" (" + //
                "\"ADDRESS\" TEXT," + // 0: address
                "\"CONTRACT_ADDRESS\" TEXT," + // 1: contractAddress
                "\"END_TIME\" TEXT," + // 2: endTime
                "\"FACE_VALUE\" TEXT," + // 3: faceValue
                "\"SPU_ID\" TEXT," + // 4: spuId
                "\"START_TIME\" TEXT," + // 5: startTime
                "\"TRANCHE_ID\" TEXT," + // 6: trancheId
                "\"VOUCHER_ACCEPTANCE\" TEXT," + // 7: voucherAcceptance
                "\"VOUCHER_ISSUER\" TEXT," + // 8: voucherIssuer
                "\"VOUCHER_ICON\" TEXT," + // 9: voucherIcon
                "\"VOUCHER_ID\" TEXT," + // 10: voucherId
                "\"VOUCHER_NAME\" TEXT," + // 11: voucherName
                "\"VOUCHER_SPEC\" TEXT," + // 12: voucherSpec
                "\"DESCRIPTION\" TEXT);"); // 13: description
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VOUCHER_PACKAGE_DETAIL_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, VoucherPackageDetailModel entity) {
        stmt.clearBindings();
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(1, address);
        }
 
        String contractAddress = entity.getContractAddress();
        if (contractAddress != null) {
            stmt.bindString(2, contractAddress);
        }
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(3, endTime);
        }
 
        String faceValue = entity.getFaceValue();
        if (faceValue != null) {
            stmt.bindString(4, faceValue);
        }
 
        String spuId = entity.getSpuId();
        if (spuId != null) {
            stmt.bindString(5, spuId);
        }
 
        String startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindString(6, startTime);
        }
 
        String trancheId = entity.getTrancheId();
        if (trancheId != null) {
            stmt.bindString(7, trancheId);
        }
 
        VoucherAcceptanceBean2 voucherAcceptance = entity.getVoucherAcceptance();
        if (voucherAcceptance != null) {
            stmt.bindString(8, voucherAcceptanceConverter.convertToDatabaseValue(voucherAcceptance));
        }
 
        VoucherIssuerBean2 voucherIssuer = entity.getVoucherIssuer();
        if (voucherIssuer != null) {
            stmt.bindString(9, voucherIssuerConverter.convertToDatabaseValue(voucherIssuer));
        }
 
        String voucherIcon = entity.getVoucherIcon();
        if (voucherIcon != null) {
            stmt.bindString(10, voucherIcon);
        }
 
        String voucherId = entity.getVoucherId();
        if (voucherId != null) {
            stmt.bindString(11, voucherId);
        }
 
        String voucherName = entity.getVoucherName();
        if (voucherName != null) {
            stmt.bindString(12, voucherName);
        }
 
        String voucherSpec = entity.getVoucherSpec();
        if (voucherSpec != null) {
            stmt.bindString(13, voucherSpec);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(14, description);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, VoucherPackageDetailModel entity) {
        stmt.clearBindings();
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(1, address);
        }
 
        String contractAddress = entity.getContractAddress();
        if (contractAddress != null) {
            stmt.bindString(2, contractAddress);
        }
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(3, endTime);
        }
 
        String faceValue = entity.getFaceValue();
        if (faceValue != null) {
            stmt.bindString(4, faceValue);
        }
 
        String spuId = entity.getSpuId();
        if (spuId != null) {
            stmt.bindString(5, spuId);
        }
 
        String startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindString(6, startTime);
        }
 
        String trancheId = entity.getTrancheId();
        if (trancheId != null) {
            stmt.bindString(7, trancheId);
        }
 
        VoucherAcceptanceBean2 voucherAcceptance = entity.getVoucherAcceptance();
        if (voucherAcceptance != null) {
            stmt.bindString(8, voucherAcceptanceConverter.convertToDatabaseValue(voucherAcceptance));
        }
 
        VoucherIssuerBean2 voucherIssuer = entity.getVoucherIssuer();
        if (voucherIssuer != null) {
            stmt.bindString(9, voucherIssuerConverter.convertToDatabaseValue(voucherIssuer));
        }
 
        String voucherIcon = entity.getVoucherIcon();
        if (voucherIcon != null) {
            stmt.bindString(10, voucherIcon);
        }
 
        String voucherId = entity.getVoucherId();
        if (voucherId != null) {
            stmt.bindString(11, voucherId);
        }
 
        String voucherName = entity.getVoucherName();
        if (voucherName != null) {
            stmt.bindString(12, voucherName);
        }
 
        String voucherSpec = entity.getVoucherSpec();
        if (voucherSpec != null) {
            stmt.bindString(13, voucherSpec);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(14, description);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public VoucherPackageDetailModel readEntity(Cursor cursor, int offset) {
        VoucherPackageDetailModel entity = new VoucherPackageDetailModel( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // address
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // contractAddress
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // endTime
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // faceValue
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // spuId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // startTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // trancheId
            cursor.isNull(offset + 7) ? null : voucherAcceptanceConverter.convertToEntityProperty(cursor.getString(offset + 7)), // voucherAcceptance
            cursor.isNull(offset + 8) ? null : voucherIssuerConverter.convertToEntityProperty(cursor.getString(offset + 8)), // voucherIssuer
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // voucherIcon
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // voucherId
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // voucherName
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // voucherSpec
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // description
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, VoucherPackageDetailModel entity, int offset) {
        entity.setAddress(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setContractAddress(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setEndTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFaceValue(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSpuId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStartTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setTrancheId(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setVoucherAcceptance(cursor.isNull(offset + 7) ? null : voucherAcceptanceConverter.convertToEntityProperty(cursor.getString(offset + 7)));
        entity.setVoucherIssuer(cursor.isNull(offset + 8) ? null : voucherIssuerConverter.convertToEntityProperty(cursor.getString(offset + 8)));
        entity.setVoucherIcon(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setVoucherId(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setVoucherName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setVoucherSpec(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setDescription(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(VoucherPackageDetailModel entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(VoucherPackageDetailModel entity) {
        return null;
    }

    @Override
    public boolean hasKey(VoucherPackageDetailModel entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}