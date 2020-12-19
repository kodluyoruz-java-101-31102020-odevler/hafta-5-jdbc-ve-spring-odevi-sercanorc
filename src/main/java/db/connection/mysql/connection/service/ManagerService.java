package db.connection.mysql.connection.service;

import db.connection.mysql.connection.dao.ManagerDAO;
import db.connection.mysql.connection.model.Manager;

import java.util.List;

public class ManagerService {

	private ManagerDAO managerDAO;
	
	public ManagerService(ManagerDAO managerDAO) {
		this.managerDAO = managerDAO;
	}
	
	// buraya aktif yöneticileri listeleyen bir fonksiyon yazınız.
	public List<Manager> loadAllActiveManagers() {
		return this.managerDAO.loadAllActiveManagers();
	}
	
}
