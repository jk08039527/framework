package com.jerry.myframwork.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jerry.myframwork.bean.MyCurrencyPair;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MY_CURRENCY_PAIR".
*/
public class MyCurrencyPairDao extends AbstractDao<MyCurrencyPair, String> {

    public static final String TABLENAME = "MY_CURRENCY_PAIR";

    /**
     * Properties of entity MyCurrencyPair.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property New1m = new Property(1, double.class, "new1m", false, "NEW1M");
        public final static Property New10s = new Property(2, double.class, "new10s", false, "NEW10S");
        public final static Property New30s = new Property(3, double.class, "new30s", false, "NEW30S");
        public final static Property BonusPercent = new Property(4, double.class, "bonusPercent", false, "BONUS_PERCENT");
        public final static Property BuyPrice = new Property(5, double.class, "buyPrice", false, "BUY_PRICE");
        public final static Property SellPrice = new Property(6, double.class, "sellPrice", false, "SELL_PRICE");
        public final static Property BuyTime10s = new Property(7, long.class, "buyTime10s", false, "BUY_TIME10S");
        public final static Property BuyTime30s = new Property(8, long.class, "buyTime30s", false, "BUY_TIME30S");
        public final static Property BuyTime1m = new Property(9, long.class, "buyTime1m", false, "BUY_TIME1M");
        public final static Property SellTime = new Property(10, long.class, "sellTime", false, "SELL_TIME");
    }


    public MyCurrencyPairDao(DaoConfig config) {
        super(config);
    }
    
    public MyCurrencyPairDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MY_CURRENCY_PAIR\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"NEW1M\" REAL NOT NULL ," + // 1: new1m
                "\"NEW10S\" REAL NOT NULL ," + // 2: new10s
                "\"NEW30S\" REAL NOT NULL ," + // 3: new30s
                "\"BONUS_PERCENT\" REAL NOT NULL ," + // 4: bonusPercent
                "\"BUY_PRICE\" REAL NOT NULL ," + // 5: buyPrice
                "\"SELL_PRICE\" REAL NOT NULL ," + // 6: sellPrice
                "\"BUY_TIME10S\" INTEGER NOT NULL ," + // 7: buyTime10s
                "\"BUY_TIME30S\" INTEGER NOT NULL ," + // 8: buyTime30s
                "\"BUY_TIME1M\" INTEGER NOT NULL ," + // 9: buyTime1m
                "\"SELL_TIME\" INTEGER NOT NULL );"); // 10: sellTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MY_CURRENCY_PAIR\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MyCurrencyPair entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindDouble(2, entity.getNew1m());
        stmt.bindDouble(3, entity.getNew10s());
        stmt.bindDouble(4, entity.getNew30s());
        stmt.bindDouble(5, entity.getBonusPercent());
        stmt.bindDouble(6, entity.getBuyPrice());
        stmt.bindDouble(7, entity.getSellPrice());
        stmt.bindLong(8, entity.getBuyTime10s());
        stmt.bindLong(9, entity.getBuyTime30s());
        stmt.bindLong(10, entity.getBuyTime1m());
        stmt.bindLong(11, entity.getSellTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MyCurrencyPair entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
        stmt.bindDouble(2, entity.getNew1m());
        stmt.bindDouble(3, entity.getNew10s());
        stmt.bindDouble(4, entity.getNew30s());
        stmt.bindDouble(5, entity.getBonusPercent());
        stmt.bindDouble(6, entity.getBuyPrice());
        stmt.bindDouble(7, entity.getSellPrice());
        stmt.bindLong(8, entity.getBuyTime10s());
        stmt.bindLong(9, entity.getBuyTime30s());
        stmt.bindLong(10, entity.getBuyTime1m());
        stmt.bindLong(11, entity.getSellTime());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public MyCurrencyPair readEntity(Cursor cursor, int offset) {
        MyCurrencyPair entity = new MyCurrencyPair( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.getDouble(offset + 1), // new1m
            cursor.getDouble(offset + 2), // new10s
            cursor.getDouble(offset + 3), // new30s
            cursor.getDouble(offset + 4), // bonusPercent
            cursor.getDouble(offset + 5), // buyPrice
            cursor.getDouble(offset + 6), // sellPrice
            cursor.getLong(offset + 7), // buyTime10s
            cursor.getLong(offset + 8), // buyTime30s
            cursor.getLong(offset + 9), // buyTime1m
            cursor.getLong(offset + 10) // sellTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MyCurrencyPair entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setNew1m(cursor.getDouble(offset + 1));
        entity.setNew10s(cursor.getDouble(offset + 2));
        entity.setNew30s(cursor.getDouble(offset + 3));
        entity.setBonusPercent(cursor.getDouble(offset + 4));
        entity.setBuyPrice(cursor.getDouble(offset + 5));
        entity.setSellPrice(cursor.getDouble(offset + 6));
        entity.setBuyTime10s(cursor.getLong(offset + 7));
        entity.setBuyTime30s(cursor.getLong(offset + 8));
        entity.setBuyTime1m(cursor.getLong(offset + 9));
        entity.setSellTime(cursor.getLong(offset + 10));
     }
    
    @Override
    protected final String updateKeyAfterInsert(MyCurrencyPair entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(MyCurrencyPair entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MyCurrencyPair entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}