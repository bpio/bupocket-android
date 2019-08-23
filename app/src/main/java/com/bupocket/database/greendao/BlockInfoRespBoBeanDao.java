package com.bupocket.database.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bupocket.model.BlockInfoRespBoBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BLOCK_INFO_RESP_BO_BEAN".
*/
public class BlockInfoRespBoBeanDao extends AbstractDao<BlockInfoRespBoBean, Void> {

    public static final String TABLENAME = "BLOCK_INFO_RESP_BO_BEAN";

    /**
     * Properties of entity BlockInfoRespBoBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Address = new Property(0, String.class, "address", false, "ADDRESS");
        public final static Property OptNo = new Property(1, String.class, "optNo", false, "OPT_NO");
        public final static Property CloseTimeDate = new Property(2, String.class, "closeTimeDate", false, "CLOSE_TIME_DATE");
        public final static Property Hash = new Property(3, String.class, "hash", false, "HASH");
        public final static Property PreviousHash = new Property(4, String.class, "previousHash", false, "PREVIOUS_HASH");
        public final static Property Seq = new Property(5, int.class, "seq", false, "SEQ");
        public final static Property TxCount = new Property(6, int.class, "txCount", false, "TX_COUNT");
        public final static Property ValidatorsHash = new Property(7, String.class, "validatorsHash", false, "VALIDATORS_HASH");
    }


    public BlockInfoRespBoBeanDao(DaoConfig config) {
        super(config);
    }
    
    public BlockInfoRespBoBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BLOCK_INFO_RESP_BO_BEAN\" (" + //
                "\"ADDRESS\" TEXT," + // 0: address
                "\"OPT_NO\" TEXT," + // 1: optNo
                "\"CLOSE_TIME_DATE\" TEXT," + // 2: closeTimeDate
                "\"HASH\" TEXT," + // 3: hash
                "\"PREVIOUS_HASH\" TEXT," + // 4: previousHash
                "\"SEQ\" INTEGER NOT NULL ," + // 5: seq
                "\"TX_COUNT\" INTEGER NOT NULL ," + // 6: txCount
                "\"VALIDATORS_HASH\" TEXT);"); // 7: validatorsHash
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BLOCK_INFO_RESP_BO_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BlockInfoRespBoBean entity) {
        stmt.clearBindings();
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(1, address);
        }
 
        String optNo = entity.getOptNo();
        if (optNo != null) {
            stmt.bindString(2, optNo);
        }
 
        String closeTimeDate = entity.getCloseTimeDate();
        if (closeTimeDate != null) {
            stmt.bindString(3, closeTimeDate);
        }
 
        String hash = entity.getHash();
        if (hash != null) {
            stmt.bindString(4, hash);
        }
 
        String previousHash = entity.getPreviousHash();
        if (previousHash != null) {
            stmt.bindString(5, previousHash);
        }
        stmt.bindLong(6, entity.getSeq());
        stmt.bindLong(7, entity.getTxCount());
 
        String validatorsHash = entity.getValidatorsHash();
        if (validatorsHash != null) {
            stmt.bindString(8, validatorsHash);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BlockInfoRespBoBean entity) {
        stmt.clearBindings();
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(1, address);
        }
 
        String optNo = entity.getOptNo();
        if (optNo != null) {
            stmt.bindString(2, optNo);
        }
 
        String closeTimeDate = entity.getCloseTimeDate();
        if (closeTimeDate != null) {
            stmt.bindString(3, closeTimeDate);
        }
 
        String hash = entity.getHash();
        if (hash != null) {
            stmt.bindString(4, hash);
        }
 
        String previousHash = entity.getPreviousHash();
        if (previousHash != null) {
            stmt.bindString(5, previousHash);
        }
        stmt.bindLong(6, entity.getSeq());
        stmt.bindLong(7, entity.getTxCount());
 
        String validatorsHash = entity.getValidatorsHash();
        if (validatorsHash != null) {
            stmt.bindString(8, validatorsHash);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public BlockInfoRespBoBean readEntity(Cursor cursor, int offset) {
        BlockInfoRespBoBean entity = new BlockInfoRespBoBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // address
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // optNo
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // closeTimeDate
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // hash
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // previousHash
            cursor.getInt(offset + 5), // seq
            cursor.getInt(offset + 6), // txCount
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // validatorsHash
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BlockInfoRespBoBean entity, int offset) {
        entity.setAddress(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setOptNo(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCloseTimeDate(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setHash(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPreviousHash(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSeq(cursor.getInt(offset + 5));
        entity.setTxCount(cursor.getInt(offset + 6));
        entity.setValidatorsHash(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(BlockInfoRespBoBean entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(BlockInfoRespBoBean entity) {
        return null;
    }

    @Override
    public boolean hasKey(BlockInfoRespBoBean entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}