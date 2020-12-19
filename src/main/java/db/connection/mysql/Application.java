package db.connection.mysql;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import db.connection.mongo.connection.collection.MongoEmployeeCollectionDao;
import db.connection.mysql.connection.dao.DepartmentDAO;
import db.connection.mysql.connection.dao.EmployeeDAO;
import db.connection.mysql.connection.dao.ManagerDAO;
import db.connection.mysql.connection.dao.SalaryDAO;
import db.connection.mysql.connection.model.Department;
import db.connection.mysql.connection.model.Employee;
import db.connection.mysql.connection.model.EmployeeProfile;
import db.connection.mysql.connection.model.Manager;
import db.connection.mysql.connection.service.DepartmentService;
import db.connection.mysql.connection.service.EmployeeService;
import db.connection.mysql.connection.service.ManagerService;
import db.connection.mysql.connection.service.SalaryService;

public class Application {

	private static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		
		// Data layer
		EmployeeDAO employeeDAO = new EmployeeDAO();
		SalaryDAO salaryDAO = new SalaryDAO();
		ManagerDAO managerDAO = new ManagerDAO();
		DepartmentDAO departmentDAO = new DepartmentDAO();
		MongoEmployeeCollectionDao mongoEmployeeCollectionDao = new MongoEmployeeCollectionDao("employee");
		
		// Business layer
		EmployeeService employeeService = new EmployeeService(employeeDAO, mongoEmployeeCollectionDao);
		SalaryService salaryService = new SalaryService(salaryDAO);
		ManagerService managerService = new ManagerService(managerDAO);
		DepartmentService departmentService = new DepartmentService(departmentDAO);
		
		// Presentation layer
		runPresentationLayer(employeeService, salaryService, managerService, departmentService);
	}
	

	private static void runPresentationLayer(
			EmployeeService employeeService, 
			SalaryService salaryService,
			ManagerService managerService,
			DepartmentService departmentService) {
		
		showMenu();
		
		while(true) {
			
			int choice = makeProcessChoice();
			
			if(choice == 10) {
				break;
			}
			
			switch (choice) {
				case 1:
					showAllEmployees(employeeService);
					System.out.println();
					break;
				case 2:
					showEmployeeProfile(employeeService, salaryService);
					System.out.println();
					break;
				case 3:
					insertEmployee(employeeService);
					System.out.println();
					break;
				case 4:
					updateEmployee(employeeService);
					System.out.println();
					break;
				case 5:
					deleteEmployee(employeeService);
					System.out.println();
					break;
				case 6:
					showMenu();
					System.out.println();
					break;
				case 7:
					listActiveManagers(managerService);
					break;
				case 8:
					listDepartments(departmentService);
					break;
				case 9:
					draftEmployeeProfileOperations(employeeService, salaryService);
					break;
			}
			
		}
	}
	
	
	private static void showMenu() {
		
		System.out.println("1- Tüm çalışan listesi ");
		System.out.println("2- Çalışan profili sorgulama ");
		System.out.println("3- Yeni çalışan ekleme ");
		System.out.println("4- Çalışan verilerini güncelleme ");
		System.out.println("5- Çalışan silme ");
		System.out.println("6- Menüyü tekrar yazdır ");
		System.out.println("7- Aktif yöneticilik yapanları listele ");
		System.out.println("8- Departmanları listele ");
		System.out.println("9- Geçici profil işlemleri ");
		System.out.println("10- Çıkış ");
	}
	
	public static int makeProcessChoice() {
		
		System.out.println("İşlem tercihinizi yapınız...");
		int choice = scanner.nextInt();
		return choice;
	}
	
	public static void showAllEmployees(EmployeeService employeeService) {
		
		Set<Employee> employees = employeeService.findAll();
		
		Iterator<Employee> iterator = employees.iterator();
		while(iterator.hasNext()) {
			
			Employee employee = iterator.next();
			System.out.println(employee);
		}
	}
	
	public static void showEmployeeProfile(EmployeeService employeeService, SalaryService salaryService) {
		
		System.out.println("Çalışan ID'sini giriniz: ");
		long empNo = scanner.nextLong();
		
		EmployeeProfile employeeProfile = employeeService.loadEmployeeProfile(empNo);
		
		if(employeeProfile == null) {
			System.out.println("Girdiğiniz ID değerine uygun bir çalışan profili bulunamamıştır");
			return;
		}
		
		List<Long> salaries = salaryService.getSalaries(empNo);
		employeeProfile.setSalaries(salaries);
		
		System.out.println(employeeProfile.getEmployee());
		System.out.println("Çalıştığı Departman: " + employeeProfile.getDepartmentName());
		System.out.println("Maaşları:");
		for(Long salary : employeeProfile.getSalaries()) {
			System.out.println(salary);
		}
	}
	
	public static void insertEmployee(EmployeeService employeeService) {
		
		System.out.println("Çalışan verilerini giriniz:");
		
		scanner.nextLine();
		
		System.out.println("İsim");
		String name = scanner.nextLine();
		
		System.out.println("Soyismi");
		String lastName = scanner.nextLine();
		
		System.out.println("Cinsiyet (M veya F giriniz)");
		String gender = scanner.next();
		gender = String.valueOf(gender.charAt(0));
		
		Employee employee = new Employee();
		employee.setName(name);
		employee.setLastName(lastName);
		employee.setGender(gender);
		employee.setHireDate(new Date());
		employee.setBirthDate(new Date());
		
		employee = employeeService.save(employee);
		System.out.println("Yeni oluşturulan çalışan bilgileri");
		System.out.println(employee);
	}
	
	public static void updateEmployee(EmployeeService employeeService) {
		
		System.out.println("Çalışan verilerini giriniz:");
		
		System.out.println("Çalışan ID'sini giriniz: ");
		long empNo = scanner.nextLong();
		
		scanner.nextLine();
		
		System.out.println("İsim");
		String name = scanner.nextLine();
		
		System.out.println("Soyismi");
		String lastName = scanner.nextLine();
		
		Employee employee = employeeService.update(empNo, name, lastName);
		
		System.out.println("Bilgileri güncellenen çalışan kaydı");
		System.out.println(employee);
	}
	
	public static void deleteEmployee(EmployeeService employeeService) {
		
		System.out.println("Çalışan ID'sini giriniz: ");
		long empNo = scanner.nextLong();
		
		boolean result = employeeService.delete(empNo);
		 
		if(!result) 
		{
			System.out.println("Girdiğiniz ID değerine sahip bir çalışan bulunmamaktadır");
			return;
		}
		else
		{
			System.out.println(empNo + " ID'li çalışan silinmiştir!");
		}
	}
	
	public static void listActiveManagers(ManagerService managerService) {

		List<Manager> allManager = managerService.loadAllActiveManagers();
		System.out.println("Aktif görev yapan yöneticiler");
		for (Manager manager : allManager) {
			System.out.println("Adı: "+manager.getEmployee().getName()+" departmanı: "+ manager.getDepartmentName());
		}

		
	}
	
	public static void listDepartments(DepartmentService departmentService) {
		List<Department> allDepart = departmentService.findAll();
		System.out.println("Tüm Departmanlar");
		for (Department department : allDepart) {
			System.out.println(department.getName());
		}
	}
	
	public static void draftEmployeeProfileOperations(EmployeeService employeeService, SalaryService salaryService) {
		
		System.out.println("1- Yeni profil bilgisi kaydetmek");
		System.out.println("2- Profil bilgisi almak");

		int choice = scanner.nextInt();
		
		if(choice == 1) {
			
			System.out.println("Çalışan ID'sini giriniz: ");
			long empNo = scanner.nextLong();
			
			EmployeeProfile employeeProfile = employeeService.loadEmployeeProfile(empNo);
			
			if(employeeProfile == null) {
				System.out.println("Girdiğiniz ID değerine uygun bir çalışan profili bulunamamıştır");
				return;
			}
			
			List<Long> salaries = salaryService.getSalaries(empNo);
			employeeProfile.setSalaries(salaries);
			
			
			boolean saveResult = employeeService.saveAsDraft(employeeProfile);
			if(saveResult) {
				System.out.println("Draft başarıyla kaydedildi.");
			}
			else {
				System.out.println("Draft kaydedilemedi!");
			}
			
		}
		else if(choice == 2) {
			
			System.out.println("Çalışan ID'sini giriniz: ");
			long empNo = scanner.nextLong();
			
			EmployeeProfile employeeProfile = employeeService.loadProfileAsDraft(empNo);
			
			if(employeeProfile == null) {
				System.out.println("Girdiğiniz ID değerine uygun bir çalışan profili bulunamamıştır");
				return;
			}
			
			System.out.println(employeeProfile);
		}
		
	}

}
