package com.cg.digi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cg.digi.dao.IDeviceSelectionMatrixDao;
import com.cg.digi.logger.DigiLoggerUtils;
import com.cg.digi.model.Account;
import com.cg.digi.model.Dashboard;
import com.cg.digi.model.DeviceCatalog;
import com.cg.digi.model.Market;
import com.cg.digi.model.MobileLabCatalog;
import com.cg.digi.model.OsDetails;
import com.cg.digi.model.OsDistribution;
import com.cg.digi.model.Project;
import com.cg.digi.model.Reservation;
import com.cg.digi.model.User;
import com.cg.digi.model.VendorMarketTrend;
import com.cg.digi.model.Vendor_MarketShare;
import com.cg.digi.utilities.CSVUtils;
import com.cg.digi.utilities.CalendarUtils;
import com.cg.digi.utilities.DeviceDetails;
import com.cg.digi.utilities.PerfectoAPI1;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component("matrixService")
public class DeviceSelectionMatrixServiceImpl implements IDeviceSelectionMatrixService {
	@Autowired
	IDeviceSelectionMatrixDao matrixDao;

	@Override
	public String getPerfectoReserveId(String deviceId) {
		return matrixDao.getPerfectoReserveIdDao(deviceId);
	}

	@Override
	public List<Market> getMarkets() {
		return matrixDao.getMarkets();
	}

	@Override
	public List<DeviceCatalog> getDevices() {
		updateDeviceCatalog();
		return matrixDao.getDevices();
	}

	//Adding for Market selection
	/*@Override
	public List<DeviceCatalog> getPopularDevices(String market, String count) {
		updateDeviceCatalog();
		return matrixDao.getPopularDevices(market, count);
	}*/
	
	@Override
	public List<Vendor_MarketShare> getHighestMarket(String market) {
		return matrixDao.getHighestMarket(market);
	}

	@Override
	public List<DeviceCatalog> getRecommendedDevices(String market, String count) {
		List<DeviceCatalog> resultingDevice = new ArrayList<DeviceCatalog>();

		List<DeviceCatalog> devices = getDevices();
		//Adding for Market selection
		//List<DeviceCatalog> devices = getPopularDevices(market, count);
		Collections.sort(devices);

		List<Vendor_MarketShare> marketShare = getHighestMarket(market);
		double total = 0;
		Map<String, Integer> marketMap = new HashMap<String, Integer>();
		for (Vendor_MarketShare vendor_MarketShare : marketShare) {
			total += vendor_MarketShare.getValue();
		}
		for (Vendor_MarketShare vendor_MarketShare : marketShare) {

			marketMap.put(vendor_MarketShare.getVendor_name(), (int) ((vendor_MarketShare.getValue() / total) * 100));
		}
		marketMap = sortByComparator(marketMap);

		Iterator it = marketMap.entrySet().iterator();
		HashMap<String, Integer> deviceCount = new HashMap<String, Integer>();
		int totalDevices = 0;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int requiredCount = (Integer.parseInt(pair.getValue() + "") * Integer.parseInt(count)) / 100;
			totalDevices += requiredCount;
			deviceCount.put(pair.getKey().toString(), requiredCount);

		}
		if (totalDevices != Integer.parseInt(count + "")) {
			int remainingCount = Integer.parseInt(count + "") - totalDevices;
			Object[] keys = marketMap.keySet().toArray();

			deviceCount.put(keys[0] + "", deviceCount.get(keys[0]) + (remainingCount / 2));
			deviceCount.put(keys[1] + "", deviceCount.get(keys[1]) + remainingCount - (remainingCount / 2));
		}

		List<DeviceCatalog> returningCatalog = new ArrayList<DeviceCatalog>();
		it = marketMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int requiredCount = deviceCount.get(pair.getKey().toString());
			for (DeviceCatalog deviceCatalog : devices) {
				if (requiredCount > 0) {
					if (deviceCatalog.getManufacturer().equalsIgnoreCase(pair.getKey().toString())) {
						returningCatalog.add(deviceCatalog);
						requiredCount--;
					}
				}
			}
		}

		for (int i = 0; i < 2; i++) {
			it = marketMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				List<DeviceCatalog> vendorDevices = new ArrayList<DeviceCatalog>();
				if ((int) pair.getValue() > 25) {
					vendorDevices = getDevices(returningCatalog, pair.getKey().toString(), i);

				} else if ((int) pair.getValue() > 0 && i == 0) {
					vendorDevices = getDevices(returningCatalog, pair.getKey().toString(), 3);
				}
				resultingDevice.addAll(vendorDevices);
			}
		}

		return resultingDevice;

	}

	@SuppressWarnings("unused")
	private static List<DeviceCatalog> getDevices(List<DeviceCatalog> device, String vendor, int index) {
		List<DeviceCatalog> returningCatalog = new ArrayList<DeviceCatalog>();
		for (DeviceCatalog deviceCatalog : device) {
			if (deviceCatalog.getManufacturer().equalsIgnoreCase(vendor)) {
				returningCatalog.add(deviceCatalog);
			}
		}

		if (returningCatalog.size() > 0) {
			if (index == 0) {

				return returningCatalog.subList(index, returningCatalog.size() / 2);

			}
			if (index == 1) {
				return returningCatalog.subList((returningCatalog.size() / 2), returningCatalog.size());
			}
			return returningCatalog.subList(0, returningCatalog.size());
		} else
			return returningCatalog;

	}

	@SuppressWarnings("unused")
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {

				return o2.getValue().compareTo(o1.getValue());

			}
		});

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	@Override
	public boolean addReservationDetails(Reservation reserve) {
		matrixDao.updateDeviceCatalog(reserve.getDevicecatalogid(), "In Use");
		return matrixDao.addReservationDetails(reserve);

	}

	@Override
	public boolean updateReservationDetails(String deviceid) {
		matrixDao.updateDeviceCatalog(deviceid, "Available");
		return matrixDao.updateReservationDetails(deviceid);
	}

	@Override
	public boolean updateDeviceCatalog() {
		JSONObject deviceData = DeviceDetails.getSeetestDevices();
		JSONArray seetsetDevices = null;
		if (deviceData != null) {
			seetsetDevices = (JSONArray) deviceData.get("data");
		}
		
		JSONArray perfectoDevices = new PerfectoAPI1().getDeviceList("mobileapplicationtestinglab.in@capgemini.com", "asdf123");
		if (matrixDao.updateDeviceCatalog(seetsetDevices, "Seetest")
				&& matrixDao.updateDeviceCatalog(perfectoDevices, "Perfecto Partner")
				&& updateOffLineDeviceReservationNow()) {

			return true;
		}

		return false;
	}

	private boolean updateOffLineDeviceReservationNow() {
		boolean result = false;

		List<Reservation> reservationList = matrixDao.getCurrentOfflinrDeviceReservations();
		List<DeviceCatalog> devices = matrixDao.getDevices("Lab");
		for (DeviceCatalog deviceCatalog : devices) {

			if (reservationList != null) {
				boolean flag = true;
				for (Reservation reservation : reservationList) {
					if (deviceCatalog.getDevicecatalogid().equals(reservation.getDevicecatalogid())) {
						flag = false;
						result = matrixDao.updateDeviceCatalog(reservation.getDevicecatalogid(), "In Use");
					}
				}
				if (flag) {
					result = matrixDao.updateDeviceCatalog(deviceCatalog.getDevicecatalogid(), "Available");
				}
			} else {
				result = matrixDao.updateDeviceCatalog(deviceCatalog.getDevicecatalogid(), "Available");
			}
		}
		return result;

	}

	@Override
	public List<Reservation> getReservations(List<String> deviceId, String startDate, String endDate) {
		/*
		 * List<LocalDate> allDates = CalendarUtils.getDatesBetween(startDate, endDate);
		 */
		List<Reservation> reservations = matrixDao.getReservations(deviceId, startDate, endDate);
		/* List<Reservation> tempList = new ArrayList<Reservation>(); */

		/*
		 * for (LocalDate date : allDates) { for (String devId : deviceId) { boolean
		 * flag = true; for (Reservation reservation : reservations) { if
		 * (reservation.getDevicecatalogid().equals(devId) &&
		 * reservation.getStarttime().equals( date.toString()) &&
		 * !reservation.getApprovalStatus() .equalsIgnoreCase("Declined") &&
		 * !reservation.getApprovalStatus() .equalsIgnoreCase("Cancelled")) { flag =
		 * false; break; }
		 * 
		 * } if (flag) { Reservation newEntry = new Reservation(); DeviceCatalog device
		 * = matrixDao.getDeviceDetails(devId); newEntry.setDevicecatalogid(devId);
		 * newEntry.setDevicename(device.getDevicename());
		 * newEntry.setStarttime(date.toString());
		 * newEntry.setApprovalStatus("Available"); tempList.add(newEntry); }
		 * 
		 * } }
		 */

		/*
		 * for (String date : allDates) { boolean flag = true; for (Reservation
		 * reservation : reservations) { if (reservation.getStarttime().equals(date)) {
		 * flag = false; break; } } if (flag) { Reservation newEntry = new
		 * Reservation(); DeviceCatalog device = matrixDao.getDeviceDetails(deviceId);
		 * newEntry.setDevicecatalogid(deviceId);
		 * newEntry.setDevicename(device.getDevicename()); newEntry.setStarttime(date);
		 * newEntry.setApprovalStatus("Available"); tempList.add(newEntry); }
		 * 
		 * }
		 */

		/* reservations.addAll(tempList); */
		return reservations;
	}

	@Override
	public boolean bookDevice(Reservation reservation) {

		return matrixDao.addReservationDetails(reservation);
	}

	@Override
	public List<MobileLabCatalog> getdeviceRequests() {
		return matrixDao.getdeviceRequests();
	}

	@Override
	public boolean updateDeviceReservations(String reservationid, String status, String comment) {
		return matrixDao.updateDeviceReservations(reservationid, status, comment);
	}

	@Override
	public List<DeviceCatalog> getDevices(String vendor) {
		return matrixDao.getDevices(vendor);
	}

	@Override
	public boolean addDeviceDetails(DeviceCatalog device, String vendor) {
		return matrixDao.addDeviceDetails(device, vendor);
	}

	@Override
	public DeviceCatalog getDeviceDetails(String devicecatalogid) {
		DeviceCatalog device = matrixDao.getDeviceDetails(devicecatalogid);
		if (device.getBrowsers() != null) {
			String browser = device.getBrowsers().get(0);
			if (browser.contains(",")) {
				List<String> browsers = Arrays.asList(browser.split(","));
				device.setBrowsers(browsers);

			}
		}
		return device;
	}

	@Override
	public boolean deleteDevice(String devicecatalogid) {
		return matrixDao.deleteDevice(devicecatalogid);
	}

	@Override
	public boolean updateDeviceCatalog(DeviceCatalog device) {
		return matrixDao.updateDeviceCatalog(device);
	}

	@Override
	public List<MobileLabCatalog> getAllRequests() {
		return matrixDao.getAllRequestsDao();
	}

	@Override
	public List<MobileLabCatalog> getAllRequests(String createdby) {
		return matrixDao.getAllRequestsDao(createdby);
	}

	@Override
	public List<MobileLabCatalog> getAllRequestedBookings() {
		return matrixDao.getAllRequestedBookingsDao();
	}

	@Override
	public List<MobileLabCatalog> getUserRequestedBookings(String createdby) {
		return matrixDao.getUserRequestedBookingsDao(createdby);
	}

	@Override
	public List<User> viewUsers() {

		return matrixDao.viewUsersDao();
	}

	@Override
	public boolean addUser(User user) {
		return matrixDao.addUserDao(user);
	}

	@Override
	public List<Reservation> getAllReservationsDetails(String startDate, String endDate) {
		List<DeviceCatalog> devices = getDevices();
		List<String> deviceId = new ArrayList<String>();
		for (DeviceCatalog device : devices) {
			deviceId.add(device.getDevicecatalogid());
		}
		// LocalDate startDate = LocalDate.now();
		return getReservations(deviceId, startDate, endDate);
	}

	// /////////PROJECTSS////////////////////
	@Override
	public List<Project> viewProjects() {
		return matrixDao.viewProjectsDao();
	}

	@Override
	public boolean deleteProject(String projectId) {
		return matrixDao.deleteProjectDao(projectId);
	}

	@Override
	public boolean checkProject(String projectname) {
		return matrixDao.checkProjectDao(projectname);
	}

	@Override
	public boolean addProject(Project project) {
		return matrixDao.addProjectDao(project);
	}

	@Override
	public boolean editProject(Project project) {
		return matrixDao.editProjectDao(project);
	}

	@Override
	public List<Account> getAccounts() {
		return matrixDao.getAccountsDao();
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getDeviceUsage() {
		List<Dashboard> data = matrixDao.getDeviceUsage();
		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (Dashboard object : data) {
			obj = new JSONObject();
			obj.put("name", object.getKey());
			obj.put("y", Integer.parseInt(object.getValue()));
			array.add(obj);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getUserUsage() {
		List<Dashboard> data = matrixDao.getUserUsage();
		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (Dashboard object : data) {
			obj = new JSONObject();
			obj.put("name", object.getKey());
			obj.put("y", Integer.parseInt(object.getValue()));
			array.add(obj);
		}
		return array;

	}

	@Override
	public boolean addDeviceDeatils(File file) {
		List<DeviceCatalog> devices = readExcel(file);
		boolean result = true;
		if (devices != null) {
			for (DeviceCatalog device : devices) {
				if (!matrixDao.addDeviceDetails(device, device.getVendor())) {
					result = false;
				}
			}
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	public List<DeviceCatalog> readExcel(File file) {
		List<DeviceCatalog> devices = null;
		InputStream ExcelFileToRead;
		try {
			ExcelFileToRead = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);

			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row = null;
			XSSFCell cell = null;
			row = sheet.getRow(0);
			Iterator firstRowIterator = row.cellIterator();
			List<String> headings = new ArrayList<String>();
			while (firstRowIterator.hasNext()) {
				cell = (XSSFCell) firstRowIterator.next();
				headings.add(readCell(cell));
			}

			// iterate over data
			devices = new ArrayList<DeviceCatalog>();
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				DeviceCatalog device = new DeviceCatalog();
				row = sheet.getRow(i);
				List<String> browsers = new ArrayList<String>();
				for (int j = 0; j < row.getLastCellNum(); j++) {

					cell = row.getCell(j);
					String cellData = readCell(cell);
					switch (headings.get(j)) {
					case "Device Name":
						device.setDevicename(cellData);
						break;
					case "Model":
						device.setModel(cellData);

						break;
					case "Manufacturer":
						device.setManufacturer(cellData);
						break;
					case "Date of Procurement":
						device.setProcurementdate(cellData);
						break;
					case "Phone Number":
						device.setSimno(cellData);
						break;
					case "Device Type":
						device.setDevicetype(cellData);
						break;
					case "IMEI":
						device.setImei(cellData);
						break;
					case "OS":
						device.setOs(cellData);
						break;
					case "Version":
						device.setVersion(cellData);
						break;
					case "Chrome":
						if (cellData.equals("Yes")) {
							browsers.add(headings.get(j));
						}
						break;
					case "Safari":
						if (cellData.equals("Yes")) {
							browsers.add(headings.get(j));
						}
						break;
					case "IE":
						if (cellData.equals("Yes")) {
							browsers.add(headings.get(j));
						}
						break;
					case "Location":
						device.setLocation(cellData);
						break;
					case "Connected Via":
						device.setConnectivity(cellData);
						break;
					case "Remarks":
						device.setRemarks(cellData);
						break;

					default:
						DigiLoggerUtils.log(
								"Additional device feature is been added in Excel file : " + headings.get(j),
								DigiLoggerUtils.LEVEL.warn);
						break;
					}

				}
				device.setBrowsers(browsers);
				device.setVendor("Lab");
				device.setAvailability("Available In Lab");
				devices.add(device);
			}

		} catch (FileNotFoundException e) {
			DigiLoggerUtils.log(e.getMessage(), DigiLoggerUtils.LEVEL.error, this.getClass());
		} catch (IOException e) {
			DigiLoggerUtils.log(e.getMessage(), DigiLoggerUtils.LEVEL.error, this.getClass());
		}

		return devices;

	}

	public String readCell(XSSFCell cell) {
		String result = null;
		if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
			result = cell.getStringCellValue();
		} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			result = cell.getNumericCellValue() + "";
		} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
			result = cell.getBooleanCellValue() + "";
		}
		return result;

	}

	@Override
	public List<String> getVendors() {
		return matrixDao.getVendors();
	}

	@Override
	public List<Reservation> getEverydayReservation(List<String> deviceId, String startDate, String endDate) {

		List<LocalDate> allDates = CalendarUtils.getDatesBetween(startDate, endDate);
		List<Reservation> reservations = matrixDao.getReservations(deviceId, startDate, endDate);
		System.out.println("Before Reservation:" + reservations);
		List<Reservation> tempList = new ArrayList<Reservation>();

		for (LocalDate date : allDates) {
			for (String devId : deviceId) {
				boolean flag = true;
				for (Reservation reservation : reservations) {
					if (reservation.getDevicecatalogid().equals(devId)
							&& reservation.getStarttime().equals(date.toString())
							&& !reservation.getApprovalStatus().equalsIgnoreCase("Declined")
							&& !reservation.getApprovalStatus().equalsIgnoreCase("Cancelled")) {
						flag = false;
						break;
					}

				}
				if (flag) {
					Reservation newEntry = new Reservation();
					DeviceCatalog device = matrixDao.getDeviceDetails(devId);
					newEntry.setDevicecatalogid(devId);
					newEntry.setDevicename(device.getDevicename());
					newEntry.setStarttime(date.toString());
					newEntry.setApprovalStatus("Available");
					tempList.add(newEntry);
				}

			}
		}

		/*
		 * for (String date : allDates) { boolean flag = true; for (Reservation
		 * reservation : reservations) { if (reservation.getStarttime().equals(date)) {
		 * flag = false; break; } } if (flag) { Reservation newEntry = new
		 * Reservation(); DeviceCatalog device = matrixDao.getDeviceDetails(deviceId);
		 * newEntry.setDevicecatalogid(deviceId);
		 * newEntry.setDevicename(device.getDevicename()); newEntry.setStarttime(date);
		 * newEntry.setApprovalStatus("Available"); tempList.add(newEntry); }
		 * 
		 * }
		 */

		reservations.addAll(tempList);
		// System.out.println("After Reservation:"+ reservations);
		return reservations;
	}

	@Override
	public List<Reservation> getReservations(String userid, String deviceId, String startDate) {
		return matrixDao.getReservations(userid, deviceId, startDate);
	}

	@Override
	public JSONArray getDeviceStatusRate() {
		List<DeviceCatalog> devices = getDevices();
		return getDeviceStatusCount(devices);
	}

	@SuppressWarnings("unchecked")
	private JSONArray getDeviceStatusCount(List<DeviceCatalog> devices) {
		JSONObject deviceList = new JSONObject();

		for (DeviceCatalog device : devices) {
			String status = device.getAvailability();
			if (status.contains("Available In Lab")) {
				status = "Available";
			}
			if (deviceList.containsKey(status)) {
				int count = (int) deviceList.get(status);
				count++;
				deviceList.put(status, count);

			} else {

				deviceList.put(status, 1);
			}

		}

		JSONArray statusArray = new JSONArray();
		Set<String> status = deviceList.keySet();
		for (String statusName : status) {
			JSONObject statusObj = new JSONObject();
			statusObj.put("name", statusName);
			statusObj.put("y", deviceList.get(statusName));
			if (statusName.equalsIgnoreCase("Available")) {
				statusObj.put("color", "#50CF4D");

			} else if (statusName.equalsIgnoreCase("In Use")) {
				statusObj.put("color", "#FDB45C");

			} else if (statusName.equalsIgnoreCase("Offline")) {
				statusObj.put("color", "#CCCCCC");

			} else {
				statusObj.put("color", "#FF0000");
			}
			statusArray.add(statusObj);
		}

		return statusArray;

	}

	@Override
	public List<Reservation> getReservationCount() {
		return matrixDao.getReservationCount();
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getDeviceUsage(String time) {
		LocalDate end = null;
		LocalDate start = null;
		List<Dashboard> data = null;
		if (time.equalsIgnoreCase("week")) {
			end = LocalDate.now();
			start = end.with(DayOfWeek.MONDAY);
			data = matrixDao.getDeviceUsage(start, end);
		} else if (time.equalsIgnoreCase("month")) {
			LocalDate initial = LocalDate.now();
			start = initial.withDayOfMonth(1);
			end = initial.withDayOfMonth(initial.lengthOfMonth());
			data = matrixDao.getDeviceUsage(start, end);
		} else if (time.equalsIgnoreCase("allTime")) {
			data = matrixDao.getDeviceUsage();
		}

		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (Dashboard object : data) {
			obj = new JSONObject();
			obj.put("name", object.getKey());
			obj.put("y", Integer.parseInt(object.getValue()));
			array.add(obj);
		}
		return array;

	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getDeviceUsage(String start, String end) {
		List<Dashboard> data = null;
		LocalDate startDate = LocalDate.parse(start);
		LocalDate endDate = LocalDate.parse(end);
		data = matrixDao.getDeviceUsage(startDate, endDate);
		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (Dashboard object : data) {
			obj = new JSONObject();
			obj.put("name", object.getKey());
			obj.put("y", Integer.parseInt(object.getValue()));
			array.add(obj);
		}
		return array;

	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getUserUsage(String time) {
		LocalDate end = null;
		LocalDate start = null;
		List<Dashboard> data = null;
		if (time.equalsIgnoreCase("week")) {
			end = LocalDate.now();
			start = end.with(DayOfWeek.MONDAY);
			data = matrixDao.getUserUsage(start, end);
		} else if (time.equalsIgnoreCase("month")) {
			LocalDate initial = LocalDate.now();
			start = initial.withDayOfMonth(1);
			end = initial.withDayOfMonth(initial.lengthOfMonth());
			data = matrixDao.getUserUsage(start, end);
		} else if (time.equalsIgnoreCase("allTime")) {
			data = matrixDao.getUserUsage();
		}

		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (Dashboard object : data) {
			obj = new JSONObject();
			obj.put("name", object.getKey());
			obj.put("y", Integer.parseInt(object.getValue()));
			array.add(obj);
		}
		return array;

	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getUserUsage(String start, String end) {
		List<Dashboard> data = null;
		LocalDate startDate = LocalDate.parse(start);
		LocalDate endDate = LocalDate.parse(end);
		data = matrixDao.getUserUsage(startDate, endDate);
		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (Dashboard object : data) {
			obj = new JSONObject();
			obj.put("name", object.getKey());
			obj.put("y", Integer.parseInt(object.getValue()));
			array.add(obj);
		}
		return array;

	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getReservationCount(String time) {
		LocalDate end = null;
		LocalDate start = null;
		List<Reservation> data = null;
		if (time.equalsIgnoreCase("week")) {
			end = LocalDate.now();
			start = end.with(DayOfWeek.MONDAY);
			data = matrixDao.getReservationCount(start, end);
		} else if (time.equalsIgnoreCase("month")) {
			LocalDate initial = LocalDate.now();
			start = initial.withDayOfMonth(1);
			end = initial.withDayOfMonth(initial.lengthOfMonth());
			data = matrixDao.getReservationCount(start, end);
		} else if (time.equalsIgnoreCase("allTime")) {
			data = matrixDao.getReservationCount();
		}
		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (Reservation object : data) {
			obj = new JSONObject();
			obj.put("creationtime", object.getCreationtime());
			obj.put("devicecatalogid", object.getDevicecatalogid());
			array.add(obj);
		}

		return array;

	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getReservationCount(String start, String end) {
		List<Reservation> data = null;
		LocalDate startDate = LocalDate.parse(start);
		LocalDate endDate = LocalDate.parse(end);
		data = matrixDao.getReservationCount(startDate, endDate);
		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (Reservation object : data) {
			obj = new JSONObject();
			obj.put("creationtime", object.getCreationtime());
			obj.put("devicecatalogid", object.getDevicecatalogid());
			array.add(obj);
		}

		return array;
	}

	@Override
	public List<Reservation> updateDeviceStatus(List<Reservation> reservations, LocalDate startDate,
			LocalDate endDate) {
		List<DeviceCatalog> devices = matrixDao.getDevices();

		List<LocalDate> allDates = CalendarUtils.getDatesBetween(startDate.toString(), endDate.toString());
		System.out.println(startDate.atStartOfDay());

		/*
		 * for (LocalDate date : allDates) {
		 * 
		 * startDate.atStartOfDay();
		 * 
		 * 
		 * boolean flag = true; for (Reservation reservation : reservations) { if
		 * (reservation.getDevicecatalogid().equals(devId) &&
		 * reservation.getStarttime().equals(date.toString()) &&
		 * !reservation.getApprovalStatus().equalsIgnoreCase("Declined") &&
		 * !reservation.getApprovalStatus().equalsIgnoreCase("Cancelled")) { flag =
		 * false; break; }
		 * 
		 * } if (flag) { Reservation newEntry = new Reservation(); DeviceCatalog device
		 * = matrixDao.getDeviceDetails(devId); newEntry.setDevicecatalogid(devId);
		 * newEntry.setDevicename(device.getDevicename());
		 * newEntry.setStarttime(date.toString());
		 * newEntry.setApprovalStatus("Available"); tempList.add(newEntry); }
		 * 
		 * }
		 */

		return null;
	}

	@Override
	public JsonObject getVendorMarketShare(String marketid) {
		List<VendorMarketTrend> data = matrixDao.getVendorMarketShare(marketid);
		Collections.sort(data);
		return formatMarketData(data);
	}

	@Override
	public Market getMarket(String marketid) {
		return matrixDao.getMarket(marketid);
	}

	@Override
	public boolean addMarketTrend(String market, String filePath, String type) {
		boolean result = true;
		if (type.equals("vendor")) {
			updateMarketVendorStatus("N", market);
		} else {
			updateMarketOSStatus("N",market);
		}
		try {
			List<List<String>> data = CSVUtils.readCSV(filePath);
			VendorMarketTrend marketTrend = null;
			List<String> headers = data.get(0);
			for (int i = 1; i < headers.size(); i++) {
				for (int j = 1; j < data.size(); j++) {
					List<String> row = data.get(j);
					float value=Float.parseFloat(row.get(i).trim());
					if(value>=5) {
					marketTrend = new VendorMarketTrend();
					
					marketTrend.setStatus("Y");
					marketTrend.setMarketid(market);
					marketTrend.setVendorname(headers.get(i));
					marketTrend.setMonth(row.get(0));
					marketTrend.setValue(row.get(i));

					if (type.equals("vendor")) {
						result = matrixDao.addMarketVendorData(marketTrend);
					} else {
						result = matrixDao.addMarketOSData(marketTrend);
					}
					}
				}
			}
		} catch (Exception e) {
			result = false;
		}

		if (result && type.equals("vendor")) {
			
			removeMarketVendorOldData(market);
		} else if(!result && type.equals("vendor")) {
			updateMarketVendorStatus("Y", market);
		}else if (result && !type.equals("vendor")) {
			
			removeOSOldData(market);
		} else if(!result && type.equals("vendor")) {
			updateMarketOSStatus("Y", market);
		}
		return result;
	}

	private boolean removeOSOldData(String market) {
		return matrixDao.removeOSOldData(market);		
	}

	private boolean updateMarketOSStatus(String status, String marketid) {
		return matrixDao.updateMarketOSStatus(status, marketid);
	}

	private boolean removeMarketVendorOldData(String market) {

		return matrixDao.removeMarketVendorOldData(market);
	}

	private boolean updateMarketVendorStatus(String status, String marketid) {

		return matrixDao.updateMarketVendorData(status, marketid);
	}

	@Override
	public JsonObject getOsMarketShare(String marketid) {
		List<VendorMarketTrend> data = matrixDao.getOsMarketShare(marketid);
		Collections.sort(data);
		return formatMarketData(data);
	}

	public JsonObject formatMarketData(List<VendorMarketTrend> data) {
		JsonObject vendorData = new JsonObject();
		JsonArray timeArray = new JsonArray();
		JsonArray valueArray = new JsonArray();
		JsonArray series = new JsonArray();
		Set<String> vendorList = new HashSet<String>();
		Set<String> timeList = new LinkedHashSet<String>();
		for (VendorMarketTrend vendorMarketTrend : data) {
			vendorList.add(vendorMarketTrend.getVendorname());
			timeList.add(vendorMarketTrend.getMonth());
		}
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		timeArray = parser.parse(gson.toJson(timeList)).getAsJsonArray();
		vendorData.add("categories", timeArray);
		for (String vendor : vendorList) {
			JsonObject perVendorData = new JsonObject();
			ArrayList<Double> value = new ArrayList<Double>();
			perVendorData.addProperty("name", vendor);
			for (String time : timeList) {
				for (VendorMarketTrend vendorMarketTrend : data) {
					if (vendor.equals(vendorMarketTrend.getVendorname()) && time.equals(vendorMarketTrend.getMonth())) {
						value.add(Double.parseDouble(vendorMarketTrend.getValue()));
					}
				}
			}
			valueArray = parser.parse(gson.toJson(value)).getAsJsonArray();
			perVendorData.add("data", valueArray);
			series.add(perVendorData);
		}
		vendorData.add("series", series);
		return vendorData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getOsDistribution(String osid) {
		List<OsDistribution> osDistribution = matrixDao.getOsDistribution(osid);
		JSONArray array = new JSONArray();
		JSONObject obj = null;
		for (OsDistribution object : osDistribution) {
			obj = new JSONObject();
			obj.put("name", object.getVersion());
			obj.put("y", Double.parseDouble(object.getRate()));
			array.add(obj);
		}
		return array;
	}

	@Override
	public OsDetails getOsDetails(String osName) {
		return matrixDao.getOsDetails(osName);
	}

}
