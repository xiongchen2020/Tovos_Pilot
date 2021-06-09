package com.tovos.uav.sample.databean.sql;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.tovos.uav.sample.databean.sql.bean.DBHoverPoint;
import com.tovos.uav.sample.databean.sql.bean.DbAction;
import com.tovos.uav.sample.databean.sql.bean.DbMediaPoint;
import com.tovos.uav.sample.databean.sql.bean.DbTower;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;


public class TovosDBOpenHelper extends OrmLiteSqliteOpenHelper {

    public TovosDBOpenHelper(Context context) {

        super(context, "tovos.db", null, 1);

    }

    @Override

    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {

            // 创建表
            TableUtils.createTable(connectionSource, DbUAVRoute.class);
            TableUtils.createTable(connectionSource, DbTower.class);
            TableUtils.createTable(connectionSource, DBHoverPoint.class);
            TableUtils.createTable(connectionSource, DbAction.class);
            TableUtils.createTable(connectionSource, DbMediaPoint.class);

        } catch (SQLException | java.sql.SQLException e) {

            e.printStackTrace();

        }

    }

    @Override

    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {

            // 更新表

            TableUtils.dropTable(connectionSource, DbUAVRoute.class, true);

            onCreate(database, connectionSource);

        } catch (SQLException | java.sql.SQLException e) {

            e.printStackTrace();

        }

    }

    private static TovosDBOpenHelper instance;

    public static synchronized TovosDBOpenHelper getInstance(Context context) {

        if (instance == null) {

            synchronized (TovosDBOpenHelper.class) {

                if (instance == null) {

                    instance = new TovosDBOpenHelper(context);

                }

            }

        }

        return instance;

    }

    private Dao dao;

    // 获取操作数据库的DAO

    public Dao getUserDao(Class c) throws SQLException, java.sql.SQLException {

        if (dao == null) {

            dao = getDao(c);

        }

        return dao;

    }

    @Override

    public void close() {

        super.close();

        dao = null;

    }

}
