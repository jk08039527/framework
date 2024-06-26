package com.jerry.myframwork.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.jerry.myframwork.bean.MyCurrencyPair;

import com.jerry.myframwork.greendao.MyCurrencyPairDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig myCurrencyPairDaoConfig;

    private final MyCurrencyPairDao myCurrencyPairDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        myCurrencyPairDaoConfig = daoConfigMap.get(MyCurrencyPairDao.class).clone();
        myCurrencyPairDaoConfig.initIdentityScope(type);

        myCurrencyPairDao = new MyCurrencyPairDao(myCurrencyPairDaoConfig, this);

        registerDao(MyCurrencyPair.class, myCurrencyPairDao);
    }
    
    public void clear() {
        myCurrencyPairDaoConfig.clearIdentityScope();
    }

    public MyCurrencyPairDao getMyCurrencyPairDao() {
        return myCurrencyPairDao;
    }

}
