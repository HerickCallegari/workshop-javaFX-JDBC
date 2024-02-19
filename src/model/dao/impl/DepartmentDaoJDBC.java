package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try {

			st = conn.prepareStatement("INSERT INTO department" + " (Name) VALUES (?);");

			st.setString(1, obj.getName());

			st.execute();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?;");

			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			st.execute();
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
	}

	
	@Override
	public void delete(Department obj) {
	    PreparedStatement st = null;
	    try {
	        st = conn.prepareStatement("DELETE FROM department WHERE id = ?;");
	        st.setInt(1, obj.getId());

	        st.executeUpdate();

	    } catch (SQLException e) {
	        throw new DbException(e.getMessage());
	    } finally {
	        DB.closeStatement(st);
	    }
	}


	@Override
	public Department findById(Integer id) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("select * from department where Id = ?");
			st.setInt(1, id);

			rs = st.executeQuery();

			if (rs.next()) {
				return instanceDepartment(rs);
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
		return null;

	}

	public static Department instanceDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("Id"), rs.getString("Name"));

	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			List<Department> deps = new ArrayList<>();

			st = conn.prepareStatement("SELECT * from department;");
			rs = st.executeQuery();

			while (rs.next()) {
				deps.add(instanceDepartment(rs));
			}

			return deps;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

}
