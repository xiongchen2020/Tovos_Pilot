package com.tovos.uav.sample.databean.sql.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.tovos.uav.sample.databean.sql.TovosDBOpenHelper;
import com.tovos.uav.sample.databean.sql.bean.DbUAVRoute;

import java.sql.SQLException;
import java.util.List;

public class UavDao {
    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<DbUAVRoute, Integer> dao;

    public UavDao(Context context) {
        this.context = context;
        try {
            this.dao = TovosDBOpenHelper.getInstance(context).getDao(DbUAVRoute.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 向表中添加一条数据
    public void insert(DbUAVRoute data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除表中的一条数据
    public void delete(DbUAVRoute data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleById(int id){
        try {
            dao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改表中的一条数据
    public void update(DbUAVRoute data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询表中的所有数据
    public List<DbUAVRoute> selectAll() {
        List<DbUAVRoute> users = null;
        try {
            users = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // 根据ID取出信息
    public DbUAVRoute queryById(int id) {
        DbUAVRoute user = null;
        try {
            user = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
