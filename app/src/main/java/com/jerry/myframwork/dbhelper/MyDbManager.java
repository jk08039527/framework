package com.jerry.myframwork.dbhelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import com.jerry.baselib.App;
import com.jerry.baselib.BuildConfig;
import com.jerry.baselib.util.CollectionUtils;
import com.jerry.baselib.util.LogUtils;
import com.jerry.myframwork.greendao.DaoMaster;
import com.jerry.myframwork.greendao.DaoSession;


/**
 * 进行数据库的管理 1.创建数据库 2.创建数据库表 3.对数据库进行增删查改 4.对数据库进行升级
 */
public class MyDbManager {

    private DaoMaster.DevOpenHelper mHelper;
    private DaoSession mDaoSession;

    private static final String DB_PRO = "pro.db";
    private static volatile MyDbManager sDbManager;


    public static MyDbManager getInstance() {
        if (sDbManager == null) {
            synchronized (MyDbManager.class) {
                if (sDbManager == null) {
                    sDbManager = new MyDbManager(DB_PRO);
                }
            }
        }
        return sDbManager;
    }

    private MyDbManager() {
    }

    private MyDbManager(String dbName) {
        mHelper = new MyDbHelper(App.getInstance(), dbName);
        DaoMaster daoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
        setDebug();
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     */
    private void setDebug() {
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;
    }

    /**************************数据库插入操作***********************/
    /**
     * 插入单个对象
     */
    public boolean insertObject(Object object) {
        boolean flag = false;
        try {
            flag = mDaoSession.insert(object) != -1;
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }

    /**
     * 插入否则更新单个对象
     */
    public boolean insertOrReplaceObject(Object object) {
        boolean flag = false;
        try {
            flag = mDaoSession.insertOrReplace(object) != -1;
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }

    /**
     * 插入否则更新单个对象
     */
    public boolean update(Object object) {
        try {
            mDaoSession.update(object);
            return true;
        } catch (Exception e) {
            LogUtils.e(e.toString());
            return false;
        }
    }

    /**
     * 插入多个对象，并开启新的线程
     */
    public boolean insertOrReplaceObjects(final List<?> objects) {
        boolean flag = false;
        try {
            if (!CollectionUtils.isEmpty(objects)) {
                for (Object object : objects) {
                    mDaoSession.insertOrReplace(object);
                }
                flag = true;
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }


    /**
     * 数据库删除操作 删除某个数据库表
     */
    public boolean delete(Object obj) {
        boolean flag;
        try {
            mDaoSession.delete(obj);
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e.toString());
            flag = false;
        }
        return flag;
    }

    /**
     * 数据库删除操作 删除某个数据库表
     */
    public boolean deleteAll(Class<?> clss) {
        boolean flag;
        try {
            mDaoSession.deleteAll(clss);
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e.toString());
            flag = false;
        }
        return flag;
    }

    /**
     * 查询所有对象
     */
    public <T> QueryBuilder<T> obtainQueryBuilder(Class<T> object) {
        try {
            return (QueryBuilder<T>) mDaoSession.getDao(object).queryBuilder();
        } catch (Throwable e) {
            LogUtils.e(e.toString());
        }
        return null;
    }

    /**
     * 查询所有对象
     */
    public <T> T queryObj(Class<T> object, WhereCondition... conds) {
        try {
            QueryBuilder queryBuilder = mDaoSession.getDao(object).queryBuilder();
            if (conds != null) {
                for (WhereCondition cond : conds) {
                    if (cond != null) {
                        queryBuilder.where(cond);
                    }
                }
            }
            return (T) queryBuilder.list().get(0);
        } catch (Throwable e) {
            LogUtils.e(e.toString());
        }
        return null;
    }

    /**
     * 查询所有对象
     */
    @NonNull
    public <T> List<T>  queryAll(Class<T> object, Property order, WhereCondition... conds) {
        return queryObjs(object,0,order,conds);
    }

    /**
     * 查询所有对象
     */
    @NonNull
    public <T> List<T> queryObjs(Class<T> object, int limit, Property order, WhereCondition... conds) {
        try {
            QueryBuilder<T> queryBuilder = (QueryBuilder<T>) mDaoSession.getDao(object).queryBuilder();
            if (limit > 0) {
                queryBuilder.limit(limit);
            }
            for (WhereCondition cond : conds) {
                queryBuilder.where(cond);
            }
            if (order != null) {
                queryBuilder.orderDesc(order);
            }
            return queryBuilder.list();
        } catch (Throwable e) {
            LogUtils.e(e.toString());
        }
        return new ArrayList<>();
    }

    /***************************关闭数据库*************************/
    /**
     * 关闭数据库一般在Odestory中使用
     */
    public void closeDataBase() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (null != mDaoSession) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }
}
